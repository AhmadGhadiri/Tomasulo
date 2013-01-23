package simulator;
import java.io.*;
import java.util.*;
import tsgui.*;

public class Simulator{
	//both of these are for the GUI 
    boolean gui;
    private ArrayList<CycleSnapShot> snapshots = null;
    BranchUnit branchUnit;			// An instance of branch functinonal unit
    IntegerUnit intUnit;			// An instance of integer functional unit
    FPMult multiplierUnit;			// An instance of floating point multiplier functional unit
    FPDiv dividerUnit;				// An instance of floating point divider functional unit
    FPAdd adderUnit;				// An instance of floating point adder/subtarctor functional unit
    MemoryUnit memoryUnit;			// An instance of memory unit
    public boolean branchExe;			// boolean that shows if there is a branch executing
    public boolean halt;				// boolean showing that a halt is issued
    public boolean strstall;			// boolean that shows if there is an structural stall

    public Simulator(String file, boolean flag) throws IOException{
    	//initializing functional units
        branchUnit = new BranchUnit();
        intUnit = new IntegerUnit();
        multiplierUnit = new FPMult();
        dividerUnit = new FPDiv();
        adderUnit = new FPAdd();
        memoryUnit = new MemoryUnit();
        Memory.getInstance().loadMemory(file);
		//initialize flags for halt, branch and structural stall
        branchExe = false;
        halt = false;
        strstall = false;
        gui = flag;
    }
    /**
     * check all the functional unit for the end of 
     * program
     * @return true if the program is there is no instruction in
     * functional units and false otherwise
     */
    public boolean isFinished(){
    	return (memoryUnit.isFinished() &&
    			adderUnit.isFinished() && 
    			multiplierUnit.isFinished() &&
    			dividerUnit.isFinished() &&
    			intUnit.isFinished() &&
    			branchUnit.isFinished());
    }
	/**
	 *	start the program and all the functional units,
	 *	this function reads instructions from memory, and 
	 *	issues them to functional units, execute them and 
	 *	write the result to the CDB, also after the end of 
	 *	program dump memory*
	 */
    public void simulate(){
    	//initialize variables passed to gui
        long cdbResult = 0L;		//for GUI function as CDB result
        String cdbSrc = null;		//for GUI as CDB source
        int instruction = 0;		
        CDB commonDataBus;
		PC.getInstance().set(0);	//initialize the PC
        Memory simulatorMem = Memory.getInstance();
        //this will be your driver loop which will execute until
        //the halt is executed
        while (halt==false || !isFinished()){			//check for the end of program
        	commonDataBus = write();
        	branchExe = execute();		// check if a branch is executing
        	if (halt==false && branchExe==false){	//if there is no branch and no halt issued
        		//get the instruction from memory
        		instruction = simulatorMem.getInstruction(PC.getInstance().get());
        		//issue the instruction and check for structural stall and halt
        		strstall = issue(instruction);
        		if(halt==false && strstall==false ) //if there is no stall and no halt issued
					PC.getInstance().set(PC.getInstance().get()+4);//increment the PC
        	}
        	//update Reservation Stations
        	updateReservationStations(commonDataBus);
        	//clear Reservation Stations
        	clearReservationStations();
        	//update clock
        	Clock.getInstance().increment();
        	
            //leave this call in for the GUI, but make sure that the 
            //instruction, PC, CDB result and CDB source are set
            //properly  	
        	//after write
        	if (commonDataBus != null){ //make the variables from CDB for addSnapShot function
				cdbResult = commonDataBus.getResult();
				cdbSrc = commonDataBus.getSrc();
			}
            if (gui) addSnapShot(instruction, PC.getInstance().get(), cdbResult, cdbSrc);
        }
		//dump memory, registers and status table
        Memory.getInstance().dump();
        GPR.getInstance().dump();
        FPR.getInstance().dump();
        StatusTable.getInstance().dump();
        System.out.println("Total clock cycles: " + Clock.getInstance().get());
        if (gui == true)
        	new TSGui(snapshots);
    }
    /**
     * decode the instruction and issue them (insert to functional units)
     * this function update the status table for dump, nop and halt since
     * these instructions does not need to be inserted to any functional
     * unit
     * @param instruction
     * @return a boolean represent issue or stall
     */
    public boolean issue(int instruction){
    	// decode the instruction here and issue it
    	int opcode = Tools.grabBits(instruction, 0, 5);
    	char type = findInstructionType(opcode); //find the type r,l, or others
    	if(type=='r')
    		return issueRType(instruction);
    	else if(type=='l')
    		return issueLType(instruction);
    	else if(opcode == 2) //if it is a jump
    		//inset jump
    		return branchUnit.insertJump(Tools.grabBits(instruction, 6, 31));
    	else if(opcode == CP.InsOps.dump){ //if it is a dunp
    		// update status table
    		StatusTable.getInstance().addInstruction("dump " + Tools.grabBits(instruction, 6, 31), "DUMP");
    		dump(Tools.grabBits(instruction, 6, 31));  //dump the offset
    		return false;
    	}
    	else if(opcode == CP.InsOps.nop){
    		//update status table
    		StatusTable.getInstance().addInstruction("nop", "NOP");
    		return false;
    	}
    	else if(opcode == CP.InsOps.halt){
    		// update the status table
    		StatusTable.getInstance().addInstruction("halt", "HALT");
    		halt = true;  //set that a halt instruction is being issued
    		return false;
    	}
    	else{ //in case of wrong input file
    		System.out.println("Undefined instruction");
    		System.exit(0);
    	}
    	halt = true; 	//if the instruction was wrong 
    	return false; //for the sake of Eclipse (Warning)
    }
    /**
     * decode and insert Rtype instructions
     * @param instruction
     * @return stall or issue
     */
    public boolean issueRType(int instruction){
    	int functionCode = Tools.grabBits(instruction, 26, 31); //grab the functional code
    	String opcodeString="";
    	for(int i=0;i<6;i++){ 		//find the opcode string of instruction
    		if(functionCode == CP.InsOps.rtypeFC[i])
    			opcodeString = CP.InsOps.rtypeName[i];
    	}
    	// grab the bits related to each operands of the rtype instruction
    	int rs = Tools.grabBits(instruction, 6, 10);
		int rt = Tools.grabBits(instruction, 11, 15);
		int rd = Tools.grabBits(instruction, 16, 20);
		switch (functionCode){		//issue the instruction to corresponding functional unit
		case 44:	//dadd
		case 46:	//dsub
			return intUnit.insertInstruction(opcodeString, rs, rt, rd);
		case 47:	//add.d
		case 48:	//sub.d
			return adderUnit.insertInstruction(opcodeString, rs, rt, rd);
		case 49:	//mul.d
			return multiplierUnit.insertInstruction(opcodeString, rs, rt, rd);
		case 50:	//div.d
			return dividerUnit.insertInstruction(opcodeString, rs, rt, rd);
		default:	//wrong rtype
			System.out.println("undefined Rtype instruction"+functionCode);
			System.exit(0);
			return false;
		}
    }
    /**
     * decode and issue Ltype instructions
     * @param instruction
     * @return stall or issue
     */
    public boolean issueLType(int instruction){
    	int opcode = Tools.grabBits(instruction, 0, 5);
    	String opcodeString="";
    	for(int i=0;i<8;i++){		//find the opcode string of instruction
    		if(opcode == CP.InsOps.lType[i])
    			opcodeString = CP.InsOps.ltypeName[i];
    	}
    	// grab the bits related to each operands of the ltype instruction
    	int rs = Tools.grabBits(instruction, 6, 10);
		int rt = Tools.grabBits(instruction, 11, 15);
		int rd = Tools.grabBits(instruction, 16, 31);
		switch (opcode){
		case 4:		//beq
		case 5:		//bne
			return branchUnit.insertBranch(opcodeString, rs, rt, rd);
		case 24:	//daddi
		case 25:	//daddiu
			return intUnit.insertInstruction(opcodeString, rs, rt, rd);
		case 53: 	//l.d
		case 55:	//ld
		case 61:	//s.d
		case 63:	//sd
			return memoryUnit.insertInstruction(opcodeString, rs, rt, rd);
		default:
			System.out.println("Wrong ltype instruction"+opcode);
			System.exit(0);
			return false;
		}
    }
    /**
     * find the type of instructions from opcode
     * @param opcode opcode of the instruction
     * @return type as a char
     */
    public char findInstructionType(int opcode){
    	if(opcode == 0)
    		return 'r';
    	for(int i=0;i<8;i++)
    		if(opcode == CP.InsOps.lType[i])
    			return 'l';
    	for(int i=0;i<3;i++)
    		if(opcode == CP.InsOps.jtype[0])
    			return 'j';
    	if(opcode == CP.InsOps.dump)
    		return 'd';
    	return 'U';
    }
    //This method is for the GUI, do not modify this
    public void addSnapShot(int instr, int PCValue, 
                            long cdbValue, String cdbSrc){
    	if (snapshots == null) snapshots = new ArrayList<CycleSnapShot>();
    	snapshots.add(new CycleSnapShot(Clock.getInstance().get(), instr,
                        PCValue, buildFunctionalUnitImageList(), cdbValue,
                        cdbSrc));
    }
    /**
     * update all reservation stations from the 
     * common data bus, the order is the same as 
     * the assignment explanation
     * @param cdb
     */
    public void updateReservationStations(CDB cdb){
    	if(cdb!=null){
    		memoryUnit.updateReservationStations(cdb);
    		dividerUnit.updateReservationStations(cdb);
    		multiplierUnit.updateReservationStations(cdb);
    		adderUnit.updateReservationStations(cdb);
    		intUnit.updateReservationStations(cdb);
    		//update GPR and FPR
    		GPR gprRegs = GPR.getInstance();
    		FPR fprRegs = FPR.getInstance();
    		gprRegs.updateRegisters(cdb);
    		fprRegs.updateRegisters(cdb);
    		branchUnit.updateReservationStations(cdb);
    	}
    }
    /**
     * execute all functional units
     * @return branchExe or execution
     */
    public boolean execute(){
		boolean state;
		//The Same order as updateReservationStations function
		state = memoryUnit.execute();
		state = dividerUnit.execute();
		state = multiplierUnit.execute();
		state = adderUnit.execute();
		state = intUnit.execute();
		state = branchUnit.execute();
		return state;
	}
    /**
     * call the write stage from each
     * functional unit and write it to
     * common data bus, the order is the 
     * same as project explanation
     * @return common data bus
     */
    public CDB write(){
    	CDB result = branchUnit.write();
    	if(result != null)
    		return result;
    	result = memoryUnit.write();
    	if(result != null)
    		return result;
    	result = dividerUnit.write();
    	if(result != null)
    		return result;
    	result = multiplierUnit.write();
    	if(result != null)
    		return result;
    	result = adderUnit.write();
    	if(result != null)
    		return result;
    	result = intUnit.write();
    	if(result != null)
    		return result;
    	return result;
    }
    /**
     * clear all the reservation station from all 
     * functional units
     */
    public void clearReservationStations(){
    	branchUnit.clear();
    	memoryUnit.clear();
    	dividerUnit.clear();
    	multiplierUnit.clear();
    	adderUnit.clear();
    	intUnit.clear();
    }
    /**
     * dump the reservation stations
     * and also memory and registers
     * the same order as explained in the
     * project explanation
     * this function changes the input to binary 
     * string and reverse it, then use the chars at
     * each index to dump each hardware
     * @param input
     */
    void dump(int input){
    	String binaryString = Integer.toString(input,2);
    	binaryString = new StringBuffer(binaryString).reverse().toString();
    	int len = binaryString.length();
    	if(len>0 && binaryString.charAt(0)=='1')
    		 Memory.getInstance().dump();
    	//dump General Purpose Registers;
    	if(len>1 && binaryString.charAt(1)=='1')
    		GPR.getInstance().dump();
    	//dump Floating Point Registers;
    	if(len>2 && binaryString.charAt(2)=='1')
    		FPR.getInstance().dump();
    	//dump Floating point Adder Reservation station
    	if(len>3 && binaryString.charAt(3)=='1')
    		adderUnit.dump();
    	//dump Floating point Multiply Reservation station
    	if(len>4 && binaryString.charAt(4)=='1')
    		multiplierUnit.dump();
    	//dump Floating point Divide Reservation station
    	if(len>5 && binaryString.charAt(5)=='1')
    		dividerUnit.dump();
    	//dump Integer Reservation station
    	if(len>6 && binaryString.charAt(6)=='1')
    		intUnit.dump();
    	//dump load/buffer station
    	if(len>7 && binaryString.charAt(7)=='1')
    		memoryUnit.dump();
    	//dump Instruction Status Table
    	if(len>8 && binaryString.charAt(8)=='1')
    		StatusTable.getInstance().dump();
    }
    //You'll need to modify this method to use the GUI
    private ArrayList<FUnitImage> buildFunctionalUnitImageList(){
    	ArrayList<FUnitImage> list = new ArrayList<FUnitImage>();    	
        //you'll need to create a new FUnitImage object for each
        //of your Functional Units and pass that object to list.add
        //Each Functional Unit will have an array of Stations,
        //a count of the Stations, an executionCount, the currentInstruction,
        //a flag indicating whether it is busy or not, and the
        //number of remaining execution Cycles
        //
        //Here is a sample call, although it won't work until there
        //is an intUnit object.
    	list.add(new FUnitImage("Integer", intUnit.RS, intUnit.RScount,
                   intUnit.executionCount, intUnit.currentInstruction,
                   intUnit.FUbusy, intUnit.executionCycles));
    	list.add(new FUnitImage("FPAdder", adderUnit.RS, adderUnit.RScount,
                adderUnit.executionCount, adderUnit.currentInstruction,
                adderUnit.FUbusy, adderUnit.executionCycles));
    	list.add(new FUnitImage("FPMultiplier", multiplierUnit.RS, multiplierUnit.RScount,
                multiplierUnit.executionCount, multiplierUnit.currentInstruction,
                multiplierUnit.FUbusy, multiplierUnit.executionCycles));
    	list.add(new FUnitImage("FPDivider", dividerUnit.RS, dividerUnit.RScount,
                dividerUnit.executionCount, dividerUnit.currentInstruction,
                dividerUnit.FUbusy, dividerUnit.executionCycles));
    	list.add(new FUnitImage("Branch", branchUnit.RS, branchUnit.RScount,
                branchUnit.executionCount, branchUnit.currentInstruction,
                branchUnit.FUbusy, branchUnit.executionCycles));
    	//for memory we need to build the RS in memory and size of it is twice as the normal RScount
    	list.add(new FUnitImage("Memory", memoryUnit.RS, memoryUnit.RScount * 2,
    			memoryUnit.executionCount, memoryUnit.currentInstruction,
                memoryUnit.FUbusy, memoryUnit.executionCycles));
    	return list;
     }
}
