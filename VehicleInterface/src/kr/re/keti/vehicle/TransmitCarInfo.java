package kr.re.keti.vehicle;

//import gnu.io.PortInUseException;



/*  Return CAMRY ECU information as a designated format. 
 * 
 * format:  {{ steering wheel (2bytes),
 *   speed (1byte),
 *   rpm (2bytes),
 *   break  (1byte),
 *   gear status (1byte),
 *   parking brake  (1byte),
 *   total mileage (3bytes),
 *   light  (1byte),
 *   direction (1byte) }}
 * 
 */


public class TransmitCarInfo  {

	//static public CarDataList CarDataList;
	
	public static byte [] carEcuInfoByteArr;

	private static ToyotaCamry vehicleCamry; //for Camry
//	private static AvanteMD vehicleAvante;	//for test
	private static VehicleDataContainer dataContainer; 
	private ELM327 ifModule;
	
	
	public TransmitCarInfo (ELM327 ifModule) {
		
		dataContainer = null;
		carEcuInfoByteArr = new byte [numberOfEcuInfo];
		this.ifModule = ifModule;
		

		makeVehicleDataContainer();
	}
	
	public TransmitCarInfo () {
		
		dataContainer = null;
		carEcuInfoByteArr = new byte [numberOfEcuInfo];
		ifModule = null;
		
	}
	
	public void connectELM327 (ELM327 ifModule)	{
		this.ifModule = ifModule;
		makeVehicleDataContainer();
	}
	
	private void makeVehicleDataContainer( )	{
		
		/* for Camry */ 
		vehicleCamry = new ToyotaCamry(ifModule);
		
		dataContainer = vehicleCamry.getVehicleDataContainer();
		vehicleCamry.startAnalyze();
		

		

		 /* for test  
		vehicleAvante = new AvanteMD(ifModule);
		
		dataContainer = vehicleAvante.getVehicleDataContainer();
		
		vehicleAvante.startAnalyze();
	*/
	}

	
	public void setCarEcuInfo()	{
		
		int tempIntForShift = 0;

		
		//steering wheel section  //test ok
		if (dataContainer.getSteeringWheelDirection())	{
			carEcuInfoByteArr [steeringWheelDirectionOffset] = right;	//right
			CarDataList.steeringWheelDirection = right;
		}
		else	{
			carEcuInfoByteArr [steeringWheelDirectionOffset] = left;	//left
			CarDataList.steeringWheelDirection = left;
		}

		
		
		//System.out.print("\rSteering Wheel direction " + carEcuInfoByteArr [steeringWheelDirectionOffset]);
		
		tempIntForShift = dataContainer.getSteeringWheelDegree() >> 8;
		carEcuInfoByteArr [steeringWheelOffsetLow] = (byte)tempIntForShift ;
		carEcuInfoByteArr [steeringWheelOffsetHigh] = (byte)dataContainer.getSteeringWheelDegree() ;
		
		//System.out.print("\r[TCI] Steering Wheel degree " +  dataContainer.getSteeringWheelDegree() +"   "+ tempIntForShift +" "+ carEcuInfoByteArr [steeringWheelOffsetHigh]);
		
		
		//steeringWheelAngle = dataContainer.getSteeringWheelDegree(); //temporal
		
		//speed section //test ok
		carEcuInfoByteArr [speedOffset] = (byte)dataContainer.getSpeed() ;
	//	System.out.print("\r  speed " +  dataContainer.getSpeed());
		
		//rpm section
		tempIntForShift = dataContainer.getEngineRevolution() >> 8;
		carEcuInfoByteArr [rpmOffsetLow] = (byte)tempIntForShift ;
		carEcuInfoByteArr [rpmOffsetHigh] = (byte)dataContainer.getEngineRevolution() ;
	//	System.out.print("\r  RPM " +  tempIntForShift +" "+ carEcuInfoByteArr [rpmOffsetHigh]);
		
//		rpmValue = dataContainer.getEngineRevolution();
		
		
		//brake section //test ok
		if (dataContainer.getBrake())
			carEcuInfoByteArr [breakOffset] = set;	//set
		else
			carEcuInfoByteArr [breakOffset] = unset;	//unset
		
	//	System.out.print("\rBrake " + carEcuInfoByteArr [breakOffset]);

		
		//gear status section //test ok
		carEcuInfoByteArr [gearStatusOffset] = dataContainer.getGearSelection() ;
	//	System.out.print("\r Gear " + carEcuInfoByteArr [gearStatusOffset]);

		
		//parking brake section //test ok
		if (dataContainer.getParkingBrake() )
			carEcuInfoByteArr [parkingBrakeOffset] = set;		//set
		else
			carEcuInfoByteArr [parkingBrakeOffset] = unset;		//unset

	//	System.out.print("\r Parking " + carEcuInfoByteArr [parkingBrakeOffset]);

		
		//total mileage section
		tempIntForShift = dataContainer.getMileage() >> 16;
		carEcuInfoByteArr [totalMileageOffsetLow] = (byte)tempIntForShift ;
		tempIntForShift = dataContainer.getMileage() >> 8;
		carEcuInfoByteArr [totalMileageOffsetMiddle] = (byte)tempIntForShift ;
		carEcuInfoByteArr [totalMileageOffsetHigh] = (byte)dataContainer.getMileage() ;
		
	/*	System.out.print ("\r "+dataContainer.getMileage());
		System.out.format("  mileage %X = %02X %02X %02X",dataContainer.getMileage() ,carEcuInfoByteArr [totalMileageOffsetLow] 
				 , carEcuInfoByteArr [totalMileageOffsetMiddle] ,carEcuInfoByteArr [totalMileageOffsetHigh]);
*/
		//totalMileage = dataContainer.getMileage();
	/*	if (CarDataList.mileage == 123456)
			CarDataList.mileage = 654321;//dataContainer.getMileage();
		else 
			CarDataList.mileage = 123456;
			*/
		//light section //
		carEcuInfoByteArr [lightOffset] = (byte)dataContainer.getLight() ;
//		System.out.print("\r ligh " + carEcuInfoByteArr [lightOffset]);

		//direction section //test ok
		carEcuInfoByteArr [directionOffset] = dataContainer.getDirectionIndicator() ;
		//System.out.print("\r direction " + carEcuInfoByteArr [directionOffset] + "\r");

		
		
	/*	for (int nLoop = steeringWheelDirectionOffset; nLoop <= directionOffset; nLoop ++)	{
			System.out.print(carEcuInfoByteArr [nLoop] + " " );
		}
		*/
		//System.out.print("\r");
	}

	
	public String getCarEcuInfo()	{
		
		StringBuilder CarInfoByteToString = new StringBuilder(carEcuInfoByteArr.length * 2);
        for(byte b: carEcuInfoByteArr)
        	CarInfoByteToString.append(String.format("%02x", b & 0xff));
              
	//	for (int nLoop = 0; nLoop < carEcuInfoByteArr.length; nLoop ++) 	//for TEST and matching
	//		System.out.format (" %02X", carEcuInfoByteArr [nLoop]);
	        
        return CarInfoByteToString.toString();
		
	}
	
	
	/* for test. translate string to hex per 1 byte */ 
	public static String stringToHex(String s) {
	    String result = "";

	    for (int i = 0; i < s.length(); i++) {
	      result += String.format("%02X ", (int) s.charAt(i));
		//	System.out.format("\r %02S", result);

	    }
	  	    
	    return result;
	  }

	
	/*  Good working : byte array to hex-string
	 * 
	 *  
	static public String toHexString(byte[] array, int startPos, int length)
	{
		final int singleByteStringLength = 2;
		
		if(array == null)
		{
			return null;
		}
		
		if(startPos >= array.length || startPos + length > array.length)
		{
			return null;
		}
		
		StringBuffer strBuf = new StringBuffer(length * singleByteStringLength);
		
		for(int i = startPos; i < startPos + length; ++i)
		{
			String currentByte = (((array[i] & 0xFF) < 0x10)?("0"):("")) + Integer.toHexString(array[i] & 0xFF).toUpperCase();
			
			strBuf.append(currentByte);
		}
		
		return strBuf.toString();
	}
	
	static public String toHexString(byte[] array)
	{
		return toHexString(array, 0, array.length);
	}
	*/
	
