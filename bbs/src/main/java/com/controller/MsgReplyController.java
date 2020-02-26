package com.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.db.*;
import com.google.gson.Gson;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.google.gson.JsonObject;

import com.util.DateUtil;
import xspring.RestData;
import xspring.RestError;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MsgReplyController
{
	@GetMapping("/u/reply/add")
	public String add(@RequestParam long refId1,
					  Model model) throws Exception
	{
		try(SqlSession db = MyBatis.factory.openSession())
		{
			Message ref = db.selectOne("mapper.reply.query", refId1);

			if(ref == null)
				throw new Exception("无此记录: MsgId=" + refId1);
			
			model.addAttribute("ref", ref);
		}
		
		return "reply/add";
	}
	
	@PostMapping("/u/reply/save.do")
	public Object save(@RequestBody String sreq,
			@SessionAttribute User user,
			@SessionAttribute UserAbility userAbility) throws Exception
	{
		if(userAbility.replyCount <= 0) return new RestError("今日回复次数已用完！请明天再试！MAX=" + userAbility.replyMax);

		Gson gson = new Gson();

		JsonObject jreq = gson.fromJson(sreq, JsonObject.class);

		long refId1 = jreq.get("refId1").getAsLong();

		try(SqlSession db = MyBatis.factory.openSession())
		{
			Message original = db.selectOne("mapper.reply.query", refId1);
			if(original == null)
				throw new Exception("无此记录: MsgId=" + refId1);
			
			Message row = new Message();
			// title, content字段由前端传送过来
			row.creator = user.id; // 创建者
			row.title = jreq.get("title").getAsString();
			row.content = jreq.get("content").getAsString();
			row.cat1 = row.cat2 = row.cat3 = 0;
			row.ref1 = refId1; // ref1=0表示这一条为主帖(楼主)

			//若原文为主贴 则回复帖为一级回复 引文2为0
			if(original.ref1 == 0)
				row.ref2 = 0L;
			//若原文为一级回复 则回复为二级回复 引文2为原文的引文1
			else if(original.ref2 == 0)
				row.ref2 = original.ref1;
			//若原文为二级回复 则回复引文2为原文的引文2
			else if(original.ref2 > 0)
				row.ref2 = original.ref2;

			row.timeCreate = new Date(); // 发贴时间
			row.numReply = 0;
			row.numLike = 0;
			row.imgCount = 0;		
			row.niceFlag = 0;
			row.topFlag = 0;
			row.banFlag = false;
			row.delFlag = false;
			row.closeFlag = false;
			row.storePath = MessageController.makeStorePath();
			if (row.img1 == null)
				row.img1 = "";
			if (row.img2 == null)
				row.img2 = "";
			if (row.img3 == null)
				row.img3 = "";
			
			//插入数据
			db.insert("mapper.reply.insert", row);
			
			//更新统计数据
			if(true) 
			{
				Map<String, Object> parms = new HashMap<String, Object>();
				parms.put("msgId", refId1);
				parms.put("replyUser", row.creator);
				parms.put("replyText", row.title);
				parms.put("replyName", user.name);
				parms.put("replyTime", new Date());
				
				db.update("mapper.reply.update", parms);
				db.commit(true);
			}
			
		}
		
		//回复次数减一
		try{UserAbilityUtil.useReplyCount(userAbility);}catch(Exception e) {}
		
		return new RestData("");
	}
	
	//查看原文的回复（帖子的具体信息）
	@GetMapping("/reply/list")
	public String replyList(@RequestParam long msgId,
			HttpServletRequest request,
			Integer pageNumber,
			Model model) throws Exception
	{
		if(pageNumber == null) pageNumber = 1;
		
		try(SqlSession db = MyBatis.factory.openSession())
		{
			//原文及作者信息
			Map<String, Object> ref = db.selectOne("mapper.reply.messageInfo", msgId);
			
			if(ref == null) throw new Exception("未找到帖子 msgId="+msgId);
			else {
				handlerImageUrl(ref, "img1");
				handlerImageUrl(ref, "img2");
				handlerImageUrl(ref, "img3");
			}
			
			//时间转换
			if(true)
			{
				Date time = (Date)ref.get("timeCreate");
				if(time!=null){
					String timeCreate = DateUtil.simpleExecute(time);
					ref.put("timeCreate", timeCreate);
				}
				
				time = (Date)ref.get("replyTime");
				if(time!=null) {
					String replyTime = DateUtil.simpleExecute(time);
					ref.put("replyTime", replyTime);
				}
				
			}

			//收藏帖子标识
			int isCollector = 0;

			User user = (User) request.getSession().getAttribute("user");

			if(user != null) {
				Map<String, Object> parm = new HashMap<>();
				parm.put("collector", user.id);
				parm.put("msgId", (long) ref.get("id"));
				//判断此帖子是否当前用户的收藏帖子
				Collect collect = db.selectOne("mapper.collect.collector", parm);
				if(collect != null)
					isCollector = 1;
			}
			
			//回复数
			int count = (int) ref.get("numReply");
			int pageSize = 20;
			int pageCount = count / pageSize;
			if(count % pageSize != 0) pageCount += 1;
			
			int startIndex = pageSize * (pageNumber - 1);
			
			Map<String, Object> parms = new HashMap<String, Object>();
			parms.put("ref1", msgId);
			parms.put("startIndex", startIndex);
			parms.put("pageSize", pageSize);
			
			//回复列表
			List<Map> replyList = db.selectList("mapper.reply.replyList", parms);
			
			//时间处理等
			for(Map m : replyList)
			{
				String timeCreate = DateUtil.simpleExecute((Date)m.get("timeCreate"));
				m.put("timeCreate", timeCreate);

				long ref2 = (long)m.get("ref2");
				//二级回复
				if(ref2 == msgId)
				{
					//此回复的引文，被回复帖子的ID
					long ref1 = (long)m.get("ref1");

					User refUser = db.selectOne("mapper.user.userName", ref1);
					m.put("refName", refUser.name);
				}
			}
			
			model.addAttribute("ref", ref);
			model.addAttribute("replyList", replyList);
			model.addAttribute("count", count);
			model.addAttribute("pageNumber", pageNumber);
			model.addAttribute("pageCount", pageCount);
			model.addAttribute("pageSize", pageSize);
			model.addAttribute("baseIndex", startIndex);
			model.addAttribute("isCollector", isCollector);
		}
		return "reply/list";
	}
	
	public void handlerImageUrl(Map msg, String column)
	{
		String url = "";
		String img = (String) msg.get(column);
		if(img != null && img.length() > 0)
		{
			String storePath = (String) msg.get("storePath");
			url = MsgImgController.store.getUrl(storePath + img);
		}
		msg.put(column, url);
	}
	
}
