package xspring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

public class RestError extends RestView
{
	public int error = -1;
	public String reason = "";
	
	public RestError(int error)
	{
		this.error = error;
	}
	
	public RestError(String reason)
	{
		this.reason = reason;
	}
	
	public RestError(Exception e)
	{
		if(e instanceof SpringException)
		{
			SpringException e2 = (SpringException) e;
			error = e2.error;
			reason = e2.reason;
		}
		else
		{
			error = -1;
			reason = e.getMessage();
		}
		
		if(reason == null)
			this.reason = e.getClass().getName();
	}
	
	@Override
	public void render(Map<String, ?> model
			, HttpServletRequest request
			, HttpServletResponse response) throws Exception
	{
		JsonObject jresp = new JsonObject();
		jresp.addProperty("error", error);
		jresp.addProperty("reason", reason);
		
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.getWriter().print(jresp);
	}

}
