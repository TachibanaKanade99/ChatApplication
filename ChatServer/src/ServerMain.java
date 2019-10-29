//Created by gemini 10/23/2019

public class ServerMain {
    //Simple server main function:
    public static void main(String[] args) {
        int port = 80;
        Server server = new Server(port);

        //Start the server thread:
        server.start();
    }
}
