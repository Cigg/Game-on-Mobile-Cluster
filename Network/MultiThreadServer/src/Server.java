import java.util.ArrayList;

public class Server {
	// public ArrayList<Objects> _objects;
	
	private static float _time1;
	
	DeviceManager _deviceManager;
	
	enum GLOBAL_STATE{
		ADD_DEVICE,
		MAP_DEVICE
	}
	
	enum LOCAL_STATE{
		MAPPING_STEP1,
		MAPPING_STEP2
	}
	
	LOCAL_STATE _internalState;
	
	public Server(){
		_internalState = LOCAL_STATE.MAPPING_STEP1;
	}
	
	//TODO - Send objects that the devices should draw.
	public void update(){
		
	}
	
	// TODO - Extract data from string
	public void recieveEvent(String str){
		String[] args = str.split(" ");
		GLOBAL_STATE state = GLOBAL_STATE.values()[Integer.parseInt(args[0])];
		switch(state){
			case ADD_DEVICE:
				_deviceManager.addDevice(	Integer.parseInt(args[1]), 		// id
											Integer.parseInt(args[2]), 		// ppi
											Integer.parseInt(args[3]), 		// resX
											Integer.parseInt(args[4])	);	// resY
				break;
				
			case MAP_DEVICE:	
				
				switch(_internalState) {
					case MAPPING_STEP1:
						_time1 = System.nanoTime();
						_deviceManager.deviceMappingStep1(	Integer.parseInt(args[1]), 		// id
															Float.parseFloat(args[2]), 		// x1
															Float.parseFloat(args[3]), 		// y1
															Float.parseFloat(args[4]), 		// x2
															Float.parseFloat(args[5]), 		// y2
															Float.parseFloat(args[6])	);  // t1
						_internalState = LOCAL_STATE.MAPPING_STEP2;
						break;
					case MAPPING_STEP2:
						float time2 = System.nanoTime();
						float time3 = time2 - _time1;
						_deviceManager.deviceMappingStep2(	Integer.parseInt(args[1]), 		// id
															Float.parseFloat(args[2]), 		// x1
															Float.parseFloat(args[3]), 		// y1
															Float.parseFloat(args[4]), 		// x2
															Float.parseFloat(args[5]), 		// y2
															Float.parseFloat(args[6]),		// t2
															time3						);  // t3
						
						break;
					default:
						break;
				}
				
				
											
			
				break;
			default:
				System.out.println("\nERROR STATE UNKNOWN: " + state + "\n");
				break;
		}
	}
}
