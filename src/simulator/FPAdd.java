package simulator;

public class FPAdd extends FunctionalUnit{
	public FPAdd(){
		//creates and initializes reservation stations
		 RScount = CP.getInstance().FPAdderUnitRSCount;
		 executionCount = CP.getInstance().FPAdderExecutionTime;
		 RS = new Station[RScount];
		 for (int i = 0; i < RScount; i++) 
			 this.RS[i] = new Station("FPAdd" + i);
		 currentInstruction = 0;
		 this.FUbusy = false;
	}
	/**
	 * insert add.d and sub.d instruction into memory
	 * @param opcode
	 * @param rs
	 * @param rt
	 * @param rd
	 * @return true for issue, and false for stall
	 */
	public boolean insertInstruction(String opcode, int rs,int rt, int rd){
		FPR fprReg = FPR.getInstance();
		for(int i=0;i<RScount;i++){
			if(!RS[i].busy){
				//Update Status table
				StatusTable.getInstance().addInstruction(opcode+" f"+rd+" f"+rs+" f"+rt, RS[i].name);
				RS[i].busy = true;
				RS[i].operation = opcode;
				/*
				 * check the availability of registers
				 * if the registers are available put their
				 * value if not put the the reservation station
				 * they are waiting for
				 */
				if(fprReg.isAvailable(rs))
					RS[i].Vj = fprReg.getReg(rs);
				else
					RS[i].Qj = fprReg.getQi(rs);
				if(fprReg.isAvailable(rt))
					RS[i].Vk = fprReg.getReg(rt);
				else
					RS[i].Qk = fprReg.getQi(rt);
				
				//set the reservation station for the resutl register
				fprReg.setQi(RS[i].name, rd);
				return false;
			}
		}
		return true;
	}
	/**
	 * compute the result of the current instruction
	 */
	void computeResult(int input){
		//change the long bits to double to compute the result 
		double rs = Double.longBitsToDouble(RS[input].Vj);
		double rt = Double.longBitsToDouble(RS[input].Vk);
		double result = 0;
		
		//calculate the result depend on instructions
		if(RS[input].operation.equals("add.d"))		
			result = rs + rt;
		else if(RS[input].operation.equals("sub.d"))
			result = rs - rt;
		//change the numbers back to long
		RS[input].result = Double.doubleToLongBits(result);
	}
	void dump(){
		System.out.println("FP Adder Reservation Stations");
		super.dump();
	}
}
