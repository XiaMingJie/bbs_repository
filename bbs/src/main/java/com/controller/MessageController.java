package com.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.google.gson.JsonObject;

import com.db.Message;
import com.db.MyBatis;
import com.db.User;
import com.db.UserAbility;
import com.util.DateUtil;
import com.util.MyUtil;
import com.util.TmpFile;
import xspring.RestData;
import xspring.RestError;

@Controller
public class MessageController
{
	@GetMapping("/u/message/add")
	public String add()
	{
		return "message/add";
	}
	
	@PostMapping("/u/message/save.do")
	public Object add(@RequestBody Message row,
			@SessionAttribute User user,
			@SessionAttribute UserAbility userAbility,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session) throws Exception
	{
		if(user == null) throw new Exception("尚未登录!");
		
		if(userAbility.msgCount <= 0) return new RestError("今天发帖数量太多，请明天再试！MAX=" + userAbility.msgMax);
		
		// title, content字段由前端传送过来
		row.creator = user.id; // 创建者
		row.cat1 = row.cat2 = row.cat3 = 0;
		row.ref1 = 0L; // ref1=0表示这一条为主帖(楼主) 
		row.ref2 = 0L;		
		row.timeCreate = new Date(); // 发贴时间
		row.numReply = 0;
		row.numLike = 0;
		row.imgCount = 0;		
		row.niceFlag = 0;
		row.topFlag = 0;
		row.banFlag = false;
		row.delFlag = false;
		row.closeFlag = false;
		row.replyUser = user.id;
		row.replyTime = row.timeCreate;
		row.replyText = row.title;
		row.replyName = user.name;
		
		//附件图片路径 示例 202002/01/15273155904/
	    row.storePath = makeStorePath();

	    row.img1 = moveTmptoStore(request, row.storePath, row.img1);
	    row.img2 = moveTmptoStore(request, row.storePath, row.img2);
	    row.img3 = moveTmptoStore(request, row.storePath, row.img3);
		
		try(SqlSession db = MyBatis.factory.openSession())
		{
			db.insert("mapper.message.insert", row);
			db.commit(true);
		}
		
		//发文次数减一
		try{UserAbilityUtil.useMsgCount(userAbility);}catch(Exception e) {}
		
		return new RestData("");
	}
	
	//临时文件名由前端传递过来
	public String moveTmptoStore(HttpServletRequest request, 
			String storePath, String tmpName)
	{
		if(tmpName == null || tmpName.length() == 0) return "";
		
		//获得临时文件
		File tmpFile = TmpFile.getFile(request, tmpName);
		//获得本地存储路径
		File storeFile = MsgImgController.store.getFile(storePath);
		try
		{
			//移动文件
			FileUtils.moveFileToDirectory(tmpFile, storeFile, true);
			//String path = storePath + tmpName;
			return tmpName;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	@GetMapping("/android/message/list")
	public Object android_list()
	{
		try(SqlSession db = MyBatis.factory.openSession())
		{
			List<Map> result = db.selectList("mapper.message.android_query");

			Gson gson = new Gson();
			JsonArray jresp = (JsonArray) gson.toJsonTree(result);
			return new RestData(jresp);
		}
	}
	
	@GetMapping("/message/list")
	public String list(Model model,
			String filter,
			Integer plate,
			HttpSession session,
			Integer pageNumber) throws Exception
	{
		try(SqlSession db = MyBatis.factory.openSession())
		{
			if(pageNumber == null) pageNumber = 1;
			if(filter == null || filter.length() == 0) filter = "";
			if(plate == null) plate = 0;
			
			int count = db.selectOne("mapper.message.count");

			//一页显示的数目
			int pageSize = 20;
			int pageCount = count / pageSize;
			if((count % pageSize != 0)) pageCount += 1;
			
			int startItem = pageSize * (pageNumber - 1);
			
			Map<String, Object> parms = new HashMap<String, Object>();
			parms.put("startItem", startItem);
			parms.put("pageSize", pageSize);
			parms.put("filter", filter);

			if(plate == 1)
			{
				User user = (User) session.getAttribute("user");
				if(user == null)
					throw new Exception("尚未登录!");
				else
					parms.put("userId", user.id);
			}

			parms.put("plate", plate);
			
			List<Map> messageList = db.selectList("mapper.message.query", parms);
			
			for(Map m: messageList)
			{
				Date time = (Date) m.get("timeCreate");
				String timeCreate = DateUtil.Date2Str(time, "yyyy-MM-dd HH:mm:ss");
				timeCreate = DateUtil.format(timeCreate);
				m.put("timeCreate", timeCreate);
				
				time = (Date) m.get("replyTime");
				if(time != null)
				{
					String replyTime = DateUtil.Date2Str(time, "yyyy-MM-dd HH:mm:ss");
					replyTime = DateUtil.format(replyTime);
					m.put("replyTime", replyTime);
				}
			}

			//筛选主贴
//			Iterator iterator = messageList.iterator();
//			while(iterator.hasNext())
//			{
//				HashMap<String, Object> map = (HashMap<String, Object>) iterator.next();
//				long ref1 = (long)map.get("ref1");
//				//搜索条件下不用删除
//				if(ref1 != 0 && filter.length() == 0)
//					iterator.remove();
//			}
			
			model.addAttribute("messageList", messageList);
			model.addAttribute("pageNumber", pageNumber);
			model.addAttribute("pageSize", pageSize);
			model.addAttribute("pageCount", pageCount);
			model.addAttribute("count", count);
			model.addAttribute("filter", filter);
			model.addAttribute("plate", plate);
		}
		
		return "message/list";
		
	}
	
	// 附件图片存储路径 示例 201911/01/15725791906031/
	public static String makeStorePath()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM/dd/");
		return sdf.format(new Date()) + MyUtil.guid2() + "/";
	}
	
	//删除帖子（回复帖子一并删掉）
	@PostMapping("/u/message/userSetFlags.do")
	public Object usetSetFlags(@SessionAttribute User user,
			@RequestBody String sreq) throws Exception
	{
		Gson gson = new Gson();
		JsonObject jreq = gson.fromJson(sreq, JsonObject.class);

		try(SqlSession db = MyBatis.factory.openSession())
		{
			long msgId = jreq.get("msgId").getAsLong();
			String cmd = jreq.get("cmd").getAsString();
			
			if(!cmd.equals("del"))
			{
				if(user.isAdmin == null || !user.isAdmin)
					return new RestError("仅管理员可操作");
			}
			
			//原文
			Message ref = db.selectOne("mapper.message.creator", msgId);
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("id", msgId);
			
			if(cmd.equals("del"))
			{
				//判断当前用户与帖子用户是否一致
				if(ref.creator.intValue() == user.id || user.isAdmin)
					db.update("mapper.message.del", msgId);
				else
					throw new Exception("无权删除此帖");
			}
			else if(cmd.equals("top"))
			{
				p.put("topFlag", ref.topFlag == 0 ? 1 : 0);
				db.update("mapper.message.top", p);
			}
				
			else if(cmd.equals("nice"))
			{
				p.put("niceFlag", ref.niceFlag == 0 ? 1 : 0);
				db.update("mapper.message.nice", p);
			}
			
			db.commit(true);
		}
		
		return new RestData("");
	}
}
