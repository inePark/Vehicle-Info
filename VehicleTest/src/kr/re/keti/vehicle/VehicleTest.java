/////////////////////////////////////////
// MAIN TEST PROGRAM : VehicleTest.java
////////////////////////////////////////

package kr.re.keti.vehicle;

public class VehicleTest
{
	public static void main(String args[]) throws Exception
	{
		final String portName = "\\\\.\\COM20"; //temporal port number
	//	final String portName = "COM1"; //temporal port number
		final int portSpeed = 115200;  
		
		RXTX_Serial commModule = new RXTX_Serial();  // opening RXTX_Serial port
		
		commModule.open(portName);
		commModule.setSpeed(portSpeed);
		
		ELM327 ifModule = new ELM327();  //connect to ELM327
		ifModule.open(commModule);       //open
		
		// Create library main function.
		// parameter: a ELM327 class variable
		TransmitCarInfo vehicleCamry = new TransmitCarInfo (ifModule); 
				
		while (true)	{
			Thread.sleep(500); 		// polling type -> 0.5s

			// Call ECU information updating(setting) function.
			// MUST call this function before get ECU information.
			// parameter: NULL
			// return: NULL
			vehicleCamry.setCarEcuInfo();	

			// Get ECU information of CAMRY as an uniformed format.
			// parameter: NULL
			// return: a String type value (String length is 28)
			System.out.print ("\r\r[get car info]\r" + vehicleCamry.getCarEcuInfo());
			
		} //end of while

	} //end of main

} //end of class
