import java.nio.ByteBuffer;

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
		//Random secrets = new Random();
		
	}
	
	/**
	 * This function takes a packet received in a1 to create a session.
	 * If the packet from a1 was not formed correctly null will be returned.
	 * Otherwise a session object will be returned with the correct student number
	 * and secrets set.
	 * @param a1Packet
	 * @return
	 */
	public static Session createSession(ByteBuffer a1Packet) {
		// Parse and check the packet header
		PacketHeader packet = new PacketHeader(a1Packet);
		if(packet.getPSecret() != 0 || packet.getPayloadLen() != 12 || 
				packet.getStep() != 1) {
			return null;
		}
		
		// Parse and check the payload
/*		byte[] payload = new byte[12];
		a1Packet.get(payload, 0, 11);
		String hello = new String(payload);
		if(!hello.equals("Hello World")) {
			return null;
		}*/
		
		// If the first packet is valid then create the session
		Session session = new Session(packet.getStudentNum());
		return session;
	}



	
}
