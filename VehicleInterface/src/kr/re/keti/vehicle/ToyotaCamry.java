package kr.re.keti.vehicle;


public class ToyotaCamry implements VehicleDataAnalyzer
{
	
    public ToyotaCamry()
    {
        working = false;
        dataContainer = new VehicleDataContainer();
        logger = null;
    }

    public ToyotaCamry(VehicleCommunicator comm)
    {
        this();

        open(comm);
    }

    public void setLogger(Logger l)
    {
        logger = l;
    }

    public final void open(VehicleCommunicator comm)
    {
        communicator = comm;
    }

    public void close()
    {
        
    }

    public VehicleDataContainer getVehicleDataContainer()
    {
        return dataContainer;
    }

    private void initThread()
    {
        monitoringThread = new Thread()
        {
            @Override
            public void run()
            {

                String[] receivedData = null;
                
                while(!isInterrupted())
                {
                    if(!working)
                    {
                    	System.out.print("[camry] initThread not working\r");

                        break;
                    }

                    try
                    {
                        if(communicator.available() < leastAvailable)
                        {
                            try
                            {
                                Thread.sleep(waitingTimeForAvailable);
                            }
                            catch(Exception e)
                            {
                                continue;
                            }

                            continue;
                        }
                        receivedData = communicator.getData(); //CANID + CANDATA
                        
                    }
                    catch(Exception e)
                    {

                        try
                        {
                            
                            communicator.ready();
                        }
                        catch(Exception ee)
                        {
                            if(logger != null)
                            {
                                logger.log("[ToyotaCamry] " + ee.toString());
                            }

                            continue;
                        }

                        continue;
                    }


                    if(receivedData != null)
                    {
                        if(logger != null)
                        {
                            int rdLength = receivedData.length;

                            String rd = receivedData[0];

                            for(int i = 1; i < rdLength; ++ i)
                            {
                                rd = rd + " " + receivedData[i];
                            }

                            logger.log("[ToyotaCamry] Received data: "+ rd);
                        }
                        
                        String dataID = receivedData[dataIDIndex];		
                        int dlc = 0;
                        
                        try
                        {
                        	dlc = Integer.parseInt(receivedData[dlcIndex], hexaRadix);
                        }
                        catch(Exception e)
                        {
                        	continue;
                        }
                        
                        if(dlc < minimumDLCValue || dlc > maximumDLCValue || receivedData.length - dataStartIndex != dlc)
                        {
                        	continue;
                        }
                        
                        boolean correctDataStringSize = true;
                        
                        for(int i = dataStartIndex; i < receivedData.length; ++i)
                        {
                        	if(receivedData[i].length() != eachDataStringSize)
                        	{
                        		correctDataStringSize = false;
                        		
                        		break;
                        	}
                        }
                        
                        if(!correctDataStringSize)
                        {
                        	continue;
                        }
                        
                        if(dataID.equals(steeringWheelID))
                        {
                            final int steeringWheelLowerByteOffset = dataStartIndex+1;
                            final int steeringWheelHigherByteOffset = dataStartIndex;


                            if(receivedData.length > steeringWheelHigherByteOffset)
                            {
                                String steeringWheelHexaData = receivedData[steeringWheelHigherByteOffset] + receivedData[steeringWheelLowerByteOffset];

                                short steeringWheelDecimalData = 0;
                                steeringWheelDecimalData |= (Integer.parseInt(steeringWheelHexaData, hexaRadix) &0x0FFF);
                                
                                steeringWheelDecimalData  = (short)(steeringWheelDecimalData << 4);
                                                                
                                if(steeringWheelDecimalData >= 0)
                                {
                                    dataContainer.setSteeringWheelDirection(VehicleDataContainer.STEERING_LEFT);

                                    dataContainer.setSteeringWheelDegree((steeringWheelDecimalData / 16) * (1.556));
                                }
                                else
                                {
                                    dataContainer.setSteeringWheelDirection(VehicleDataContainer.STEERING_RIGHT);

                                    dataContainer.setSteeringWheelDegree((steeringWheelDecimalData / 16) * (-1.556));
                                }
                            }
                        }
                        else if(dataID.equals(speedID))
                        {
                            final int speedByteOffset = dataStartIndex + 2;

                            if(receivedData.length > speedByteOffset)
                            {
                                String speedHexaData = receivedData[speedByteOffset];

                                short speedDecimalData = Short.parseShort(speedHexaData, hexaRadix);

                                dataContainer.setSpeed(speedDecimalData);
                            }
                            
                        }
                        else if(dataID.equals(brakeID))
                        {
                            final int brakeByteOffset = dataStartIndex;

                            if(receivedData.length > brakeByteOffset)
                            {
                                final byte brakeOff = 0;

                                String brakeHexaData = receivedData[brakeByteOffset];

                                byte brakeData = (byte)(Byte.parseByte(brakeHexaData, hexaRadix) & 0xF0);

                                if(brakeData == brakeOff)
                                {
                                    dataContainer.setBrake(false);  //brake unset
                                }
                                else
                                {
                                    dataContainer.setBrake(true);	//brake set
                                }
                            }
                        }
                        else if(dataID.equals(gearStatusID))
                        {
                            final int gearStatusByteOffset = dataStartIndex + 5;

                            if(receivedData.length > gearStatusByteOffset)
                            {
                            	final byte gearParking = 0x0;
                                final byte gearReverse = 0x10;
                                final byte gearNeutral = 0x20;
                                final byte gearDrive = 0x30;

                                String gearStatusHexaData = receivedData[gearStatusByteOffset];

                                byte gearSelectionData = (byte)(Byte.parseByte(gearStatusHexaData, hexaRadix) & 0xF0);

                                
                                switch(gearSelectionData)
                                {
                                    case gearParking:
                                        dataContainer.setGearSelection(VehicleDataContainer.GEAR_PARKING);
                                        break;
                                    case gearReverse:
                                        dataContainer.setGearSelection(VehicleDataContainer.GEAR_REVERSE);
                                        break;
                                    case gearNeutral:
                                        dataContainer.setGearSelection(VehicleDataContainer.GEAR_NEUTRAL);
                                        break;
                                    case gearDrive:
                                        dataContainer.setGearSelection(VehicleDataContainer.GEAR_DRIVE);
                                        break;
                                }
                            }
                        }
                        else if(dataID.equals(parkingBreakID))
                        {
                            final int parkingBrakeByteOffset = dataStartIndex + 3;
                            
                            if(receivedData.length > parkingBrakeByteOffset)
                            {
                                String parkingBrakeHexaData = receivedData[parkingBrakeByteOffset];

                                byte parkingBrakeData = (byte)(Byte.parseByte(parkingBrakeHexaData, hexaRadix) & 0x0F);


                                if (parkingBrakeData == 0)
                                    dataContainer.setParkingBrake(false);	//parkingBrakeData 1: set, 0: unset
                                else
                                    dataContainer.setParkingBrake(true);	//parkingBrakeData 1: set, 0: unset


                            }
                        }
                        else if(dataID.equals(mileageID))
                        {
                        	final int mileageHigherByteOffset = dataStartIndex + 5;
                            final int mileageMiddleByteOffset = dataStartIndex + 6;
                            final int mileageLowerByteOffset = dataStartIndex + 7;

                            if(receivedData.length > mileageHigherByteOffset)
                            {
                                String mileageHexaData = receivedData[mileageHigherByteOffset] + receivedData[mileageMiddleByteOffset] + receivedData[mileageLowerByteOffset];

                                int mileageDecimalData = Integer.parseInt(mileageHexaData, hexaRadix);

                                dataContainer.setMileage(mileageDecimalData);
                            }
                        }
                        else if(dataID.equals(lightID))
                        {
                            
                        	final int lightByteOffset = dataStartIndex + 3;
                        	
                        	if (receivedData.length > lightByteOffset)
                        	{
                        		final byte lightOff = 0x0;
                        		final byte lightOnWeak = 0x10;
                        		final byte lightOnLow = 0x30;
                        		final byte lightOnHigh = 0x60;
                        		final byte lightHighBeam = 0x70; 
                        		String lightHexData = receivedData[lightByteOffset];
                        		
                        		byte lightByteData = (byte)(Byte.parseByte(lightHexData, hexaRadix) & 0xF0);
                        		
                        		switch (lightByteData)
                        		{
                        		case lightOff:
                        			dataContainer.setLight(VehicleDataContainer.LIGHT_OFF);
                        			break;
                        		case lightOnWeak:
                        			dataContainer.setLight(VehicleDataContainer.LIGHT_WEAK);
                        			break;
                        		case lightOnLow:
                        			dataContainer.setLight(VehicleDataContainer.LIGHT_LOW);
                        			break;
                        		case lightOnHigh:
                        			dataContainer.setLight(VehicleDataContainer.LIGHT_HIGH);
                        			break;
                        		case lightHighBeam:
                        			dataContainer.setLight(VehicleDataContainer.LIGHT_HIGHBEAM);
                        			break;
                        		}
                        	}                  	
                        	
                        	
                        }
                       else if(dataID.equals(rpmID))
                        {
                    	   final int rpmLowerByteOffset = dataStartIndex+1;
                           final int rpmHigherByteOffset = dataStartIndex;

                           if(receivedData.length > rpmHigherByteOffset)
                           {
                               String rpmHexaData = receivedData[rpmHigherByteOffset] + receivedData[rpmLowerByteOffset];

                               short rpmDecimalData = Short.parseShort(rpmHexaData, hexaRadix);

                               dataContainer.setEngineRevolution((short)(rpmDecimalData));
                           }
                        }
                        else if(dataID.equals(directionID))
                        {
                            final int directionByteOffset = dataStartIndex + 3;

                            if(receivedData.length > directionByteOffset)
                            {
                                final byte directionOff = 0x30;
                                final byte directionLeft = 0x10;
                                final byte directionRight = 0x20;
                                final byte directionEmergency = 0x38;

                                String directionHexaData = receivedData[directionByteOffset];

                                byte directionData = (byte)(Byte.parseByte(directionHexaData, hexaRadix) & 0xFF);

                                switch(directionData)
                                {
                                    case directionOff:
                                        dataContainer.setDirectionIndicator(VehicleDataContainer.DIRECTION_OFF);
                                        break;
                                    case directionLeft:
                                        dataContainer.setDirectionIndicator(VehicleDataContainer.DIRECTION_LEFT);
                                        break;
                                    case directionRight:
                                        dataContainer.setDirectionIndicator(VehicleDataContainer.DIRECTION_RIGHT);
                                        break;
                                    case directionEmergency:
                                        dataContainer.setDirectionIndicator(VehicleDataContainer.DIRECTION_EMERGENCY);
                                        break;
                                }

                            }
                        }
                    }
                }
            }
        };
    }

