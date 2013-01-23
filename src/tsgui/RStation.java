/***
 * The RStation class provides the graphical representation of the reservation stations.
 * 
 * Author:	Stephen Ellison, Jr.
 */

package tsgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class RStation extends JPanel{

	public String name;
	public int RSCount;
	public String[][] entries;
	private JTable stations;
	
	
	public RStation(String name, int RSCount)
	{
		this.name = name;
		this.RSCount = RSCount;
		entries = new String[RSCount][6];
		for (int i = 0; i < RSCount; i++)
		{
			entries[i][0] = "";
			entries[i][1] = "0";
			entries[i][2] = "0";
			entries[i][3] = "";
			entries[i][4] = "0";
			entries[i][5] = "";
		}
		
		String[] header = {"Op", "Vj", "Vk", "Qj", "Qk", "A"};
		
		stations = new JTable(entries, header);
		
		JScrollPane spane = new JScrollPane(stations);
		stations.setFillsViewportHeight(true);
		spane.setPreferredSize(new Dimension(300, 160));
				
		add(spane);
	}
	
	public void setEntry(int entry, String operation, long Vj, long Vk, String Qj, String Qk, long A)
	{
		stations.setValueAt(operation , entry, 0);
		stations.setValueAt("" + Vj , entry, 1);
		stations.setValueAt("" + Vk , entry, 2);
		stations.setValueAt(Qj , entry, 3);
		stations.setValueAt(Qk , entry, 4);
		stations.setValueAt("" + A , entry, 5);           		               
	}

}
