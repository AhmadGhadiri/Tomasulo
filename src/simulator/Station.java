package simulator;
public class Station{
    public String name;       //name of reservation station
    public boolean busy;      //is station holding an operationg
    public String operation;  //type of operation
    public long Vj;           //value of operand
    public long Vk;           //value of operand
    public String Qj;         //name of reservation station producing Vj
    public String Qk;         //name of reservation station producing Vk
    public long A;            //used to hold immediate field or off address
    public long result;       //used to hold result 
    /**
     * The resultReady helps Function Unit to check which Station is ready for 
     * the right and it is saving the result value
     */
    public boolean resultReady;   //flag indicating result is ready to be written
    public boolean resultWritten;  //flag indicating the result has been written

    public Station(String name){
        this.name = name;
        busy = false;
        operation = null;
        Vj = Vk = A = 0;
        Qj = Qk = null;
        resultReady = false;
        resultWritten = false;
    }
    //after result is written, clear the reservation station
    public void clear(){
        busy = false;
        operation = null;
        Vj = Vk = A = 0;
        Qj = Qk = null;
        resultReady = false;
        resultWritten = false;
    }

    //determines whether the operands are available and therefore ready
    //for execution
    public boolean ready(){
        return (busy == true && Qj == null && Qk == null && 
                resultReady == false);
    }

    //outputs the contents of the Station
    public void dump(){
        System.out.print(Tools.pad(name, 8, " ", Direction.RIGHT));
        System.out.print(Tools.pad(Boolean.toString(busy), 8, " ", Direction.RIGHT));
        if (operation == null)
            System.out.print(Tools.pad("null", 8, " ", Direction.RIGHT));
        else
            System.out.print(Tools.pad(operation, 8, " ", Direction.RIGHT));
        System.out.print(" ");
        System.out.print(Tools.pad(Long.toHexString(Vj), 16, "0", Direction.RIGHT));
        System.out.print(" ");
        System.out.print(Tools.pad(Long.toHexString(Vk), 16, "0", Direction.RIGHT));
        if (Qj == null)
            System.out.print(Tools.pad("null", 8, " ", Direction.RIGHT));
        else
            System.out.print(Tools.pad(Qj, 8, " ", Direction.RIGHT));
        if (Qk == null)
            System.out.print(Tools.pad("null", 8, " ", Direction.RIGHT));
        else
            System.out.print(Tools.pad(Qk, 8, " ", Direction.RIGHT));
        System.out.print(" ");
        System.out.println(Tools.pad(Long.toHexString(A), 16, "0", Direction.RIGHT));
    }

    public static void dumpHeader(){        
        System.out.print(Tools.pad("Name", 8, " ", Direction.RIGHT));
        System.out.print(Tools.pad("Busy", 8, " ", Direction.RIGHT));
        System.out.print(Tools.pad("Op", 8, " ", Direction.RIGHT));
        System.out.print(" ");
        System.out.print(Tools.pad("Vj", 16, " ", Direction.RIGHT));
        System.out.print(" ");
        System.out.print(Tools.pad("Vk", 16, " ", Direction.RIGHT));
        System.out.print(Tools.pad("Qj", 8, " ", Direction.RIGHT));
        System.out.print(Tools.pad("Qk", 8, " ", Direction.RIGHT));
        System.out.print(" ");
        System.out.println(Tools.pad("A", 16, " ", Direction.RIGHT));
    }
}
