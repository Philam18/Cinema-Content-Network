import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class ConnectionHandler extends Thread{

  Socket s;
  DataOutputStream dataOutput;
  BufferedReader dataInput;

  public ConnectionHandler(Socket s) throws Exception {
    this.s = s;
    dataInput = new BufferedReader(new InputStreamReader(s.getInputStream()));
    dataOutput = new DataOutputStream(s.getOutputStream());
  }

  @Override
  public void run(){

    try{
      String requestString = "";

      // Incoming data detected, build the request char by char
      while(dataInput.ready() || requestString.length() == 0){
        requestString += (char) dataInput.read();
      }

      // Request has been completed, print out
      System.out.println("Incoming Data....");
      System.out.println(requestString);

      // If the request is an HTTP request....
      if(requestString.contains("HTTP")){
        System.out.println("------------- HTTP REQUEST -------------");
        System.out.println(requestString);
        System.out.println("----------------------------------------");
        HttpRequest request = new HttpRequest(requestString);
        // Extract the requested file from the http request
        String filename = request.filename.substring(1);
        DataInputStream dataInput = new DataInputStream(s.getInputStream());
        DataOutputStream dataOutput = new DataOutputStream(s.getOutputStream());

        try{
          // Attempt to get file
          File file = new File(System.getProperty("user.dir") + "/" + filename);
          // Open a new file stream to send the file to the Write Buffer
          FileInputStream fileInputStream = new FileInputStream(file);
          long filesize = (int)file.length();

          // Packet Data size is 1024 bytes
          byte[] dataPacket = new byte[1024];
          dataOutput.writeUTF(filename);
          dataOutput.flush();
          dataOutput.writeUTF(Long.toString(filesize));
          dataOutput.flush();
          System.out.println("Sending File: " + filename + " " + (filesize/(1024*1024)) + "MB");

          int read;
          while((read = fileInputStream.read(dataPacket)) != -1){
            dataOutput.write(dataPacket, 0, read);
            dataOutput.flush();
          }
          fileInputStream.close();
          System.out.println("Transfer Complete");
        }catch(Exception e){
          System.out.println("ERROR: " + e);
        }
        dataInput.close();
        dataOutput.close();
        s.close();
      }

    }catch(Exception e){
      e.printStackTrace();
    }
  }

}
