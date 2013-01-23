/***
 * The TSGui class is the main driving class for the representation of the simulation.
 * This class can simply be instantiated with a list of CycleSnapShots and it will
 * provide the user with the representation of the simulation complete with controls.
 * 
 * Author:	Stephen Ellison, Jr.
 */

package tsgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

import simulator.Simulator;


public class TSGui extends JFrame implements ActionListener{

	public Canvas canvas;
	public MemPanel memory;
	private ArrayList<CycleSnapShot> snapshots;
	private int frameIndex;
	
	private final static int slowSpeed = 2000;
	private final static int mediumSpeed = 1000;
	private final static int fastSpeed = 500;
	private int selectedSpeed = mediumSpeed;
	private Timer timer;
	
	private JButton startButton;
	private JTextField cycleGoTo;
	private JTextField pcGoTo;
	
	public TSGui(ArrayList<CycleSnapShot> snapshots)
	{
		super("Tomasulo Simulator");
		this.snapshots = snapshots;
		timer = new Timer();
		if (snapshots.size() <= 0)
		{
			System.out.println(snapshots.size() + " No snapshots found");
			System.exit(0);
		}
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(100, 50);
        setSize(new Dimension(900, 700));
        setMinimumSize(new Dimension(900, 700));
        setResizable(true);
        setLayout(new BorderLayout());
        
        add(createMenu(), BorderLayout.NORTH);
        
        canvas = new Canvas(this, snapshots.get(0));
        add(canvas, BorderLayout.CENTER);
        
        memory = new MemPanel();
        add(memory, BorderLayout.EAST);
                       
        // designPanelLayout();
        populateGuiFromSnapshot(0);
        pack();
		setVisible(true);
	}
	
	/***
	 * Builds the menu bar for the dM client and returns the JMenuBar object
	 * @return
	 */
	private JMenuBar createMenu()
	{
		JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        JMenu speedMenu = new JMenu("Speed");
        
        JMenuItem quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.addActionListener(this);
              
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);
        
        JRadioButtonMenuItem slow = new JRadioButtonMenuItem("Slow");
        slow.addActionListener(this);
        JRadioButtonMenuItem medium = new JRadioButtonMenuItem("Medium");
        medium.addActionListener(this);
        medium.setSelected(true);
        JRadioButtonMenuItem fast = new JRadioButtonMenuItem("Fast");
        fast.addActionListener(this);
        
        ButtonGroup speed = new ButtonGroup();
        speed.add(slow);
        speed.add(medium);
        speed.add(fast);
        speedMenu.add(slow);
        speedMenu.add(medium);
        speedMenu.add(fast);
        
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(this);
        helpMenu.add(aboutMenuItem);
        
        menuBar.add(fileMenu);
        menuBar.add(speedMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
        
        return menuBar;
	}
	
	private void populateGuiFromSnapshot(int index)
	{
		CycleSnapShot css = snapshots.get(index);
		canvas.setPC(css.getPC());
		canvas.setInstrReg(css.getInstruction());
		canvas.setCDB(css.getValue(), css.getSrc());
		fillMemory(css);
		fillRegs(css);
		canvas.updateFUnits(css);
		canvas.setCycle(css.getCycle());
	}
	
	private void fillMemory(CycleSnapShot css)
	{
		for (int i = 0; i < css.getMemSize(); i++)
		{
			memory.setMemLocation(i, css.getMemoryValueAt(i));
		}
	}
	
	private void fillRegs(CycleSnapShot css)
	{
		for (int i = 0; i < css.getRegistersSize(); i++)
		{
			canvas.setFPR(i, css.getFPRRegAt(i), css.getFPRQiAt(i));
			canvas.setGPR(i, css.getGPRRegAt(i), css.getGPRQiAt(i));
		}
	}
	
	private void goToCycle(int cycleNumber)
	{
		frameIndex = (cycleNumber >= 0 && cycleNumber < snapshots.size())? cycleNumber : frameIndex;
		populateGuiFromSnapshot(frameIndex);
	}
	
	private void goToPC(int pcNumber)
	{
		if (pcNumber%4 != 0)
			return;
		
		for (int i = 0; i < snapshots.size(); i++)
		{
			if (snapshots.get(i).getPC() == pcNumber)
			{
				frameIndex = i;
				populateGuiFromSnapshot(frameIndex);
				return;
			}
		}
	}
	
	public int getFrameIndex()
	{
		return frameIndex;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Quit"))
			System.exit(0);
		else if (e.getActionCommand() == "Start")
		{
			canvas.setStartButtonEnabled(false);
			timer = new Timer();
			timer.schedule(new ExecuteCycle(this), 0, selectedSpeed);
		}
		else if (e.getActionCommand() == "Stop")
		{
			canvas.setStartButtonEnabled(true);
			timer.cancel();
		}
		else if (e.getActionCommand() == "Next")
		{
			frameIndex = (frameIndex >= snapshots.size()-1)? frameIndex : frameIndex + 1;
			populateGuiFromSnapshot(frameIndex);
		}
		else if (e.getActionCommand() == "Prev")
		{
			frameIndex = (frameIndex <= 0)? frameIndex : frameIndex - 1;
			populateGuiFromSnapshot(frameIndex);
		}
		else if (e.getActionCommand() == "GoTo Cycle")
		{
			goToCycle(canvas.getCycleNumber());
		}
		else if (e.getActionCommand() == "GoTo PC")
		{
			goToPC(canvas.getPCNumber());
		}
		else if (e.getActionCommand() == "Slow")
		{
			selectedSpeed = slowSpeed;
			stopAndRestartTimer();
		}
		else if (e.getActionCommand() == "Medium")
		{
			selectedSpeed = mediumSpeed;
			stopAndRestartTimer();
		}
		else if (e.getActionCommand() == "Fast")
		{
			selectedSpeed = fastSpeed;
			stopAndRestartTimer();
		}
		else if (e.getActionCommand() == "About")
		{
			String aboutInfo = "Graphical View for Tomasulo Simulator\n" +
							   "following the guidelines from CS5483 @ ASU\n" +
							   "by: Stephen Ellison, Jr.\n" +
							   "version 1.0";
			JOptionPane.showMessageDialog(this, aboutInfo, "About", 1);
		}
	}
	
	private void stopAndRestartTimer()
	{
		timer.cancel();
		timer = new Timer();
		timer.schedule(new ExecuteCycle(this), 0, selectedSpeed);
	}

	public class ExecuteCycle extends TimerTask
	{
		private TSGui parent;

		public ExecuteCycle(TSGui parent)
		{
			this.parent = parent;
		}
		
		@Override
		public void run() 
		{
			frameIndex = (frameIndex >= snapshots.size()-1)? frameIndex : frameIndex + 1;
			parent.populateGuiFromSnapshot(frameIndex);
		}
		
	}
}
