package simulator;
import java.util.*;

//This class is used to keep statistics about instructions.
class StatusTable{
    //information stored about each instruction
    class Entry{
        String instruction;    
        int issue;
        int startEX;
        int endEX;
        int write;
        String station;
    }

    Vector <StatusTable.Entry> v;
    static StatusTable statPtr = null;

    //StatusTable class is a singleton
    static StatusTable getInstance(){
        if (statPtr == null)
            statPtr = new StatusTable();
        return statPtr;
    }

    //private constructor since StatusTable is a singleton
    private StatusTable(){
        v = new Vector<StatusTable.Entry>(2000);
    }

    //When the instruction gets issued, this function needs to be
    //called.
    void addInstruction(String instruction, String station){
        Entry entry = new Entry();
        entry.instruction = instruction;
        entry.station = station;
        entry.issue = Clock.getInstance().get();
        entry.startEX = -1;
        entry.endEX = -1;
        entry.write = -1;
        v.addElement(entry);
    }

    //When an instruction in the station indicated begins execution,
    //this function must be called.
    void updateStartEX(String station){
        int i;
        for (i = 0; i < v.size(); i++)
            if (v.elementAt(i).station.equals(station) && 
                v.elementAt(i).startEX == -1){
                v.elementAt(i).startEX = Clock.getInstance().get();
                return;
            }
        System.out.println("ERROR: couldn't find startEX for station "
                           + station);
    }

    //When an instruction in the station indicated ends execution,
    //this function must be called.
    void updateEndEX(String station){
        int i;
        for (i = 0; i < v.size(); i++)
            if (v.elementAt(i).station.equals(station) && 
                v.elementAt(i).endEX == -1){
                v.elementAt(i).endEX = Clock.getInstance().get();
                return;
            }
        System.out.println("ERROR: couldn't find endEX for station "
                           + station);
    }

    //When an instruction in the station indicated writes its result,
    //this function must be called.
    void updateWrite(String station){
        int i;
        for (i = 0; i < v.size(); i++){
            if (v.elementAt(i).station.equals(station) && 
                v.elementAt(i).write == -1){
                v.elementAt(i).write = Clock.getInstance().get();
                return;
            }
        }
        System.out.println("ERROR: couldn't find write for station "
                           + station);
    }

    //output the statistics about each instruction
    void dump(){
        int i;
        System.out.println(Tools.pad("Instruction", 20, " ", Direction.LEFT) +
                           Tools.pad("Issue",  7, " ", Direction.RIGHT) +
                           Tools.pad("Start",  7, " ", Direction.RIGHT) +
                           Tools.pad("End",    7, " ", Direction.RIGHT) +
                           Tools.pad("Write",  7, " ", Direction.RIGHT));
        for (i = 0; i < v.size(); i++){
            System.out.print(Tools.pad(v.elementAt(i).instruction, 20, " ",
                                       Direction.LEFT) +
                             Tools.pad(Integer.toString(v.elementAt(i).issue), 
                                       7, " ", Direction.RIGHT));

            if (v.elementAt(i).startEX != -1)
                System.out.print(Tools.pad(Integer.toString(v.elementAt(i).startEX), 
                                           7, " ", Direction.RIGHT));
            if (v.elementAt(i).endEX != -1)
                System.out.print(Tools.pad(Integer.toString(v.elementAt(i).endEX), 
                                           7, " ", Direction.RIGHT));
            if (v.elementAt(i).write != -1)
                System.out.println(Tools.pad(Integer.toString(v.elementAt(i).write), 
                                             7, " ", Direction.RIGHT));
            else 
                System.out.println();
        }
    }
}
