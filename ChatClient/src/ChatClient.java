import java.io.*;
import java.net.Socket;
import java.nio.Buffer;


public class ChatClient {
    private int port;
    private String address;

    ChatClient(String address, int serverport){
        this.address = address;
        this.port = serverport;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public void run() throws IOException {
        Socket clientSocket = new Socket(address, port);

        WriteServer writeServer = new WriteServer(this, clientSocket);
        ReadServer readServer = new ReadServer(this, clientSocket);

        // start read and write from server:
        writeServer.start();
        readServer.start();
    }

    public static void main(String[] args) throws IOException {

        String address = "localhost";
        int serverport = 80;
        ChatClient chatClient = new ChatClient(address, serverport);
        chatClient.run();

    }

}
