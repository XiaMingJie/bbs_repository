package com.controller;

import org.apache.ibatis.session.SqlSession;

import com.db.MyBatis;
import com.db.UserAbility;

public class UserAbilityUtil
{
	
	public static void init(UserAbility ual, int userId)
	{
		ual.userId = userId;
		ual.banDate = null;
		
		//每天10张图片，5个帖子，20条回复
		ual.imageCount = ual.imageMax = 10;
		ual.msgCount = ual.msgMax = 5;
		ual.replyCount = ual.replyMax = 20;
	}
	
	public static void useImageCount(UserAbility ual)
	{
		ual.imageCount -= 1;
		
		try(SqlSession session = MyBatis.factory.openSession())
		{
			session.update("mapper.userAbility.imageCount", ual.userId);
			session.commit(true);
		}
	}
	
	public static void useMsgCount(UserAbility ual)
	{
		ual.msgCount -= 1;
		
		try(SqlSession session = MyBatis.factory.openSession())
		{
			session.update("mapper.userAbility.msgCount", ual.userId);
			session.commit(true);
		}
	}
	
	public static void useReplyCount(UserAbility ual)
	{
		ual.replyCount -= 1;
		
		try(SqlSession session = MyBatis.factory.openSession())
		{
			session.update("mapper.userAbility.replyCount", ual.userId);
			session.commit(true);
		}
	}
}
