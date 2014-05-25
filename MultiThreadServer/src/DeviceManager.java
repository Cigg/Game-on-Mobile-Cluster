package src;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;


// TODO: Make all computations based on new data types like vectors and
// 		 move the math part to a separate class.

// This class handles the interaction between the server and the devices.
public class DeviceManager {

	private volatile static AtomicBoolean shouldUpdateScores = new AtomicBoolean(true);
	
	public static boolean shouldUpdateScores() {
		return shouldUpdateScores.get();
	}
	
	public static void setUpdateScores(final boolean upd) {
		shouldUpdateScores.set(upd);
	}
	
	public void clearAllScores() {
		for (Device device : this.devices) {
			device.setScore(0);
		}
	}
	
	// Data type for a device.
	// Only the DeviceManager should be able to modify this class.
	private class Device {

		String ip;			// Use the ip as an id.
		float rotZ;			// The device rotation relative the global positive x-axis.
		float posX;			// The position of the device's local origin relative the global origin.
		float posY;		
		int xDPI;			// The device's dots per inch.
		int yDPI;
		int resX;			// The device's resolution.
		int resY;
		boolean needsMapping;
		short type;
		int score;
		
		private LinkedBlockingQueue<byte[]> messagesToSend = new LinkedBlockingQueue <byte[]>();
		
		public Device(String ip, short type, int xDPI, int yDPI, int resX, int resY) {
			this.ip = ip;
			this.xDPI = xDPI;
			this.yDPI = yDPI;
			this.resX = resX;
			this.resY = resY;
			this.rotZ = 0; 			// Zero rotation for main-device.
			this.posX = 0; 			// The global origin is defined on main-device.
			this.posY = 0; 			// The global origin is defined on main-device.
			
			this.type = type;
			
			int score = 0;
			
			if(type == 0) { // type == 0 is middle device
				needsMapping = false;
				
				// Add middle object here?
				
			} else {
				needsMapping = true;
			}
		}
		public float getPosX(){
			return posX;
		}
		public float getPosY(){
			return posY;
		}
		public float getRotZ(){
			return rotZ;
		}
		
		public void setScore(final int score) {
			this.score = score;
		}
		
		public void incrementScore(){
			setUpdateScores(true);
			score++;
			System.out.print(score);
			System.out.println();
		}
	}
	
	// Container with all added devices.
	public volatile ArrayList<Device> devices;
	
