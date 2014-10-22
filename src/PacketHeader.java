import java.nio.ByteBuffer;


public class PacketHeader {
	private int payloadLen;
	private int pSecret;
	private short step;
	private short studentNum;
	
	public PacketHeader(ByteBuffer packet) {
		payloadLen = packet.getInt();
		pSecret = packet.getInt();
		step = packet.getShort();
		studentNum = packet.getShort();
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

	public boolean checkHeader(int payloadLen, int pSecret) {
		return getPayloadLen() == payloadLen && getPSecret() == pSecret && getStep() == 1;
	}
    
   
}
