package simulator;

import java.util.LinkedList;

public class MemoryUnit extends FunctionalUnit{
	LinkedList<Station> executionQueue; //Queue for execution of memory instruction
	LinkedList<Station> writeQueue; //Queue for writing of memory instruction
	Station[] loadBuffer; //reservation station for load operations
	Station[] storeBuffer;	//reservation station for store operations
	public MemoryUnit() {
		//creates and initializes reservation stations
		RScount = CP.getInstance().MemoryRSCount;		
		executionCount = CP.getInstance().MemoryExecutionTime;
		loadBuffer = new Station[RScount];
		storeBuffer = new Station[RScount];
		for (int i = 0; i < RScount; i++){
			loadBuffer[i] = new Station("Load" + i);
			storeBuffer[i] = new Station("Store" + i);
		}
		//this part is for GUI
		RS = new Station[RScount*2];
		for (int i=0; i< RScount;i++){
			RS[i] = loadBuffer[i];
			RS[i+RScount] = storeBuffer[i];
		}
		executionQueue = new LinkedList<Station>();
		writeQueue = new LinkedList<Station>();
	}
	boolean isLoad(String opcodeString){
		// return true if opcodeString is load instruction
		return (opcodeString.equals("ld")) || (opcodeString.equals("l.d"));
	}

