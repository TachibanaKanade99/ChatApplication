import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class ChatClient {

    //socket to connect to main server:
    private Socket socket;
    private String server_address;
    private int port;
    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    //User status:
    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MsgListener> msgListeners = new ArrayList<>();
    private ArrayList<FilesendListener> filesendListeners = new ArrayList<>();

    ChatClient(String server_address, int server_port){
//        this.server_address = "192.168.174.1";
        this.server_address = server_address;
        this.port = server_port;
    }

//    public static void main(String[] args) throws IOException {
//        ChatClient chatClient = new ChatClient(5000);
//        chatClient.addUserStatusListener(new UserStatusListener() {
//            @Override
//            public void Online(String client_name) {
//                System.out.println(client_name + " Online");
//            }
//
//            @Override
//            public void Offline(String client_name) {
//                System.out.println(client_name + " Offline");
//            }
//        });
//
//        chatClient.addMsgListener(new MsgListener() {
//            @Override
//            public void onMsg(String client_name, String msg) {
//                System.out.println(client_name + " >> " + msg);
//            }
//        });
//        chatClient.Connect();
//        chatClient.Login("admin", "admin");
//        System.out.println(chatClient.Response());
//        String directory = "C:\\Users\\KANADE\\OneDrive\\Desktop\\Hello.txt";
//        System.out.println(directory.substring(directory.lastIndexOf("\\")+1));
//        System.out.println(directory.substring(directory.lastIndexOf("\\" + 1)));
//    }

    //Connect to Server:
    public void Connect() throws IOException {
            this.socket = new Socket(this.server_address, this.port);
            this.serverIn = this.socket.getInputStream();
            this.serverOut = this.socket.getOutputStream();
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.serverIn));
            this.printWriter = new PrintWriter(this.serverOut, true);
    }

    //Send one request to server:
    public void Request(String str) throws IOException {
        printWriter.println(str);
    }

    //Return one message from server:
    public String Response() throws IOException {
        return bufferedReader.readLine();
    }

    //Login:
    public void Login(String client_name, String password) throws IOException {
        String cmd = "login" + " " + client_name + " " + password;
        this.Request(cmd);
//        String response = this.Response();
//        if ("Ok login successfully".equalsIgnoreCase(response)){
//            this.readMsg();
//        }
    }

    //Register:
    public void Register(String client_name, String password) throws IOException {
        String cmd = "register" + " " + client_name + " " + password;
        this.Request(cmd);
    }

    //Logoff:
    public void Logoff() throws IOException {
        String cmd = "logoff";
        this.Request(cmd);
    }

    //Chat: chat sendTo msg
    public void Chat(String client_name, String msg) throws IOException {
        String cmd = "chat" + " " + client_name + " " + msg;
        this.Request(cmd);
    }

    //FileSend: filesend sendTo msg
    public void FileSend(String client_name, String file_direc) throws IOException {
        String cmd = "filesend" + " " + client_name + " " + file_direc;
        this.Request(cmd);
    }

    //User status methods:
    public void addUserStatusListener(UserStatusListener userStatusListener){
        userStatusListeners.add(userStatusListener);
    }

    public void removeUserStatusListener(UserStatusListener userStatusListener){
        userStatusListeners.remove(userStatusListener);
    }

    //MsgListener methods:
    public void addMsgListener(MsgListener msgListener){
        msgListeners.add(msgListener);
    }

    public void removeMsgListener(MsgListener msgListener){
        msgListeners.remove(msgListener);
    }

    //FileSendListener methods:
    public void addFilesendListener(FilesendListener filesendListener){
        filesendListeners.add(filesendListener);
    }

    public void removeFilesendListener(FilesendListener filesendListener){
        filesendListeners.remove(filesendListener);
    }

    //Handle Online User:
    // tokens: User + "Online"
    private void handleOnline(String[] tokens) throws IOException {
        String client_name = tokens[0];
        for (UserStatusListener userStatusListener: userStatusListeners){
            userStatusListener.Online(client_name);
        }
    }

    //Handle Offline User:
    private void handleOffline(String[] tokens) throws IOException {
        String client_name = tokens[0];
        for (UserStatusListener userStatusListener: userStatusListeners){
            userStatusListener.Offline(client_name);
        }
    }

    //Read Messages Loop from Server:
    public void readMsg() {
        Thread t = new Thread(() -> {
            try {
                readMsgLoop();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    private void readMsgLoop() throws IOException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.serverIn));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                String[] tokens = line.split(" ");
                if (tokens != null && tokens.length > 0){
//                    String onlineMsg = tokens[1];
//                    System.out.println(line);
                    if ("Online".equalsIgnoreCase(tokens[1])){
                        handleOnline(tokens);
                    }
                    else if ("Offline".equalsIgnoreCase(tokens[1])){
                        handleOffline(tokens);
                    }
                    else if (">>".equalsIgnoreCase(tokens[1])){
                        String[] Msgtokens = line.split(" ", 3);
                        handleMsg(Msgtokens);
                    }
                    else if ("filesend".equalsIgnoreCase(tokens[0])){
                        String[] Filetokens = line.split(" ", 4);
                        handleFilesend(Filetokens);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            this.socket.close();
        }
    }

    private void handleFilesend(String[] filetokens) throws IOException {
        String client_name = filetokens[1];
        String file_name = filetokens[2];
        String file_content = filetokens[3];

        for (FilesendListener filesendListener: filesendListeners){
            filesendListener.onFile(client_name, file_name, file_content);
        }
    }

    private void handleMsg(String[] Msgtokens) {
        String client_name = Msgtokens[0];
        String msg = Msgtokens[2];

        for (MsgListener msgListener: msgListeners){
            msgListener.onMsg(client_name, msg);
        }
    }
}


