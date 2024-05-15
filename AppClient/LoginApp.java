package AppClient;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import appServer.RequestType;
import appServer.appMain;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;


public class LoginApp extends JFrame {
  private JPanel contentPane;
  
  /**
   * Creates a JTextField called username, where the user enters their username.
   */
  private JTextField username;
  
  /**
   * Creates a JPasswordField with Echo-characters which permits the user to enter their password.
   */
  private JPasswordField password;
  
  /**
   * Stores the name of the user.
   */
  public String user;
  
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
  }
  /**
   * Create the frame.
   */
  public LoginApp() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
    this.setTitle("Login");

    setBounds(100, 100, 450, 450);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);

    // Create a JLabel for the image
    JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("img.jpeg")));
    iconLabel.setBounds(150, 20, 180, 180); // Adjust the size and position as needed
    contentPane.add(iconLabel);
    //title of the windows


    /*
    JPanel panel = new JPanel();
    panel.setBounds(35, 75, 93, 94);
    contentPane.add(panel);
    */

    JPanel panel_1 = new JPanel();
    panel_1.setBounds(126, 230, 261, 94);
    contentPane.add(panel_1);
    panel_1.setLayout(null);
    
    username = new JTextField();
    username.setBounds(98, 13, 116, 22);
    panel_1.add(username);
    username.setColumns(10);
    
    JLabel lblNewLabel = new JLabel("Username:");
    lblNewLabel.setBounds(12, 16, 74, 16);
    panel_1.add(lblNewLabel);
    
    JLabel lblNewLabel_1 = new JLabel("Password:");
    lblNewLabel_1.setBounds(12, 62, 74, 16);
    panel_1.add(lblNewLabel_1);
    
    password = new JPasswordField();
    password.setBounds(98, 59, 116, 22);
    panel_1.add(password);
    
       
    JButton loginBtn = new JButton("Login");
    //button style
    loginBtn.setBackground(Color.BLACK);
    loginBtn.setForeground(Color.WHITE);
    loginBtn.setFont(new Font("Arial", Font.BOLD, 12));


    loginBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //When the "login" button is clicked a  request is made to the MySQL database to check if the 
        //user is registered and if their entered passwords match.
        try {
          Class.forName("com.mysql.cj.jdbc.Driver");
          //Create a JDBC connection with the database.
          Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
          //Creates a statement using the connection
          Statement stmt = con.createStatement();
          String name = username.getText();
          String pass = new String(password.getPassword());
          //Create a String which is the query to be run in SQL
          String qry = "select * from login where Username="+" '"+name+"';";
          //Stores the returned value of the query in the ResultSet
          ResultSet rs = stmt.executeQuery(qry);
          //Checks if the ResultSet has values stored for the given query
          if(rs.next()) {
            //If the password and username match, it launches the main application.
            if(pass.equals(rs.getString("Password"))) {
              Main main = new Main(name);
              main.setVisible(true);
              main.names.add(name);
              //Creates a new Thread to keep the running() method always running
              Thread newThread = new Thread(new Runnable() {
                @Override
                public void run() {
                  try {
                    main.running();
                  } catch (IOException e) {
                    // TODO
                  }
                }
              });
              newThread.start();
              setVisible(false);
            }
            else {
              JOptionPane.showMessageDialog(null, "Incorrect Password");
            }
          }
          else {
            JOptionPane.showMessageDialog(null, "User does not exist!");
          }
        }
        catch(Exception ew) {
          JOptionPane.showMessageDialog(null, "No connection!");
        }
      }
    });
    loginBtn.setBounds(120, 350, 97, 25);
    contentPane.add(loginBtn);
    
    JButton registerBtn = new JButton("New User");
    //button style
    registerBtn.setBackground(Color.BLACK);
    registerBtn.setForeground(Color.WHITE);
    registerBtn.setFont(new Font("Arial", Font.BOLD, 12));


    registerBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        RegisterUser registerUser = new RegisterUser();
        registerUser.setVisible(true);
        dispose();
      }
    });
    registerBtn.setBounds(250, 350, 97, 25);
    contentPane.add(registerBtn);
  }
  
  
}
