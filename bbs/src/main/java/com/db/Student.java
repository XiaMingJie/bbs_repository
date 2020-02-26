package com.db;

import java.util.Date; 

/** 本类由 POJO生成器 自动生成于 2020-01-31 17:31:22
    作者：阿发你好      官网: http://afanihao.cn 
*/ 

/** INSERT语句 ( 预处理方式 ) 
  INSERT INTO `student`
        (`id`, `name`, `sex`, `cellphone`) 
  VALUES(?, ?, ?, ?) 
*/ 

/** INSERT语句 ( MyBatis方式 ) 
  INSERT INTO `student`
        (`id`, `name`, `sex`, `cellphone`) 
  VALUES(#{id}, #{name}, #{sex}, #{cellphone}) 

  自增主键: 无
*/ 

public class Student 
{ 
 
	public Integer id ; 
	public String name ; 
	public Boolean sex ; 
	public String cellphone ; 


	public void setId(Integer id)
	{
		this.id=id;
	}
	public Integer getId()
	{
		return this.id;
	}
	public void setName(String name)
	{
		this.name=name;
	}
	public String getName()
	{
		return this.name;
	}
	public void setSex(Boolean sex)
	{
		this.sex=sex;
	}
	public Boolean getSex()
	{
		return this.sex;
	}
	public void setCellphone(String cellphone)
	{
		this.cellphone=cellphone;
	}
	public String getCellphone()
	{
		return this.cellphone;
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return "学号:" + id + ", 姓名:" + name + ", 性别:" + sex + ", 手机号:" + cellphone;
	}
} 
 