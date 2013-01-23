/***
 * The Decoder class is the grab bag of logic needed to decode the suibset of mips instructions
 * from the integer value.
 * 
 * Author:	Stephen Ellison, Jr.
 */

package tsgui;

import java.util.HashMap;

import simulator.Tools;

public class Decoder {

	public static String decodeInstruction(int instr)
	{
		String instruction = "";
		int opcode = Tools.grabBits(instr, 0, 5);
		
		if (opcode == 0)						// rtype instruction
		{
			instruction = decodeRType(instr);
		}
		else if (opcode <= 3 || opcode == 44)	// jtype instruction
		{
			instruction = decodeJType(instr);
		}
		else									// itype instruction
		{
			instruction = decodeIType(instr);
		}
		
		return instruction;
	}
	
	private static String decodeRType(int instr)
	{
		String decodedInstr = "";
		HashMap<Integer, String> opcodes = fillOpcodes();
		
		int rs = Tools.grabBits(instr, 6, 10);
		int rt = Tools.grabBits(instr, 11, 15);
		int rd = Tools.grabBits(instr, 16, 20);
		int funct = Tools.grabBits(instr, 25, 31);
		char registerType = (funct == 44 || funct == 46) ? 'r' : 'f';
		
		decodedInstr = opcodes.get(funct) + " " + registerType + rd + " " + 
					   registerType + rs + " " + registerType + rt;
		
		return decodedInstr;
	}
	
	private static String decodeIType(int instr)
	{
		String decodedInstr = "";
		HashMap<Integer, String> opcodes = fillOpcodes();
		
		int opcode = Tools.grabBits(instr, 0, 5);
		int rs = Tools.grabBits(instr, 6, 10);
		int rt = Tools.grabBits(instr, 11, 15);
		int imm = Tools.grabBits(instr, 16, 31);
		char registerType = (opcode == 53 || opcode == 61) ? 'f' : 'r';
		if (opcode != 25)
			imm = Tools.signExtend(16, imm);
		imm = (opcode == 4 || opcode == 5)?imm*4 : imm;
		
		if (opcode == 24 || opcode == 25 || opcode == 4 || opcode == 5)
			decodedInstr = opcodes.get(opcode) + " " + registerType + rt + 
			" r" + rs + " " + imm;
		else
			decodedInstr = opcodes.get(opcode) + " " + registerType + rt + 
						" " + imm + " r" + rs;
		
		return decodedInstr;
	}
	
	private static String decodeJType(int instr)
	{
		String decodedInstr = "";
		HashMap<Integer, String> opcodes = fillOpcodes();
		
		int opcode = Tools.grabBits(instr, 0, 5);
		int offset = Tools.grabBits(instr, 6, 31);
		
		int imm = offset;
		
		decodedInstr = "" + opcodes.get(opcode) + " " + offset;
		
		return decodedInstr;
	}
	
	public static boolean isValidInstruction(int instr)
	{
		HashMap<Integer, String> opcodes = fillOpcodes();
		int opcode = Tools.grabBits(instr, 0, 5);
		int funct = Tools.grabBits(instr, 25, 31);
		if (opcode != 0)
			return (opcodes.containsKey(opcode))? true : false;
		else if (funct >= 44 && funct <= 50)
		{
			return true;
		}
		else
			return false;
	}
	
	private static HashMap<Integer, String> fillOpcodes()
	{
		HashMap<Integer, String> opcodes = new HashMap<Integer, String>();
		opcodes.put(55,"ld");
		opcodes.put(53,"l.d");
		opcodes.put(63,"sd");
		opcodes.put(61,"s.d");
		opcodes.put(24,"daddi");
		opcodes.put(25,"daddiu");
		opcodes.put(4, "beq");
		opcodes.put(5, "bne");
		opcodes.put(2,"j");
		opcodes.put(1,"halt");
		opcodes.put(3,"nop");
		opcodes.put(44,"dump");
		opcodes.put(44,"dadd");
		opcodes.put(46,"dsub");
		opcodes.put(47,"add.d");
		opcodes.put(48,"sub.d");
		opcodes.put(49,"mul.d");
		opcodes.put(50,"div.d");
		
		
		return opcodes;
	}
	
}
