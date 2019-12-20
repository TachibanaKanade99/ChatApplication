import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;

public class MsgPanel extends JPanel implements MsgListener, FilesendListener {

    private String client_name;
    private ChatClient chatclient;

    private DefaultListModel<String> MsgListModel = new DefaultListModel<>();
    private JList<String> MsgList = new JList<>(this.MsgListModel);
    private JTextField MsgField = new JTextField();
    private JButton openButton = new JButton("Open");

    public MsgPanel(ChatClient chatClient, String client_name) {
        this.chatclient = chatClient;
        this.client_name = client_name;

        this.chatclient.addMsgListener(this);
        this.chatclient.addFilesendListener(this);

        BorderLayout layout = new BorderLayout();
        layout.setHgap(10);
        layout.setVgap(10);
        setLayout(layout);
        add(new JScrollPane(this.MsgList), BorderLayout.NORTH);
        add(this.MsgField, BorderLayout.CENTER);
        add(this.openButton, BorderLayout.LINE_END);

//        add(this.openButton, BorderLayout.SOUTH.indexOf(2));

        MsgField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = MsgField.getText();
                try {
                    chatClient.Chat(client_name, msg);
                    MsgListModel.addElement("You >> " + msg);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser file = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
//                file.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                file.showSaveDialog(null);

                String file_direc = file.getSelectedFile().toString();
//                System.out.println(file_direc);
                try {
                    chatClient.FileSend(client_name, file_direc);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onMsg(String client_name, String msg) {
        String line = client_name + " >> " + msg;
        if (client_name.equals(this.client_name)) {
            MsgListModel.addElement(line);
        }
//        System.out.println(client_name);
//        System.out.println(this.client_name);
//        MsgListModel.addElement(line);
    }

    @Override
    public void onFile(String client_name, String file_name, String file_content) throws IOException {
        if (client_name.equals(this.client_name)) {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.showSaveDialog(null);

            String saved_file = fileChooser.getCurrentDirectory().toString();
            System.out.println(saved_file);

            String file_direc = saved_file + "\\" + file_name;
            System.out.println(file_direc);
            FileOutputStream fileO = new FileOutputStream(file_direc);
            fileO.write(file_content.getBytes());
            System.out.println("Send successfully");
//            fileChooser.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    String saved_file = fileChooser.getCurrentDirectory().toString();
//                    System.out.println(saved_file);
//                    try {
//                        String file_direc = saved_file + "\\" + file_name;
//                        System.out.println(file_direc);
//                        FileOutputStream fileO = new FileOutputStream(file_direc);
//                        fileO.write(file_content.getBytes());
//                        System.out.println("Send Successfully");
//                    }
//                    catch (FileNotFoundException ex) {
//                        ex.printStackTrace();
//                    }
//                    catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });
        }
    }
}
