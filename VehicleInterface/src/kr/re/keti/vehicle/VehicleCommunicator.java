package kr.re.keti.vehicle;

/**
 * Accesses the OBD-II interface module.
 */
public interface VehicleCommunicator
{
    /**
     * Reads and returns data from the OBD-II interface module.
     * @return
     * An array of data that is obtained through the interface module (the first index of data means CAN ID, the second is DLC(Data Length Counter), and the others are actual CAN data)
     * @throws java.io.IOException
     * @throws VehicleCommunicatorException
     */
    public String[] getData() throws java.io.IOException, VehicleCommunicatorException;

    /**
     * Sends data to the OBD-II interface module.
     * @param id
     * CAN ID
     * @param data
     * User's data
     * @throws java.io.IOException
     */
    public void sendData(short id, short[] data) throws java.io.IOException;

    /**
     * Makes the OBD-II interface module to be ready to monitor the data provided from the vehicle.
     * @throws java.io.IOException
     */
    public void ready() throws java.io.IOException;
    
    /**
     * Stops the OBD-II interface module from monitoring the vehicle status.
     * @throws java.io.IOException
     */
    public void stop() throws java.io.IOException;

    /**
     * Returns an available data length in the stream.
     * @return the available data length
     * @throws java.io.IOException
     */
    public int available() throws java.io.IOException;
}
