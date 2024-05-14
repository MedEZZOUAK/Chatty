package appServer;


import data.Data;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class appMain{

  public static final int PORT = 59003;
  
  /**
   * A HashSet which stores the names of all the users registered to use the app.
   */
  public static HashSet<String> names = new HashSet<String>();
  
  /**
   * A HashMap that stores the Socket of each user along with their username as a key value.
   */
  public static HashMap<String, Socket> clients = new HashMap<String, Socket>();
  
  /**
   * A HashMap that stores each users OutputStream along with their username as a key value.
   */
  public static HashMap<String, ObjectOutputStream> map = new HashMap<>();
  public static HashMap <String, String> key = new HashMap<>();


  private static class Handler extends Thread{
    
    private Socket socket;
    private String friend_;
    
    private ObjectInputStream in;
    
    private ObjectOutputStream out;
    
    private ObjectOutputStream friendOut;
    
    public Handler(Socket socket) {
      this.socket = socket;
    }
    
    public void run() {
      String name;
      while(true) {
        try {
          out = new ObjectOutputStream(socket.getOutputStream());
          in = new ObjectInputStream(socket.getInputStream());
          while(true) {
            out.writeObject("SUBMIT");
            name = (String) in.readObject();
            if (name == null){
              return;
            }
            clients.put(name, socket);
            map.put(name, out);
            while(true) {
              out.writeObject("CONNECTED");
              String read = (String) in.readObject();
              if(read.equals("SUCCESS")) {
                break;
              }
            }
            break;
          }
          
          while(true) {
            try {
              in = new ObjectInputStream(socket.getInputStream());
              Object inputMsg = in.readObject();
              if(inputMsg == null) {
                continue;
              }
              System.out.println("TYPE = " + ((DefaultListModel)inputMsg).elementAt(0));
              checkMsgType((DefaultListModel<Object>) inputMsg, name);
            }
            catch(Exception e) {
              //TODO
            }
          }
        }
        catch(Exception e) {
          //TODO
        }
      }
    }
    
    private void checkMsgType(DefaultListModel input, String sender) {
      switch((int) input.elementAt(0)) {
        case RequestType.SEND_MSG:
          System.out.println("Send DETECTED");
          sendMessage(input, sender);
          break;
        case RequestType.SEND_FILE:
          System.out.println("File detected");
          sendFile(input, sender);
          break;
        case RequestType.LOGOUT:
          Logout(input, sender);
          break;
        default:
          break;
      }
    }
    
    private void sendFile(DefaultListModel input, String sender) {
      String user = (String) input.elementAt(2);
      Data data = (Data) input.elementAt(1);
      String key =getFriendKey(sender);
      setFriend_((String)input.elementAt(4));
      String friendKey = getFriendKey(friend_);
      try {
        data.setFile(FileEncryption.decrypt(data.getFile() , key));
      }
      catch(Exception e) {
        //TODO
      }
      DefaultListModel<String> friendList = (DefaultListModel<String>) input.elementAt(3);
      for(int i = 0; i < friendList.size(); i++) {
        String friendName = friendList.elementAt(i);
        if (friendName.equals(sender)){
          continue;
        }
        friendOut = map.get(friendName);
        if(user.equals(sender) && friendOut != null) {
          try {
            DefaultListModel model = new DefaultListModel();
            model.addElement(RequestType.SEND_FILE);
            data.setFile(FileEncryption.encrypt(data.getFile(), friendKey));
            model.addElement(data);
            model.addElement(sender);
            //model.addElement(key);
            friendOut = new ObjectOutputStream((clients.get(friendName)).getOutputStream());
            friendOut.writeObject(model);
            friendOut.flush();
            System.out.println("Send file successful!");
          }
          catch (IOException e){
            //TODO
            System.out.println("FAILED TO SEND");
          }
        }
      }
    }
    
    private void sendMessage(DefaultListModel input, String sender) {
      String user = (String) input.elementAt(2);
      String message = (String) input.elementAt(1);
      String key = (String) input.elementAt(4);
      DefaultListModel<String> friendList = (DefaultListModel<String>) input.elementAt(3);
      for(int i = 0; i < friendList.size(); i++) {
        String friendName = friendList.elementAt(i);
        if (friendName.equals(sender)){
          continue;
        }
        friendOut = map.get(friendName);
        if(user.equals(sender) && friendOut != null) {
          try {
            //send the same data to the friend
            DefaultListModel model = new DefaultListModel();
            model.addElement(RequestType.SEND_MSG);
            model.addElement(message);
            model.addElement(sender);
            model.addElement(key);
            friendOut = new ObjectOutputStream((clients.get(friendName)).getOutputStream());
            friendOut.writeObject(model);
            friendOut.flush();
            System.out.println("Send message successful!");
            System.out.println("Message: " + message);
            System.out.println("To: " + friendName);
            System.out.println("From: " + sender);
          }
          catch (IOException e){
            //TODO
            System.out.println("FAILED TO SEND");
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
    
    private void Logout(DefaultListModel input, String sender) {
      System.out.println("Client\"" + sender +"\" just logged out!\r");
      Thread.currentThread().stop();
    }

    public void setFriend_(String friend_) {
      this.friend_ = friend_;
    }
  }

  public static void main(String[] args) {
    System.out.println("The server is running.....");
    ExecutorService threadPool = Executors.newFixedThreadPool(500);
    try(ServerSocket listener = new ServerSocket(PORT);){
      while(true) {
        new Handler (listener.accept()).start();
      }
    }
    catch(Exception e) {
      //TODO
    }
  }

  public static String getFriendKey(String name) {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "");
      Statement stmt = con.createStatement();
      String qry = "select * from login where Username="+" '"+name+"';";
      ResultSet rs = stmt.executeQuery(qry);
      if(rs.next()) {
        return rs.getString("PrivateKey");
      }
    }
    catch(Exception e) {
      //TODO
    }
    return null;
  }

  
}
