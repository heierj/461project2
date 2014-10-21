import java.nio.ByteBuffer;


public class PacketHeader {
	private int payloadLen;
	private int pSecret;
	private short step;
	private short studentNum;
	
	public PacketHeader(ByteBuffer packet) {
		System.out.println("Packet size: " + packet.array().length);
		payloadLen = packet.getInt(0);
		pSecret = packet.getInt(4);
		step = packet.getShort(8);
		studentNum = packet.getShort(10);
	}
	
    public int getPayloadLen() {
    	return payloadLen;
    }
    
    public int getPSecret() {
    	return pSecret;
    }
    
    public short getStep() {
    	return step;
    }
    
    public short getStudentNum() {
    	return studentNum;
    }
   
}
