package com.controller;

import com.db.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import xspring.RestData;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Controller
public class TestController
{
    @GetMapping("/test/login")
    public Object login(HttpServletResponse response) throws Exception
    {
//        User user = new User();
//        user.name = "mingjie";
//        user.password = "991030";
//
//        String name = "user";
//        String jsonStr = new Gson().toJson(user);
        String user = "mingjie";

        Cookie cookie = new Cookie("user", user);
        //缓存保存30天
        cookie.setMaxAge(30*24*60*60);

        response.addCookie(cookie);

        return "redirect:/test/index";
    }

    @GetMapping("/test/index")
    public Object index(HttpServletRequest request) throws Exception
    {
        Cookie[] cookies = request.getCookies();
        if(cookies != null)
        {
            for(Cookie c: cookies)
            {
                String name = c.getName();
                if(name.equals("user"))
                {
                    Gson gson = new Gson();
                    String value = c.getValue();
//                    JsonObject json = gson.fromJson(value, JsonObject.class);
//                    User user = gson.fromJson(json, User.class);
                    if(value != null)
                        return new RestData("你好!" + value);
                }
            }
        }

        return new RestData("请登录");
    }
}
