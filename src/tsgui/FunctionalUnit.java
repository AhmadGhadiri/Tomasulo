/***
 * The FunctionalUnit class is the graphical representation of a Functional Unit.  This class
 * is designed to use the information from the FUnitImage class in order to be independent of
 * different simulator implementations.
 * 
 * Stephen Ellison, Jr.
 */

package tsgui;

/**
 * This class is designed to provide a graphical container to put information regarding the functional
 * units.  Instantiate this class for each functional unit.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import simulator.*;

public class FunctionalUnit extends JPanel implements MouseListener{

	private String name;
	private int instruction;
	private int cyclesLeft;
	private int executionCount;
	private int numRS;				// number of reservation stations that feed this unit
	
	private JLabel instructionLabel;
	private JLabel cyclesLeftLabel;
	private JLabel busyLabel;
	
	private JFrame mainWindow;
	private JFrame rsWindow;
	private RStation rs;
	
		
	public FunctionalUnit(FUnitImage unit, JFrame mainWindow)
	{
		instruction = 0;
		cyclesLeft = 0;
		this.name = unit.name;
		this.numRS = unit.RSCount;
		this.mainWindow = mainWindow;
		this.executionCount = unit.executionCount;
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.lightGray, Color.black));
		setSize(200, 400);
		setLayout(new BorderLayout());
		
		JLabel nameLabel = new JLabel(name);
		nameLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		nameLabel.setHorizontalAlignment(0);
		add(nameLabel, BorderLayout.NORTH);
		
		JPanel instrPanel = new JPanel();
		instrPanel.setLayout(new GridBagLayout());		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2, 4, 2, 4);
		c.anchor = c.WEST;
		c.fill = c.HORIZONTAL;
		busyLabel = new JLabel("N");
		busyLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
		instrPanel.add(new JLabel("Busy? "), c);
		c.gridx++;
		instrPanel.add(busyLabel, c);
		instructionLabel = new JLabel("NA");
		c.gridx = 0;
		c.gridy++;
		instrPanel.add(new JLabel("Executing RS: "), c);
		c.gridx++;
		instrPanel.add(instructionLabel, c);
		
		c.gridx = 0;
		c.gridy++;
		cyclesLeftLabel = new JLabel("" + cyclesLeft);
		instrPanel.add(new JLabel("Cycles left: "), c);
		c.gridx++;
		instrPanel.add(cyclesLeftLabel, c);
		add(instrPanel, BorderLayout.CENTER);
		createRSWindow(unit.name, unit.RSCount);
		
		this.addMouseListener(this);
	}
	
	public void setInstructionLabel(String instr)
	{
		instructionLabel.setText(instr);
	}
	
	public void setCyclesLeft(int num)
	{
		cyclesLeftLabel.setText("" + num);
	}
	
	public void setBusyLabel(boolean busy)
	{
		if (busy)
			busyLabel.setText("Y");
		else
			busyLabel.setText("N");
	}
	
	private void createRSWindow(String name, int RSCount)
	{
		rsWindow = new JFrame(name + " Reservation Station");
		rsWindow.setVisible(false);
		rsWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		rsWindow.setLocation(250, 100);
		rsWindow.setMinimumSize(new Dimension(320, 190));
		rs = new RStation(name, RSCount);
		rsWindow.add(rs);
	}
	
	public void setRSEntry(int index, StationImage station)
	{
		rs.setEntry(index, station.operation, station.Vj, station.Vk, station.Qj, station.Qk, station.A);
	}
	
	public String getName()
	{
		return name;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		rsWindow.setVisible(true);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		setBackground(Color.lightGray);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		setBackground(Color.white);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.lightGray, Color.black));
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.lightGray, Color.black));
	}
}
