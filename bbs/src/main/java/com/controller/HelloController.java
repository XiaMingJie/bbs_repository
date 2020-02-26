package com.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import xspring.RestData;

@Controller
public class HelloController
{
	@GetMapping("/hello")
	public Object hello()
	{
		JsonObject json = new JsonObject();
		json.addProperty("data", "hello");
		return new RestData(json);
	}

	@GetMapping("/view.do")
	public Object view(HttpSession session)
	{
		String info = (String) session.getAttribute("info");
		return new RestData(info);
	}
	
	@GetMapping("/test.do")
	public Object test2(@SessionAttribute String info)
	{
		info = "test";
		return new RestData(info);
	}
	
	@GetMapping("/input.do")
	public Object login(HttpSession session)
	{
		String info = "hello";
		session.setAttribute("info", info);
		return new RestData(info);
	}
}
