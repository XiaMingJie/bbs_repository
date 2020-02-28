package com.util;

import org.springframework.util.ResourceUtils;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

/**
 * 临时文件的位置信息
 * 上传上来的临时图片，直接就存放在WebRoot\tmp\目录下，以便可以直接URL访问浏览
 * */
public class TmpFile
{
	//临时文件目录：WebRoot\tmp\
	public static File getDir(HttpServletRequest request)
	{
		try{
			String docPath = ResourceUtils.getURL("classpath:").getPath();
			return new File(docPath + "/static/tmp");
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	//获取临时文件
	public static File getFile(HttpServletRequest request, String tmpName)
	{
		File dir = getDir(request);
		dir.mkdirs();
		return new File(dir, tmpName);
	}
	
	//获取临时文件URL
	public static String getUrl(HttpServletRequest request, String tmpName)
	{
		return request.getContextPath() + "/tmp/" + tmpName;
	}
}
