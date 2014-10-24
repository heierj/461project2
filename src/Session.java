import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.RecursiveAction;

/**
 * This class will handle the management of a particular client's session.
 * 
 * @author Jordan Heier, Cameron Hardin, Will McNamara
 */
@SuppressWarnings("serial")
public class Session extends RecursiveAction {
	private static final int HEADER_SIZE = 12;

	private DatagramPacket initialPacket;
	private static Random rand = new Random();

	private short studentNum;
	private int secretA, secretB, secretC, secretD;
	private int udpPort, tcpPort;
	private int num, len, num2, len2;
	private byte c;

	Session(DatagramPacket packet) {
		initialPacket = packet;
	}

	/**
	 * Performs all stages for this client
	 */
	@Override
	protected void compute() {
		DatagramSocket bSocket = phaseA();
		if(bSocket == null) {
			return;
		}
		ServerSocket cSocket = phaseB(bSocket);
		if(cSocket == null) {
			return;
		}
		Socket dSocket = phaseC(cSocket);
		if(dSocket == null) {
			return;
		}
		phaseD(dSocket);
	}

	private DatagramSocket phaseA() {
		ByteBuffer receiveData = ByteBuffer.wrap(initialPacket.getData());

		PacketHeader header = getHeader(receiveData);
		if(header == null) {
			return null;
		}

		if(!header.checkHeader(12, 0)) {
			System.err.println("Incorrect packet header for phase A");
			return null;
		}
		
		byte[] payload = new byte[12];
		receiveData.get(payload, 0, header.getPayloadLen());
		String hello = new String(payload, 0, header.getPayloadLen());
		if (!"hello world\0".equals(hello)) {
			System.err.println("Phase A payload had incorrect payload");
			return null;
		}

		num = rand.nextInt(50);
		len = rand.nextInt(100);

		DatagramSocket bSocket;
		while(true) {
			try {
				// Random number between [10000, 60000) which are reasonable port values
				udpPort = rand.nextInt(50000) + 10000;
				bSocket = new DatagramSocket(udpPort);
				break;
			} catch (SocketException e1) {
				// Random port was not available try again
			}
		}
		secretA = rand.nextInt();

		ByteBuffer response = startPacket(16, 0, (short) 2);

		// Attach payload
		response.putInt(num);
		response.putInt(len);
		response.putInt(udpPort);
		response.putInt(secretA);
		
		
		byte[] packet = response.array();
		DatagramPacket responsePacket = new DatagramPacket(packet,
				packet.length, initialPacket.getAddress(), initialPacket.getPort());
		DatagramSocket sock;

		try {
			sock = new DatagramSocket();
			sock.send(responsePacket);
			sock.close();
		} catch (Exception e) {
			System.err.println("Error sending response in phase A");
			return null;
		}

		
		return bSocket;
	}

