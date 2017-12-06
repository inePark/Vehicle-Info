package kr.re.keti.vehicle;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class ELM327 implements VehicleCommunicator
{
    public void open(CommunicationModule ioDevice) throws IOException
    {
        port = ioDevice;
        outputStream = new DataOutputStream(port.getOutputStream());
        logger = null;
        reader = new BufferedInputStream(port.getInputStream());
        
        initialize ();
        
    }
    
    public void initialize () throws IOException	{
    	
    	cmdCount = 0;
    	
    	System.out.print ("Initializing");
    	
    	for (int nLoop = 0; nLoop < initialCommandNumber; nLoop ++)	{
        	outputStream.writeBytes(initialCommand [nLoop]);
        	System.out.print (".");

        	
        	try
        	{
        		 Thread.sleep(300);
        	}
        	catch(Exception e)	{
        		/* NONE */
        	}
    	}
    	
    	try
    	{
    		 Thread.sleep(1000);
    	}
    	catch(Exception e)	{
    		/* NONE */
    	}
    }

    public void close() throws IOException
    {
        outputStream.close();
        reader.close();
    }

    public void setLogger(Logger l)
    {
        logger = l;
    }

    public void sendData(short id, short[] data) throws IOException
    {
        if(data != null)
        {
            final int dataInitialIndex = 0;

            String dataID = Short.toString(id);
            String dataStr = Short.toString(data[dataInitialIndex]);

            for(short i = dataInitialIndex + 1; i < data.length; ++i)
            {
                dataStr += Short.toString(data[i]);
            }

            outputStream.writeBytes(setHeaderCommand + dataID + '\r' + dataStr + '\r');
        }
    }

    public String[] getData() throws IOException, VehicleCommunicatorException
    {
        String readLine = null;
        byte[] byteBuffer = new byte[bufferSize];
        int byteCounter;
        boolean validData = true;

        for(byteCounter= 0; byteCounter < bufferSize - 1; ++byteCounter)
        {
            byteBuffer[byteCounter] = (byte)reader.read();
            
            if(byteBuffer[byteCounter] == '\r' || byteBuffer[byteCounter] == '\n')
            {
                if(byteCounter == initialByteIndex)
                {
                    --byteCounter;
                    
                    continue;
                }
                else
                {
                    break;
                }
            }
            else if(!((byteBuffer[byteCounter] >= '0' && byteBuffer[byteCounter] <= '9') || (byteBuffer[byteCounter] >= 'A' && byteBuffer[byteCounter] <= 'F') || byteBuffer[byteCounter] == ' '))
            {
                validData = false;
            }
        }
        byteBuffer[byteCounter] = '\0';

        readLine = new String(byteBuffer, initialByteIndex, byteCounter);
        
        if(logger != null)
        {
            logger.log("[ELM327] Received data: " + readLine);
        }

        if(readLine.equals(bufferFullStr))
        {
            throw new VehicleCommunicatorException(bufferFullStr);
        }
        else if(readLine.equals(stoppedStr))
        {
            throw new VehicleCommunicatorException(stoppedStr);
        }
        else if(validData && readLine.length() > minResponseLength && readLine.length() < maxResponseLength)
        {
            String[] receivedDataStr = readLine.split(dataSplitter);
            
            if(receivedDataStr.length < minimumDataSize ||
            		receivedDataStr.length > defaultDataSize ||
            		receivedDataStr[initialDataIndex].length() != dataIDLength)
            {
                return null;
            }
            else
            {
                return receivedDataStr;
            }
        }
        else
        {
            return null;
        }
    }

    public void ready() throws java.io.IOException
    {
        reader.skip(reader.available());
        outputStream.writeBytes(getMonitorCmd());	//for CAMRY
      //  outputStream.writeBytes(monitoringCommand);	//for test 
    }
    
    public void stop() throws java.io.IOException
    {
    	outputStream.writeByte(charCR);
    }

    public int available() throws java.io.IOException
    {
        return reader.available();
    }
    
    private String getMonitorCmd()
    {
    	if(cmdCount >= cmdChangePeriod)
    	{
    		cmdCount = 0;
    		
    		return monitoringHelperCmd;
    	}
    	else if(cmdCount == cmdChangePeriod - 1)
    	{
    		++cmdCount;
    		
    		return "ATMR02\r";
    	}
    	else if(cmdCount == cmdChangePeriod - 2)
    	{
    		++cmdCount;
    		
    		return "ATMR01\r";
    	}
    	else
    	{
    		++cmdCount;
    		
    		return monitoringCommand;
    	}
    }
    
    private short cmdCount;
    private final short cmdChangePeriod = 5;

    private CommunicationModule port;
    private DataOutputStream outputStream;
    private BufferedInputStream reader;

    private String bufferFullStr = "BUFFER FULL";
    private String stoppedStr = "STOPPED";
    
    private final int charCR = '\r';

    private final String monitoringCommand = "ATMA\r"; //ATMR0X //ATMA
    private final String monitoringHelperCmd = "ATMR06\r";
    private final String setHeaderCommand = "ATSH";
    private final String [] initialCommand = {"ATSPB\r", "ATPP2DSV01\r", "ATPP2DON\r", "ATPP01SV00\r", "ATPP01ON\r", "ATPP29SV00\r", "ATPP29ON\r", "ATPP09SVFF\r", "ATPP09ON\r", "ATZ\r"}; 
    
    private final int initialCommandNumber = 10;
    private final int minResponseLength = 8;
    private final int maxResponseLength = 31;
    private final String dataSplitter = " ";

    private final int defaultDataSize = 10;
    private final int bufferSize = 0xFF;
    private final int initialByteIndex = 0;
    private final int initialDataIndex = 0;
    private final int dataIDLength = 3;
    private final int minimumDataSize = 3;

    private Logger logger;
}
