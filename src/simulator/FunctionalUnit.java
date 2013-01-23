package simulator;
/**
 * This class is the parent for all functional units
 * it provides the shared functionality between all
 * functional units
 * @author Ahmad
 *
 */
public class FunctionalUnit {
	Station [] RS; //array of reservation stations that feed functional unit
	int RScount; //number of reservation stations
	int executionCount; //number of execution cycles required by the functional unit
	int currentInstruction; //index into the RS array identifying current instruction being executed
	boolean FUbusy; //flag indicating whether the functional unit is currently executing an instruction
	int executionCycles; //number of execution cycles remaining for currently executing instruction
	/**
	 * checks the reservation stations and find the first one
	 * which is ready
	 * @return the index of the first ready instruction
	 */
	int findInstructionToExecute(){
		for(int i=0;i<RScount;i++){
			if(RS[i].ready())
				return i;
		}
		return -1;
	}
	/**
	 * execute the current instruction
	 * if the functional unit is not busy then add one instruction to execute
	 * else decrease the number of execution cycles of the current instruction
	 * @return false for not branch instructions, true for branch execution
	 */
	public boolean execute(){
		if(!FUbusy){
			currentInstruction = findInstructionToExecute();
			if(currentInstruction != -1){
				//update status table
				StatusTable.getInstance().updateStartEX(RS[currentInstruction].name);
				FUbusy = true;
				//set the number of execution cycles
				executionCycles = executionCount - 1;
			}
		}
		else{		//if no instruction is been executing
			//decrease the number of execution cycles
			if (executionCycles > 0) 
				executionCycles = executionCycles - 1;
			if(executionCycles == 0 && !RS[currentInstruction].resultReady){	//if the execution is finished
				//update status table
				StatusTable.getInstance().updateEndEX(RS[currentInstruction].name);
				//compute the result
				computeResult(currentInstruction);
				//set result ready in RS to true
				RS[currentInstruction].resultReady = true;
				//set the functional unit to not busy
				FUbusy = false;
			}
		}
		return false;
	}
	/**
	 * This function checks all waiting reservation stations and 
	 * if their source is the same as common data bus, update
	 * them
	 * @param cdb
	 */
	void updateReservationStations(CDB cdb){
		for(int i=0;i<RScount;i++){
			if(RS[i].busy && cdb.getSrc().equals(RS[i].Qj)){
				RS[i].Qj = null;
				RS[i].Vj = cdb.getResult();
			}
			if(RS[i].busy && cdb.getSrc().equals(RS[i].Qk)){
				RS[i].Qk = null;
				RS[i].Vk = cdb.getResult();
			}
		}
	}
	/**
	 * find the first reservation station that is ready to write
	 * @return index of the first RS
	 */
	int findInstructionToWrite(){
		for(int i=0;i<RScount;i++){
			if(RS[i].resultReady) 
				return i;
		}
		return -1;
	}
	/**
	 * write the result of the instructon on the 
	 * common data bus
	 * @return common data bus
	 */
	public CDB write(){
		int i = findInstructionToWrite();
		if(i!=-1){
			StatusTable.getInstance().updateWrite(RS[i].name);
			CDB simCDB = new CDB();
			RS[i].resultWritten = true;
			simCDB.setResult(RS[i].result);
			simCDB.setSrc(RS[i].name);
			return simCDB;
		}
		return null;
	}
	/**
	 * clear all reservation statations that their result
	 * have been written
	 * In case of MemoryUnit it clears load and store buffers
	 */
	void clear(){
		if(this instanceof MemoryUnit){		//clear reservation stations in MemoryUnit
			MemoryUnit mem = (MemoryUnit)this;
			for(int i=0;i<mem.RScount;i++){
				if(mem.loadBuffer[i].resultWritten)
					mem.loadBuffer[i].clear();
				if(mem.storeBuffer[i].resultWritten)
					mem.storeBuffer[i].clear();
			}
		}
		else{			//clear reservation stations for other functional units
			for(int i=0;i<RScount;i++){
				if(RS[i].resultWritten)
					RS[i].clear();
			}
		}
	}
	/**
	 * checks all of the Reservation stations to see
	 * whether they are busy or the functional unit is 
	 * finished
	 * @return false if the functional unit is still working, true in other case
	 */
	public boolean isFinished(){
		for(int i=0;i<RScount; i++){
				if(RS[i].busy)
					return false;
		}
		return true;
	}
	void computeResult(int r){
		//abstract function to implement by each function unit
	}
	/**
	 * dump all the reservation stations and print the result 
	 * on screen
	 */
	void dump(){
		//calls dump for each RS
		Station.dumpHeader();
		for(int i=0;i<RScount;i++)
				RS[i].dump();
 		System.out.println();
	}
}
