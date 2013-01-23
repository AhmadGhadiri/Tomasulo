/***
 * The Canvas class holds most of the graphical elements in cluding the functional units, the registers,
 * user controls and other relevant information.  This class also provides the functionality to update 
 * these elements to the main GUI class.
 * 
 * Author:	Stephen Ellison, Jr.
 */

package tsgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tsgui.CycleSnapShot;


public class Canvas extends JPanel{

	public FunctionalUnit fpadderUnit;
	public FunctionalUnit fpdiv;
	public FunctionalUnit fpmult;
	public FunctionalUnit integerunit;
	public FunctionalUnit memoryunit;
	public FunctionalUnit branchunit;
	public HashMap<String, FunctionalUnit> funits;
	private JFrame mainWindow;
	
	private JButton startButton;
	private JTextField cycle;
	private JTextField PC;						// accessor available to set value
	private InstrReg instrReg;
	private CDBgui cdb;
	private Register fpr;
	private Register gpr;
	
	public Canvas(JFrame mainWindow, CycleSnapShot css)
	{
		this.mainWindow = mainWindow;
		setSize(400, 400);
		setMinimumSize(new Dimension(400,400));
		setMaximumSize(new Dimension(400,400));
		setBorder(BorderFactory.createLineBorder(Color.black));
		setLayout(new BorderLayout());
		
		
		add(buildRegistersAndControls(), BorderLayout.EAST);

		add(buildPCAndInstrQAndCDB(css), BorderLayout.WEST);
		
	}
	
	private JPanel buildRegistersAndControls()
	{
		JPanel complete = new JPanel();
		complete.setLayout(new BorderLayout());
		
		JPanel registers = new JPanel();
		gpr = new Register("GPR");
		registers.add(gpr);
		fpr = new Register("FPR");
		registers.add(fpr);
		
		JPanel controls = new JPanel();
		controls.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 10, 5, 10);
		c.gridx = 0;
		c.gridy = 0;
		JButton button = new JButton("Prev");
		button.addActionListener((ActionListener)mainWindow);
		controls.add(button, c);
		c.gridx++;
		button = new JButton("Next");
		button.addActionListener((ActionListener)mainWindow);
		controls.add(button, c);
		
		c.gridx = 0;
		c.gridy++;
		button = new JButton("Stop");
		button.addActionListener((ActionListener)mainWindow);
		controls.add(button, c);		
		c.gridx++;
		startButton = new JButton("Start");
		startButton.addActionListener((ActionListener)mainWindow);
		controls.add(startButton, c);
		
		complete.add(registers, BorderLayout.NORTH);
		complete.add(controls, BorderLayout.CENTER);
		return complete;
	}
