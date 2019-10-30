import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadServer extends Thread {

    public ChatClient chatClient;
    public Socket socket;
    public BufferedReader bufferedReader;
    public InputStream serverIn;


    ReadServer(ChatClient chatClient, Socket socket){
        this.chatClient = chatClient;
        this.socket = socket;

        try{

            this.serverIn = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(this.serverIn));

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String response;

        try{
            while((response = bufferedReader.readLine()) != null){
                System.out.println("From server: " + response);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
