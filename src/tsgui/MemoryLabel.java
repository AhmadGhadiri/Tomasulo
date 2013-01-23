/***
 * MemoryLabel provides a simple method of storing and representing the value at a memory location.
 * 
 * Author:	Stephen Ellison, Jr.
 */

package tsgui;


public class MemoryLabel{

	int value;
	
	public MemoryLabel(int value)
	{
		this.value = value;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public String toString()
	{
		return toZeroPaddedHex(value);
	}
	
	private String toZeroPaddedHex(int val)
	{
		String hex = Integer.toHexString(val);
		String padding = "";
		int len = hex.length();
		
		for (int i = 0; i < 8-len; i++)
		{
			padding += "0";
		}
		
		return padding + hex;
	}
}
