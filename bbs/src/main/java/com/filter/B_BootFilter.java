package com.filter;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.task.*;
import org.springframework.core.annotation.Order;

@WebFilter(filterName = "bootFilter")
public class B_BootFilter implements Filter
{
	DailyTaskManager daily = new DailyTaskManager();

	public B_BootFilter()
	{

	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException
	{
		// TODO Auto-generated method stub
		daily.addTask(new MsgDelTask(), "清理message表");
		daily.addTask(new TmpFileClearTask(
				new File(arg0.getServletContext().getRealPath("/tmp"))), "清理/tmp过期文件");
		daily.addTask(new UserAbilityInitTask(), "初始化用户权利");

		daily.addTask(new CollectDelTask(), "清理collect表");
		
		daily.doStart();
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		daily.doQuit();
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException
	{
		// TODO Auto-generated method stub
		arg2.doFilter(arg0, arg1);
	}

}
