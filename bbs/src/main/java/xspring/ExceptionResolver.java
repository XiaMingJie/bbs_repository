package xspring;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

@Order(-100) //异常处理优先级，值越小优先级越高
public class ExceptionResolver implements HandlerExceptionResolver
{

	@Override
	public ModelAndView resolveException(HttpServletRequest request, 
			HttpServletResponse response, Object handler, Exception exception)
	{
		//获取URI
		String uri = request.getRequestURI();
		
		//根据后缀名判断是否为REST请求
		if(uri.endsWith(".do"))
		{
			//构建Model
			Map<String, Object> model = new HashMap<String, Object>();
			//构建异常处理的View，将异常传进去
			View view = new RestError(exception);
			System.out.println("访问:" + uri + "时出错，错误信息:" + exception.getMessage());
			exception.printStackTrace();
			//返回一个ModelAndView类
			return new ModelAndView(view, model);
		}
		//不是REST请求则是MVC请求
		else
		{
			//构建Model
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("message", exception.getMessage());
			
			//异常信息
			StringWriter strWriter = new StringWriter();
			exception.printStackTrace(new PrintWriter(strWriter));
			model.put("data", strWriter.toString());
			
			return new ModelAndView("error", model);
		}
	}

}
