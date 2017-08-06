import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientMain {
    private JTextField userText;
    private JTextField passText;
    private JButton loginButton;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JButton logoutButton;
    private JPanel userManagement;
    private JLabel errorLabel;
    private JTextField toText;
    private JButton msgButton;
    private JLabel toLabel;
    private JTextField msgText;
    private JLabel msgLabel;
    private JTextArea chatText;
    private JLabel helloLabel;
    private String server_ip;

    public ClientMain(String s_ip) {
        loginButton.addActionListener(new LoginBtnClicked());
        logoutButton.addActionListener(new LogoutBtnClicked());
        logoutButton.setVisible(false);
        chatText.setVisible(false);
        toLabel.setVisible(false);
        toText.setVisible(false);
        msgLabel.setVisible(false);
        msgText.setVisible(false);
        msgButton.setVisible(false);
        msgButton.addActionListener(new MsgBtnClicked());
        server_ip = s_ip;
    }

    private class MsgBtnClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String msg = msgText.getText();
                String to = toText.getText();
                chatText.setText(chatText.getText() + "To " + to + ": " + msg + "\n");
                msgText.setText("");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "msg");
                jsonObject.put("to", to);
                jsonObject.put("text", msg);
                Socket socket = new Socket(server_ip, 4000);

                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.println(jsonObject.toJSONString());
                printWriter.flush();
            } catch (IOException e1) {
                errorLabel.setText("Can't connect to server");
            }
        }
    }

    private class LogoutBtnClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                usernameLabel.setVisible(true);
                passwordLabel.setVisible(true);
                userText.setVisible(true);
                passText.setVisible(true);
                loginButton.setVisible(true);
                logoutButton.setVisible(false);
                chatText.setVisible(false);
                toLabel.setVisible(false);
                toText.setVisible(false);
                msgLabel.setVisible(false);
                msgText.setVisible(false);
                msgButton.setVisible(false);
                helloLabel.setText("");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type","logout");
                Socket socket = new Socket(server_ip, 4000);

                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.println(jsonObject.toJSONString());
                printWriter.flush();
            } catch (IOException e1) {
                errorLabel.setText("Can't connect to server");
            }
        }
    }

    private class LoginBtnClicked implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String user = userText.getText();
                String pass = passText.getText();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "login");
                jsonObject.put("user", user);
                jsonObject.put("pass", pass);

                Socket socket = new Socket(server_ip, 4000);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.println(jsonObject.toJSONString());
                printWriter.flush();

                while (socket.getInputStream().available() == 0);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject1 = (JSONObject) jsonParser.parse(bufferedReader.readLine());
                boolean loggedin = (boolean) jsonObject1.get("loggedin");
                if(loggedin) {
                    helloLabel.setText("Hello, " + user + "!");
                    errorLabel.setText("");
                    usernameLabel.setVisible(false);
                    passwordLabel.setVisible(false);
                    userText.setVisible(false);
                    passText.setVisible(false);
                    loginButton.setVisible(false);
                    logoutButton.setVisible(true);
                    chatText.setVisible(true);
                    toLabel.setVisible(true);
                    toText.setVisible(true);
                    msgLabel.setVisible(true);
                    msgText.setVisible(true);
                    msgButton.setVisible(true);
                }
                else {
                    errorLabel.setText("Invalid username or password");
                }
            } catch (IOException e1) {
                errorLabel.setText("Can't connect to server");
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException, ParseException {
        if(args.length > 0) {
            ClientMain clientMain = new ClientMain(args[0]);
            JFrame frame = new JFrame("User Management");
            frame.setContentPane(clientMain.userManagement);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new Dimension(500, 500));
            frame.pack();
            frame.setVisible(true);

            ServerSocket serverSocket = new ServerSocket(5000);
            while (true) {
                Socket socket = serverSocket.accept();
                while (socket.getInputStream().available() == 0);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(bufferedReader.readLine());
                String from = (String) jsonObject.get("from");
                String msg = (String) jsonObject.get("text");
                clientMain.chatText.setText(clientMain.chatText.getText() + "From " + from + ": " + msg + "\n");
            }
        }
        else {
            System.out.println("Enter server ip as an argument!");
        }
    }
}