	boolean isStore(String opcodeString){
		// return true if opcodeString is store instruction
		return (opcodeString.equals("sd")) || (opcodeString.equals("s.d"));
	}
	/**
	 * since in memory unit we have two reservation station
	 * we cant use findInstrucionMemory from functional unit
	 * so here we use this function
	 * finds the first not busy memory unit from the executionQueue
	 * @return the first not busy memory reservation station
	 */
	public Station findMemoryInstructionToExecute(){
		Station memStation = executionQueue.peek();
		if(memStation==null){ //if the Queue is empty
			return null;
		}
		// Checking for load/store instruction and station to insert
		if(memStation.busy && (memStation.Qj == null)){ //if the operands are ready
			if(memStation.name.substring(0, 4).equals("Load")){		//if the station is load
				return memStation;
			}
			// for Store we also need to check the Qk field for execution
			else if((memStation.Qk == null) && memStation.name.substring(0, 5).equals("Store"))
			{
				return memStation;
			}
		}
		return null;
	}
	/**
	 * execute the current instruction in the executionQueue(head), if
	 * there is no instruction being executing, it start executing the 
	 * first ready instruction in executionQueue and then update Status 
	 * table, otherwise decrease the execution cycle and check for the end
	 * of execution
	 */
	public boolean execute(){
		Station tempStation;
		if (!FUbusy){
			tempStation = findMemoryInstructionToExecute();
			if (tempStation != null){
				//update Status Table
				StatusTable.getInstance().updateStartEX(tempStation.name);
				FUbusy = true;
				//load/store step 1 from page 180 of the book
				tempStation.A = tempStation.Vj + tempStation.A;
				//set the execution cycle
				executionCycles = executionCount - 1;
			}
		}
		else{
			//get the head of queue
			tempStation = executionQueue.peek();
			if (executionCycles > 0) 
				executionCycles -= 1;
			// if the execution has finished
			if (executionCycles == 0 && !tempStation.resultReady){ 
				//update Status Table
				StatusTable.getInstance().updateEndEX(tempStation.name);
				FUbusy = false;
				tempStation.resultReady = true;
				tempStation = executionQueue.remove();
				// add the reservation station to writeQueue
				writeQueue.add(tempStation);
			}
		}
		return false;
	}
	/**
	 * insert Load instruction into load buffer reservation stations
	 * @param instruction
	 * @param op1
	 * @param op2
	 * @param op3
	 * @return false for issue and true in case of stall
	 */
	public boolean insertLoadIns(String instruction, 
			int op1, int op2, int op3){
		GPR gprReg = GPR.getInstance();
		Registers reg;
		char type; //for storing the type of instruction (Integer or Float)
		for(int i=0;i<RScount;i++){
			if(!loadBuffer[i].busy){
				if(isFloatingPointIns(instruction)){  //check if the instruction is float
					reg = FPR.getInstance();
					type = 'F';
				}
				else{
					reg = GPR.getInstance();
					type = 'I';
				}
				// set the corresponding fields of load buffer
				loadBuffer[i].busy = true;
				loadBuffer[i].operation = instruction;
				loadBuffer[i].A = op3;
				/*
				 * check the availability of registers
				 * if the registers are available put their
				 * value if not put the the reservation station
				 * they are waiting for
				 */
				if(gprReg.isAvailable(op1))
					loadBuffer[i].Vj = gprReg.getReg(op1);
				else
					loadBuffer[i].Qj = gprReg.getQi(op1);
				// Adding instruction to the Status table
				if(type=='F')
					StatusTable.getInstance().addInstruction(instruction + " f" + op2 + " " + op3 + " r" + op1, this.loadBuffer[i].name);
				if(type=='I')
					StatusTable.getInstance().addInstruction(instruction + " r" + op2 + " " + op3 + " r" + op1, this.loadBuffer[i].name);
				
				//set the reservation station for the result register
				reg.setQi(loadBuffer[i].name, op2);
				
				// add the reservation station to the load buffer
				executionQueue.add(loadBuffer[i]);
				return false;
			}
		}
		return true;
	}
	/**
	 * insert Store instruction into store buffer reservation stations
	 * @param instruction
	 * @param op1
	 * @param op2
	 * @param op3
	 * @return
	 */
	public boolean insertStoreIns(String instruction, 
			int op1, int op2, int op3){
		GPR gprReg = GPR.getInstance();
		Registers reg;
		char type; //for storing the type of instruction (Integer or Float)
		for(int i=0;i<RScount;i++){
			if(!storeBuffer[i].busy){
				if(isFloatingPointIns(instruction)){	//if the instruction is flaot
					reg = FPR.getInstance();
					type = 'F';
				}
				else{ 			//if the instruction is integer
					reg = GPR.getInstance();
					type = 'I';
				}
				// set the corresponding fields in store buffer
				storeBuffer[i].busy = true;
				storeBuffer[i].operation = instruction;
				storeBuffer[i].A = op3;
				/*
				 * check the availability of registers
				 * if the registers are availabe put their
				 * value if not put the the reservation station
				 * they are waiting for
				 */
				if(gprReg.isAvailable(op1))
					storeBuffer[i].Vj = gprReg.getReg(op1);
				else
					storeBuffer[i].Qj = gprReg.getQi(op1);
				if(reg.isAvailable(op2))
					storeBuffer[i].Vk = reg.getReg(op2);
				else
					storeBuffer[i].Qk = reg.getQi(op2);
				
				// Adding instruction to the Status table
				if(type=='F')
					StatusTable.getInstance().addInstruction(instruction + " f" + op2 + " " + op3 + " r" + op1, storeBuffer[i].name);
				if(type=='I')
					StatusTable.getInstance().addInstruction(instruction + " r" + op2 + " " + op3 + " r" + op1, storeBuffer[i].name);
				
				// add reservation station to the executionQueue
				executionQueue.add(storeBuffer[i]);
				return false;
			}
		}
		return true;
	}
	/**
	 * use insertLoadInstruction and insertStoreInstruction to insert the memory
	 * after decoding the opcode
	 * @param instruction
	 * @param op1
	 * @param op2
	 * @param op3
	 * @return false for issue and true in case of stall
	 */
	public boolean insertInstruction(String instruction, 
			int op1, int op2, int op3){
		if(isLoad(instruction))		// check if the instruction is load
			return insertLoadIns(instruction,op1,op2,op3);
		else if(isStore(instruction)) 	//check if the instruction is store
			return insertStoreIns(instruction,op1,op2,op3);
		System.out.println("Wrong instruction-issue memory instructon");
		System.exit(0);
		return false;
	}
	/**
	 * update all waiting reservation stations in memory buffers
	 * from common data bus
	 */
	public void updateReservationStations(CDB cdb){
		//update load buffer reservation stations
		for(int i=0;i<RScount;i++){
			if(loadBuffer[i].busy){ 	
				if(cdb.getSrc().equals(loadBuffer[i].Qj)){
					loadBuffer[i].Qj = null;
					loadBuffer[i].Vj = cdb.getResult();
				}
				if(cdb.getSrc().equals(loadBuffer[i].Qk)){
					loadBuffer[i].Qk = null;
					loadBuffer[i].Vk = cdb.getResult();
				}
			}
			//update store buffer reservation stations
			if(storeBuffer[i].busy){
				if(cdb.getSrc().equals(storeBuffer[i].Qj)){
					storeBuffer[i].Qj = null;
					storeBuffer[i].Vj = cdb.getResult();
				}
				if(cdb.getSrc().equals(storeBuffer[i].Qk)){
					storeBuffer[i].Qk = null;
					storeBuffer[i].Vk = cdb.getResult();
				}
			}
		}
	}
	/**
	 * check all the reservation stations in memory to see
	 * if there is any instruction still running in them
	 * return true if it is finished, otherwise false
	 */
	public boolean isFinished(){
		for(int i=0;i<RScount ; i++)
			if(loadBuffer[i].busy || storeBuffer[i].busy)
					return false;
		return true;
	}
	public boolean isFloatingPointIns(String instruction){
		// check if the instruction is Floating Point from the instruction
		return (instruction.equals("l.d")||instruction.equals("s.d"));
	}
	/**
	 * write to the common data bus from writeQueue, first check if it is not empty
	 * and then find the first instruction and write the result into common data bus
	 * also since store does not write in common data bus. it can write to memory in
	 * the same cycle as write
	 */
	public CDB write(){
		if (writeQueue.isEmpty())
			return null;
		boolean store = false; //to check if a store writes to memory
		CDB cdb = null;
		while ((cdb == null) && (store == false) && (!this.writeQueue.isEmpty())){
			Station tempStation = writeQueue.remove();
			//update status table
			StatusTable.getInstance().updateWrite(tempStation.name);
			tempStation.resultWritten = true;
			if (tempStation.name.substring(0,5).equals("Store")){ //if the instrucion is store
				store = true;
				// write the result of store into memory as a long
				Memory.getInstance().putLongInMemory(tempStation.A, tempStation.Vk);
			}
			else { 
				// if the instruction is load
				// update common data bus from load
				cdb = new CDB();
				// load the long from memory
				cdb.setResult(Memory.getInstance().getLongFromMemory(tempStation.A));
				//set the source of common data bus
				cdb.setSrc(tempStation.name);
			}
		}
		return cdb;
	}
	/**
	 * dump all the reservation stations of the memory
	 * first load buffer and then store buffers
	 */
	public void dump(){
		System.out.println("Load Buffers");
		Station.dumpHeader();
		for(int i=0;i<RScount;i++)
			loadBuffer[i].dump();
		System.out.println("\nStore Buffers");
		Station.dumpHeader();
		for(int i=0;i<RScount;i++)
			storeBuffer[i].dump();
		System.out.println();
	}
}