	/* for TEST */
	 
	public byte getSteeringWheelDirection ()	{
		return carEcuInfoByteArr [steeringWheelDirectionOffset];
	}
	public byte getSpeed ()	{
		return carEcuInfoByteArr [speedOffset];
	}
	public byte getBrake ()	{
		return carEcuInfoByteArr [breakOffset];
	}
	public byte getGearStatus ()	{
		return carEcuInfoByteArr [gearStatusOffset];
	}
	public byte getParkingBrake ()	{
		return carEcuInfoByteArr [parkingBrakeOffset];
	}
	public byte getLight ()	{
		return carEcuInfoByteArr [lightOffset];
	}
	public byte getDirection ()	{
		return carEcuInfoByteArr [directionOffset];
	}
	public int getTotalMileage ()	{
		return totalMileage;
	}
	public short getSteeringWheel()	{
		return steeringWheelAngle;
	}
	public short getRpmValue	()	{
		return rpmValue;
	}
	
	private static short rpmValue;
	private static short steeringWheelAngle;
	private static int totalMileage;
	
	

	private final static int numberOfEcuInfo = 14;
	
	private final static byte set = 1; 
	private final static byte unset = 0;
	private final static byte right = 1;
	private final static byte left = 0;
	
	private final static int steeringWheelDirectionOffset = 0;
	private final static int steeringWheelOffsetLow = 1;
	private final static int steeringWheelOffsetHigh = 2;
	private final static int speedOffset = 3;
	private final static int rpmOffsetLow = 4;
	private final static int rpmOffsetHigh = 5;
	private final static int breakOffset = 6;
	private final static int gearStatusOffset = 7;
	private final static int parkingBrakeOffset = 8;
	private final static int totalMileageOffsetLow =9; 
	private final static int totalMileageOffsetMiddle =10;
	private final static int totalMileageOffsetHigh = 11;
	private final static int lightOffset = 12;
	private final static int directionOffset = 13;	

	
	
}