package simulator;

// General Purpose Registers
public class GPR extends Registers{
    private static GPR gpr;

    //constructor is private since GPR is singleton
    private GPR(){
        super();
    }

    //This class is a singleton
    public static GPR getInstance(){
        if (gpr == null) gpr = new GPR();
        return gpr;
    }
   
    //output the header of one row
    public void dumpHeading(int j){
        String Headings[] = {"R0-R3:   ", "R4-R7:   ", "R8-R11:  ",
                             "R12-R15: ", "R16-R19: ", "R20-R23: ", 
                             "R24-R27: ", "R28-R31: "};
        System.out.print(Headings[j]);
    }

    //output the contents of the register file
    public void dump(){
        int i, j;
        for (j = 0, i = 0; j < 8; j++, i+=4){
            dumpHeading(j);
            dumpRow(i, 4);
        }
        System.out.println();
    }
}
