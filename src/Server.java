import java.io.*;
import java.net.*;
import java.util.concurrent.ForkJoinPool;

/** 
 * This class will act as the entry point for clients. It will
 * receive the first packet and then spin off a thread to handle 
 * the session for that client.
 * 
 * @author Jordan Heier, Cameron Hardin, Will McNamara
 */
public class Server {
  private static ForkJoinPool fjPool = new ForkJoinPool();
  private static final int PORT_NUM = 12235;
  
	public static void main(String[] args) throws IOException {
	  DatagramSocket serverSocket = null;
	  
	  try { 
	    serverSocket = new DatagramSocket(PORT_NUM);
	    
	    // Loop endlessly, receiving the initial packets and then handing off
	    // responsibility to a new thread
		  while (true) {
		    DatagramPacket receivePacket = new DatagramPacket(new byte[64], 64);
		    serverSocket.receive(receivePacket);
		    fjPool.execute(new Session(receivePacket));
			}
		} catch (IOException e) {
		  // Attempt to close up the server socket
		  if (serverSocket != null) {
        serverSocket.close();
		  }
		  System.err.println(e.getMessage());
		  System.exit(-1);
		}
	}
}
