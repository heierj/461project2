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

  public void phaseB() {
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
}
