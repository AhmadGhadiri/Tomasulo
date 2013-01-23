
/**
 * Class to specify the graphical representation of registers, instantiate this class
 * for both general purpose and floating point registers.
 * 
 * Author:	Stephen Ellison, Jr.
 */
package tsgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Register extends JPanel {

	private String name;
	public RegisterLabel[] values;
	private Object[][] entries;
	private JTable table;
	
	public Register(String name)
	{
		this.name = name;

		setBorder(BorderFactory.createLineBorder(Color.black));

		values = new RegisterLabel[32];
		entries = new Object[32][3];
		for (int i = 0; i < 32; i++)
		{
			entries[i][0] = "0x" + Integer.toHexString(i);
			entries[i][1] = new String("");
			entries[i][2] = new RegisterLabel(0);
			values[i] = (RegisterLabel)entries[i][2];
		}
		
		String[] header = {"Addr", "Qi", "Value"};
		
		table = new JTable(entries, header);
		
		JScrollPane spane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		spane.setPreferredSize(new Dimension(170, 500));
		
		setLayout(new BorderLayout());
		
		add(new JLabel(name), BorderLayout.NORTH);
		add(spane, BorderLayout.SOUTH);
	}
	
	public void setRegister(int index, long val, String Qi)
	{
		values[index].setValue(val);
		table.setValueAt(values[index], index, 2);
		table.setValueAt(Qi, index, 1);
	}
}
