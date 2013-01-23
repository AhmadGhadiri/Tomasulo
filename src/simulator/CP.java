package simulator;
/**
 * THis class serves as a base for holding the values
 * of enums and other constants used in this program
 * creating this class makes it easier to change the
 * program later and it aggregates all the constants
 * in one place
 * @author Ahmad
 */
public class CP {
	/**
	 * Instruction Opcodes for using in the simulator
	 * @author Ahmad
	 *
	 */
	public static class InsOps{
		public static final int lType[] = {4,5,24,25,53,55,61,63}; // The opcode numbers for ltype instructions
		public static final int rtypeFC[] = {44,46,47,48,49,50}; // The opcode numbers for rtype instructions
		public static final int jtype[] = {1,2,3}; // The opcode numbers for jtype instructios
		public static final int dump = 44;
		public static final int nop = 3;
		public static final int halt = 1;
		public static final int j = 2;
		public static final String ltypeName[] = {"beq","bne","daddi","daddiu","l.d","ld","s.d","sd"};
		public static final String rtypeName[] = {"dadd","dsub","add.d","sub.d","mul.d","div.d"};
		public static final String dumpName = "dump";
	}
	public int IntegerUnitRSCount;
	public int FPAdderUnitRSCount;
	public int FPMultUnitRSCount;
	public int FPDivUnitRSCount;
	public int BranchUnitRSCount;
	public int MemoryRSCount;
	public int IntegerExecutionTime;
	public int FPAdderExecutionTime;
	public int FPMultExecutionTime;
	public int FPDivExecutionTime;
	public int jumpBranchExecutionTime;
	public int BranchExecutionTime;
	public int MemoryExecutionTime;
	/**
	 * constructor for class CP
	 * set the RScounts and
	 * Execution times for each 
	 * functional unit
	 */
	private CP(){
		IntegerUnitRSCount = 4;
		FPAdderUnitRSCount = 4;
		FPMultUnitRSCount = 4;
		FPDivUnitRSCount = 4;
		BranchUnitRSCount = 1;
		MemoryRSCount = 4;
		IntegerExecutionTime = 7;
		FPAdderExecutionTime = 13;
		FPMultExecutionTime = 15;
		FPDivExecutionTime = 17;
		BranchExecutionTime = 9;
		jumpBranchExecutionTime = 2;
		MemoryExecutionTime = 11;
	}
	public static CP allConst = null; // a global instant of the Constant Parameters class
	public static CP getInstance(){
        if (allConst == null)
            allConst = new CP();
        return allConst;
    }
}