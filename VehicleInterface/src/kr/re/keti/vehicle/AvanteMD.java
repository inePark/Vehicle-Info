package kr.re.keti.vehicle;

public class AvanteMD implements VehicleDataAnalyzer
{
    public AvanteMD()
    {
        working = false;
        dataContainer = new VehicleDataContainer();
        logger = null;
    }

    public AvanteMD(VehicleCommunicator comm)
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
                        receivedData = communicator.getData();
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
                                logger.log("[AvanteMD] " + ee.toString());
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

                            logger.log("[AvanteMD] Received data: "+ rd);
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
                        
                        if(dataID.equals(accelID))
                        {
                            final int accelByteOffset = dataStartIndex;
                            final int maxAccelValue = 0xFE;

                            if(receivedData.length > accelByteOffset)
                            {
                                String accelHexaData = receivedData[accelByteOffset];

                                short accelDecimalData = Short.parseShort(accelHexaData, hexaRadix);

                                dataContainer.setAcceleratorPedal((short)(accelDecimalData * 100 / maxAccelValue));
                            }
                        }
                        else if(dataID.equals(coolantID))
                        {
                            final int coolantByteOffset = dataStartIndex + 3;

                            if(receivedData.length > coolantByteOffset)
                            {
                                String coolantHexaData = receivedData[coolantByteOffset];

                                short coolantDecimalData = Short.parseShort(coolantHexaData, hexaRadix);
                                coolantDecimalData = (short)((coolantDecimalData - 32) * 10 / 18);

                                dataContainer.setCoolant(coolantDecimalData);
                            }
                        }
                        else if(dataID.equals(steeringWheelID))
                        {
                            final int steeringWheelLowerByteOffset = dataStartIndex;
                            final int steeringWheelHigherByteOffset = dataStartIndex + 1;

                            if(receivedData.length > steeringWheelHigherByteOffset)
                            {
                                String steeringWheelHexaData = receivedData[steeringWheelHigherByteOffset] + receivedData[steeringWheelLowerByteOffset];

                                short steeringWheelDecimalData = 0;
                                steeringWheelDecimalData |= Integer.parseInt(steeringWheelHexaData, hexaRadix);

                                
                                if(steeringWheelDecimalData >= 0)
                                {
                                    dataContainer.setSteeringWheelDirection(VehicleDataContainer.STEERING_LEFT);
                                    dataContainer.setSteeringWheelDegree(steeringWheelDecimalData);
                                }
                                else
                                {
                                    dataContainer.setSteeringWheelDirection(VehicleDataContainer.STEERING_RIGHT);
                                    dataContainer.setSteeringWheelDegree((short)(steeringWheelDecimalData * (-1)));
                                }
                            }
                        }
                        else if(dataID.equals(engineID))
                        {
                            final int speedByteOffset = dataStartIndex + 6;
                            final int rpmLowerByteOffset = dataStartIndex + 2;
                            final int rpmHigherByteOffset = dataStartIndex + 3;

                            if(receivedData.length > speedByteOffset)
                            {
                                String speedHexaData = receivedData[speedByteOffset];
                                String rpmHexaData = receivedData[rpmHigherByteOffset] + receivedData[rpmLowerByteOffset];

                                short speedDecimalData = Short.parseShort(speedHexaData, hexaRadix);
                                short rpmDecimalData = Short.parseShort(rpmHexaData, hexaRadix);

                                dataContainer.setSpeed(speedDecimalData);
                                dataContainer.setEngineRevolution((short)(rpmDecimalData / 4));
                            }
                        }
                        else if(dataID.equals(brakeID))
                        {
                            final int brakeByteOffset = dataStartIndex + 4;

                            if(receivedData.length > brakeByteOffset)
                            {
                                final byte brakeOff = 1;

                                String brakeHexaData = receivedData[brakeByteOffset];

                                byte brakeData = (byte)(Byte.parseByte(brakeHexaData, hexaRadix) & 0x03);

                                if(brakeData == brakeOff)
                                {
                                    dataContainer.setBrake(false);
                                }
                                else
                                {
                                    dataContainer.setBrake(true);
                                }
                            }
                        }
                        else if(dataID.equals(transmissionID))
                        {
                            final int gearByteOffset = dataStartIndex;
                            final int gearSelectionByteOffset = dataStartIndex + 1;

                            
                          
                            if(receivedData.length > gearSelectionByteOffset)
                            {
                                final byte gearDisabled = 0x0F;
                                final byte gearParking = 0x00;
                                final byte gearReverse = 0x07;
                                final byte gearNeutral = 0x06;
                                final byte gearDrive = 0x05;
                                final byte gearManual = 0x08;

                                String gearHexaData = receivedData[gearByteOffset];
                                String gearSelectionHexaData = receivedData[gearSelectionByteOffset];

                                byte gearData = (byte)(Byte.parseByte(gearHexaData, hexaRadix) & 0x07);
                                byte gearSelectionData = (byte)(Byte.parseByte(gearSelectionHexaData, hexaRadix) & 0x0F);

                                dataContainer.setGearValue(gearData);

                                switch(gearSelectionData)
                                {
                                    case gearDisabled:
                                        dataContainer.setGearSelection(VehicleDataContainer.GEAR_DISABLED);
                                        break;
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
                                    case gearManual:
                                        dataContainer.setGearSelection(VehicleDataContainer.GEAR_MANUAL);
                                        break;
                                }
                            }
                        }
                        else if(dataID.equals(drivingStatusID))
                        {
                            final int parkingBrakeByteOffset = dataStartIndex;
                            final int mileageHigherByteOffset = dataStartIndex + 7;
                            final int mileageMiddleByteOffset = dataStartIndex + 6;
                            final int mileageLowerByteOffset = dataStartIndex + 5;

                            if(receivedData.length > mileageHigherByteOffset)
                            {
                                String parkingBrakeHexaData = receivedData[parkingBrakeByteOffset];
                                String mileageHexaData = receivedData[mileageHigherByteOffset] + receivedData[mileageMiddleByteOffset] + receivedData[mileageLowerByteOffset];

                                int mileageDecimalData = Integer.parseInt(mileageHexaData, hexaRadix);

                                dataContainer.setMileage(mileageDecimalData);

                                byte parkingBrakeData = 0;
                                parkingBrakeData |= Integer.parseInt(parkingBrakeHexaData, hexaRadix);
                                parkingBrakeData &= 0x80;

                                dataContainer.setParkingBrake(parkingBrakeData != 0);
                            }
                        }
                        else if(dataID.equals(batteryID))
                        {
                            final int batteryByteOffset = dataStartIndex + 3;

                            if(receivedData.length > batteryByteOffset)
                            {
                                String batteryHexaData = receivedData[batteryByteOffset];

                                short batteryDecimalData = Short.parseShort(batteryHexaData, hexaRadix);

                                dataContainer.setBatteryVoltage(batteryDecimalData);
                            }
                        }
                        else if(dataID.equals(fuelID))
                        {
                            final int fuelConsumptionByteOffset = dataStartIndex + 2;

                            if(receivedData.length > fuelConsumptionByteOffset)
                            {
                                String fuelConsumptionHexaData = receivedData[fuelConsumptionByteOffset];

                                short fuelConsumptionDecimalData = Short.parseShort(fuelConsumptionHexaData, hexaRadix);

                                dataContainer.setFuelConsumption(fuelConsumptionDecimalData);
                            }
                        }
                        else if(dataID.equals(loadID))
                        {
                            final int loadByteOffset = dataStartIndex;

                            if(receivedData.length > loadByteOffset)
                            {
                                String cvlHexaData = receivedData[loadByteOffset];

                                short loadDecimalData = Short.parseShort(cvlHexaData, hexaRadix);
                                
                                loadDecimalData = (short)(loadDecimalData * 100 / 0xFF);

                                dataContainer.setLoadValue(loadDecimalData);
                            }
                        }
                        else if(dataID.equals(convenienceID))
                        {
                            final int directionByteOffset = dataStartIndex + 3;
                            final int keyByteOffset = dataStartIndex;
                            final int seatBeltByteOffset = dataStartIndex + 2;

                            if(receivedData.length > directionByteOffset)
                            {
                                final byte directionOff = 0x00;
                                final byte directionLeft = 0x01;
                                final byte directionRight = 0x02;
                                final byte directionEmergency = 0x03;

                                final byte keyRemoved = 0x00;
                                final byte keyLock = 0x01;
                                final byte keyAcc = 0x02;
                                final byte keyOn = 0x03;
                                final byte keyStart = 0x04;

                                String directionHexaData = receivedData[directionByteOffset];
                                String keyHexaData = receivedData[keyByteOffset];
                                String seatBeltHexaData = receivedData[seatBeltByteOffset];

                                byte directionData = (byte)(Byte.parseByte(directionHexaData, hexaRadix) & 0x03);
                                byte keyData = (byte)(Byte.parseByte(keyHexaData, hexaRadix) & 0x07);
                                byte seatBeltData = (byte)(Byte.parseByte(seatBeltHexaData, hexaRadix) & 0x01);

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

                                switch(keyData)
                                {
                                    case keyRemoved:
                                        dataContainer.setKeySelection(VehicleDataContainer.KEY_REMOVED);
                                        break;
                                    case keyLock:
                                        dataContainer.setKeySelection(VehicleDataContainer.KEY_LOCK);
                                        break;
                                    case keyAcc:
                                        dataContainer.setKeySelection(VehicleDataContainer.KEY_ACC);
                                        break;
                                    case keyOn:
                                        dataContainer.setKeySelection(VehicleDataContainer.KEY_ON);
                                        break;
                                    case keyStart:
                                        dataContainer.setKeySelection(VehicleDataContainer.KEY_START);
                                        break;
                                }

                                dataContainer.setSeatBelt(seatBeltData == 0);
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
            logger.log("[AvanteMD] start analyze");
        }

        initThread();

        working = true;

        monitoringThread.start();

        if(logger != null)
        {
            logger.log("[AvanteMD] start analyze: monitoring thread start");
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
            logger.log("[AvanteMD] start analyze: interface module ready");
        }

        if(logger != null)
        {
            logger.log("[AvanteMD] start analyze: success");
        }
    }

    public void stopAnalyze()
    {
        if(logger != null)
        {
            logger.log("[AvanteMD] stop analyze");
        }

        monitoringThread.interrupt();

        working = false;

        if(logger != null)
        {
            logger.log("[AvanteMD] stop analyze: monitoring thread interrupted");
        }
        
        try
        {
            monitoringThread.join();
        }
        catch(Exception e)
        {
            if(logger != null)
            {
                logger.log("[AvanteMD] " + e.toString());
            }
        }

        if(logger != null)
        {
            logger.log("[AvanteMD] stop analyze: monitoring thread joined");
        }

        if(logger != null)
        {
            logger.log("[AvanteMD] stop analyze: success");
        }
    }

    public boolean isWorking()
    {
        return working;
    }

    private final String accelID = "080";
    private final String coolantID = "280";
    private final String steeringWheelID = "2B0";
    private final String engineID = "316";
    private final String brakeID = "329";
    private final String transmissionID = "43F";
    private final String drivingStatusID = "4F0";
    private final String batteryID = "545";
    private final String convenienceID = "690";
    private final String fuelID = "18F";
    private final String loadID = "0A0";

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
