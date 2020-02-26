package com;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PathAdapter
{
	@GetMapping({"", "/", "/index.html"})
	public String index()
	{
		return "forward:/message/list";
	}
}
