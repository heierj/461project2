/** 
 * This class will act as the entry point for clients. It will
 * receive the first packet and then spin off a thread to handle 
 * the session for that client.
 * 
 * @author Jordan Heier, Cameron Hardin, Will McNamara
 */
import java.util.concurrent.ForkJoinPool;

public class Server {
	public static void main(String[] args) {
		ForkJoinPool pool = new ForkJoinPool();
		SessionThread st = new SessionThread();
		pool.invoke(st);
	}
}
