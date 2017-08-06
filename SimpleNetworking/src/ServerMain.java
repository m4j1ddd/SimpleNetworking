import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class ServerMain {
    private JPanel showUsers;
    private JTextArea reportsText;
    private JLabel reportsLabel;
    private JLabel errorLabel;
    private ServerSocket serverSocket;
    private HashMap<String, String> users;
    private HashMap<String, String> ipUser;
    private HashMap<String, String> userIp;

    public ServerMain(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        users = new HashMap<String, String>();
        ipUser = new HashMap<String, String>();
        userIp = new HashMap<String, String>();
    }

    public static void main(String[] args) throws ParseException, IOException {
        ServerMain serverMain = new ServerMain(4000);
        JFrame frame = new JFrame("Server Show");
        frame.setContentPane(serverMain.showUsers);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(500, 500));
        frame.pack();
        frame.setVisible(true);
        if(new File("users.txt").exists()) {
            FileReader fileReader = new FileReader("users.txt");
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNext()) {
                String user = scanner.next();
                String pass = scanner.next();
                serverMain.users.put(user, pass);
            }

            while (true) {
                Socket socket = serverMain.serverSocket.accept();
                while (socket.getInputStream().available() == 0) ;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(bufferedReader.readLine());
                String ip = socket.getInetAddress().getHostAddress();
                String type = (String) jsonObject.get("type");
                String user;
                JSONObject jsonObject1;
                if (type.equals("login")) {
                    user = (String) jsonObject.get("user");
                    String pass = (String) jsonObject.get("pass");
                    jsonObject1 = new JSONObject();
                    if (serverMain.users.containsKey(user) && serverMain.users.get(user).equals(pass)) {
                        serverMain.ipUser.put(ip, user);
                        serverMain.userIp.put(user, ip);
                        serverMain.reportsText.setText(serverMain.reportsText.getText() + "Loggedin -> IP: " + ip + ", User: " + user + "\n");
                        jsonObject1.put("loggedin", true);
                    }
                    else {
                        jsonObject1.put("loggedin", false);
                    }

                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                    printWriter.println(jsonObject1.toJSONString());
                    printWriter.flush();
                } else if (type.equals("logout")) {
                    user = serverMain.ipUser.get(ip);
                    serverMain.ipUser.remove(ip);
                    serverMain.userIp.remove(user);
                    serverMain.reportsText.setText(serverMain.reportsText.getText() + "Loggedout -> IP: " + ip + ", User: " + user + "\n");
                } else if (type.equals("msg")) {
                    try {
                        user = serverMain.ipUser.get(ip);
                        String to = (String) jsonObject.get("to");
                        String msg = (String) jsonObject.get("text");
                        if (serverMain.userIp.containsKey(to)) {
                            String toIp = serverMain.userIp.get(to);

                            jsonObject1 = new JSONObject();
                            jsonObject1.put("from", user);
                            jsonObject1.put("text", msg);

                            Socket socket1 = new Socket(toIp, 5000);
                            PrintWriter printWriter = new PrintWriter(socket1.getOutputStream());
                            printWriter.println(jsonObject1.toJSONString());
                            printWriter.flush();
                        } else {
                            serverMain.reportsText.setText(serverMain.reportsText.getText() + "There is no client with this username: " + user + "\n");
                        }
                    } catch (IOException e) {
                        serverMain.reportsText.setText(serverMain.reportsText.getText() + "Can't connect to client\n");
                    }
                }
            }
        }
        else {
            serverMain.errorLabel.setText("There is no users.txt file in this directory");
        }
    }
}
