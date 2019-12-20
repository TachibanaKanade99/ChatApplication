// Created by gemini in 10/23/2019

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class ServerWorker extends Thread {
    private Server server;
    private Socket clientSocket;
    private Socket chatSocket;

    private String login = null;
    private InputStream inputStream;
    private OutputStream outputStream;
    private FileInputStream fileI;

    public ServerWorker(Server server, Socket clientSocket){
        this.server = server;
        this.clientSocket = clientSocket;
    }

    public String getLogin() {
        return login;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public void run() {
        try {
            ClientSocket();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ClientSocket() throws IOException, InterruptedException {
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
        String line = "";

        while ((line = bufferedReader.readLine()) != null){
            String[] tokens = line.split(" "); // Split the string base on whitespace character

            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];

                if ("login".equalsIgnoreCase(cmd)){
                    Login(tokens);
                }

                else if ("logoff".equalsIgnoreCase(cmd) ||"quit".equalsIgnoreCase(cmd)) {
                    Logoff();
                    break;
                }

                else if ("chat".equalsIgnoreCase(cmd)){
                    String[] Msgtokens = line.split(" ", 3);
                    Chat(Msgtokens);
                }

                else if ("register".equalsIgnoreCase(cmd)){
                    Register(tokens);
                }

                else if ("filesend".equalsIgnoreCase(cmd)){
                    String[] MsgTokens = line.split(" ", 3);
                    fileSend(MsgTokens);
                }

                else{
                    String nolti = "Unknown " + cmd + "\n" + "Enter again" + "\n";
                    this.outputStream.write(nolti.getBytes());
                }

            }
        }

    }

    public void send(String message) throws IOException {
        this.outputStream.write(message.getBytes());
    }

    public void Login(String[] tokens) throws IOException {
        if (tokens.length == 3){
            String login = tokens[1];
            String password = tokens[2];
            String nolti;

//            if ((login.equals("tuan") && password.equals("tuan")) ||
//                    (login.equals("guest") && password.equals("guest")) ||
//                    (login.equals("gemini") && password.equals("gemini")))
//            {
            if (CorrectPassword(login, password)) {
                this.login = login;
                nolti = "Ok login successfully" + "\n";
                this.send(nolti);
                //  send online messages to others serverWorker:
                List<ServerWorker> serverWorkerList = server.getServerWorkersList();

                // Send current users online status to online user:
                for (ServerWorker worker: serverWorkerList){
                    if (!worker.getLogin().equals(null)){
                        if (!this.getLogin().equals(worker.getLogin())){
                            String message = worker.getLogin() + " Online" + "\n";
                            this.send(message);
                        }
                    }
                    else{
                        String message = "This worker is null" + "\n";
                        this.send(message);
                    }
                }

                //Send online user status to currrent users:
                nolti = this.getLogin() + " Online" + "\n";
                for (ServerWorker worker: serverWorkerList){
                    if (!this.getLogin().equals(worker.getLogin())){
                        worker.send(nolti);
                    }
                }

            }
            else{
                nolti = "Login Failed" + "\n";
                this.send(nolti);
            }
        }
    }

    // logoff
    public void Logoff() throws InterruptedException, IOException {

        this.server.RemoveWorker(this);

        List<ServerWorker> serverWorkerList = server.getServerWorkersList();

        //Send offline user status to currrent users:
        String message = this.getLogin() + " Offline" + "\n";
        for (ServerWorker worker: serverWorkerList){
            if (!this.getLogin().equals(worker.getLogin())){
                worker.send(message);
            }
        }

        //Delay the server thread to let only one user accept at the time:
        for (int i = 0; i < 10; i++) {
            Thread.sleep(100);
        }
        // Close client socket:
        clientSocket.close();
    }

    // register user_name user_password
    public void Register(String[] tokens) throws IOException {
        if (User_available(tokens[1]))
            this.outputStream.write("This user is available!\n".getBytes());

        else {
            FileWriter file = new FileWriter("F:/Tuan_Minh/HK191/Computer_Network/Assignment/Assignment1/ChatApplication/ChatServer/RegisterList/userdata.csv", true);

            BufferedWriter writer = new BufferedWriter(file);
            writer.write(tokens[1]+","+tokens[2]+"\n");
            this.outputStream.write("OK registered successfully\n".getBytes());
            writer.close();
            file.close();
        }
    }

    public boolean User_available(String searchUsername) throws IOException {
        BufferedReader buffer = Files.newBufferedReader(Paths.get("F:/Tuan_Minh/HK191/Computer_Network/Assignment/Assignment1/ChatApplication/ChatServer/RegisterList/userdata.csv"));
        String line = "";
        String tokens[];

        while ((line = buffer.readLine()) != null) {
            tokens = line.split(",",2);

            if (searchUsername.equals(tokens[0])) {
                return true;
            }
        }
        return false;
    }

    public boolean CorrectPassword(String username, String password) throws IOException {
        BufferedReader buffer = Files.newBufferedReader(Paths.get("F:/Tuan_Minh/HK191/Computer_Network/Assignment/Assignment1/ChatApplication/ChatServer/RegisterList/userdata.csv"));
        String line = "";
        String tokens[];

        while ((line = buffer.readLine()) != null) {
            tokens = line.split(",",2);
            if (username.equals(tokens[0]) && password.equals(tokens[1])) {
                return true;
            }
        }
        return false;
    }

//    // sendfile sendTo msg
//    private void fileSend(String[] tokens) throws IOException {
//        String sendTo = tokens[1];
//        String msg = tokens[2];
//
//        fileI = new FileInputStream(tokens[2]);
//        int reader;
//        int i = 0;
//        String buffer = "";
//
//        while ((reader = fileI.read()) != -1) {
//            buffer += (char) reader;
//            i++;
//        }
//        List<ServerWorker> serverWorkerList = server.getServerWorkersList();
//
//        for (ServerWorker worker: serverWorkerList){
//            if (sendTo.equalsIgnoreCase(worker.getLogin())){
//                worker.send("file::"+this.getLogin()+"::"+msg.substring(msg.lastIndexOf("/")+1)+"::"+buffer+"\n");
//            }
//        }
//        fileI.close();
//    }
//
//    // chat sendTo msg
//    private void Chat(String[] tokens) throws IOException {
//        String sendTo = tokens[1];
//        String msg = tokens[2];
//
//        List<ServerWorker> serverWorkerList = server.getServerWorkersList();
//
//        for (ServerWorker worker: serverWorkerList){
//            if (sendTo.equalsIgnoreCase(worker.getLogin())){
//                String message = this.getLogin() + " >> " + msg + "\n";
//                worker.send(message);
//            }
//        }
//    }

    // chat sendTo msg
    private void Chat(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String msg = tokens[2];


        List<ServerWorker> serverWorkerList = server.getServerWorkersList();

        for (ServerWorker worker: serverWorkerList){
            if (sendTo.equalsIgnoreCase(worker.getLogin())){
                ServerSocket chatNode;
                try {
                    chatNode = new ServerSocket(1234);
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
                try {
                    this.outputStream = chatSocket.getOutputStream();
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }
                // worker là thằng nhận
                worker.chatSocket = new Socket("127.0.0.1",1234);
                String message = this.getLogin() + " >> " + msg + "\n";

                worker.send(message);
                worker.chatSocket.close();
                try {
                    this.chatSocket.close();
                }
                catch (Exception e)
                {
                    System.out.println(e);
                }

                chatNode = null;
                this.chatSocket = null;
            }
        }
    }

    private void fileSend(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String msg = tokens[2];

        fileI = new FileInputStream(tokens[2]);
        int reader;
        int i = 0;
        String buffer = "";

        while ((reader = fileI.read()) != -1) {
            buffer += (char) reader;
            i++;
        }
        List<ServerWorker> serverWorkerList = server.getServerWorkersList();

        for (ServerWorker worker : serverWorkerList) {
            if (sendTo.equalsIgnoreCase(worker.getLogin())) {

                ServerSocket chatNode;
                try {
                    chatNode = new ServerSocket(1234);
                } catch (Exception e) {
                    System.out.println(e);
                }
                try {
                    this.outputStream = chatSocket.getOutputStream();
                } catch (Exception e) {
                    System.out.println(e);
                }
                // worker là thằng nhận
                worker.chatSocket = new Socket("127.0.0.1", 1234);

//                worker.send("filesend" + " " + this.getLogin() + " " + msg.substring(msg.lastIndexOf("/") + 1) + " " + buffer);
                String file_name = msg.substring(msg.lastIndexOf("\\")+1);
                System.out.println(file_name);
                worker.send("filesend" + " " + this.getLogin() + " " + file_name + " " + buffer);
                worker.chatSocket.close();
                try {
                    this.chatSocket.close();
                } catch (Exception e) {
                    System.out.println(e);
                }

                chatNode = null;
                this.chatSocket = null;
            }
        }
        fileI.close();
    }
}
