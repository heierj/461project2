import java.nio.ByteBuffer;
import java.util.Random;

/**
 * This class will handle the management of a particular
 * client's session.
 * 
 * @author Jordan Heier, Cameron Hardin, Will McNamara
 */
public class Session {
	private int studentNum;
	private int secretA, secretB, secretC, secretD;
	private int portB, portC;
	
	private Session(int studentNum) {
		this.studentNum = studentNum;
		
		// Generate the secrets
		Random secrets = new Random();
		secretA = secrets.nextInt();
		secretB = secrets.nextInt();
		secretC = secrets.nextInt();
		secretD = secrets.nextInt();
	}
	
	/**
	 * This function takes a packet received in a1 to create a session.
	 * If the packet from a1 was not formed correctly null will be returned.
	 * Otherwise a session object will be returned with the correct student number
	 * and secrets set.
	 */
	public static Session createSession(byte[] a1Packet) {
		// Parse and check the packet header
		ByteBuffer packet = ByteBuffer.wrap(a1Packet);
		PacketHeader pHeader = new PacketHeader(packet);
		if(pHeader.getPSecret() != 0 || pHeader.getPayloadLen() != 12 || 
				pHeader.getStep() != 1) {
			return null;
		}
		
		// Parse and check the payload
		byte[] payload = new byte[11];
		packet.get(payload, 0, 11);
		String hello = new String(payload);
		if(!hello.equals("hello world") || packet.get() != 0) {
			return null;
		}
		
		// If the first packet is valid then create the session
		Session session = new Session(pHeader.getStudentNum());
		return session;
	}
}
