import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadServer extends Thread {

//    public ChatClient chatClient;
    private Socket socket;
    private BufferedReader bufferedReader;
    private InputStream serverIn;
    private String msg;


    ReadServer(Socket socket){
        this.socket = socket;

        try{

            this.serverIn = this.socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(this.serverIn));

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public void run() {
        try{
            while((msg = bufferedReader.readLine()) != null){
                System.out.println("From server " + msg);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
