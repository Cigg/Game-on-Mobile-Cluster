
public class Server {
	// public ArrayList<Objects> _objects;
	
	private static float time1;
	private static int index1;
	private static int index2;
	
	DeviceManager deviceManager = new DeviceManager();
	
	enum GLOBAL_STATE{
		ADD_DEVICE,
		MAP_DEVICE
	}
	
	enum LOCAL_STATE{
		MAPPING_STEP1,
		MAPPING_STEP2
	}
	
	LOCAL_STATE internalState;
	
	public Server(){
		internalState = LOCAL_STATE.MAPPING_STEP1;
	}
	
	//TODO - Send objects that the devices should draw.
	public void update(){
		// for längden av arraylisten med devices 
		// Rita ut fyrkanter baserat på translation och rotation av devicerna
		// Skicka tillbaka fyrkanter lite här och där 

	}

	
	// TODO - Extract data from string
	public void recieveEvent(String str){
		//System.out.println("recieceEvent(" + str + ")");
		String[] args = str.split(" ");
		GLOBAL_STATE state = GLOBAL_STATE.values()[Integer.parseInt(args[1])];
		switch(state){
			case ADD_DEVICE:
				System.out.println("ADD_DEVICE: " + args[0]);
				deviceManager.addDevice(	Integer.parseInt(args[0]), 		// id
											Integer.parseInt(args[2]), 		// ppi
											Integer.parseInt(args[3]), 		// resX
											Integer.parseInt(args[4])	);	// resY
				break;
				
			case MAP_DEVICE:	
				
				switch(internalState) {
					case MAPPING_STEP1:
						System.out.println("MAPPING_STEP1");
						time1 = (float) (System.nanoTime()/(Math.pow(10,9)));
						index1 = Integer.parseInt(args[0]);
						deviceManager.deviceMappingStep1(	Integer.parseInt(args[0]), 		// id
															Float.parseFloat(args[2]), 		// x1
															Float.parseFloat(args[3]), 		// y1
															Float.parseFloat(args[4]), 		// x2
															Float.parseFloat(args[5]), 		// y2
															Float.parseFloat(args[6])	);  // t1
						internalState = LOCAL_STATE.MAPPING_STEP2;
						break;
					case MAPPING_STEP2:
						System.out.println("MAPPING_STEP2");
						if(deviceManager.devices.size() > 1){
								
							float time2 = (float) (System.nanoTime()/Math.pow(10, 9));
							float time3 = time2 - time1;
							index2 = Integer.parseInt(args[0]);
							deviceManager.deviceMappingStep2(	Integer.parseInt(args[0]), 		// id
																Float.parseFloat(args[2]), 		// x1
																Float.parseFloat(args[3]), 		// y1
																Float.parseFloat(args[4]), 		// x2
																Float.parseFloat(args[5]), 		// y2
																Float.parseFloat(args[6]),		// t2
																time3						);  // t3
							//screenTest();
							internalState = LOCAL_STATE.MAPPING_STEP1;						
						}else{
							System.out.println("Must be more then 1 device, add another device and try again");
						}
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
	
	public String screenTest(){
		//System.out.println("Server - screenTest()");
		String str1 = deviceManager.getInputForScreenTest(index1);
		String str2 = deviceManager.getInputForScreenTest(index2);
		
		String strFinal = str1 + " " + str2;
		//System.out.println("Server - screenTest() - returns: " + strFinal);
		return strFinal;
	}	
}
