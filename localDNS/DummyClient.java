import java.io.*;
import java.net.*;

public class DummyClient{
  public static final String LOCAL_DNS_IP = "192.168.1.104";
  public static final int LOCAL_DNS_PORT = 40280;

  public static void main(String argv[]) throws Exception{

    // Create a request to send to server
    DatagramSocket clientSocket = new DatagramSocket();

    InetAddress IPAddress = InetAddress.getByName(LOCAL_DNS_IP);
    byte[] sendData = new byte[1024];
    byte[] receiveData = new byte[1024];

    // The request should be a DNS query
    String requestURL = "video.hiscinema.com";
    sendData = requestURL.getBytes();
    // Send the DNS Query to the server, and await response
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, LOCAL_DNS_PORT);
    System.out.println("TO SERVER: " + requestURL);
    clientSocket.send(sendPacket);

    // HANDLE THE RESPONSE FROM THE SERVER
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.receive(receivePacket);
    String serverResponse = new String(receivePacket.getData());
    System.out.println("FROM SERVER: " + serverResponse);
    clientSocket.close();


  }
}
