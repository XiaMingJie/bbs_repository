package com.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.db.User;
import com.db.UserAbility;
import com.util.FileStore;
import com.util.MyUtil;
import com.util.TmpFile;
import xspring.RestData;
import xspring.RestError;

@Controller
public class MsgImgController
{
	public static FileStore store = 
			new FileStore("C:/bbsfile/message", "/bbsfile/message/");
	
	@PostMapping("/u/message/imageUp.do")
	public Object upload(HttpServletRequest request,
			@SessionAttribute User user,
			@SessionAttribute UserAbility userAbility) throws Exception
	{
		if(userAbility.imageCount <= 0) return new RestError("今日图片次数已用完！请明天再试！MAX=" + userAbility.imageMax);
		
		MultipartHttpServletRequest mhr = (MultipartHttpServletRequest) request;
		
		MultipartFile mf = mhr.getFile("file");	//表单里的 name='file'
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(mf != null && !mf.isEmpty())
		{
			//取得上传上来的文件名
			String realName = mf.getOriginalFilename();
			//获得后缀名
			String suffix = MyUtil.getSuffix(realName);
			//改文件名为随机数字和后缀名的拼接
			String tmpName = MyUtil.guid2() + suffix;
			
			//获得临时文件
			File tmpFile = FileStore.tmpStore.getFile(tmpName);
			
			//接收上传
			mf.transferTo(tmpFile);
			System.out.println("** file: " + tmpFile.getAbsolutePath());
			
			if(tmpFile.length() > 1000000)
				throw new Exception("图片太大！需小于1M！");
			
			result.put("realName", realName);
			result.put("tmpName", tmpName);
			result.put("tmpUrl", FileStore.tmpStore.getUrl(tmpName));
		}
		
		//图片次数减一
		try{UserAbilityUtil.useImageCount(userAbility);}catch(Exception e) {}
		
		return new RestData(result);
	}
}
