package xspring;

public class SpringException extends RuntimeException
{
	int error = -1;
	String reason = "";
	
	public SpringException(){};
	
	public SpringException(int error)
	{
		this.error = error;
	}
	
	public SpringException(String reason)
	{
		this.reason = reason;
	}
	
	public SpringException(int error, String reason)
	{
		this.error = error;
		this.reason = reason;
	}
}
