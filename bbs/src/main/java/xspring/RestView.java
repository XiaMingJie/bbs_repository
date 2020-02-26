package xspring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

public class RestView implements View
{

	@Override
	public void render(Map<String, ?> model, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception
	{
		
	}

}
