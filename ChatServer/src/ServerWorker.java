// Created by gemini in 10/23/2019

import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.net.Socket;
import java.util.List;


public class ServerWorker extends Thread {

    public Server server;
    public Socket clientSocket;

    private String login = null;
    public InputStream inputStream;
    public OutputStream outputStream;

    public ServerWorker(Server server, Socket clientSocket){
        this.server = server;
        this.clientSocket = clientSocket;
    }

    public String getLogin() {
        return login;
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

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";

        while ((line = bufferedReader.readLine()) != null){
            String[] tokens = StringUtils.split(line); // Split the string base on whitespace character

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
                    String[] Msgtokens = StringUtils.split(line, null, 3);
                    Chat(Msgtokens);
                }

                else{
                    String nolti = "Unknown" + cmd + "\n" + "Enter again" + "\n";
                    this.outputStream.write(nolti.getBytes());
                }

            }
        }

    }

    // chat receiver msg
    private void Chat(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String msg = tokens[2];

        List<ServerWorker> serverWorkerList = server.getServerWorkersList();

        for (ServerWorker worker: serverWorkerList){
            if (sendTo.equalsIgnoreCase(worker.getLogin())){
                String message = this.getLogin() + ">> " + msg + "\n";
                worker.send(message);
            }
        }
    }

    public void Login(String[] tokens) throws IOException {
        if (tokens.length == 3){
            String login = tokens[1];
            String password = tokens[2];
            String nolti;

            if ((login.equals("tuan") && password.equals("tuan")) ||
                    (login.equals("guest") && password.equals("guest")) ||
                    (login.equals("gemini") && password.equals("gemini")))
            {
                this.login = login;
                nolti = "Ok login sucessfully" + "\n";
                this.outputStream.write(nolti.getBytes());

                //  send online messages to others serverWorker:
                List<ServerWorker> serverWorkerList = server.getServerWorkersList();

                // Send current users online status to online user:
                for (ServerWorker worker: serverWorkerList){
                    if (!worker.getLogin().equals(null)){
                        if (!this.getLogin().equals(worker.getLogin())){
                            String message = worker.getLogin() + " is Online" + "\n";
                            this.send(message);
                        }
                    }
                    else{
                        String message = "This worker is null" + "\n";
                        this.send(message);
                    }
                }

                //Send online user status to currrent users:
                nolti = this.getLogin() + " is Online" + "\n";
                for (ServerWorker worker: serverWorkerList){
                    if (!this.getLogin().equals(worker.getLogin())){
                        worker.send(nolti);
                    }
                }

            }
            else{
                nolti = "Login Failed" + "\n";
                this.outputStream.write(nolti.getBytes());
            }
        }
    }

    public void Logoff() throws InterruptedException, IOException {

        this.server.RemoveWorker(this);

        List<ServerWorker> serverWorkerList = server.getServerWorkersList();

        //Send offline user status to currrent users:
        String message = this.getLogin() + " is Offline" + "\n";
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

    public void send(String message) throws IOException {
        this.outputStream.write(message.getBytes());
    }
}
