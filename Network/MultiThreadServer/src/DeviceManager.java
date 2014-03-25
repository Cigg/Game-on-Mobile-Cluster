import java.util.ArrayList;


public class DeviceManager{

	public ArrayList<Device> devices = new ArrayList<Device>();
	public static float theta1;
	public static float velocity1;
	public static float pos2x;
	public static float pos2y;
	
	private class Device{

		int id;
		float deviceRotZ;
		float devicePosX;
		float devicePosY;
		int PPI;
		int deviceResX;
		int deviceResY;
		
		public Device(int id, int PPI, int deviceResX, int deviceResY){
			this.id = id;
			this.PPI = PPI;
			this.deviceResX = deviceResX;
			this.deviceResY = deviceResY;
			this.deviceRotZ = 0; //Main device is always 0
			this.devicePosX = 0;
			this.devicePosY = 0;
		}
		
		public boolean objectOnScreen(float globalPosX, float globalPosY, Device device){
			// TODO - Complete function with equations
			//
			
			return false;
		}
	}

	// TODO - Loop through all devices to make sure that the same device
	// is not added twice. 
	public void addDevice(int id, int PPI, int deviceResX, int deviceResY){
		devices.add(new Device(id, PPI, deviceResX, deviceResY));
	}
	
	private float computeTheta(float x1, float y1, float x2, float y2){
		float deltaY = y2 - y1;
		float deltaX = x2 - x1;
		float theta = (float) Math.atan2(deltaY,deltaX);
		return theta;
	}
		
	private float computeVelocity(float x1, float y1, float x2, float y2, float t, int ppi){
		float length = (float) Math.sqrt(Math.pow((x2 - x1)/ppi, 2) + Math.pow((y2 - y1)/ppi,  2));
		float velocity = length/t;
		return velocity;
	}
	
	public void deviceMappingStep1(int id, float x1, float y1, float x2, float y2, float t) {
		
		for (Device device : devices) {
		    if (device.id == id) {

				y1 = device.deviceResY - y1;
				y2 = device.deviceResY - y2;
				
		    	pos2x = x2/device.PPI;
		    	pos2y = y2/device.PPI;
		    	
		    	theta1 = computeTheta(x1, y1, x2, y2);
		    	velocity1 = computeVelocity(x1, y1, x2, y2, t, device.PPI);
		    }
		}
	}
	
	public void deviceMappingStep2(int id, float x1, float y1, float x2, float y2, float t, float deltaT) {
		for (Device device : devices) {
		    if (device.id == id) {

				y1 = device.deviceResY - y1;
				y2 = device.deviceResY - y2;
				
		    	float theta2 = computeTheta(x1, y1, x2, y2);
		    	device.deviceRotZ = theta1 - theta2;
		    	
		    	float velocity2 = computeVelocity(x1, y1, x2, y2, t, device.PPI);
		    	float velocity3 = (velocity1+velocity2)/2;
		    	
		    	float pos3x = (float) (velocity3 * deltaT * Math.cos(theta1) + pos2x);
		    	float pos3y = (float) (velocity3 * deltaT * Math.sin(theta1) + pos2y);
		    	
		    	device.devicePosX = (float) (pos3x - (1/device.PPI)*(x1*Math.cos(device.deviceRotZ) + y1*Math.sin(device.deviceRotZ)));
		    	device.devicePosY = (float) (pos3y - (1/device.PPI)*(-x1*Math.sin(device.deviceRotZ) + y1*Math.cos(device.deviceRotZ)));
		    }
		}		
	}
	
	public String getInputForScreenTest(int id){
		String strInput = null;
		for (Device device : devices){
			if(device.id == id){
				
				strInput = device.devicePosX + " " + device.devicePosY + " " + device.deviceResY + " " + device.deviceResX + " " + device.deviceRotZ;				
				return strInput;
			}
		}
		return strInput;
	}
	
}
