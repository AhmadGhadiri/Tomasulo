/***
 * The StationImage class is designed to be an independent representation of the
 * information that is stored by the reservation stations of each functional unit.
 * 
 * Author:	Stephen Ellison, Jr.
 */

package tsgui;

import simulator.Station;

public class StationImage {

	public String name;       //name of reservation station
    public boolean busy;      //is station holding an operation
    public String operation;  //type of operating
    public long Vj;           //value of operand
    public long Vk;           //value of operand
    public String Qj;         //name of reservation station producing Vj
    public String Qk;         //name of reservation station producing Vk
    public long A;            //used to hold immediate field or eff address
    
	public StationImage(Station station)
	{
		name = station.name;
		busy = station.busy;
		operation = station.operation;
		Vj = station.Vj;
		Vk = station.Vk;
		Qj = station.Qj;
		Qk = station.Qk;
		A = station.A;
	}
}
