
import java.util.ArrayList;


// TODO: Make all computations based on new data types like vectors and
// 		 move the math part to a separate class.

// This class handles the interaction between the server and the devices.
public class DeviceManager {

	// Data type for a device.
	// Only the DeviceManager should be able to modify this class.
	private class Device {

		String ip;				// Use the ip as an id.
		float deviceRotZ;		// The device rotation relative the global positive x-axis.
		float devicePosX;		// The position of the device's local origin relative the global origin.
		float devicePosY;		
		int xDPI;				// The device's dots per inch.
		int yDPI;
			
		int deviceResX;			// The device's resolution.
		int deviceResY;
		
		public Device(String ip, int xDPI, int yDPI, int deviceResX, int deviceResY) {
			this.ip = ip;
			this.xDPI = xDPI;
			this.yDPI = yDPI;
			
			this.deviceResX = deviceResX;
			this.deviceResY = deviceResY;
			this.deviceRotZ = 0; 			// Zero rotation for main-device.
			this.devicePosX = 0; 			// The global origin is defined on main-device.
			this.devicePosY = 0; 			// The global origin is defined on main-device.
		}
	
	}
	
	
	// Container with all added devices.
	public ArrayList<Device> devices;
	
	// Temporary variables for point-mapping devices algorithm.
	public float theta1;
	public float velocity1;
	public float pos2x;
	public float pos2y;
	
	
	
	DeviceManager() {
		this.devices = new ArrayList<Device>();
	}

	
	public void addDevice(String ip, int xDPI, int yDPI, int deviceResX, int deviceResY) {
		if(deviceIsAdded(ip)) {	
			System.out.println("Device is already addedd: " + ip + " " + xDPI + " " + yDPI + " " + deviceResX + " " +deviceResY);
			return;
		}
		
		this.devices.add(new Device(ip, xDPI, yDPI, deviceResX, deviceResY));
		System.out.println("Added device: " + ip + " " + xDPI + " " + yDPI + " " + deviceResX + " " +deviceResY);
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
	
	
	// TODO: Fix devision by zero. (t, ppi)
	// Compute global coordinate velocity along one axis of a vector based on two given positions and a delta time. 
	private float computeVel(float x1, float x2, float t, int ppi){
		float deltaX = (x2 - x1) / ppi;
		float velocity = deltaX/t;
		return velocity;
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
	
	
	// TODO: Complete function...........
	// Converts a position in local coordinates to global coordinates
	public boolean localToGlobal(String ip, float x1, float y1) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	float x2 = (float) (device.devicePosX + ((x1/device.xDPI)*Math.cos(device.deviceRotZ) - (y1/device.yDPI)*Math.sin(device.deviceRotZ)));
		    	float y2 = (float) (device.devicePosY + ((x1/device.yDPI)*Math.sin(device.deviceRotZ) + (y1/device.yDPI)*Math.cos(device.deviceRotZ)));
		    	return ((x2 <= device.deviceResX/device.xDPI) && (y2 <= device.deviceResY/device.yDPI));
		    }
		}
		return false;
	}
	
	
	// TODO: Fix devision by zero. (xDPI, yDPI)
	// Point mapping on main device.
	public void devicePointMappingStep1(String ip, float x1, float y1, float x2, float y2, float t) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	// Set origin to the lower left corner.
				y1 = device.deviceResY - y1;
				y2 = device.deviceResY - y2;
				
				// Compute the released position in global coordinates.
		    	this.pos2x = x2/device.xDPI;
		    	this.pos2y = y2/device.yDPI;
		    	
		    	// Compute the vector's angle relative the global positive x-axis.
		    	this.theta1 = computeTheta(x1, y1, x2, y2);
		    	
		    	// Compute the velocity between push down and release.
		    	this.velocity1 = computeVelocity(x1, y1, x2, y2, t, device.xDPI, device.yDPI);
		    	
		    	// The data needed for step 2 is now set, no need to loop through the other devices.
		    	return;
		    }
		}
	}
	
	
	// TODO: Fix devision by zero. (xDPI, yDPI)
	// Point mapping on regular device.
	public void devicePointMappingStep2(String ip, float x1, float y1, float x2, float y2, float t, float deltaT) {
		for (Device device : this.devices) {
		    if (device.ip.equals(ip)) {
		    	// Set origin to the lower left corner.
				y1 = device.deviceResY - y1;
				y2 = device.deviceResY - y2;
				
				// Set the time to be between the first release position and the second push down position.
				deltaT = deltaT - t;
				
				// Compute the angle relative to the positive x-axis of the device.
		    	float theta2 = computeTheta(x1, y1, x2, y2);
		    	
		    	// Compute the angle of the device's positive x-axis relative to the global positive x-axis.
		    	float alfa = theta1 - theta2;

		    	// Set the device's rotation relative the global positive x-axis.
		    	device.deviceRotZ = alfa;
		    	
		    	// Compute the velocity between the second push down and release positions.
		    	float velocity2 = computeVelocity(x1, y1, x2, y2, t, device.xDPI, device.yDPI);
		    	
		    	// Set the final velocity to be the mean between the main-device's velocity and device's velocity.
		    	float velocity3 = (velocity1+velocity2)/2;
		    	
		    	// Compute the second push down position in global coordinates.
		    	float pos3x = (float) (pos2x + velocity3 * deltaT * Math.cos(theta1));
		    	float pos3y = (float) (pos2y + velocity3 * deltaT * Math.sin(theta1));
		    	
		    	// Compute position of local origin in global coordinates.
		    	device.devicePosX = (float) (pos3x - ((x1/device.xDPI)*Math.cos(device.deviceRotZ) - (y1/device.yDPI)*Math.sin(device.deviceRotZ)));
		    	device.devicePosY = (float) (pos3y - ((x1/device.yDPI)*Math.sin(device.deviceRotZ) + (y1/device.yDPI)*Math.cos(device.deviceRotZ)));
		    	
		    	// Print the result
		    	System.out.println("--------------------------");
		    	
		    	System.out.println("device on: " + localToGlobal(ip, x2, y2));
		     	System.out.println("device._devicePosX = " + device.devicePosX + " eller " + device.devicePosX*2.5);
		    	System.out.println("device._devicePosY = " + device.devicePosY + " eller " + device.devicePosY*2.5);
		    	System.out.println("--------------------------");
		    	
		    	// The device is now mapped, no need to loop through the other devices.
		    	return;
		    }
		}		
	}
	
	
}