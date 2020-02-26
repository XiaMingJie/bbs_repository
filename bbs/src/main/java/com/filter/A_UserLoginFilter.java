package com.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import com.db.User;

@WebFilter(filterName = "userLoginFilter", urlPatterns = "/u/*")
public class A_UserLoginFilter implements Filter
{

	public A_UserLoginFilter()
	{

	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		// TODO Auto-generated method stub
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		String uri = req.getRequestURI();
		
		//检查用户登录
		if(true)
		{
			User user = (User) req.getSession().getAttribute("user");
			
			if(user == null)
			{
				if(uri.endsWith(".do"))
				{
					JsonObject jresp = new JsonObject();
					jresp.addProperty("error", -100);
					jresp.addProperty("reason", "尚未登录!");
					resp.setCharacterEncoding("utf-8");
					resp.setContentType("application/json");
					resp.getWriter().write(jresp.toString());
					return;
				}
				else
				{
					//GET参数也带上
					String query = req.getQueryString();
					if(query != null && query.length() > 0)
						uri += "?" + query;
					
					//MVC请求： 返回302重定向
					resp.sendRedirect(req.getContextPath()
							+ "/login"
							+ "?returnUrl="
							+ uri);
					return;
				}
			}
		}
			
		chain.doFilter(request, response);
	}

}
