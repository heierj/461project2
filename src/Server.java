import java.nio.ByteBuffer;

/** 
 * This class will act as the entry point for clients. It will
 * receive the first packet and then spin off a thread to handle 
 * the session for that client.
 * 
 * @author Jordan Heier, Cameron Hardin, Will McNamara
 */
import java.net.*;
import java.io.*;

public class Server {

	public static void main(String[] args) throws IOException {
		int portNumber = 12235;
		try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
		    while (true) {
			    Socket s = serverSocket.accept();
			    new SessionThread(s).start();
			}
		} catch (IOException e) {
		    System.err.println("Could not listen on port " + portNumber);
		    System.exit(-1);
                }
       }
}
