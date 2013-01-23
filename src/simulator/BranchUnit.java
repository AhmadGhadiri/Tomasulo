package simulator;



public class BranchUnit extends FunctionalUnit{
	int jumpExecutionCount; //Number of execution statges for jump
	int target; //integer to store the target of branch
	public BranchUnit(){
		//creates and initializes reservation stations
		 RScount = CP.getInstance().BranchUnitRSCount;
		 executionCount = CP.getInstance().BranchExecutionTime;
		 jumpExecutionCount = CP.getInstance().jumpBranchExecutionTime;
		 RS = new Station[RScount];
		 for (int i = 0; i < RScount; i++) 
			 this.RS[i] = new Station("Branch" + i);
		 currentInstruction = 0;
		 this.FUbusy = false;
	}
	/**
	 * issue the jump instructions
	 * @param offset
	 * @return state of the simulator
	 */
	public boolean insertJump(int offset){
		for(int i=0;i<RScount;i++){		//Looks through the reservation stations for an availabe spot
			if(!RS[i].busy){	//finds the available spot
				StatusTable.getInstance().addInstruction("j " + offset, RS[i].name);
				RS[i].busy = true;
				RS[i].operation = "j";
				RS[i].A = offset * 4;  //the offset to put in PC
				return false;
			}
		}
		return true;
	}
	/**
	 * insert the bne and beq instruction into branch unit
	 * @param opcode
	 * @param rs
	 * @param rt
	 * @param immediate
	 * @return issue(false) or stall(true)
	 */
	public boolean insertBranch(String opcode,int rs,int rt,int immediate){
		for(int i=0;i<RScount;i++){		//Looks through the reservation stations for an availabe spot
			if(!RS[i].busy){	//finds the available spot
				RS[i].busy = true;
				RS[i].operation = opcode;
				//find the offset to add to PC
				RS[i].A = Tools.signExtend(16, immediate) * 4;
				StatusTable.getInstance().addInstruction(opcode+" r"+rt+" r"+rs+" "+RS[i].A, RS[i].name);
				GPR gprReg = GPR.getInstance();
				/*
				 * check the availability of registers
				 * if the registers are available put their
				 * value if not put the the reservation station
				 * they are waiting for
				 */
				if (gprReg.isAvailable(rs))
					RS[i].Vj = gprReg.getReg(rs);
				else 
					RS[i].Qj = gprReg.getQi(rs);
				if (gprReg.isAvailable(rt))
					RS[i].Vk = gprReg.getReg(rt);
				else 
					RS[i].Qk = gprReg.getQi(rt); 
				return false;
			}
		}
		return true;
	}
	/**
	 * execute the branch instruction
	 * and return true, return false if
	 * nothing is executed
	 */
	public boolean execute(){
		if(!FUbusy){ 		//if no instruction is executing
			currentInstruction = findInstructionToExecute();
			if(currentInstruction!=-1){
				//update status table
				StatusTable.getInstance().updateStartEX(RS[currentInstruction].name);
				FUbusy = true;
				if(RS[currentInstruction].operation.equals("j")) //if the instruction is jump
					executionCycles = jumpExecutionCount - 1;
				else
					executionCycles = executionCount - 1;				
				return true;
			}
		}
		else{
			if (executionCycles > 0) 
				executionCycles = executionCycles - 1;
			if ((executionCycles == 0) && (!RS[currentInstruction].resultReady)){
				//update status table
				StatusTable.getInstance().updateEndEX(RS[currentInstruction].name);
				FUbusy = false;
				RS[currentInstruction].resultReady = true;
				//Compute the result
				computeResult(currentInstruction);
			}
			return true;
		}
		for (int i=0; i<RScount;i++)
			if (RS[i].busy)
				return true;
		return false;
	}
	/**
	 * compute the result of the branch
	 * and put the result into the target
	 */
	public void computeResult(int input){
		target = 0;
		long l1 = RS[input].Vj;		//first operand
		long l2 = RS[input].Vk;		//second operand
		int j = PC.getInstance().get();
		int k = (int)RS[input].A;
		RS[input].result = 0L;
		if (RS[input].operation.equals("beq")){
			target = j + k;
			if (l1 == l2){ 		//check the condition
				RS[input].result = 1L; //branch is taken
			}
		}
		else if (RS[input].operation.equals("bne")){
			target = j + k;
			if (l1 != l2){ 		//check the condition
				RS[input].result = 1L; //branch is taken
			}
		}
		else if (RS[input].operation.equals("j")){
			target = k;
			RS[input].result = 1L;  //jump is always taken	
		}
	}
	/**
	 * compute the target and write the 
	 * result into PC, also update the 
	 * value of common data bus
	 */
	public CDB write() {
		int i = findInstructionToWrite();
		if(i!=-1){
			//update status table
			StatusTable.getInstance().updateWrite(RS[i].name);
			if(RS[i].result == 1L){   //if the branch is taken
				PC.getInstance().set(target);
			}
			RS[i].resultWritten = true;
		}
		return null;
	}
	public void dump(){
		//dump using functional unit dump function
		System.out.println("Branch Reservation Stations");
		super.dump(); 
	}
}
