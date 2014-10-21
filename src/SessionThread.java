import java.nio.ByteBuffer;
import java.net.*;
import java.io.*;

public class SessionThread extends Thread {
    private Socket socket = null;

    public SessionThread(Socket socket) {
        super("SessionThread");
        this.socket = socket;
    }
    
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
	    new InputStreamReader(socket.getInputStream()));
	    char[] responsePacket = new char[255];
	    int bytesRead = 0;
            while(bytesRead < responsePacket.length) {
	        bytesRead += in.read(responsePacket);
	    }
	    socket.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
