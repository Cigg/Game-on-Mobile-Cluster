import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;


public class MultiThreds {
	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
	
	private static final int maxClientCount = 10;
	private static final clientThread[] threads = new clientThread[maxClientCount];
	private static final UpdateLoop updateLoop = new UpdateLoop(threads,maxClientCount);
	
	public static void main(String args[]) {
		int portNumber = 4444;
		if(args.length < 1) {
			System.out.println("Now using portnumber=" + portNumber);
		}else {
			portNumber = Integer.valueOf(args[0]).intValue();
		}
		
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		updateLoop.start();
		
		while(true) {
			try {
				clientSocket = serverSocket.accept();
				int i = 0;
				for(i=0; i<maxClientCount; i++) {
					if(threads[i] == null) {
						(threads[i] = new clientThread(clientSocket,threads,updateLoop)).start();
						break;
					}
				}
				if(i == maxClientCount) {
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server too busy. Try later");
					os.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}//End of Main
} // End of MultiThreds
