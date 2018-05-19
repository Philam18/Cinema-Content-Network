import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

class HerCDN{
  public static int HER_CDN_PORT; // Default 40284
  ServerSocket serverSocket;

  public static void main(String args[])throws Exception{
    // Set the IP/PORT constants
    initialize();
    // Start the server
    new HerCDN().runServer();
  }

  public void runServer() throws Exception{
    //Create a welcoming socket at our specific port
    System.out.println("Listening on PORT " + HER_CDN_PORT + " for Requests...");
    serverSocket = new ServerSocket(HER_CDN_PORT);
    acceptRequests();
  }

  private void acceptRequests() throws Exception {
    while(true){
      //wait, on welcoming socket for contanct by client
      Socket s = serverSocket.accept();
      ConnectionHandler ch = new ConnectionHandler(s);
      ch.start();
    }
  }

  private static void initialize(){
    // INITIALIZES THE FOLLWING CONSTANTS
    // HER_CDN_PORT; // Default 40284
    // Set the IP/PORT constants
    Scanner scanner = new Scanner(System.in);
    String line;
    // HER_CDN_PORT --------------------------------------------------------------------------
    System.out.println("Enter PORT of www.herCDN.com Web Server (or press 'Enter' for 40284)");
    line = scanner.nextLine();
    if(line.isEmpty()){
      System.out.println("Using 40284");
      line = "40284";
    }
    while(!checkPORT(line)){
      System.out.println("[Error] Invalid PORT, try again!");
      System.out.println("Enter PORT of www.herCDN.com Web Server (or press 'Enter' for 40284)");
      line = scanner.nextLine();
    }
    HER_CDN_PORT = Integer.parseInt(line);
    // --------------------------------------------------------------------------
    return;
  }

  private static boolean checkIP(String input){
    Pattern p = Pattern.compile("([0-9]+[.]){3}[0-9]{1}");
    Matcher m = p.matcher(input);
    if(m.find()){
      return true;
    }
    return false;
  }

  private static boolean checkPORT(String input){
    Pattern p = Pattern.compile("[0-9]+");
    Matcher m = p.matcher(input);
    if(m.find()){
      return true;
    }
    return false;
  }

}
