package simulator;

//class to represent the Floating Point Registers
public class FPR extends Registers{
    private static FPR fpr;

    //constructor is private since class is a singleton
    private FPR(){
        super();
    }
 
    //get reference to the single instance
    public static FPR getInstance(){
        if (fpr == null) fpr = new FPR();
        return fpr;
    }

    //output the header of one row
    public void dumpHeading(int j){
        String Headings[] = {"F0-F3:   ", "F4-F7:   ", "F8-F11:  ",
                             "F12-F15: ", "F16-F19: ", "F20-F23: ", 
                             "F24-F27: ", "F28-F31: "};
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
