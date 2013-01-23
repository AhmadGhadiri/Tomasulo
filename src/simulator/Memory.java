package simulator;
import java.io.*;
import java.math.BigInteger;

//This class is incomplete.  You'll still need methods for
//loading memory and getting and setting 32 and 64 bit
//words.  Remember accesses must be aligned.

public class Memory{
    private static Memory mem;
    private int [] memory;
    private int size;

    private Memory(int size){
        this.size = size;
        memory = new int[size];
    }
    public int getSize(){
		return size;
	}
	public void setSize(int size){
		this.size = size;
	}
	/**
	 * read the input file, decode it and load it into memory as integers
	 * @param inputFile name of the input file
	 */
    public void loadMemory(String inputFile){
		int pc = 0;
		try{
			RandomAccessFile raf = new RandomAccessFile(new File(inputFile),"rw");
			String strLine;
			int decimalNumber;
			while ((strLine = raf.readLine())!=null){
				strLine = strLine.substring(0, 8); //get the first 8 char(hex instruction)
				decimalNumber = new BigInteger(strLine, 16).intValue(); //decode the hex into integer
				loadInstructionInMemory(pc, decimalNumber);
				pc += 4;
			}
			raf.close();
		}
		catch (Exception e){
			System.out.println("Error:");
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}
    /**
     * read the instruction from memory
     * @param index = PC of the program
     * @return instruction as an integer
     */
    public int getInstruction(int index){
    	if (index % 4 != 0){ //check to see if the index is aligned
			System.out.println("Wrong number for PC");
			System.exit(0);
		}
		return memory[(index / 4)];
    }
    /**
     * put one instruction into memory
     * @param pc PC of the program
     * @param decimalNumber integer to put in memory
     */
    public void loadInstructionInMemory(int pc, int decimalNumber){
		if (pc % 4 != 0){
			System.out.println("Wrong number for PC");
			System.exit(0);
		}
		memory[(pc / 4)] = decimalNumber;
	}
    /**
     * get two integer from memory and combine them as a long and
     * return the number
     * @param number: index in memory(module of eight)
     * @return long from memory
     */
    public long getLongFromMemory(long number){
		if (number % 8L != 0L){
			System.out.println("Wrong number for access in memory");
			System.exit(0);
		}
		long l1 = 0L;
		try{
			int i = memory[((int)number / 4)];
			int j = memory[(((int)number + 4) / 4)];
			long l3 = i;
			l3 &= 0xFFFFFFFFL; //bitwise AND with FFFFFFFF long
			long l2 = j; 
			l2 <<= 32;		//shift 32 bits to left
			l1 = l2 | l3; 	//bitwise OR to get the result
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			System.exit(0);
		}
		return l1;
	}
    /**
     * get a long and put it in the memory in a big endian bit numbering manner
     * @param number1
     * @param number2
     */
	public void putLongInMemory(long number1, long number2){
		if (number1 % 8L != 0L){
			System.out.println("Wrong number for access in memory");
			System.exit(0);
		}
		try{
			int i = (int)(number2 & 0xFFFFFFFF); //bitwise AND to get the integer
			int j = (int)(number2 >> 32 & 0xFFFFFFFF); //shift 32 bits to right and AND bitwise to get the second integer
			//put the numbers in memory big endian
			memory[((int)number1 / 4)] = i;
			memory[(((int)number1 + 4) / 4)] = j;
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}
	/**
	 * get the static instance of memory
	 * @return
	 */
    public static Memory getInstance(){
        if (mem == null) mem = new Memory(4000);
        return mem;
    }

    //helper function for dumping memory
    private String buildLine(int i){
        String line;
        int j;
        line = new String();

        for (j = i; j < i + 8; j++){
            line = line + Tools.pad(Integer.toHexString(memory[j]), 8, 
                                    "0", Direction.RIGHT) + " ";
        }
        return line;
    }

    //output contents of memory
    public void dump(){
        int address = 0;
        String lastline = new String("junk");
        String nextline;
        boolean star = false, needNewline = false;
        for (int i = 0; i < memory.length; i+=8){
            nextline = buildLine(i);
            if (! lastline.equals(nextline)){
                star = false;
                if (needNewline) System.out.println(); 
                System.out.print(Tools.pad(Integer.toHexString(address), 4, "0",
                                           Direction.RIGHT) + ":\t");
                System.out.print(nextline);  
                needNewline = true;
            } 
			else if (lastline.equals(nextline) && (star == false)){
               System.out.println(" *");
               needNewline = false;
               star = true;
            } 
            address = address + 32;
            lastline = nextline;
        }
        System.out.println();
    }

    //needed by the GUI
    public int[] cloneMemory(){
        return memory.clone();
    }
}
