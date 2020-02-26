package com.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil
{
	public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final int YEAR = 1;
	public static final int MONTH = 2;
	public static final int DAY = 3;
	public static final int HOUR = 4;
	public static final int MINUTE = 5;
	public static final int SECOND = 6;
	//取得当前日期的年、月、日
	public static int getCurrentDateStr(int tag) throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date d = sdf.parse(sdf.format(new Date()));

		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		if(tag == YEAR)
			return c.get(Calendar.YEAR);
		else if(tag == MONTH)
			return c.get(Calendar.MONTH);
		else if(tag == DAY)
			return c.get(Calendar.DAY_OF_MONTH);
		else if(tag == HOUR)
			return c.get(Calendar.HOUR_OF_DAY);
		else if(tag == MINUTE)
			return c.get(Calendar.MINUTE);
		else if(tag == SECOND)
			return c.get(Calendar.SECOND);
		
		return 0;
	}
	
	//取得自己的日期
	public static int getDateStr(String time, int tag) throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date d = sdf.parse(time);

		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		if(tag == YEAR)
			return c.get(Calendar.YEAR);
		else if(tag == MONTH)
			return c.get(Calendar.MONTH);
		else if(tag == DAY)
			return c.get(Calendar.DAY_OF_MONTH);
		else if(tag == HOUR)
			return c.get(Calendar.HOUR_OF_DAY);
		else if(tag == MINUTE)
			return c.get(Calendar.MINUTE);
		else if(tag == SECOND)
			return c.get(Calendar.SECOND);
		
		return 0;
	}
	
	//格式化日期
	public static String getDateFormat(String time, String format) throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = sdf.parse(time);
		return sdf.format(date);
	}
	
	//日期转字符串
	public static String Date2Str(Date date, String format) throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	//字符串转日期
	public static Date Str2Date(String time, String format) throws Exception
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(time);
	}
	
	//自动处理年月日时分秒:当前发的显示时间，以前发的显示日期
	public static String format(String timeStr)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(new Date());
		
		if(timeStr.indexOf(today) == 0)
			return timeStr.substring(11, 16);
		else
			return timeStr.substring(5, 10);
	}
	
	//统一简单处理
	public static String simpleExecute(Date date) throws Exception
	{
		return format(Date2Str(date, FORMAT));
	}
}
