package kr.re.keti.vehicle;


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

public class CarDataList 	{
	static byte steeringWheelDirection;
	static short steeringWheelAngle;
	static byte speed;
	static short rpm;
	static byte brake;
	static byte gearStatus;
	static byte parkingBrake;
	static int mileage;
	static byte light;
	static byte direction;
	
}