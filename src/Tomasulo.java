import simulator.*;
import java.io.*;


class Tomasulo
{
    public static void main(String args[]) throws IOException{
        Simulator TS;
        if ((args.length == 0) || (args[0].endsWith(".hex") == false)){
            System.out.println("Usage: java Tomasulo <filename>.hex [-gui]");
            System.exit(1);
        }
        if (!((new File(args[0])).exists())){
            System.out.println(args[0] + " does not exist");
            System.out.println("Usage: java Tomasulor <filename>.hex [-gui]");
            System.exit(0);
        }
        boolean gui = false;
        if (args.length > 1 && args[1].equals("-gui")) gui = true;

        TS = new Simulator(args[0], gui);
        TS.simulate();
    }
}
