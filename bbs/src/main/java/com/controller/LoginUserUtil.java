package com.controller;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;

import com.db.MyBatis;
import com.db.User;
import com.db.UserAbility;

public class LoginUserUtil
{
	public static void login(HttpSession session, User user)
	{
		try(SqlSession db = MyBatis.factory.openSession())
		{
			session.setAttribute("user", user);
			UserAbility userAbility = db.selectOne("mapper.userAbility.query", user.id);
			session.setAttribute("userAbility", userAbility);
		}
	}
	
	public static void logout(HttpSession session)
	{
		session.removeAttribute("user");
		session.removeAttribute("userAbility");
	}
}
