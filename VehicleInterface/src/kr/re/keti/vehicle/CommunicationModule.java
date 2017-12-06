package kr.re.keti.vehicle;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Makes a communication channel and extracts input/output streams from them through which the VehicleCommunicator objects can communicate with the OBD-II interface(interpreter) hardware module.
 */
public interface CommunicationModule
{
    /**
     * Returns the InputStream object.
     * @return
     * InputStream object
     */
    public InputStream getInputStream();

    /**
     * Returns the OutputStream object.
     * @return
     * OutputStream object.
     */
    public OutputStream getOutputStream();
}