//	
//	private JPanel buildFunits()
//	{
//		funits = new HashMap<String, FunctionalUnit>();
//		JPanel funitsPanel = new JPanel();
//		funitsPanel.setLayout(new GridLayout(3,2));
//		fpadderUnit = new FunctionalUnit("FPAdder",5, mainWindow, 4);
//		funits.put(fpadderUnit.getName(), fpadderUnit);
//		fpdiv = new FunctionalUnit("FPDiv",5, mainWindow, 4);
//		funits.put(fpdiv.getName(), fpdiv);
//		fpmult = new FunctionalUnit("FPMult",5, mainWindow, 4);
//		funits.put(fpmult.getName(), fpmult);
//		integerunit = new FunctionalUnit("Integer",5, mainWindow, 4);
//		funits.put(integerunit.getName(), integerunit);
//		memoryunit = new FunctionalUnit("Memory",5, mainWindow, 4);
//		funits.put(memoryunit.getName(), memoryunit);
//		branchunit = new FunctionalUnit("Branch",5, mainWindow, 4);
//		funits.put(branchunit.getName(), branchunit);
//		funitsPanel.add(fpadderUnit);
//		funitsPanel.add(fpdiv);
//		funitsPanel.add(fpmult);
//		funitsPanel.add(integerunit);
//		funitsPanel.add(memoryunit);
//		funitsPanel.add(branchunit);
//		
//		return funitsPanel;
//	}
//	
	private JPanel buildFunitswithSnapshot(CycleSnapShot css)
	{
		funits = new HashMap<String, FunctionalUnit>();
		JPanel funitsPanel = new JPanel();
		funitsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(20, 10, 20, 10);
		ArrayList<FUnitImage> unitImages = css.getFUnits();
		for (int i = 0; i < unitImages.size(); i++)
		{
			c.gridx = (i%2 == 0)? 0 : 1;
			c.gridy = i/2;
			FunctionalUnit tUnit = new FunctionalUnit(unitImages.get(i), mainWindow);
			funits.put(unitImages.get(i).name, tUnit);
			funitsPanel.add(tUnit, c);
		}
				
		return funitsPanel;
	}
	
	private JPanel buildPCAndInstrQAndCDB(CycleSnapShot css)
	{
		JPanel counterLabels = new JPanel();
		counterLabels.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = c.WEST;
		c.gridy = 0;
		c.gridx = 0;
		counterLabels.add(new JLabel("PC: "), c);
		PC = new JTextField(Integer.toHexString(0), 3);
		PC.setHorizontalAlignment(JTextField.TRAILING);
		c.gridx++;
		counterLabels.add(PC, c);
		JButton button = new JButton("GoTo PC");
		button.addActionListener((ActionListener)mainWindow);
		c.gridx++;
		c.anchor = c.EAST;
		counterLabels.add(button, c);
		
		c.anchor = c.WEST;
		c.gridy++;
		c.gridx = 0;
		counterLabels.add(new JLabel("Cycle: "), c);
		cycle = new JTextField("" + 0, 3);
		cycle.setHorizontalAlignment(JTextField.TRAILING);
		c.gridx++;
		counterLabels.add(cycle, c);
		c.gridx++;
		c.anchor = c.EAST;
		button = new JButton("GoTo Cycle");
		button.addActionListener((ActionListener)mainWindow);
		counterLabels.add(button, c);
		
		c.anchor = c.WEST;
		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 3;
		instrReg = new InstrReg();
		instrReg.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
		counterLabels.add(instrReg, c);
		
		JPanel complete = new JPanel();
		complete.setLayout(new BorderLayout());
		complete.add(counterLabels, BorderLayout.NORTH);
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		
		center.add(buildFunitswithSnapshot(css), BorderLayout.CENTER);
		complete.add(center, BorderLayout.CENTER);
		
		JPanel cdbPanel = new JPanel();
		cdbPanel.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 25, 0);
		cdb = new CDBgui();
		cdbPanel.add(cdb, c);
		complete.add(cdbPanel, BorderLayout.SOUTH);
		complete.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		
		return complete;
	}
	
	public void setCycle(int cycle)
	{
		this.cycle.setText("" + cycle);
	}
	
	public void setPC(int val)
	{
		PC.setText(Integer.toHexString(val));
	}
	
	public void setCDB(long val, String name)
	{
		cdb.setNameAndValue(val, name);
	}
	
	public void setInstrReg(int instr)
	{
		instrReg.setInstruction(instr);
	}
	
	public void setGPR(int index, long value, String name)
	{
		gpr.setRegister(index, value, name);
	}
	
	public void setFPR(int index, long value, String name)
	{
		fpr.setRegister(index, value, name);
	}
	
	public int getPCNumber()
	{
		return Integer.parseInt(PC.getText(), 16);
	}
	
	public int getCycleNumber()
	{
		return Integer.parseInt(cycle.getText());
	}

//	
//	public void setRSinFunit(String funitName, int index, String operation, long Vj, long Vk, String Qj, String Qk, long A)
//	{
//		FunctionalUnit unit = funits.get(funitName);
//		if (unit != null)
//			unit.setRSEntry(index, operation, Vj, Vk, Qj, Qk, A);
//		else
//			System.out.println("Null value from HashMap with key: " + funitName);
//	}
	
	public void setRSinFUnit(FunctionalUnit funit, FUnitImage unitImage)
	{
		for (int i = 0; i < unitImage.RS.length; i++)
			funit.setRSEntry(i, unitImage.RS[i]);
	}
	
	public void updateFUnits(CycleSnapShot css)
	{
		ArrayList<FUnitImage> units = css.getFUnits();
		
		for (int i = 0; i < units.size(); i++)
		{
			FunctionalUnit unit = funits.get(units.get(i).name);
			if (units.get(i).currentInstruction >= 0)
				unit.setInstructionLabel("" + units.get(i).currentInstruction);
			else
				unit.setInstructionLabel("NA");
			if (units.get(i).FUbusy)
			{
				int cyclesLeft = units.get(i).executionCycle;
				unit.setCyclesLeft(cyclesLeft);
			}
			else
			{
				unit.setCyclesLeft(0);
			}
			unit.setBusyLabel(units.get(i).FUbusy);
			setRSinFUnit(unit, units.get(i));
		}
	}

	public void setStartButtonEnabled(boolean setting)
	{
		startButton.setEnabled(setting);
	}
	
}
