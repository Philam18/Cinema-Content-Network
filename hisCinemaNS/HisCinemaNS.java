import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class HisCinemaNS{
  public static int HIS_CINEMA_NS_PORT; // Default 40282

  public static ArrayList<ResourceRecord> records;
  public static DatagramSocket serverSocket;

  public static void main(String argv[]) throws Exception{
    // Set the IP/PORT constants
    initialize();

    // Create and store ResourceRecords
    records =  new ArrayList<ResourceRecord>();
    records.add(new ResourceRecord("video.hiscinema.com", "herCDN.com",     "R"));
    
    // Create RECEIVING socket
    serverSocket = new DatagramSocket(HIS_CINEMA_NS_PORT);
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];

    // HisCinemaNS runs infinitely, listens for any incoming DNS queries
    System.out.println("Listening on PORT " + HIS_CINEMA_NS_PORT + " for Requests...");
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

      // RESOLVE REQUEST
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
    System.out.println("Resolving: " + url);
    ResourceRecord record = records.get(0);
    // hisCinemaNS takes 'video.hiscinema.com' and redirects to 'herCDN.com'
    if(url.trim().equals(record.name)){
      String redirect = record.value;
      System.out.println("Resolved:  " + redirect);
      return redirect;
    }
    System.out.println("Could not resolve - No record for: " + url);
    return "";
  }

  private static void initialize(){
    // INITIALIZES THE FOLLWING CONSTANTS
    // HIS_CINEMA_NS_PORT;  // Default 40282
    // Set the IP/PORT constants
    Scanner scanner = new Scanner(System.in);
    String line;
    // HIS_CINEMA_NS_PORT --------------------------------------------------------------------------
    System.out.println("Enter PORT of ns.hisCinema.com Name Server (or press 'Enter' for 40282)");
    line = scanner.nextLine();
    if(line.isEmpty()){
      System.out.println("Using 40282");
      line = "40282";
    }
    while(!checkPORT(line)){
      System.out.println("[Error] Invalid PORT, try again!");
      System.out.println("Enter PORT of ns.hisCinema.com Name Server (or press 'Enter' for 40282)");
      line = scanner.nextLine();
    }
    HIS_CINEMA_NS_PORT = Integer.parseInt(line);
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
