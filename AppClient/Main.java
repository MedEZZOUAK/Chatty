package AppClient;

import java.awt.*;

import data.Data;

import javax.crypto.KeyGenerator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import appServer.RequestType;
import appServer.appMain;

import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.awt.event.ActionEvent;

public class Main extends JFrame {

    private static final String serverAddress = "127.0.0.1";

    public static final int PORT = 59003;

    public ArrayList<String> names = new ArrayList<>();

    public String user;

    public void setFriend_(String friend_) {
        this.friend_ = friend_;
    }

    private String friend_;

    private Socket socket;

    private ObjectInputStream in;

    private ObjectOutputStream out;

    private JPanel contentPane;

    private JTextField input;

    private JTextField add;

    JButton sendBtn;

    JButton filebtn;

    JList list;

    JTextArea textArea;

    public String getName() {
        return user;
    }

    public String getSocket(String username) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
            Statement stmt = con.createStatement();
            String qry = "select Socket from " + username + " where FriendName=" + " '" + username + "';";
            ResultSet rs = stmt.executeQuery(qry);
            if (rs.next()) {
                return rs.getString("Socket");
            }
        } catch (Exception ex) {
            //TODO
        }
        return null;
    }

    public void running() throws IOException {
        this.user = this.names.get(0);
        try {
            socket = new Socket(serverAddress, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                String line = (String) in.readObject();
                if (line.startsWith("SUBMIT")) {
                    String name = getName();
                    out.writeObject(name);
                    this.setTitle("Chatty : " + name);
                } else if (line.startsWith("CONNECTED")) {
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
                        Statement stmt = con.createStatement();
                        String qry = "update " + user + " set Socket = '" + socket + "' where FriendName = '" + user + "';";
                        stmt.executeUpdate(qry);
                        System.out.println("This is the socket: " + socket);
                        out.writeObject("SUCCESS");
                        break;
                    } catch (Exception e) {
                        //TODO
                    }
                }
            }
            while (true) {
                try {
                    in = new ObjectInputStream(socket.getInputStream());
                    System.out.println("Second loop");
                    DefaultListModel input = (DefaultListModel) in.readObject();
                    if (input == null) {
                        continue;
                    }
                    System.out.println("ACCEPTED");
                    int type = (int) input.elementAt(0);
                    if (type == RequestType.SEND_MSG) {
                        System.out.println("User: " + user);
                        //receive the message
                        String message = (String) input.elementAt(1);
                        String sender = (String) input.elementAt(2);
                        String key = (String) input.elementAt(3);
                        System.out.println("From " + sender );
                        try{
                        RSA rsa = new RSA(1024);
                        PrivateKey myPrivatekey = rsa.stringToPrivateKey(getprivatekey(user));
                        String decryptedKey = rsa.decryptText(key,myPrivatekey);
                        System.out.println("Decrypted Blowfish Key: " + decryptedKey);
                        BlowfishCipher cipher = new BlowfishCipher(decryptedKey);
                        String decryptedMessage = cipher.decrypt(message);
                        System.out.println("Decrypted Message: " + decryptedMessage);
                        textArea.append(sender + ": " + decryptedMessage + "\n");
                        } catch (Exception e) {
                            System.out.println("FAILED TO DECRYPT");
                            e.printStackTrace();
                        }
                    } else if (type == RequestType.SEND_FILE) {
//                        Data data = (Data) input.elementAt(1);
//                        String sender = (String) input.elementAt(2);
//                        //String key = (String) input.elementAt(3);
//                        JFileChooser choose = new JFileChooser();
//                        int c = choose.showSaveDialog(null);
//                        if (c == JFileChooser.APPROVE_OPTION) {
////                            String Privatekey = ' ';
////                            System.out.println(Privatekey);
//                            byte[] b = data.getFile();
////                            byte[] bDec = FileEncryption.decrypt(b , Privatekey);
//                            File f = new File(choose.getSelectedFile().getPath() + data.getName().substring(data.getName().indexOf(".")));
//                            System.out.println(f.getAbsolutePath());
//                            FileOutputStream outFile = new FileOutputStream(f);
////                            outFile.write(bDec);
//                            outFile.close();
//                            textArea.append(sender + ": " + f.getAbsolutePath() + " File Saved.\n");
//                        }
                    }
                } catch (Exception ex) {
                    //TODO
                }
            }
        } catch (ClassNotFoundException e) {
        } finally {
        }
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) throws Exception {
    /*
    if(args.length == 0) {
      System.out.print("Pass the run keyword as the sole command line arguement.");
      return;
    }
    if(args.length ==1 && args[0].equalsIgnoreCase("run")) {

     */
        LoginApp login = new LoginApp();
        login.setVisible(true);
      /*
    }
    */

    }

    /**
     * Create the frame.
     *
     * @param username
     */
    public Main(String username) throws Exception {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
        setBounds(100, 100, 726, 800);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        String Key = getprivatekey(username);
        RSA rsa = new RSA(1024);
        PrivateKey mykey = rsa.stringToPrivateKey(Key);
        
        
        //Create a JLabel for the image
        JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("banner2.jpeg")));
        iconLabel.setBounds(0, 0, 726, 225); // Adjust the size and position as needed
        contentPane.add(iconLabel);

        JPanel panel = new JPanel();
        panel.setBounds(0, 20, 203, 700);
        contentPane.add(panel);
        panel.setLayout(null);


        JPanel panel_2 = new JPanel();
        panel_2.setBounds(0, 20, 203, 139);
        panel.add(panel_2);
        panel_2.setLayout(null);

        list = refreshList(username);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                input.setEditable(true);
            }
        });

        //friends list
        list.setBounds(12, 300, 179, 240);
        panel.add(list);

        //friend name input
        add = new JTextField();
        add.setBounds(12, 600, 125, 22);
        panel.add(add);
        add.setColumns(10);

        JButton addfriend = new JButton("+");


        //button style
        addfriend.setBackground(Color.CYAN);
        addfriend.setForeground(Color.BLACK);
        addfriend.setFont(new Font("Arial", Font.BOLD, 12));

        addfriend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String friendToAdd = add.getText();
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
                    Statement stmt = con.createStatement();
                    String qry = "select * from login where Username=" + " '" + friendToAdd + "';";
                    ResultSet rs = stmt.executeQuery(qry);
                    if (rs.next()) {
                        qry = "insert into " + user + " values('" + friendToAdd + "', '" + getSocket(friendToAdd) + "');";
                        stmt.executeUpdate(qry);
                        JOptionPane.showMessageDialog(null, "Friend added!");
                        add.setText("");
                    } else {
                        JOptionPane.showMessageDialog(null, "User does not exist.");
                    }
                } catch (Exception e) {
                    //TODO
                }
                list = refreshList(user);
            }
        });

        //the + button
        addfriend.setBounds(150, 600, 41, 25);
        panel.add(addfriend);

    /*
    JPanel panel_1 = new JPanel();
    panel_1.setBounds(203, 0, 505, 60);
    contentPane.add(panel_1);
    panel_1.setLayout(null);

    JButton callBtn = new JButton("Call");
    callBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        
      }
    });
    callBtn.setBounds(427, 13, 66, 34);
    panel_1.add(callBtn);

     */

        // input area for chat
        JPanel panel_3 = new JPanel();
        panel_3.setBounds(215, 600, 481, 60);
        contentPane.add(panel_3);
        panel_3.setLayout(null);

        input = new JTextField();
        input.setBounds(12, 13, 284, 34);
        panel_3.add(input);
        input.setColumns(10);
        input.setEditable(false);

        sendBtn = new JButton("Send");

        //button style
        sendBtn.setBackground(Color.BLACK);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("Arial", Font.BOLD, 12));

        sendBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    out = new ObjectOutputStream(socket.getOutputStream());
                    DefaultListModel sendModel = new DefaultListModel();
                    sendModel.addElement(RequestType.SEND_MSG);
                    String message = input.getText();
                    //create a key for blowfish
                    String blowfishKey = KeyGenerator.getInstance("Blowfish").generateKey().toString();
                    BlowfishCipher cipher = new BlowfishCipher(blowfishKey);
                    String encryptedMessage = cipher.encrypt(message);
                    //crypt the Blowfish key with RSA
                    RSA rsa = new RSA(1024);
                    PublicKey keyPublic = rsa.stringToPublicKey(getFriendKey((String) list.getSelectedValue()));
                    String encryptedBlowfishKey = rsa.encryptText(blowfishKey, keyPublic);
                    sendModel.addElement(encryptedMessage);
                    sendModel.addElement(user);
                    DefaultListModel friends = new DefaultListModel();
                    friends.addElement((String) list.getSelectedValue());
                    sendModel.addElement(friends);
                    sendModel.addElement(encryptedBlowfishKey);
                    textArea.append(user + ": " + message + "\n");
                    out.writeObject(sendModel);
                    input.setText("");
                } catch (Exception e1) {
                    // TODO
                    System.out.println("FAILED");
                }
            }
        });
        sendBtn.setBounds(397, 13, 72, 34);
        panel_3.add(sendBtn);

        filebtn = new JButton("File");
        //button style
        filebtn.setBackground(Color.BLACK);
        filebtn.setForeground(Color.WHITE);
        filebtn.setFont(new Font("Arial", Font.BOLD, 12));


        filebtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                openFile(arg0);
            }
        });
        filebtn.setBounds(318, 13, 67, 34);
        panel_3.add(filebtn);

        textArea = new JTextArea();
        textArea.setEditable(false);
        // chat area
        textArea.setBounds(215, 250, 481, 318);
        contentPane.add(textArea);

    }

    private void openFile(ActionEvent evt) {
//        try {
//            out = new ObjectOutputStream(socket.getOutputStream());
//            JFileChooser choose = new JFileChooser();
//            int c = choose.showOpenDialog(this);
//            if (c == JFileChooser.APPROVE_OPTION) {
//                File f = choose.getSelectedFile();
//                FileInputStream inFile = new FileInputStream(f);
//                byte b[] = new byte[inFile.available()];
//                inFile.read(b);
//                inFile.close();
//                //String Privatekey = userPrivate;
//                byte[] bEnc = FileEncryption.encrypt(b , Privatekey);
//                Data data = new Data();
//                data.setStatus("File");//TODO
//                data.setName(f.getName());
//                data.setFile(bEnc);
//                DefaultListModel fileModel = new DefaultListModel();
//                fileModel.addElement(RequestType.SEND_FILE);
//                fileModel.addElement(data);
//                fileModel.addElement(user);
//                DefaultListModel<String> friends = new DefaultListModel<String>();
//                friends.addElement((String) list.getSelectedValue());
//                fileModel.addElement(friends);
//                fileModel.addElement(friend_);
//                //fileModel.addElement(key);
//                out.writeObject(fileModel);
//            }
//        } catch (Exception e) {
//            //TODO
//        }
    }

    public JList refreshList(String username) {
        DefaultListModel listModel = new DefaultListModel();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
            Statement stmt = con.createStatement();
            String qry = "select * from " + username;
            ResultSet rs = stmt.executeQuery(qry);
            while (rs.next()) {
                String friend = rs.getString("FriendName");
                if (!friend.equals(username)) {
                    listModel.addElement(friend);
                }
            }
            list.setModel(listModel);
        } catch (Exception e) {
            //TODO
        }
        return new JList(listModel);
    }
    public String getFriendKey(String name) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
            Statement stmt = con.createStatement();
            String qry = "select * from login where Username=" + " '" + name + "';";
            ResultSet rs = stmt.executeQuery(qry);
            if (rs.next()) {
                return rs.getString("PublicKey");
            }
        } catch (Exception ex) {
            //TODO
        }
        return null;
    }
    public String getprivatekey(String name){
        //get the public key from the private table
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
            Statement stmt = con.createStatement();
            String qry = "select * from private where user=" + " '" + name + "';";
            ResultSet rs = stmt.executeQuery(qry);
            if (rs.next()) {
                return rs.getString("PrivateKey");
            }
        } catch (Exception ex) {
            //TODO
        }
        return null;
    }
}
