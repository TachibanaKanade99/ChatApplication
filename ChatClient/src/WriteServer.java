import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WriteServer extends Thread {

    public ChatClient chatClient;
    public Socket socket;
    public PrintWriter printWriter;
    public OutputStream serverOut;

    WriteServer(ChatClient chatClient, Socket socket){
        this.chatClient = chatClient;
        this.socket = socket;

        try{
            this.serverOut = socket.getOutputStream();
            printWriter = new PrintWriter(this.serverOut, true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        //Create scanner:
        Scanner scanner = new Scanner(System.in);

        String command = "";

        while (!command.equals("logoff") || command.equals("quit")){
            command = scanner.nextLine();
            printWriter.println(command);
        }

    }
}
