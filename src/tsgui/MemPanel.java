/***
 * The MemPanel represents the system memory in a JTable with the address,
 * the value in memory and the value decoded if it is deemed to be an instruction.
 * This class also provides the means with which to update it's information.
 * 
 * Author:	Stephen Ellison, Jr.
 */

package tsgui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import simulator.*;

public class MemPanel extends JPanel {

	public MemoryLabel[] memory;
	private JTable table;
	
	public MemPanel()
	{
		memory = new MemoryLabel[4000];
		String[] header = {"Address", "Value", "Decoded"};
		Object[][] tableEntries= new Object[4000][3];
		for (int i = 0; i < 4000; i++)
		{
			tableEntries[i][0] = toPaddedHex(i*4);  //CN - change to byte addresses
			memory[i] = new MemoryLabel(0);
			tableEntries[i][1] = memory[i];
			int val = memory[i].getValue();
			tableEntries[i][2] = (!Decoder.isValidInstruction(val))? "NA" : Decoder.decodeInstruction(val);
		}
		
		table = new JTable(tableEntries, header);
		table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
		TableColumn column = table.getColumn("Address");
		column.setPreferredWidth(8);
		column = table.getColumn("Value");
		column.setPreferredWidth(25);
		TableCellRenderer centerRenderer = new CenterRenderer();
		table.setDefaultRenderer(String.class, centerRenderer);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setDefaultRenderer(String.class, new CenterRenderer());
		JScrollPane spane = new JScrollPane(table);
		spane.setPreferredSize(new Dimension(325, 625));
		table.setFillsViewportHeight(true);
		
		add(spane);
	}
		
	public void setMemLocation(int index, int value)
	{
		memory[index].setValue(value);
		String decodedInstr = (!Decoder.isValidInstruction(value))? "NA" : Decoder.decodeInstruction(value);
		table.setValueAt(memory[index], index, 1);
		table.setValueAt(decodedInstr, index, 2);
	}
	
	private String toPaddedHex(int val)
	{
		String hex = Integer.toHexString(val);
		String padding = "0x";
				
		return padding + hex;
	}
	
	/*
    **  Center the text 
    */
    class CenterRenderer extends DefaultTableCellRenderer
    {
        public CenterRenderer()
        {
            setHorizontalAlignment( RIGHT );
        }
 
        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return this;
        }
    }
}
