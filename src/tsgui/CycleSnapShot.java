/***
 * The CycleSnapShot class is the representation of the tomasulo simulation at a given
 * clock cycle.  This class is designed to be simple to interface with independent on
 * specific implementation strategies while providing all of the relevant information
 * needed to represent the simulation to a user.
 * 
 * Author: 	Stephen Ellison, Jr.
 */

package tsgui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import simulator.*;
import tsgui.FUnitImage;

/***
 * Container class to store all relevant information about a given clock cycle in
 * the Tomasulo simulator.
 * @author Stephen Ellison, Jr.
 *
 */
public class CycleSnapShot {

	private int cycle;
	private int instruction;	// instruction waiting to be issued
	
	/* Variables needed to keep a copy of the registers */
	private long[] gprRegs;
	private String[] gprQi;
	private long[] fprRegs;
	private String[] fprQi;
	private int[] memory;		// copy of memory
	private long value;
	private String src;
	
	private Integer PC;				// Program counter for this clock cycle
	// FUNITS need to be stored
	private LinkedHashMap<String, FUnitImage> funits;
	
	
	
	// as generic as possible constructor
	public CycleSnapShot(int cycle, int instruction, Integer PC, 
                         ArrayList<FUnitImage> funits, long value, String src)
	{
		this.cycle = cycle;
		this.instruction = instruction;
		this.PC = PC;
        this.value = value;
        this.src = src;
		
		this.funits = new LinkedHashMap<String, FUnitImage>();
		for (FUnitImage unit : funits)
		{
			this.funits.put(unit.name, unit);
		}

		cloneMemory();
		cloneGPR();
		cloneFPR();
	}
	
	private void cloneMemory()
	{
		memory = Memory.getInstance().cloneMemory();
	}
	
	private void cloneGPR()
	{
		GPR gpr = GPR.getInstance();
		gprRegs = gpr.cloneRegs();
		gprQi = gpr.cloneQi();
	}
	
	private void cloneFPR()
	{
		FPR fpr = FPR.getInstance();
		fprRegs = fpr.cloneRegs();
		fprQi = fpr.cloneQi();
	}
	
	public int getCycle()
	{
		return cycle;
	}
	
	public int getPC()
	{
		return PC;
	}
	
	public int getInstruction()
	{
		return instruction;
	}
	
	
	public long getValue()
	{
		return value;
	}
	
	public String getSrc()
	{
		return src;
	}
	
	public int getMemoryValueAt(int index)
	{
		return (index >= 0 && index < memory.length)? memory[index] : 0;
	}
	
	public int getMemSize()
	{
		return memory.length;
	}
	
	public String getGPRQiAt(int index)
	{
		return (index >= 0 && index < gprQi.length)? gprQi[index] : "";
	}
	
	public long getGPRRegAt(int index)
	{
		return (index >= 0 && index < gprRegs.length)? gprRegs[index] : 0;
	}
	
	public String getFPRQiAt(int index)
	{
		return (index >= 0 && index < fprQi.length)? fprQi[index] : "";
	}
	
	public long getFPRRegAt(int index)
	{
		return (index >= 0 && index < fprRegs.length)? fprRegs[index] : 0;
	}
	
	public int getRegistersSize()
	{
		return fprRegs.length;
	}
	
	public ArrayList<FUnitImage> getFUnits()
	{
		return new ArrayList<FUnitImage>(funits.values());
	}
	
	public String toString()
	{
		String retVal = "";
		
		retVal += "Cycle:\t" + cycle + "\n";
		retVal += "PC:\t" + PC + "\n";
		retVal += "Instruction:\t" + Decoder.decodeInstruction(instruction) + 
				  "\t" + instruction + "\n";
		retVal += "Memory Size:\t" + memory.length + "\n";
		
		return retVal;
	}
}
