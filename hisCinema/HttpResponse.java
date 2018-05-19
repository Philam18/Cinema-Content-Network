import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class HttpResponse {
  public static final int OK = 200;
  public static final int NOT_FOUND = 404;
  public static final int ERROR = 500;
  public static String root = System.getProperty("user.dir");

  HttpRequest req;
  String response = "";
  String responseHeader = "";
  String responseBody = "";

  File f;

  public HttpResponse(HttpRequest request){
    req = request;
    if(req.filename.equals("/")){
      f = new File(root + "/index.html");
    }else{
      f = new File(root + req.filename);
    }
    try {
      FileInputStream fis = new FileInputStream(f);
      responseHeader = createResponseHeader(OK, f.length());
      int s;
      while ((s = fis.read()) != -1 ) {
        responseBody += (char) s;
      }
      fis.close();
      response = responseHeader + responseBody;
    }catch(FileNotFoundException e){
      System.out.println("------------------ ERROR ------------------");
      System.out.println("COULDNT FIND FILE: " + root + req.filename);
      System.out.println("(is your root variable set correctly?)");
      try{
        File file404 = new File(root + "/404.html");
        FileInputStream file404is = new FileInputStream(file404);
        responseHeader = createResponseHeader(NOT_FOUND, file404.length());
        int s;
        while ((s = file404is.read()) != -1 ) {
          responseBody += (char) s;
        }
        file404is.close();
        response = responseHeader + responseBody;
      }catch(FileNotFoundException e1){
        //Ignore
      }catch(Exception e2){
        //Ignore
      }
      System.out.println("-------------------------------------------");
    }catch(Exception e){
      System.out.println("------------------ ERROR ------------------");
      System.out.println("Something went wrong: " + e);
      try{
        File file500 = new File(root + "/500.html");
        FileInputStream file500is = new FileInputStream(file500);
        responseHeader = createResponseHeader(ERROR, file500.length());
        int s;
        while ((s = file500is.read()) != -1 ) {
          responseBody += (char) s;
        }
        file500is.close();
        response = responseHeader + responseBody;
      }catch(FileNotFoundException e1){
        //Ignore
      }catch(Exception e2){
        //Ignore
      }
      System.out.println("-------------------------------------------");
    }
  }

  private String createResponseHeader(int statusCode, long fileLength){
    String header = "";
    header += "HTTP/1.1 " + statusCode + " \r\n";
    header += "Server: Jed, Ron, and Phil HisCinema Web Server/1.0 \r\n";
    header += "Connection: close \r\n";
    header += "Content-Length: " + fileLength + "\r\n";
    header += "\r\n";
    return header;
  }

}
