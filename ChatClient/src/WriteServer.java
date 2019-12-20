import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class WriteServer extends Thread {

//    public ChatClient chatClient;
    private Socket socket;
    String str;
    public PrintWriter printWriter;
    public OutputStream serverOut;

    WriteServer(Socket socket, String str){
        this.socket = socket;
        this.str = str;

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
//        Scanner scanner = new Scanner(System.in);
//
//        String command = "";
//
//        while (!command.equals("logoff") || !command.equals("quit")){
//            command = scanner.nextLine();
//            printWriter.println(command);
//        }
        printWriter.println();

    }
}