    public void startAnalyze()
    {
        if(logger != null)
        {
            logger.log("[ToyotaCamry] start analyze");
        }

        initThread();

        working = true;

        monitoringThread.start();

        if(logger != null)
        {
            logger.log("[ToyotaCamry] start analyze: monitoring thread start");
        }

        try
        {
            communicator.ready();
        }
        catch(Exception e)
        {
            if(logger != null)
            {
                logger.log(e.toString());
            }
        }

        if(logger != null)
        {
            logger.log("[ToyotaCamry] start analyze: interface module ready");
        }

        if(logger != null)
        {
            logger.log("[ToyotaCamry] start analyze: success");
        }
    }

    public void stopAnalyze()
    {
        if(logger != null)
        {
            logger.log("[ToyotaCamry] stop analyze");
        }

        monitoringThread.interrupt();

        working = false;

        if(logger != null)
        {
            logger.log("[ToyotaCamry] stop analyze: monitoring thread interrupted");
        }
        
        try
        {
            monitoringThread.join();
        }
        catch(Exception e)
        {
            if(logger != null)
            {
                logger.log("[ToyotaCamry] " + e.toString());
            }
        }

        if(logger != null)
        {
            logger.log("[ToyotaCamry] stop analyze: monitoring thread joined");
        }

        if(logger != null)
        {
            logger.log("[ToyotaCamry] stop analyze: success");
        }
    }

    public boolean isWorking()
    {
        return working;
    }

   
    /* ----------- for Toyota Camry ---------*/
    private final String steeringWheelID = "025"; 
    private final String speedID = "610";
    private final String rpmID = "1C4";
    private final String brakeID = "224";
    private final String gearStatusID = "127";
    private final String parkingBreakID = "230";
    private final String mileageID = "611";
    private final String directionID = "614";
    private final String lightID = "622";


    private final int leastAvailable = 1;
    private final long waitingTimeForAvailable = 100;

    private final int dataIDIndex = 0;
	private final int dlcIndex = 1;
    private final int minimumDLCValue = 1;
    private final int maximumDLCValue = 8;
    private final int dataStartIndex = 2;
    private final int eachDataStringSize = 2;

    private final int hexaRadix = 16;

    private VehicleCommunicator communicator;
    private VehicleDataContainer dataContainer;

    private Thread monitoringThread;

    private boolean working;

    private Logger logger;

}
