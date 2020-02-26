package com.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DailyTaskManager extends Thread
{
	private List<TaskInfo> taskList = new ArrayList<>();
	private boolean quitFlag = false;
	
	public void doStart()
	{
		start();
	}
	
	public void doQuit()
	{
		quitFlag = true;
		interrupt();
	}
	
	public void addTask(Task task, String name)
	{
		TaskInfo info = new TaskInfo();
		info.name = name;
		info.task = task;
		taskList.add(info);
	}
	
	@Override
	public void run()
	{
		while(!quitFlag)
		{
			for(TaskInfo info: taskList)
			{
				try
				{
					Thread.sleep(1*1000);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				executeEveryDay(info);
			}
		}
	}
	
	private void executeEveryDay(TaskInfo info)
	{
		Calendar cal = Calendar.getInstance();
		int today = cal.get(Calendar.DAY_OF_YEAR);
		int toHour = cal.get(Calendar.HOUR_OF_DAY);
		if(today == info.lastValue)
			return; //今天已执行完成
		
		if(toHour < 5)
		{
			try {
				//执行任务
				System.out.printf("** 执行定期任务 [ %s ] : 。。\n", info.name);
				info.task.execute();
				info.status = 0;
				System.out.printf("** 定期任务[ %s ]: OK! \n", info.name);
			}catch(Exception e )
			{
				String reason = e.getMessage();
				if(reason == null) e.getClass().getName();
				System.out.printf("** 定期任务[ %s ]: 失败! \n", info.name);
				info.status = -1;
				e.printStackTrace();
			}
		}
		
		info.lastValue = today;
		info.lastTime = cal.getTimeInMillis();
	}
	
	private static class TaskInfo
	{
		String name = "";
		long lastTime = 0;
		int lastValue = -1;
		int fromHour = -1;
		int toHour = -1;
		int status = 0;
		int numRetry = 0;
		Task task;
	}
}
