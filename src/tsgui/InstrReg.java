/***
 * InstrReg provides the means to update and represent the instruction register.
 * 
 * Author:	Stephen Ellison, Jr.
 */

package tsgui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class InstrReg extends JPanel {

	int instr;		// instruction to display
	JLabel instrLabel;
	
	public InstrReg()
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.black));
		add(new JLabel("IR: "), BorderLayout.WEST);
		instrLabel = new JLabel("none");
		instr = 0;
		add(instrLabel, BorderLayout.CENTER);
	}
	
	public void setInstruction(int instruction)
	{
		instr = instruction;
		if (Decoder.isValidInstruction(instr))
			instrLabel.setText(Decoder.decodeInstruction(instr));
		else
			instrLabel.setText("none");
	}
}
