import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;


class UDPServer {
	
	
	// States
	public static enum GLOBAL_STATE {
		ADD_DEVICE,
		MAP_DEVICE,
		RUN_DEVICE,
		RESTART_SERVER
	}
	
	public static enum LOCAL_STATE {
		MAPPING_STEP1,
		MAPPING_STEP2
	}

	volatile static LOCAL_STATE _internalState;
	
	volatile static float _time1;
	volatile static DeviceManager _deviceManager;
	
	static UDPClient udp;
	
	public static void main(String args[]) throws Exception {
	
		System.out.println("SERVER");
		
		udp = new UDPClient();
		final int _nrOfExecutionThreads = 4;
	
		// Set initial states
		_internalState = LOCAL_STATE.MAPPING_STEP1;
	
		_deviceManager = new DeviceManager();
		
		// Run execution threads
		for(int a = 0; a < _nrOfExecutionThreads; a++) {
			final int id = a;
			Thread executer = new Thread() {
			    public void run() {
			        try {
			        	while(true) {
			        		System.out.println("BEGIN LOOP: " + id);
			        		
			        		DataPackage dataPackage = udp.enquires.take();
			        		ByteBuffer buffer = ByteBuffer.wrap(dataPackage.getData());
			        		
			        		//udp.sendData("Hej Jocke!".getBytes(), dataPackage.getIp(), dataPackage.getPort());
			        		

			        		
			        		// ----------------------------------------------------------------------------
			        		short state = buffer.getShort();
			        		String ip = dataPackage.getIp();

			        		switch(GLOBAL_STATE.values()[state]) {
			        			case ADD_DEVICE:
			        				
			        				short ids = buffer.getShort();	// not needed
			        				int xDPI = buffer.getInt();
			        				int yDPI = buffer.getInt();
			        				int deviceResX = buffer.getInt();
			        				int deviceResY = buffer.getInt();
			        				
			        				_deviceManager.addDevice(ip, xDPI, yDPI, deviceResX, deviceResY);
			        			break;
			        			
			        			case MAP_DEVICE:
			        				
					        		switch(_internalState) {
						        		case  MAPPING_STEP1:
						        			System.out.println("MAPPING_STEP1");
						        			
						        			short id = buffer.getShort();	// not needed
						        			float x1 = buffer.getFloat();	// byt ut mot lämplig
								        	float y1 = buffer.getFloat();	// byt ut mot lämplig
						        			float x2 = buffer.getFloat();	// byt ut mot lämplig
						        			float y2 = buffer.getFloat();	// byt ut mot lämplig
						        			float t  = buffer.getFloat();	// byt ut mot lämplig
						  
						        			_time1 = System.nanoTime();
						        			_deviceManager.devicePointMappingStep1(ip, x1, y1, x2, y2, t);
						        			_internalState = LOCAL_STATE.MAPPING_STEP2;
						        			
						        			System.out.println("MAPPING_STEP1 DONE");
						        		break;
						        		
						        		case  MAPPING_STEP2:
						        			System.out.println("MAPPING_STEP2");
						        
						        			float time2 = System.nanoTime();
						        			float deltaTime = time2 - _time1;
						        			
						        			short id1 = buffer.getShort();	// not needed
						        			float x11 = buffer.getFloat();	// byt ut mot lämplig
								        	float y11 = buffer.getFloat();	// byt ut mot lämplig
						        			float x22 = buffer.getFloat();	// byt ut mot lämplig
						        			float y22 = buffer.getFloat();	// byt ut mot lämplig
						        			float t1 = buffer.getFloat();	// byt ut mot lämplig

						        			_deviceManager.devicePointMappingStep2(ip, x11, y11, x22, y22, t1, deltaTime);
						        			
						        			_internalState = LOCAL_STATE.MAPPING_STEP1;
						        			System.out.println("MAPPING_STEP2 DONE");
							        	break;
						        		
						        		default:
						        		break;
					        		}
					        	break;
					        	
			        			case RUN_DEVICE:
			        			break;    			

				        		
				        		default:
				        		break;
			        		}
			        		// ----------------------------------------------------------------------------
			        		
			        		
			        		
			        	}
			        	
			        } catch(Exception e) {
						e.printStackTrace();
					}
			    } 
			  
			};
			executer.start();
		}
		
	}
	
} 