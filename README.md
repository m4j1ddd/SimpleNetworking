In the name of God

In this project I implement a simple client and server using java that communicate with each other using sockets.
I use JSON to serialize I/O of sockets.

For running a network of hosts you can use mininet or any thing else that represents different ips for different hosts.

For running server go to /SimpleNetworking/out/artifacts/SimpleNetworkingServer then you can see list of users and passwords in users.txt file that you can change it for yourself.
For excuting server program, run the command below on terminal:
java -jar SimpleNetworkingServer.jar

For running client go to /SimpleNetworking/out/artifacts/SimpleNetworkingClient.
For excuting client program, run the command below on terminal:
java -jar SimpleNetworkingClient.jar server_ip

Notice: server_ip in the command above is the ip of server that client should connect.
