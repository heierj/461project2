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
	     
	    socket.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
