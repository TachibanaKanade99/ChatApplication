import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.ConnectException;

public class ChatApp implements UserStatusListener {
    // Attribute:
    private ChatClient chatClient;

    private JFrame jFrame;
    private JPanel Root_pannel;

    //Pannel1:
    private JPanel panel1;
    private JLabel addressLabel;
    private JLabel portLabel;
    private JTextField addressField;
    private JTextField portField;
    private JButton enterPortButton;
    private JLabel resultLabel;

    //Pannel2:
    private JPanel panel2;
    private JLabel LoginLabel;
    private JLabel UsernameLoginLabel;
    private JLabel PasswordLoginLabel;
    private JTextField UsernameLoginField;
    private JTextField PasswordLoginField;
    private JButton LoginButton;
    private JButton RegisterButton;
    private JLabel ResponseLabel;

    //Pannel3:
    private JPanel panel3;

    //UserList Panel:
    private JPanel UserListPanel;
    private DefaultListModel<String> UserListModel;
    private JList<String> UserList;


    //Panel for Msg:
    private MsgPanel msgPanel;

    public ChatApp() throws IOException {
        enterPortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String address = addressField.getText();
                int port = Integer.parseInt(portField.getText());
                try {
//                    chatClient = new ChatClient(port);
//                    chatClient.addUserStatusListener(ChatApp);
                    AddUsertoList(address, port);
                    chatClient.Connect();
                    resultLabel.setText("Connecting successfully");
                    jFrame.setContentPane(panel2);
                }
                catch (ConnectException ce) {
                    resultLabel.setText("Connection Refused");
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        LoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    chatClient.Login(UsernameLoginField.getText(), PasswordLoginField.getText());
                    ResponseLabel.setText(chatClient.Response());
                    if (ResponseLabel.getText().equals("Ok login successfully")) {
                        chatClient.readMsg();
                        jFrame.setContentPane(UserListPanel);
                    }
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        RegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    chatClient.Register(UsernameLoginField.getText(), PasswordLoginField.getText());
                    ResponseLabel.setText(chatClient.Response());
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    public void AddUsertoList(String server_address, int port){
        this.chatClient = new ChatClient(server_address, port);
        chatClient.addUserStatusListener(this);
    }

    public ChatClient getChatClient() {
        return chatClient;
    }

    public static void main(String[] args) throws IOException {
        ChatApp chatApp = new ChatApp();
        chatApp.jFrame = new JFrame("ChatApp");
        chatApp.jFrame.setContentPane(chatApp.panel1);
        chatApp.jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatApp.jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    chatApp.getChatClient().Logoff();
                    e.getWindow().dispose();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        chatApp.jFrame.pack();
        chatApp.jFrame.setVisible(true);
    }

    @Override
    public void Online(String client_name) {
        UserListModel.addElement(client_name);
    }

    @Override
    public void Offline(String client_name) {
        UserListModel.removeElement(client_name);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        this.UserListModel = new DefaultListModel<>();
        this.UserList = new JList<>(UserListModel);

        this.UserList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1 && !UserListModel.isEmpty()){
                    String client_name = UserList.getSelectedValue();
                    msgPanel = new MsgPanel(chatClient, client_name);

                    JFrame f = new JFrame("Message " + client_name);
                    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f.setSize(500, 200);
                    f.getContentPane().add(msgPanel, BorderLayout.CENTER);
                    f.setVisible(true);
                }
            }
        });
    }
}
