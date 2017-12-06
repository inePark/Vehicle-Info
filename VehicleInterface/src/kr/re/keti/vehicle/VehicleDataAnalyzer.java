package kr.re.keti.vehicle;

/**
 * Analyzes the vehicle status data provided through OBD-II.
 */
public interface VehicleDataAnalyzer
{
    /**
     * Initializes the analyzer object.
     * @param comm
     * VehicleCommunicator object that would be used for accessing specific OBD-II interface(interpreter) module
     */
    public void open(VehicleCommunicator comm);

    /**
     * Closes the interface channel and releases the resources.
     */
    public void close();

    /**
     * Begins to analyze the vehicle status data obtained by the interface module and stores them in the VehicleDataContainer object.
     */
    public void startAnalyze();

    /**
     * Stops the analyzing process.
     */
    public void stopAnalyze();

    /**
     * Informs whether this object is analyzing the vehicle status data.
     * @return
     * True when the analyzing process is working, and false if not
     */
    public boolean isWorking();

    /**
     * Returns the VehicleDataContainer object that has the analyzed vehicle status data.
     * @return
     * VehicleDataContainer object
     */
    public VehicleDataContainer getVehicleDataContainer();
}