	// Temporary variables for point-mapping devices algorithm.
	public float theta1;
	public float velocity1;
	public float pos2x;
	public float pos2y;
	
	
	DeviceManager() {
		this.devices = new ArrayList<Device>();
	}
	
	
    public int meters2Pixels(String ip, float meters) {
    	for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return (int) (meters * device.xDPI * 100 / 2.54);
		    }
		}
    	return 0;
    }
    
	
	public boolean isMiddle(String ip) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return device.type == 0;
		    }
		}
		return false;
	}
	
	
	public String getMiddleIp() {
		for (Device device : this.devices) {
		    if (device.type == 0) {
		    	return device.ip;
		    }
		}
		return null;
	}
	
	
	public float getMidX(String ip) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return device.resX / 2;
		    }
		}
		return 0;
	}
	
	
	public float getMidY(String ip) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return device.resY / 2;
		    }
		}
		return 0;
	}
	
	
	public float getPosX(String ip) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return device.posX;
		    }
		}
		return 0;
	}
	
	
	public float getPosY(String ip) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return device.posY;
		    }
		}
		return 0;
	}
	
	
	public float getRotZ(String ip) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return device.rotZ;
		    }
		}
		return 0;
	}
	
	public float getWidthInch(String ip) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return (float)device.resX/(float)device.xDPI;
		    }
		}
		return 0;
	}
	
	
	public float getHeightInch(String ip) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return (float)device.resY/(float)device.yDPI;
		    }
		}
		return 0;
	}
	
	public String getIp(int i) {
		if(i < devices.size()){
			return devices.get(i).ip;
		}
		return null;
	}
	
	
	public void addDevice(String ip, short type, int xDPI, int yDPI, int resX, int resY) {
	
		for (Device device : this.devices) {
			  if (device.ip.equals(ip)) {
				  System.out.println("ALREADY ADDED CHANGE TO NEW");
				  device = new Device(ip, type, xDPI, yDPI, resX, resY);
				  return;
			  }
		}
		
		
		this.devices.add(new Device(ip, type, xDPI, yDPI, resX, resY));
		System.out.println("Added device: " + ip + " TYPE: " + type + "  "+ xDPI + " " + yDPI + " " + resX + " " +resY );
	}

	
	// Compute local coordinate angle (relative the positive x-axis) of a vector based on two given positions. 
	private float computeTheta(float x1, float y1, float x2, float y2) {
		float deltaY = y2 - y1;
		float deltaX = x2 - x1;
		float theta = (float) Math.atan2(deltaY, deltaX);
		return theta;
	}
	
	
	// TODO: Fix devision by zero. (t, xDPI, yDPI)
	//	     Place the computation of the vector's length in a separate function.
	// Compute global coordinate velocity of a vector based on two given positions and a delta time. 
	private float computeVelocity(float x1, float y1, float x2, float y2, float t, int xDPI, int yDPI) {
		float length = (float) Math.sqrt(Math.pow((x2 - x1)/xDPI, 2) + Math.pow((y2 - y1)/yDPI,  2));
		float velocity = length/t;
		return velocity;
	}
	
	// TODO: Se över, gör om gör rätt.
	public float computeVelocityX(String ip, float x1, float y1, float x2, float y2, float t) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	
		    	double deltaX = (x2 - x1) / device.xDPI;
		    	double deltaY = (y2 - y1) / device.yDPI;
		    	
		    	double velX = deltaX/t;
		    	double velY = deltaY/t;
				
		    	double velXF = (float) (velX*Math.cos(-device.rotZ) - velY*Math.sin(-device.rotZ));
		    	double velYF = (float) (velX*Math.sin(-device.rotZ) + velY*Math.cos(-device.rotZ));
				
				return (float) velXF;
		    }
		}
		return 0;
	}
	
	// TODO: Se över, gör om gör rätt.
	public float computeVelocityY(String ip, float x1, float y1, float x2, float y2, float t) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
				y1 = device.resY - y1;
				y2 = device.resY - y2;
		    	
				double deltaX = (x2 - x1) / device.xDPI;
		    	double deltaY = (y2 - y1) / device.yDPI;
		    	
		    	double velX = deltaX/t;
		    	double velY = deltaY/t;
				
		    	double velXF = (float) (velX*Math.cos(-device.rotZ) - velY*Math.sin(device.rotZ));
		    	double velYF = (float) (velX*Math.sin(device.rotZ) + velY*Math.cos(device.rotZ));
				
				return (float) velYF;
		    }
		}
		return 0;
	}
	
	
	// TODO: Fix devision by zero. (t, ppi)
	// Compute global coordinate velocity along one axis of a vector based on two given positions and a delta time. 
	public float computeVelX(String ip, float x1, float x2, float t){
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
				float deltaX = (x2 - x1) / device.xDPI;
				float velocity = deltaX/t;
				return velocity;
		    }
		}
		return 0;
	}
	
	public float computeVelY(String ip, float y1, float y2, float t){
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
				y1 = device.resY - y1;
				y2 = device.resY - y2;
				float deltaY = (y2 - y1) / device.yDPI;
				float velocity = deltaY/t;
				return velocity;
		    }
		}
		return 0;
	}
	
	/*
	public float addRotationX(String ip, float x){
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
				return (float) (x * Math.cos(device.rotZ));
		    }
		}
		return 0;
	}
	
	public float addRotationY(String ip, float y){
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
				return (float) (y * Math.sin(device.rotZ));
		    }
		}
		return 0;
	}
	*/
	

	public void setMappingAtMainDevice() {
		for (Device device : this.devices) {
		    if (device.type == 0) {
		    	// @ Main device
				System.out.println("Found Main-device");
				
				ByteBuffer buffer = ByteBuffer.allocate(2*2);
	    		buffer.clear();
	    		
	    		buffer.putShort((short) GLOBAL_STATE__.SET_STATE.ordinal());	// State: 		SET_STATE
	    		buffer.putShort((short) GLOBAL_STATE__.MAP_DEVICE.ordinal());	// New state: 	MAP_DEVICE
	    		
	    		int position = buffer.position();
	    		byte[] sendBytes = new byte[position];
				System.arraycopy( buffer.array(), 0, sendBytes, 0, position);
				
	    		device.messagesToSend.add(sendBytes);
		    }
		}
	}
	
	public void setMappingAtAllDevices() {
		for (Device device : this.devices) {
				System.out.println("Found Main-device");
				
				ByteBuffer buffer = ByteBuffer.allocate(2*2);
	    		buffer.clear();
	    		
	    		buffer.putShort((short) GLOBAL_STATE__.SET_STATE.ordinal());	// State: 		SET_STATE
	    		buffer.putShort((short) GLOBAL_STATE__.MAP_DEVICE.ordinal());	// New state: 	MAP_DEVICE
	    		
	    		int position = buffer.position();
	    		byte[] sendBytes = new byte[position];
				System.arraycopy( buffer.array(), 0, sendBytes, 0, position);
				
	    		device.messagesToSend.add(sendBytes);
		}
	}
	
	
	
	public byte[] getNextMessage(String ip) {
		for (Device device : this.devices) {
			if(device.ip.equals(ip)) {
				return device.messagesToSend.poll();
			}
		}
		return null;
	}
	
	public boolean hasMessagesToSend(String ip) {
		for (Device device : this.devices) {
			if(device.ip.equals(ip)) {
			    if(!device.messagesToSend.isEmpty()) {
			    	return true;
			    }
			    return false;
			}
		}
		return false;
	}
	
	// Checks if a device is added.
	public boolean deviceIsAdded(String ip) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return true;
		    }
		}
		return false;
	}
	
	public int getDeviceThread(String ip) {
		for(int i=0; i < this.devices.size(); i++) {
			if(devices.get(i).ip.equals(ip)) {
				return i;
			}
		}
		return -1;
	}
	
	// TODO: Complete function...........
	// Converts a position in local coordinates to global coordinates
	public float localToGlobalX(String ip, float x1, float y1) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	y1 = device.resY - y1;
				
		    	float x2 = (float) (device.posX + ((x1/device.xDPI)*Math.cos(device.rotZ) - (y1/device.yDPI)*Math.sin(device.rotZ)));
		    	float y2 = (float) (device.posY + ((x1/device.xDPI)*Math.sin(device.rotZ) + (y1/device.yDPI)*Math.cos(device.rotZ)));
		    	System.out.println("Global (" + x1 + ", " + y1 + ") = (" + x2 + ", " + y2 + ") = (" + x2*2.5 + ", " + y2*2.5 + ")" );
		    	//return ((x2 <= device.resX/device.xDPI) && (y2 <= device.resY/device.yDPI));
		    	
		    	return x2;
		    }
		}
		return 0;
	}
	
	public float localToGlobalY(String ip, float x1, float y1) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	y1 = device.resY - y1;
				
		    	float x2 = (float) (device.posX + ((x1/device.xDPI)*Math.cos(device.rotZ) - (y1/device.yDPI)*Math.sin(device.rotZ)));
		    	float y2 = (float) (device.posY + ((x1/device.xDPI)*Math.sin(device.rotZ) + (y1/device.yDPI)*Math.cos(device.rotZ)));
		    	System.out.println("Global (" + x1 + ", " + y1 + ") = (" + x2 + ", " + y2 + ") = (" + x2*2.5 + ", " + y2*2.5 + ")" );
		    	//return ((x2 <= device.resX/device.xDPI) && (y2 <= device.resY/device.yDPI));
		    	
		    	return y2;
		    }
		}
		return 0;
	}
	
	
	public float globalToLocalX(String ip, float xG, float yG) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	
		    	
		    	float xB = xG - device.posX;
		    	float yB = yG - device.posY;
		    	
		    	float xT = (float) (xB*Math.cos(-device.rotZ) - yB*Math.sin(-device.rotZ));
		    	float yT = (float) (xB*Math.sin(-device.rotZ) + yB*Math.cos(-device.rotZ));
		    	
		    	float xL = (xT * device.xDPI);
		    	float yL = (yT * device.yDPI);
		    	
		    	yL = device.resY - yL;
		    	
		    /*	float xB = xG - device.posX;
		    	float yB = yG - device.posY;
		    	
		    	int xL = (int) (xB * device.xDPI);
		    	int yL = (int) (yB * device.yDPI);
		    	yL = device.resY - yL;
			*/
		    	//System.out.println("Global:  " + xG + ", " + yG + "   Local: " + xL + ", " + yL);
		    	return xL;
		    }
		}
		return 0;
	}
	
	public float globalToLocalY(String ip, float xG, float yG) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	
		    	float xB = xG - device.posX;
		    	float yB = yG - device.posY;
		    	
		    	float xT = (float) (xB*Math.cos(-device.rotZ) - yB*Math.sin(-device.rotZ));
		    	float yT = (float) (xB*Math.sin(-device.rotZ) + yB*Math.cos(-device.rotZ));
		    	
		    	float xL = (xT * device.xDPI);
		    	float yL = (yT * device.yDPI);
		    	
		    	yL = device.resY - yL;
		    	//System.out.println("Global:  " + xG + ", " + yG + "   Local: " + xL + ", " + yL);
		    	return yL;
		    }
		}
		return 0;
	}
	
	
	public float globalToLocalVelX(String ip, float xVelG, float yVelG) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	
		    	
		    	double velX = xVelG;
		    	double velY = yVelG;
				
		    	double velXF = (float) (velX*Math.cos(-device.rotZ) - velY*Math.sin(-device.rotZ));
		    	double velYF = (float) (velX*Math.sin(-device.rotZ) + velY*Math.cos(-device.rotZ));
				
		    	return (float) (velXF * device.xDPI);
		    	
		    }
		}
		return 0;
	}
	
	public float globalToLocalVelY(String ip, float xVelG, float yVelG) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	
		    	double velX = xVelG;
		    	double velY = - yVelG;
				
		    	double velXF = (float) (velX*Math.cos(-device.rotZ) - velY*Math.sin(device.rotZ));
		    	double velYF = (float) (velX*Math.sin(device.rotZ) + velY*Math.cos(device.rotZ));
				
				return (float)(velYF * device.yDPI);
		    }
		}
		return 0;
	}
	
	
	public boolean isOnDevice(String ip, float xG, float yG, float rG) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	rG = meters2Pixels(ip, rG / 100.0f );
		    	

		    
		    	//System.out.println("dLx = " + dLx + "  dLy = " + dLy);
		    	
		    	float xL = globalToLocalX(ip, xG, yG);
		    	float yL = globalToLocalY(ip, xG, yG);
		    	
				if((xL >= -rG) && (yL >= -rG) && (xL <= (device.resX + rG)) && (yL <= (device.resY + rG))) {
					return true;
				}
				return false;
			}
		}
		return false;
	}
	
	
	public boolean needsMapping(String ip) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return device.needsMapping;
		    }
		}
		return false;
	}
	
	public void setNeedsMapping(String ip, boolean needsMapping) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	device.needsMapping = needsMapping;
		    	return;
		    }
		}
	}
	
	// TODO: Fix devision by zero. (xDPI, yDPI)
	// Point mapping on main device.
	public void devicePointMappingStep1(String ip, float x1, float y1Org, float x2, float y2Org, float t) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	// Set origin to the lower left corner.
				float y1 = device.resY - y1Org;
				float y2 = device.resY - y2Org;
				
				// Compute the released position in global coordinates.
		    	//this.pos2x = x2/device.xDPI;
		    	//this.pos2y = y2/device.yDPI;
		    	this.pos2x = localToGlobalX(ip, x2, y2Org);
		    	this.pos2y = localToGlobalY(ip, x2, y2Org);
		    	
		    	
		    	// Compute the vector's angle relative the global positive x-axis.
		    	//this.theta1 = computeTheta(x1, y1, x2, y2);
		    	this.theta1 = device.rotZ + computeTheta(x1, y1, x2, y2);
		    	
		    	// Compute the velocity between push down and release.
		    	this.velocity1 = computeVelocity(x1, y1, x2, y2, t, device.xDPI, device.yDPI);
		    	
		    	// The data needed for step 2 is now set, no need to loop through the other devices.
		    	return;
		    }
		}
	}
	
	
	// TODO: Fix devision by zero. (xDPI, yDPI)
	// Point mapping on regular device.
	public void devicePointMappingStep2(String ip, float x1, float y1Org, float x2, float y2Org, float t, float deltaT) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	// Set origin to the lower left corner.
				float y1 = device.resY - y1Org;
				float y2 = device.resY - y2Org;
				
				// Set the time to be between the first release position and the second push down position.
				deltaT = deltaT - t;
				
				// Compute the angle relative to the positive x-axis of the device.
		    	float theta2 = computeTheta(x1, y1, x2, y2);
		    	
		    	// Compute the angle of the device's positive x-axis relative to the global positive x-axis.
		    	float alfa = theta1 - theta2;

		    	// Set the device's rotation relative the global positive x-axis.
		    	device.rotZ = alfa;
		    	
		    	// Compute the velocity between the second push down and release positions.
		    	float velocity2 = computeVelocity(x1, y1, x2, y2, t, device.xDPI, device.yDPI);
		    	
		    	// Set the final velocity to be the mean between the main-device's velocity and device's velocity.
		    	float velocity3 = (velocity1+velocity2)/2;
		    	
		    	// Compute the second push down position in global coordinates.
		    	float pos3x = (float) (pos2x + velocity3 * deltaT * Math.cos(theta1));
		    	float pos3y = (float) (pos2y + velocity3 * deltaT * Math.sin(theta1));
		    	
		    	// Compute position of local origin in global coordinates.
		    	device.posX = (float) (pos3x - ((x1/device.xDPI)*Math.cos(device.rotZ) - (y1/device.yDPI)*Math.sin(device.rotZ)));
		    	device.posY = (float) (pos3y - ((x1/device.yDPI)*Math.sin(device.rotZ) + (y1/device.yDPI)*Math.cos(device.rotZ)));
		    	
		    	// Print the result
		    	System.out.println("--------------------------");
		    	
		    	//System.out.println("device on: " + localToGlobal(ip, x2, y2));
		    	System.out.println("device.rotZ = " + device.rotZ);
		     	System.out.println("device.posX = " + device.posX + " eller " + device.posX*2.5);
		    	System.out.println("device.posY = " + device.posY + " eller " + device.posY*2.5);
		    	System.out.println("--------------------------");
		    	
				
				
		    	this.pos2x = localToGlobalX(ip, x2, y2Org);
		    	this.pos2y = localToGlobalY(ip, x2, y2Org);
		    	//this.theta1 = theta1;
		    	this.velocity1 = velocity2;
		    	
		    	
		    	
		    	// The device is now mapped, no need to loop through the other devices.
		    	return;
		    }
		}		
	}
	
	public void score(int id){
		System.out.print("Score now is: ");
		devices.get(id).incrementScore();
	}
	
	public int getScore(String ip) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	return device.score;
		    }
		}
		return 0;
	}
}