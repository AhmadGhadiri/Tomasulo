/***
 * The CDBGui class graphically represents the information for the common data bus.
 * 
 * Author:	Stephen Ellison, Jr.
 */

package tsgui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class CDBgui extends JPanel {

	private JLabel valLabel;
	private JLabel nameLabel;
	
	public CDBgui()
	{
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = c.NONE;
		c.anchor = c.CENTER;
		setBorder(BorderFactory.createLineBorder(Color.black));
		add(new JPanel().add(new JLabel("CDB")), c);
		valLabel = new JLabel("" + 0);
		nameLabel = new JLabel("none");
		JPanel value = new JPanel();
		value.add(new JLabel("Value: "));
		value.add(valLabel);
		JPanel name = new JPanel();
		name.add(new JLabel("Source: "));
		name.add(nameLabel);
		c.gridy++;
		add(value, c);
		c.gridy++;
		add(name, c);
	}
	
	public void setValue(long val)
	{
		valLabel.setText("" + val);
	}
	
	public void setName(String name)
	{
		nameLabel.setText(name);
	}
	
	public void setNameAndValue(long val, String name)
	{
		valLabel.setText("" + val);
		nameLabel.setText(name);
	}
}
