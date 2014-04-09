import java.util.ArrayList;


public class UpdateLoop extends Thread{

	private ArrayList<OldBall> balls;
	private clientThread[] threads;
	private String[] data;
	private int maxClientCount;
	
	
	public UpdateLoop(clientThread[] threads, int maxClientCount) {
		this.threads = threads;
		this.maxClientCount = maxClientCount;
		balls = new ArrayList<OldBall>();
		data = new String[maxClientCount];
	}
	
	public void run() {
		while(true){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//System.out.println(balls.size());
			update();
		}
	}
	
	public void addBall(OldBall ball) {
		balls.add(ball);
	}
	
	private void update() {
		for(int i=0; i < maxClientCount; i++){
			data[i] = "";
		}
		if(!balls.isEmpty()){
			for(int i = 0; i < balls.size(); i++) {
				balls.get(i).updatePos();
				for(int j=0; j < maxClientCount; j++) {
					if(threads[j] != null) {
						GlobalCoords deviceCoords = threads[j].globalCoords;
						if(balls.get(i).isOnDevice(deviceCoords)) {
							float x = balls.get(i).posX - threads[j].globalCoords.minX;
							float y = balls.get(i).posY - threads[j].globalCoords.minY;
							System.out.println(x + " " + y);
							data[j] += balls.get(i).ballId + " " + x + " " + y + " n ";
						}
					}
				}
			}
			for(int j=0; j < maxClientCount; j++){
				if(threads[j] != null){
					threads[j].sendMessage(data[j]);
				}
			}
		}
	}
}// END OF UPDATELOOP
