package com.db; 

import java.util.Date; 

/** 本类由 POJO生成器 自动生成于 2020-02-21 16:51:47
    作者：阿发你好      官网: http://afanihao.cn 
*/ 

/** INSERT语句 ( 预处理方式 ) 
  INSERT INTO `collect`
        (`id`, `collector`, `msgId`, `name`, `time`, `delFlag`) 
  VALUES(?, ?, ?, ?, ?, ?) 
*/ 

/** INSERT语句 ( MyBatis方式 ) 
  INSERT INTO `collect`
        (`id`, `collector`, `msgId`, `name`, `time`, `delFlag`) 
  VALUES(#{id}, #{collector}, #{msgId}, #{name}, #{time}, #{delFlag}) 

  自增主键: id
*/ 

public class Collect 
{ 
 
	public Long id ; 
	public Integer collector ; 
	public Long msgId ; 
	public String name ; 
	public Date time ; 
	public Boolean delFlag ; 


	public void setId(Long id)
	{
		this.id=id;
	}
	public Long getId()
	{
		return this.id;
	}
	public void setCollector(Integer collector)
	{
		this.collector=collector;
	}
	public Integer getCollector()
	{
		return this.collector;
	}
	public void setMsgId(Long msgId)
	{
		this.msgId=msgId;
	}
	public Long getMsgId()
	{
		return this.msgId;
	}
	public void setName(String name)
	{
		this.name=name;
	}
	public String getName()
	{
		return this.name;
	}
	public void setTime(Date time)
	{
		this.time=time;
	}
	public Date getTime()
	{
		return this.time;
	}
	public void setDelFlag(Boolean delFlag)
	{
		this.delFlag=delFlag;
	}
	public Boolean getDelFlag()
	{
		return this.delFlag;
	}

} 
 