	private ServerSocket phaseB(DatagramSocket bSocket) {

		// Start listening for num packets
		for(int i = 0; i < num; i++) {
			DatagramPacket packet = new DatagramPacket(new byte[128], 128);
			try {
				bSocket.receive(packet);
			} catch (IOException e) {
				System.err.println("Failed to receive packet B1");
				return null;
			}
			
			// Check the packet header
			ByteBuffer checkPacket = ByteBuffer.wrap(packet.getData());
			PacketHeader header = getHeader(checkPacket);
			if(header == null) {
				return null;
			}
			if(!header.checkHeader(len + 4, secretA)) {
				System.err.println("Incorrect packet header for phase B");
				return null;
			}		
			
			// Check packet ID and decide whether to send ACK
			int packetId = checkPacket.getInt();
			if(i != packetId) {
				// Wrong packet in sequence
				return null;
			} else if(rand.nextBoolean()) {
				// Send the ACK
				ByteBuffer ack = startPacket(4, secretA, (short) 1);
				ack.putInt(i);
				byte[] ackPack = ack.array();
				DatagramPacket responsePacket = new DatagramPacket(ackPack,
						ackPack.length, packet.getAddress(), packet.getPort());
				DatagramSocket sock;

				try {
					sock = new DatagramSocket();
					sock.send(responsePacket);
					sock.close();
				} catch (Exception e) {
					System.err.println("Error sending response in phase B");
					return null;
				}
			} else {
				// Don't send the ACK
				i--;
			}
		}

		bSocket.close();
		
		// Set up the tcp socket for stage c
		ServerSocket cSocket;
		while(true) {
			try {
				// Random number between [10000, 60000) which are reasonable port values
				tcpPort = rand.nextInt(50000) + 10000;
				cSocket = new ServerSocket(tcpPort);
				cSocket.setSoTimeout(3000);
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		secretB = rand.nextInt();

		// Create the response for stage B2
		ByteBuffer response = startPacket(8, secretA, (short) 2);
		response.putInt(tcpPort);
		response.putInt(secretB);
		byte[] packet = response.array();
		DatagramPacket responsePacket = new DatagramPacket(packet,
				packet.length, initialPacket.getAddress(), initialPacket.getPort());
		
		// Send B2 response
		DatagramSocket sock;
		try {
			sock = new DatagramSocket();
			sock.send(responsePacket);
			sock.close();
		} catch (Exception e) {
			System.err.println("Error sending response in phase A");
			return null;
		}
		
		return cSocket;
	}

	/** 
	 * Waits for a connection on the socket. If it receives one before
	 * timing out it will send the response
	 * @param cSocket the socket to wait for connection on
	 */
	private Socket phaseC(ServerSocket cSocket) {
		
		// Wait for connection 
		Socket connectionSocket;
		DataOutputStream out;
		try {
			connectionSocket = cSocket.accept();
			out = new DataOutputStream(connectionSocket.getOutputStream());
		} catch(SocketTimeoutException e) {
			System.err.println("Socket has been waiting 3 seconds, timing out");
			return null;
		} catch (IOException e) {
			return null;
		}
		
		// Create response packet
		ByteBuffer response = startPacket(13, secretB, (short) 2);
		num2 = rand.nextInt(50);
		len2 = rand.nextInt(100);
		secretC = rand.nextInt();
		byte c = ((rand.nextInt(26) + 'a') + "").getBytes()[0];
		this.c = c;
		response.putInt(num2);
		response.putInt(len2);
		response.putInt(secretC);
		response.put(c);
		
		// Send response packet
		try {
			out.write(response.array());
		} catch (IOException e) {
			System.err.println("Failed to send stage C response");
			return null;
		}
		
		return connectionSocket;
	}

	private void phaseD(Socket dSocket) {
		// Wait for connection 
		InputStream in;
		DataOutputStream out;
		try {
			in = dSocket.getInputStream();
			out = new DataOutputStream(dSocket.getOutputStream());
		} catch (IOException e) {
			System.err.println("Failure creating input stream");
			return;
		}
		
		// Setup the response buffer
		int i = 0;
		for(i = 0; i < num2; i++) {
			// Make sure packet will be 4-byte aligned
			int responseSize = HEADER_SIZE + len2; 	
			if(responseSize % 4 != 0) {
				responseSize += 4 - (responseSize % 4);
			}
			byte[] responsePacket = new byte[responseSize];
			int bytesRead = 0;
			while(bytesRead < responsePacket.length) {
				try {
					// Try to read in the server response
					bytesRead += in.read(responsePacket);
				} catch (IOException e) {
					System.err.println("Failure reading server response");
					return;
				}
			}
			if(bytesRead != responseSize) {
				System.err.println("Incorrect bytes sent for phase D.");
				return;
			}

			ByteBuffer serverResponse = ByteBuffer.wrap(responsePacket, 0, bytesRead);

			// Check the packet header
			ByteBuffer checkPacket = ByteBuffer.wrap(responsePacket);
			PacketHeader header = getHeader(checkPacket);
			if(header == null) {
				System.err.println("Incorrect packet header for phase D");
				return;
			}
			if(!header.checkHeader(len2, secretC)) {
				System.err.println("Incorrect packet header for phase D");
				return;
			}		
			for( int j = HEADER_SIZE; j < HEADER_SIZE + len2; j++) {
				if(c != serverResponse.get(j)) {
					System.err.println("Incorrect character for phase D. " +  serverResponse.get(j) + " instead of " + c);
					return;
				}
			}
	        }	
		if(i != num2) {
			System.err.println("Incorrect num2 for phaseD " + i + "instead of " + num2);
			return;
                }

		// Create response packet
		secretD = rand.nextInt();
		ByteBuffer response = startPacket(4, secretC, (short) 2);
		response.putInt(secretD);

		
		// Send response packet
		try {
			out.write(response.array());
			dSocket.close();
		} catch (IOException e) {
			System.err.println("Failed to send stage C response");
			return;
		}
        }
	
	/**
	 * Verifies the header is properly formatted. Returns a PacketHeader with
	 * the header information encapsulated if the data correctly formatted and
	 * exits with an error otherwise
	 */
	private PacketHeader getHeader(ByteBuffer packetData) {
		if (packetData.array().length < HEADER_SIZE) {
			System.err.println("Malformed packet header");
			return null;
		}

		PacketHeader header = new PacketHeader(packetData);

		// Make sure packet is padded
		if (packetData.array().length % 4 != 0) {
			System.err.println("Payload not aligned to four bytes");
			return null;
		}

		if (studentNum == 0) {
			studentNum = header.getStudentNum();
		} else if (studentNum != header.getStudentNum()) {
			System.err.println("Student number changed");
			return null;
		}

		return header;
	}
	
	/**
	 * Creates a new packet that is padded and has the header filled in
	 * @param payloadLen length of packet before padding
	 * @param pSecret secret from previous stage
	 * @param step 
	 */
	private ByteBuffer startPacket(int payloadLen, int pSecret, short step) {
		int paddedLen = payloadLen + HEADER_SIZE;
		
		
		// Make sure packet will be 4-byte aligned
		if(payloadLen % 4 != 0) {
			paddedLen += 4 - (payloadLen % 4);
		}
		ByteBuffer packet = ByteBuffer.allocate(paddedLen);
		packet.putInt(payloadLen);
		packet.putInt(pSecret);
		packet.putShort(step);
		packet.putShort(studentNum);
		
		return packet;
	}
}
