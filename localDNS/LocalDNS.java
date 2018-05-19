import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class LocalDNS{
  public static String  HIS_CINEMA_NS_IP;    // Default 127.0.0.1
  public static int     HIS_CINEMA_NS_PORT;  // Default 40282
  public static String  HER_CDN_NS_IP;       // Default 127.0.0.1
  public static int     HER_CDN_NS_PORT;     // Default 40283
  public static int     LOCAL_DNS_PORT;      // Default 40281

  public static ArrayList<ResourceRecord> records;
  public static DatagramSocket serverSocket;

  public static void main(String argv[]) throws Exception{
    // Set the IP/PORT constants
    initialize();

    // Create and store ResourceRecords
    records =  new ArrayList<ResourceRecord>();
    records.add(new ResourceRecord("herCDN.com",      "ns.herCDN.com",     "NS"));
    records.add(new ResourceRecord("ns.herCDN.com",    HER_CDN_NS_IP,      "A"));
    records.add(new ResourceRecord("hisCinema.com",   "ns.hiscinema.com",  "NS"));
    records.add(new ResourceRecord("ns.hiscinema.com", HIS_CINEMA_NS_IP,   "A"));

    // Create RECEIVING socket
    serverSocket = new DatagramSocket(LOCAL_DNS_PORT);
    byte[] receiveData = new byte[1024];
    byte[] sendData = new byte[1024];

    // LocalDNS runs infinitely, listens for any incoming DNS queries
    System.out.println("Listening on PORT " + LOCAL_DNS_PORT + " for Requests...");
    while(true){
      // incoming packet arrives at the socket
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      serverSocket.receive(receivePacket);
      String requestURL = new String(receivePacket.getData());
			System.out.println("--------------------------------------------");
      System.out.println("Incoming Message: " + requestURL);

      // Prepare a response
      InetAddress returnIPAddress = receivePacket.getAddress();
      int returnPort = receivePacket.getPort();

      // Take requestURL from UDP message, and resolve it
      String response = resolve(requestURL);
			System.out.println("--------------------------------------------");

      // Prepare the UDP packet to return to the client
      sendData = response.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, returnIPAddress, returnPort);

      // Send the Response
      serverSocket.send(sendPacket);
    }
  }

  public static String resolve(String url) throws Exception{
    String hisCinemaNSurl 	= url.trim();
		String hisCinemaNS 			= "ns." + hisCinemaNSurl.substring(hisCinemaNSurl.indexOf(".")+1);
		String hisCinemaNSip 		= getNSip(hisCinemaNS);
		int 	 hisCinemaNSport  = HIS_CINEMA_NS_PORT;
		String hisCinemaNSres 	= requestToNameServer(hisCinemaNSurl, hisCinemaNS, hisCinemaNSip, hisCinemaNSport);
		String herCDNNSurl 			= hisCinemaNSres;
		String herCDNNS 				= "ns." + hisCinemaNSres;
		String herCDNNSip 		  = getNSip(herCDNNS);
		int 	 herCDNNSport 		= HER_CDN_NS_PORT;
		String herCDNNSres 			= requestToNameServer(herCDNNSurl, herCDNNS, herCDNNSip, herCDNNSport);
    return herCDNNSres;
  }

  public static String requestToNameServer(String url, String ns, String nsIP, int nsPort) throws Exception{
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];
    sendData = url.getBytes();
    InetAddress NSIPAddress = InetAddress.getByName(nsIP);
    DatagramPacket request = new DatagramPacket(sendData, sendData.length, NSIPAddress, nsPort);
    System.out.println("Sending request to " + ns + " @ "+ nsIP + ":" + nsPort + " = " + url);
    serverSocket.send(request);
		DatagramPacket response = new DatagramPacket(receiveData, receiveData.length);
    serverSocket.receive(response);
		String nsResponse = new String(response.getData());
		System.out.println("Response received from " + ns + " @ " + nsIP + ":" + nsPort + " = " + nsResponse);
    return nsResponse;
  }

	public static String getNSip(String NSurl){
    String current = NSurl.trim();
		for(int i = 0; i < records.size(); i++){
			ResourceRecord record = records.get(i);
			if(current.equals(record.name)){

				current = record.value;
				if(record.type.equals("A")){
					return current;
				}
				i = i - i - 1;
			}
		}
		return "";
	}

  private static void initialize(){
    // INITIALIZES THE FOLLWING CONSTANTS
    // LOCAL_DNS_PORT;      // Default 40281
    // HIS_CINEMA_NS_IP;    // Default 127.0.0.1
    // HIS_CINEMA_NS_PORT;  // Default 40282
    // HER_CDN_NS_IP;       // Default 127.0.0.1
    // HER_CDN_NS_PORT;     // Default 40283
    // Set the IP/PORT constants
    Scanner scanner = new Scanner(System.in);
    String line;
    // LOCAL_DNS_PORT --------------------------------------------------------------------------
    System.out.println("Enter PORT of Local DNS (or press 'Enter' for 40281)");
    line = scanner.nextLine();
    if(line.isEmpty()){
      System.out.println("Using 40281");
      line = "40281";
    }
    while(!checkPORT(line)){
      System.out.println("[Error] Invalid PORT, try again!");
      System.out.println("Enter PORT of Local DNS (or press 'Enter' for 40281)");
      line = scanner.nextLine();
    }
    LOCAL_DNS_PORT = Integer.parseInt(line);
    // HIS_CINEMA_NS_IP --------------------------------------------------------------------------
    System.out.println("Enter IP of ns.hisCinema.com Name Server (or press 'Enter' for 127.0.0.1)");
    line = scanner.nextLine();
    if(line.isEmpty()){
      System.out.println("Using 127.0.0.1");
      line = "127.0.0.1";
    }
    while(!checkIP(line)){
      System.out.println("[Error] Invalid IP, try again!");
      System.out.println("Enter IP of ns.hisCinema.com Name Server (or press 'Enter' for 127.0.0.1)");
      line = scanner.nextLine();
    }
    HIS_CINEMA_NS_IP = line;
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
    // HER_CDN_NS_IP --------------------------------------------------------------------------
    System.out.println("Enter IP of ns.herCDN.com Name Server (or press 'Enter' for 127.0.0.1)");
    line = scanner.nextLine();
    if(line.isEmpty()){
      System.out.println("Using 127.0.0.1");
      line = "127.0.0.1";
    }
    while(!checkPORT(line)){
      System.out.println("[Error] Invalid IP, try again!");
      System.out.println("Enter IP of ns.herCDN.com Name Server (or press 'Enter' for 127.0.0.1)");
      line = scanner.nextLine();
    }
    HER_CDN_NS_IP = line;
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
    System.out.println("ns.hisCinema.com SET: " + HIS_CINEMA_NS_IP + ":" + HIS_CINEMA_NS_PORT);
    System.out.println("ns.herCDN.com    SET: " + HER_CDN_NS_IP +    ":" + HER_CDN_NS_PORT);
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
