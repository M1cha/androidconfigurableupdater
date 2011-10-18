package org.m1cha.android.configurableupdater.customexceptions;

@SuppressWarnings("serial")
public class Long2IntegerException extends Exception
{
	String mistake;
	  
	public Long2IntegerException() {
		super();
		mistake = "unknown";
	}
	  
	public Long2IntegerException(String err) {
		super(err);
		mistake = err;
	}
	public String getError() {
		return mistake;
	}
}
