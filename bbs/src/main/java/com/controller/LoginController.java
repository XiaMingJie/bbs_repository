package com.controller;

import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.google.gson.JsonObject;

import com.db.MyBatis;
import com.db.User;
import com.db.UserAbility;
import com.util.MyUtil;
import xspring.RestData;
import xspring.RestError;

@Controller
public class LoginController
{
	@GetMapping("/register")
	public String register()
	{
		return "user/register";
	}
	
	@PostMapping("/register.do")
	public Object register(HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session,
			@RequestBody User user) throws Exception
	{
		user.name = user.name.trim();
		user.password = user.password.trim();
		user.isAdmin = false;
		user.vip = (byte)0;
		user.vipName = "";
		user.level = 0;

		if(user.password.length() > 0)
			user.password = MyUtil.md5(user.password);
		try(SqlSession db = MyBatis.factory.openSession())
		{
			List<User> userList = db.selectList("mapper.user.query");
			
			for(User u: userList)
			{
				if(u.name.equals(user.name))
					return new RestError("用户名已被占用!");
			}
			
			db.insert("mapper.user.insert", user);
			
			//添加用户权力
			UserAbility ual = new UserAbility();
			UserAbilityUtil.init(ual, user.id);
			
			db.insert("mapper.userAbility.insert", ual);
			db.commit(true);
		}
		
		return new RestData("");
	}
	
	@GetMapping("/login")
	//returnUrl：当出现重定向到该页面时，登录后需要返回到源页面，returnUrl即是传过来的源地址
	public String login(Model model, HttpSession session, String returnUrl)
	{
		User user = (User)session.getAttribute("user");
		if(user != null)
			return "forward:/message/list";

		if(returnUrl == null) returnUrl = "";

		model.addAttribute("returnUrl", returnUrl);
		return "user/login";
	}
	
	@PostMapping("/login.do")
	public Object login(HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session,
			@RequestBody String sreq) throws Exception
	{
		Gson gson = new Gson();

		JsonObject jreq = gson.fromJson(sreq, JsonObject.class);

		String name = jreq.get("name").getAsString();
		String password = jreq.get("password").getAsString();
		
		try(SqlSession db = MyBatis.factory.openSession())
		{
			User user = db.selectOne("mapper.user.login", name);
			
			if(user == null)
				return new RestError("该用户名不存在!");
			
			String pwdMd5 = MyUtil.md5(password);
			
			if(!user.password.equals(pwdMd5))
			//if(!user.password.equals(password))
				return new RestError("用户名密码不匹配");
			
			LoginUserUtil.login(session, user);
		}
		
		return new RestData("");
	}
	
	@GetMapping("/u/profile/edit")
	public String edit(@SessionAttribute User user, Model model)
	{
		model.addAttribute("user", user);
		return "user/profile";
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session)
	{
		LoginUserUtil.logout(session);
		
		//重定向到首页
		return "redirect:/message/list";
	}

	//更改密码
	@PostMapping("/u/user/password")
	public Object updatePwd(@SessionAttribute User user,
							@RequestBody String sreq,
							HttpSession session) throws Exception
	{
		if(user == null) return new RestError("尚未登录");

		Gson gson = new Gson();
		JsonObject jreq = gson.fromJson(sreq, JsonObject.class);
		String password = jreq.get("password").getAsString();

		if(password.length() > 0)
			user.password = password;

		try(SqlSession db = MyBatis.factory.openSession())
		{
			db.update("mapper.user.updatePwd", user);
			db.commit(true);
		}

		//修改密码后退出登录
		LoginUserUtil.logout(session);

		return new RestData("");
	}
}
