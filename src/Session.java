import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.RecursiveAction;

/**
 * This class will handle the management of a particular
 * client's session.
 * 
 * @author Jordan Heier, Cameron Hardin, Will McNamara
 */
public class Session extends RecursiveAction {
  private static final short SERVER_STUDENT_NUM = 576;
  
  private DatagramPacket initialPacket;
  private static Random rand = new Random();
  
	private int studentNum;
	private int secretA, secretB, secretC, secretD;
	private int udpPort, tcpPort;
	private int num, len, num2, len2;
	
	Session(DatagramPacket packet) {
	  initialPacket = packet;
	}
	
  @Override
  protected void compute() {
    phaseA();
    phaseB();
    phaseC();
    phaseD();
  }
	
	private void phaseA() {
	  ByteBuffer receiveData = ByteBuffer.wrap(initialPacket.getData());
	  
	  PacketHeader header = getHeader(receiveData);
	  
	  if (header.getPayloadLen() != 12) {
      System.err.println("Phase A incorrect payload length");
      System.exit(-1);
	  }
	  
	  if (header.getPSecret() != 0) {
      System.err.println("Phase A incorrect secret");
      System.exit(-1);
	  }
	  
	   if (header.getStep() != 1) {
	      System.err.println("Phase A incorrect step number");
	      System.exit(-1);
	   }
	   
	   byte[] payload = new byte[12];
	   receiveData.get(payload, 0, header.getPayloadLen());
	   
	   if (!"hello world".equals(new String(payload, 0, header.getPayloadLen() - 1))) {
       System.err.println("Phase A payload had incorrect data");
       System.exit(-1);
	   }
	   
	   num = rand.nextInt(50);
	   len = rand.nextInt(100);
	   
	   // Random number between [10000, 60000) which are reasonable port values
	   udpPort = rand.nextInt(50000) + 10000; 
	   secretA = rand.nextInt();
	   
	   ByteBuffer response = ByteBuffer.allocate(28);
	   
	   // Attach header
	   response.putInt(16);
	   response.putInt(0);
	   response.putShort((short) 2);
	   response.putShort(SERVER_STUDENT_NUM);
	   
	   // Attach payload
	   response.putInt(num);
	   response.putInt(len);
	   response.putInt(udpPort);
	   response.putInt(secretA);
	   
	   DatagramPacket responsePacket = new DatagramPacket(response.array(), 28, 
	       initialPacket.getAddress(), initialPacket.getPort());
	   DatagramSocket sock;
	   
	   try {
	     sock = new DatagramSocket();
	     sock.send(responsePacket);
	   } catch (Exception e) {
	     System.err.println("Error sending response in phase A");
	     System.exit(-1);
	   }
	}
	
  private void phaseB() {
    //  check that payload length = len + 4 (and byte aligned)

    //  boolean[] packetsAck = new boolean[num]
    //
    //  check packetId
    //  for (int i = 0; i < num; i++) {
    //    DatagramPacket receivePacket = new DatagramPacket(new byte[BUF_SIZE], BUF_SIZE)
    //    socket.receive(receivePacket)
    //
    //    ByteBuffer request = ByteBuffer.wrap(receivePacket.getData());
    //    
    //    verifyHeader(first 12 bytes of request)
    //    int packetId = first 4 bytes of payload
    //    check packetId = i
    //    check that payload length = len + 4 (also byte aligned)
    //
    //    for (int j = 16; j < 16 + len; j++) {
    //      if (request.get(j) != 0) {
    //        error
    //      }
    //    }
    //  
    //    Random r = new Random();
    //    if (r.nextInt(2)) {
    //      ByteBuffer response = new ByteBuffer()
    //      createHeader(response)?
    //      response.put(12, i);
    //      
    //      byte[] responseData = response.array();
    //      DatagramPacket ackPacket = new DatagramPacket(responseData, 
    //          responseData.length, receivePacket.getAddress(), recievePacket.getPort())
    //
    //      socket.send(ackPacket);
    //      packetsAck[i] = true;
    //    } else {
    //      i--;
    //    }
    //  }
    //
    //  for (int i = 0; i < num; i++) {
    //    if (!packetsAck[i]) {
    //      error
    //    }
    //  }
    //
    //  send final UDP packet with random TCP port number and secretB
  }
  
  private void phaseC() {
    
  }
  
  private void phaseD() {
    
  }
  
  /**
   * Verifies the header is properly formatted. Returns a PacketHeader with the
   * header information encapsulated if the data correctly formatted and exits
   * with an error otherwise
   */
  private PacketHeader getHeader(ByteBuffer packetData) {
    if (packetData.array().length < 12) {
      System.err.println("Malformed packet header");
      System.exit(-1);
    }
    
    PacketHeader header = new PacketHeader(packetData);
    
    if (header.getPayloadLen() % 4 != 0) {
      System.err.println("Payload not aligned to four bytes");
      System.exit(-1);
    }
    
    if (studentNum == 0) {
      studentNum = header.getStudentNum();
    } else {
      if (studentNum != header.getStudentNum()) {
        System.err.println("Student number changed");
        System.exit(-1);
      }
    }
    
    return header;
  }
  
  /**
   * This function takes a packet received in a1 to create a session.
   * If the packet from a1 was not formed correctly null will be returned.
   * Otherwise a session object will be returned with the correct student number
   * and secrets set.
   *
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
  }*/
}
