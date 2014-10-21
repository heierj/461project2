import java.nio.ByteBuffer;

/** 
 * This class will act as the entry point for clients. It will
 * receive the first packet and then spin off a thread to handle 
 * the session for that client.
 * 
 * @author Jordan Heier, Cameron Hardin, Will McNamara
 */
public class Server {
	
	public static void main(String[] args) {
		ByteBuffer packet = ByteBuffer.allocate(24);
		packet.putInt(12);
		packet.putInt(0);
		packet.putShort((short) 1);
		packet.putShort((short) 980);
		packet.put("hello world".getBytes());
		
		Session session = Session.createSession(packet);
		if(session == null) {
			System.out.println("Verification failed");
		} else {
			System.out.println("Verification Succeeded");
		}
	}
}
