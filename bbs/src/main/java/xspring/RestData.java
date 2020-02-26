package xspring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class RestData extends RestView
{
	Object data;
	
	public RestData(Object data)
	{
		this.data = data;
	}

	@Override
	public void render(Map<String, ?> model, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception
	{
		JsonObject jresp = new JsonObject();
		Gson gson = new Gson();
		jresp.addProperty("error", 0);
		jresp.addProperty("reason", "OK");
		
		if(data != null)
		{
			if(data instanceof JsonObject)
				jresp.add("data", gson.toJsonTree(data));
			else if(data instanceof JsonArray)
				jresp.add("data", (JsonArray)gson.toJsonTree(data));
			else
				jresp.addProperty("data", gson.toJson(data));
		}
		
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.getWriter().print(jresp);
	}

}
