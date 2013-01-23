package simulator;

//This class is incomplete.  You'll need functions
//for getting and setting registers and updating the
//register file using the value on the CDB

public class Registers{
    protected long[] Regs; 
    protected String[] Qi;
    static final int REGS = 32;
    public Registers(){
        int i;
        Regs = new long[REGS];
        Qi = new String[REGS];
        for (i = 0; i < REGS; i++){
            Regs[i] = 0L;
            Qi[i] = null;
        }
    }
    public void setReg(int regNum, long value){
    	checkRegNum(regNum);
    	Regs[regNum] = value;
    }
    public long getReg(int regNum){
    	checkRegNum(regNum);
    	return Regs[regNum];
    }
    private void checkRegNum(int regNum){
    	if(regNum<0 && regNum>=REGS){
    		System.out.println("Wrong register number");
    		System.exit(0);
    	}
    }
    //set the reservation station that register waiting for
    public void setQi(String stationName,int regNum){
    	Qi[regNum] = stationName;
    }
    //get the reservation station that register waiting for
    public String getQi(int regNum){
    	checkRegNum(regNum);
    	return Qi[regNum];
    }
    /**
     * update the registers from common data bus
     * update the valuse if the source of common data bus
     * and the reservation station that register is waiting for
     * is the same
     * @param paramCDB
     */
    public void updateRegisters(CDB paramCDB){
		for (int i = 0; i < REGS; i++){
			if ((Qi[i] != null) && (Qi[i].equals(paramCDB.getSrc()))){
				if (((this instanceof GPR)) && (i == 0)); //in case of r0
				else{
					Regs[i] = paramCDB.getResult();
					Qi[i] = null;
				}
			}
		}
	}
    /**
     * check the availability of a register by checking the value of 
     * reservation station that register is waiting for
     * @param regNum
     * @return true for available and false otherwise
     */
    public boolean isAvailable(int regNum){
    	if(Qi[regNum]!=null)
    		return false;
    	return true;
    }
    //output contents of Register File
    public void dumpRow(int start, int count){
        int i, k;
        i = start;
        for (k = 0; k < count; k++, i++){
            if (Qi[i] != null)
                System.out.print(Tools.pad(Qi[i], 16, " ", Direction.RIGHT) + " ");
            else
                System.out.print(Tools.pad(Long.toHexString(Regs[i]), 16, "0",
                                 Direction.RIGHT) + " ");
        }
        System.out.println();
    }

    //These two functions are used by the GUI
    public long[] cloneRegs(){
        return Regs.clone();
    }

    public String[] cloneQi(){
        return Qi.clone();
    }
}
