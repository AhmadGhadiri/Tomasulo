package simulator;
public class PC{
    int value;  //time in cycles
    static PC PCptr = null;

    //class is a singleton so constructor is private
    private PC(){
        value = 0;
    }

    //returns singleton instance
    static PC getInstance(){
        if (PCptr == null)
            PCptr = new PC();
        return PCptr;
    }

    //returns current PC in cycles
    int get(){
        return value;
    }

    //set the PC
    void set(int newValue){
        value = newValue;
    }
}
