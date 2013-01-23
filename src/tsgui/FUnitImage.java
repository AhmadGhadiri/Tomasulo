/***
 * The FUnitImage is designed to hold all of the relevant information for any given functional unit.
 * This allows a simpler integration with generic code, simply instantiate an FUnitImage for each
 * functional unit, and GUI can display the appropriate information.
 * 
 * Author:	Stephen Ellison, Jr.
 */

package tsgui;

import simulator.Station;

public class FUnitImage {

	public String name;					// name of the functional unit, goes to RS's too
	public StationImage[] RS;					// array of reservation stations feeding this FUnit
	public int RSCount;					// number of reservation stations that feed this FUnit
	public int executionCount;			// number of cycles this FUnit takes to execute
	public int currentInstruction;		// current instruction that is executing
	public boolean FUbusy;				// indicates that the FUnit is executing
	public int executionCycle;			// current cycle that the executing instruction is on
	
	public FUnitImage(String name, Station[] rs, int RSCount, 
			int executionCount, int currentInstruction, boolean FUBusy, int executionCycle)
	{
		this.name = name;
		this.RSCount = RSCount;
		this.executionCount = executionCount;
		this.currentInstruction = currentInstruction;
		this.FUbusy = FUBusy;
		this.executionCycle = executionCycle;
		RS = new StationImage[rs.length];
		for (int i = 0; i < rs.length; i++)
			RS[i] = new StationImage(rs[i]);
	}
}
