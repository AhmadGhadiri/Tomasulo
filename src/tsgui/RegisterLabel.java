/***
 * RegisterLabel provides a simple implementation to display register values.
 * 
 * Author:	Stephen Ellison, Jr.
 */

package tsgui;

public class RegisterLabel{

	public long value;
		
	public RegisterLabel(long value)
	{
		this.value = value;
	}
	
	public void setValue(long value)
	{
		this.value = value;
	}
	
	public long getValue()
	{
		return value;
	}
	
	public String toString()
	{
		return Long.toHexString(value);
	}
}
