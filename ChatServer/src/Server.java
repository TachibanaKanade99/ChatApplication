//Created by gemini 10/29/2019

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private int server_port;

    Server(int port){
        this.server_port = port;
    }

    // List of ServerWorker:
    private ArrayList<ServerWorker> serverWorkersList = new ArrayList<>();

    public List<ServerWorker> getServerWorkersList() {
        return serverWorkersList;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(server_port);

            // Infinite loop to continue accept the connection from client:
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accept connection from " + clientSocket);

                // Create a new thread for client to handle clientSocket:
                ServerWorker serverWorker = new ServerWorker(this, clientSocket);

                //Add the serverworker created into the serverWorkerList:
                serverWorkersList.add(serverWorker);

                // Start the client thread:
                serverWorker.start();

            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void RemoveWorker(ServerWorker serverWorker) {
        serverWorkersList.remove(serverWorker);
    }
}
