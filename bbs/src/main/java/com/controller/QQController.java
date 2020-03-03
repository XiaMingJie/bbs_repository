package com.controller;

import com.db.MyBatis;
import com.db.User;
import com.db.UserAbility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.util.HTTP;
import com.util.MyUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.rmi.CORBA.Util;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Random;

/**
 *  QQ登录服务接口
 * */
@Controller
public class QQController {

    final String appID = "101852737";
    final String appSecret = "7abf2eb622804bccff516f06fc5e26d9";
    final String callbackUrl = "http://www.mingjieyun.cn/qq/callback";

    /*
    *  跳转到 QQ 的登录授权页面
    * */
    @GetMapping("/qq/useOAuth")
    public String useOAuth(HttpSession session, HttpServletResponse response) throws Exception
    {
        //产生一个随机码，用于鉴别回调的安全性
        int rand = 10000 + new Random().nextInt(10000);
        String qqState = String.valueOf(rand);
        session.setAttribute("qqState", qqState);
//        Cookie cookie = new Cookie("qqState", qqState);
//        cookie.setMaxAge(1000*60*30);
//        response.addCookie(cookie);

        String qqAuthUrl = "https://graph.qq.com/oauth2.0/authorize"
                + "?response_type=code"
                + "&client_id=" + appID
                + "&state=" + qqState
                + "&redirect_uri=" + callbackUrl
                ;

        return "redirect:" + qqAuthUrl;
    }

    /**
     *  当用户在 QQ 页面完成授权后，浏览器将跳转回到此地址（回调地址）
     * */
    @GetMapping("/qq/callback")
    public String callback(HttpSession session,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           String code,
                           String state) throws Exception
    {
        /* 校验此次访问是否合法 */
        String qqState = (String) session.getAttribute("qqState");
//        Cookie[] cookies = request.getCookies();
//        if(cookies == null)
        if(!qqState.equals(state))
        {
            //重新登录
            return "redirect:/login";
        }

//        for(Cookie c: cookies)
//        {
//            if(c.getName().equals("qqState"))
//            {
//                if(!state.equals(c.getValue()))
//                {
//                    //重新登录
//                    return "redirect:/login";
//                }
//            }
//        }

        session.removeAttribute("qqState");

        /* 与 QQ 后台交互，取得 AccessToken */
        String url2 = "https://graph.qq.com/oauth2.0/token"
                + "?grant_type=authorization_code"
                + "&client_id=" + appID
                + "&client_secret=" + appSecret
                + "&code=" + code
                + "&redirect_uri=http://www.mingjieyun.cn/qq/callback"
                ;

        HTTP http = new HTTP();
        System.out.println("获取AccessToken：" + url2);

        String reply2 = http.get(url2, null);
        String accessToken = parseAccessToken(reply2);
        if(accessToken == null || accessToken.length() == 0)
            throw new Exception("无法解析出accessToken，应答为：" + reply2);

        /* 获取用户的 OpenID */
        String url3 = "https://graph.qq.com/oauth2.0/me"
                + "?access_token=" + accessToken;
        String reply3 = http.get(url3, null);
        System.out.println("reply3：" + reply3);
        String openid = parseOpenID(reply3);
        System.out.println("用户的openid=" + openid);

        try(SqlSession db = MyBatis.factory.openSession())
        {
            User user = db.selectOne("mapper.user.query_openid", openid);

            if(user == null)
            {
                String url4 = "https://graph.qq.com/user/get_user_info" +
                        "?access_token=" + accessToken
                        + "&oauth_consumer_key=" + appID
                        + "&openid=" + openid
                        ;

                String reply4 = http.get(url4, null);
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(reply4, JsonObject.class);

                user = new User();
                user.name = json.get("nickname").getAsString() + "-" + MyUtil.guid2(); //使用QQ昵称
                String thumb = json.get("figureurl_qq_2").getAsString(); //100*100像素QQ头像
                if(thumb == null || thumb.length() == 0)
                    thumb = json.get("figureurl_qq_1").getAsString(); //40*40像素QQ头像
                user.thumb = thumb;
                user.qqid = openid;
                user.isAdmin = false;
                user.vip = (byte)0;
                user.vipName = "";
                user.level = 0;

                //插入一条qq数据
                db.insert("mapper.user.insert", user);

                //用户权利
                UserAbility userAbility = new UserAbility();
                UserAbilityUtil.init(userAbility, user.id);

                db.insert("mapper.userAbility.insert", userAbility);

                db.commit(true);
            }

            LoginUserUtil.login(session, user);
        }

        return "forward:/message/list";
    }

    //从应答中提取access_token值
    private String parseAccessToken(String reply)
    {
        String[] sss = reply.split("&");
        for(String s: sss)
        {
            String[] aa = s.split("=");
            if(aa[0].equals("access_token"))
            {
                return aa[1];
            }
        }
        return null;
    }

    //从应答中提取 openid，示范： callback({"client_id":"101810266","openid":"F89F25F0C6EAC7003E586D8AB860D555"});
    private String parseOpenID(String reply)
    {
        int p1 = reply.indexOf('(');
        if(p1 > 0)
        {
            int p2 = reply.indexOf(')', p1+1);
            if(p2 > 0)
            {
                String jsonstr = reply.substring(p1+1, p2);
                System.out.println("jsonstr:" + jsonstr);
                JsonObject json = (JsonObject) new Gson().fromJson(jsonstr, JsonObject.class);
                if(json.has("openid"))
                {
                    return json.get("openid").getAsString();
                }
                else
                {
                    System.out.println("应答中没有openid字段！" + reply);
                }
            }
        }
        return null;
    }

}
