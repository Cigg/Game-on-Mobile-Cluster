import java.util.ArrayList;


public class DeviceManager{

	public ArrayList<Device> _devices;
	public static float theta1;
	public static float velocity1;
	public static float pos2x;
	public static float pos2y;
	
	private class Device{

		int _id;
		float _deviceRotZ;
		float _devicePosX;
		float _devicePosY;
		int _PPI;
		int _deviceResX;
		int _deviceResY;
		
		public Device(int id, int PPI, int deviceResX, int deviceResY){
			_id = id;
			_PPI = PPI;
			_deviceResX = deviceResX;
			_deviceResY = deviceResY;
			_deviceRotZ = 0; //Main device is always 0
			_devicePosX = 0;
			_devicePosY = 0;
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
		_devices.add(new Device(id, PPI, deviceResX, deviceResY));
	}
	
	private float computeTheta(float x1, float y1, float x2, float y2){
		float deltaY = y2 - y1;
		float deltaX = x2 - x1;
		float theta = (float) Math.atan(deltaY/deltaX);
		return theta;
	}
		
	private float computeVelocity(float x1, float y1, float x2, float y2, float t, int ppi){
		float length = (float) Math.sqrt(Math.pow((x2 - x1)/ppi, 2) + Math.pow((y2 - y1)/ppi,  2));
		float velocity = length/t;
		return velocity;
	}
	
	public void deviceMappingStep1(int id, float x1, float y1, float x2, float y2, float t) {
		
		for (Device device : _devices) {
		    if (device._id == id) {

				y1 = device._deviceResY - y1;
				y2 = device._deviceResY - y2;
				
		    	pos2x = x2/device._PPI;
		    	pos2y = y2/device._PPI;
		    	
		    	theta1 = computeTheta(x1, y1, x2, y2);
		    	velocity1 = computeVelocity(x1, y1, x2, y2, t, device._PPI);
		    }
		}
	}
	
	public void deviceMappingStep2(int id, float x1, float y1, float x2, float y2, float t, float deltaT) {
		for (Device device : _devices) {
		    if (device._id == id) {

				y1 = device._deviceResY - y1;
				y2 = device._deviceResY - y2;
				
		    	float theta2 = computeTheta(x1, y1, x2, y2);
		    	device._deviceRotZ = theta1 - theta2;
		    	
		    	float velocity2 = computeVelocity(x1, y1, x2, y2, t, device._PPI);
		    	float velocity3 = (velocity1+velocity2)/2;
		    	
		    	float pos3x = (float) (velocity3 * deltaT * Math.cos(theta1) + pos2x);
		    	float pos3y = (float) (velocity3 * deltaT * Math.sin(theta1) + pos2y);
		    	
		    	device._devicePosX = (float) (pos3x - (1/device._PPI)*(x1*Math.cos(device._deviceRotZ) + y1*Math.sin(device._deviceRotZ)));
		    	device._devicePosY = (float) (pos3y - (1/device._PPI)*(-x1*Math.sin(device._deviceRotZ) + y1*Math.cos(device._deviceRotZ)));
		    }
		}		
	}
	
}
