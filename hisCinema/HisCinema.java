import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class HisCinema {
  public static int HIS_CINEMA_PORT; // Default 40280
  ServerSocket serverSocket;

  public static void main(String[] args) throws Exception{
    // Set the IP/PORT constants
    initialize();
    // Start the server
    new HisCinema().runServer();
  }

  public void runServer() throws Exception{
    //Create a welcoming socket at our specific port
    System.out.println("Listening on PORT " + HIS_CINEMA_PORT + " for Requests...");
    serverSocket = new ServerSocket(HIS_CINEMA_PORT);
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
    // HIS_CINEMA_PORT; // Default 40280
    // Set the IP/PORT constants
    Scanner scanner = new Scanner(System.in);
    String line;
    // HIS_CINEMA_PORT --------------------------------------------------------------------------
    System.out.println("Enter PORT of www.hisCinema.com Web Server (or press 'Enter' for 40280)");
    line = scanner.nextLine();
    if(line.isEmpty()){
      System.out.println("Using 40280");
      line = "40280";
    }
    while(!checkPORT(line)){
      System.out.println("[Error] Invalid PORT, try again!");
      System.out.println("Enter PORT of www.hisCinema.com Web Server (or press 'Enter' for 40280)");
      line = scanner.nextLine();
    }
    HIS_CINEMA_PORT = Integer.parseInt(line);
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
