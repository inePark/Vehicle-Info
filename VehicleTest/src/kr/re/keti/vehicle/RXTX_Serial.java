package kr.re.keti.vehicle;

import gnu.io.PortInUseException;
import gnu.io.RXTXPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.InputStream;
import java.io.OutputStream;



public class RXTX_Serial implements CommunicationModule
{
    public void open(String portName) throws PortInUseException
    {
        port = new RXTXPort(portName);
    }

    public void setSpeed(int portSpeed) throws UnsupportedCommOperationException
    {
        port.setSerialPortParams(portSpeed, RXTXPort.DATABITS_8, RXTXPort.STOPBITS_1, RXTXPort.PARITY_NONE);
    }

    public void close() throws Exception
    {
        port.close();
    }

    public InputStream getInputStream()
    {
        return port.getInputStream();
    }

    public OutputStream getOutputStream()
    {
        return port.getOutputStream();
    }

    RXTXPort port;
}
