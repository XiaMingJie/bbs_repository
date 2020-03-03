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
	public Object hello(HttpSession session)
	{
		session.setAttribute("code", 1002);

		return "redirect:/view?returnUrl=/goodby";
	}

	@GetMapping("/goodby")
	public Object goodby(HttpSession session)
	{
		int code = (int)session.getAttribute("code");

		JsonObject json = new JsonObject();
		json.addProperty("code", code);
		return new RestData(json);
	}

	@GetMapping("/view")
	public Object view(HttpSession session, String returnUrl)
	{
		return "redirect:"+returnUrl;
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
