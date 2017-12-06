package kr.re.keti.vehicle;

/**
 * Stores and maintains vehicle status data.
 */
public class VehicleDataContainer
{
    public VehicleDataContainer()
    {
        acceleratorPedal = 0;
        coolant = 0;
        steeringWheelDirection = false;
        steeringWheelDegree = 0;
        speed = 0;
        brake = false;
        gearSelection = GEAR_DISABLED;
        gearValue = GEAR_NEUTRAL;
        engineRevolution = 0;
        parkingBrake = false;
        mileage = 0;
        batteryVoltage = 0;
        directionIndicator = DIRECTION_OFF;
        keySelection = KEY_REMOVED;
        seatBelt = false;
        fuelConsumption = 0;
        engineLoad = 0;
        door = DOOR_CLOSED;
        light = LIGHT_OFF;
    }

    public void setLoadValue(short load)
    {
        engineLoad = load;
    }

    public short getLoadValue()
    {
        return engineLoad;
    }
    
    public void setLight(short l)
    {
    	light = l;
    }
    
    public short getLight()
    {
    	return light;
    }
    
    public void setDoor(boolean d)
    {
    	door = d;
    }
    
    public boolean getDoor()
    {
    	return door;
    }

    public void setFuelConsumption(short fuel)
    {
        fuelConsumption = fuel;
    }

    public short getFuelConsumption()
    {
        return fuelConsumption;
    }

    public void setAcceleratorPedal(short accel)
    {
        acceleratorPedal = accel;
    }

    public short getAcceleratorPedal()
    {
        return acceleratorPedal;
    }

    public void setCoolant(short c)
    {
        coolant = c;
    }

    public short getCoolant()
    {
        return coolant;
    }

    public void setSteeringWheelDirection(boolean d)
    {
        steeringWheelDirection = d;
    }

    public boolean getSteeringWheelDirection()
    {
        return steeringWheelDirection;
    }

    public void setSteeringWheelDegree(double d)
    {
    	//System.out.print ("set steeringwheel: " +d+"\r");

        steeringWheelDegree = d;
    }

    public short getSteeringWheelDegree()
    {
    	//System.out.print ("[data container]get steeringwheel :" +steeringWheelDirection +"\r");

    	if (steeringWheelDirection)
    		return (short)(steeringWheelDegree - 0.5);	//Rounding operation
    	else
    		return (short)(steeringWheelDegree + 0.5);	//Rounding operation

    }

    public void setSpeed(short s)
    {
        speed = s;
    }

    public short getSpeed()
    {
        return speed;
    }

    public void setBrake(boolean b)
    {
        brake = b;
    }

    public boolean getBrake()
    {
        return brake;
    }

    public void setGearSelection(byte g)
    {
        gearSelection = g;
    }

    public byte getGearSelection()
    {
        return gearSelection;
    }

    public void setGearValue(byte g)
    {
        gearValue = g;
    }

    public byte getGearValue()
    {
        return gearValue;
    }

    public void setEngineRevolution(short r)
    {
        engineRevolution = r;
    }

    public short getEngineRevolution()
    {
        return engineRevolution;
    }

    public void setParkingBrake(boolean b)
    {
        parkingBrake = b;
    }

    public boolean getParkingBrake()
    {
        return parkingBrake;
    }

    public void setMileage(int m)
    {
        mileage = m;
    }

    public int getMileage()
    {
        return mileage;
    }

    public void setBatteryVoltage(short v)
    {
        batteryVoltage = v;
    }

    public short getBatteryVoltage()
    {
        return batteryVoltage;
    }

    public void setDirectionIndicator(byte i)
    {
        directionIndicator = i;
    }

    public byte getDirectionIndicator()
    {
        return directionIndicator;
    }

    public void setKeySelection(byte k)
    {
        keySelection = k;
    }

    public byte getKeySelection()
    {
        return keySelection;
    }

    public void setSeatBelt(boolean s)
    {
        seatBelt = s;
    }

    public boolean getSeatBelt()
    {
        return seatBelt;
    }

    private short acceleratorPedal;
    private short coolant;
    private boolean steeringWheelDirection;
    private double steeringWheelDegree;
    private short speed;
    private boolean brake;
    private byte gearSelection;
    private byte gearValue;
    private short engineRevolution;
    private boolean parkingBrake;
    private int mileage;
    private short batteryVoltage;
    private byte directionIndicator;
    private byte keySelection;
    private boolean seatBelt;
    private short fuelConsumption;
    private short engineLoad;
    private boolean door;
    private short light;
    
    public final static short LIGHT_OFF = 0;
    public final static short LIGHT_ON = 1;
    public final static short LIGHT_WEAK = 3;
    public final static short LIGHT_LOW = 4;
    public final static short LIGHT_HIGH = 2;
    public final static short LIGHT_HIGHBEAM = 5;

    public final static boolean STEERING_LEFT = false;
    public final static boolean STEERING_RIGHT = true;
    
    public final static boolean DOOR_CLOSED = false;
    public final static boolean DOOR_OPENED = true;

    public final static byte GEAR_PARKING = 8;
    public final static byte GEAR_REVERSE = 7;
    public final static byte GEAR_NEUTRAL = 0;
    public final static byte GEAR_DRIVE = 9;
    public final static byte GEAR_MANUAL = 10;
    public final static byte GEAR_DISABLED = 11;

    public final static byte DIRECTION_OFF = 0;
    public final static byte DIRECTION_LEFT = 1;
    public final static byte DIRECTION_RIGHT = 2;
    public final static byte DIRECTION_EMERGENCY = 3;

    public final static byte KEY_REMOVED = 0;
    public final static byte KEY_LOCK = 1;
    public final static byte KEY_ACC = 2;
    public final static byte KEY_ON = 3;
    public final static byte KEY_START = 4;
}
