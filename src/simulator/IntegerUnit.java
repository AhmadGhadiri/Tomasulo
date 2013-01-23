package simulator;

public class IntegerUnit extends FunctionalUnit{
	public IntegerUnit(){
		//creates and initializes reservation stations
		 RScount = CP.getInstance().IntegerUnitRSCount;
		 executionCount = CP.getInstance().IntegerExecutionTime;
		 RS = new Station[RScount];
		 for (int i = 0; i < RScount; i++) 
			 RS[i] = new Station("Integer" + i);
		 currentInstruction = 0;
		 FUbusy = false;
	}
	/**
	 * insert all integer instructions including rtype and ltype instructions
	 * @param opcode
	 * @param rs
	 * @param rt
	 * @param rd
	 * @return false for issue and true for the stall
	 */
	public boolean insertInstruction(String opcode, int rs,int rt, int rd){
		if(isImmediate(opcode))
			return insertImmInstruction(opcode,rs,rt,rd);
		else
			return insertAllGPRInstruction(opcode, rs, rt, rd);
	}
	public boolean isImmediate(String opcode){
		//check if the instruction is immediate
		return (opcode.equals("daddi") || opcode.equals("daddiu"));
	}
	/**
	 * insert immediate instructions into IntegerUnit
	 * @param opcode
	 * @param rs
	 * @param rt
	 * @param rd
	 * @return false for the issue and true in case of stall
	 */
	private boolean insertImmInstruction(String opcode, int rs,int rt, int rd){
		GPR gprReg = GPR.getInstance();
		for (int i = 0; i < RScount; i++)
			if (!RS[i].busy){		//find the first not busy reservation station
				long l = rd;
				//in case of negative numbers, rd is positive, for daddi we need
				//change that to the original number
				if (opcode.equals("daddi"))		
					l = l << 48 >> 48;
				//update status table
				StatusTable.getInstance().addInstruction(opcode + " r" + rt + " r" + rs + " " + l, RS[i].name);
				//set the field of reservation station
				RS[i].busy = true;
				RS[i].operation = opcode;
				RS[i].A = l;
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
				
				//set the reservation station for the result register
				gprReg.setQi("Integer" + i,rt); 
				return false;
			}
		return true;
	}
	/**
	 * insert the rtype instruction in integer unit
	 * @param opcode
	 * @param rs
	 * @param rt
	 * @param rd
	 * @return false for issue and return true in case of stall
	 */
	private boolean insertAllGPRInstruction(String opcode, int rs,int rt, int rd){
		GPR gprReg = GPR.getInstance();
		for (int i = 0; i < RScount; i++)
			if (!RS[i].busy){		//find the first not busy
				//update the status table
				StatusTable.getInstance().addInstruction(opcode + " r" + rd + " r" + rs + " r" + rt, RS[i].name);
				//set the value in the reservation station
				RS[i].busy = true;
				RS[i].operation = opcode;
				/*
				 * check the availability of registers
				 * if the registers are availabe put their
				 * value if not put the the reservation station
				 * they are waiting for
				 */
				if (gprReg.isAvailable(rs)){
					RS[i].Vj = gprReg.getReg(rs);
				}
				else 
					RS[i].Qj = gprReg.getQi(rs);
				if (gprReg.isAvailable(rt)){
					RS[i].Vk = gprReg.getReg(rt);
				}
				else 
					RS[i].Qk = gprReg.getQi(rt);
				
				//set the waiting register and corresponding reservation station
				gprReg.setQi(RS[i].name,rd); 
				return false;
			}
		return true;
	}
	/**
	 * compute the result of integer operations
	 */
	void computeResult(int input){
		long l1 = RS[input].Vj;
		long l2 = RS[input].Vk;
		long l3 = RS[input].A;
		if (RS[input].operation.equals("dadd")) //if the opcode is dadd
			RS[input].result = (l1 + l2);
		else if (RS[input].operation.equals("dsub")) //if the opcode is dsub
			RS[input].result = (l1 - l2);
		else if ((RS[input].operation.equals("daddi")) 
				|| (RS[input].operation.equals("daddiu"))) //if the opcode is ltype 
			RS[input].result = (l1 + l3);
	}
	void dump(){
		System.out.println("Integer Reservation Stations");
		super.dump();
	}
}
