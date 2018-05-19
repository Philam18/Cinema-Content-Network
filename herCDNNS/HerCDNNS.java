import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class HerCDNNS{
  public static String HER_CDN_IP;    // Default 127.0.0.4
  public static int HER_CDN_NS_PORT;  // Default 40283

  public static ArrayList<ResourceRecord> records;
  public static DatagramSocket serverSocket;

  public static void main(String argv[]) throws Exception{
    // Set the IP/PORT constants
    initialize();

    // Create and store ResourceRecords
    records =  new ArrayList<ResourceRecord>();
    records.add(new ResourceRecord("herCDN.com",      "www.herCDN.com", "CN"));
    records.add(new ResourceRecord("www.herCDN.com",  HER_CDN_IP,       "A"));

    // Create RECEIVING socket
    serverSocket = new DatagramSocket(HER_CDN_NS_PORT);
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];

    // HisCinemaNS runs infinitely, listens for any incoming DNS queries
    System.out.println("Listening on PORT " + HER_CDN_NS_PORT + " for Requests...");
    while(true){
      // incoming packet arrives at the socket
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket);
      String requestURL = new String(receivePacket.getData());
      System.out.println("--------------------------------------------");
      System.out.println("Incoming Message: " + requestURL);

      // Prepare a response
      InetAddress IPAddress = receivePacket.getAddress();
      int port = receivePacket.getPort();

      //RESOLVE REQUEST
      String response = resolve(requestURL);
      System.out.println("--------------------------------------------");

      // Prepare the UDP packet to return to the client
      sendData = response.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

      // Send the Response
      serverSocket.send(sendPacket);
    }
  }

  private static String resolve(String url){
    // herCDNNS = herCDN.com -> www.herCDN.com -> IP for www.CDN.com
    System.out.println("Resolving: " + url);
    String current = url.trim();
    for(int i = 0; i < records.size(); i++){
      ResourceRecord record = records.get(i);
      if(current.equals(record.name)){
        current = record.value;
        // Translations always end in an A type
        if(record.type.equals("A")){
          System.out.println("Resolved: " + current);
          return current;
        }
        // Reset the counter
        i = i - i - 1;
      }
    }
    System.out.println("Could not resolve");
    return "";
  }

  private static void initialize(){
    // INITIALIZES THE FOLLWING CONSTANTS
    // HER_CDN_IP;       // Default 127.0.0.1
    // HER_CDN_NS_PORT;  // Default 40283
    // Set the IP/PORT constants
    Scanner scanner = new Scanner(System.in);
    String line;
    // HER_CDN_IP --------------------------------------------------------------------------
    System.out.println("Enter IP of www.herCDN.com Web Server (or press 'Enter' for 127.0.0.1)");
    line = scanner.nextLine();
    if(line.isEmpty()){
      System.out.println("Using 127.0.0.1");
      line = "127.0.0.1";
    }
    while(!checkIP(line)){
      System.out.println("[Error] Invalid IP, try again!");
      System.out.println("Enter IP of www.herCDN.com Web Server (or press 'Enter' for 127.0.0.1)");
      line = scanner.nextLine();
    }
    HER_CDN_IP = line;
    // HER_CDN_NS_PORT --------------------------------------------------------------------------
    System.out.println("Enter PORT of ns.herCDN.com Name Server (or press 'Enter' for 40283)");
    line = scanner.nextLine();
    if(line.isEmpty()){
      System.out.println("Using 40283");
      line = "40283";
    }
    while(!checkPORT(line)){
      System.out.println("[Error] Invalid PORT, try again!");
      System.out.println("Enter PORT of ns.herCDN.com Name Server (or press 'Enter' for 40283)");
      line = scanner.nextLine();
    }
    HER_CDN_NS_PORT = Integer.parseInt(line);
    // --------------------------------------------------------------------------
    System.out.println("www.herCDN.com SET: " + HER_CDN_IP);
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
