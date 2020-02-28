package com.util;

import java.io.File;

/**
 * 文件的本地存储与访问，path和url换算
 * */
public class FileStore
{
	public static FileStore tmpStore = new FileStore("C:/bbsfile/tmp", "/bbsfile/tmp/");

	public File rootDir = new File("C:/bbsfile/message/");
	public String urlPrefix = "/bbsfile/message";

	public FileStore(String dir)
	{
		this(dir, "");
	}
	
	public FileStore(String dir, String urlPrefix)
	{
		this(new File(dir), urlPrefix);
	}
	
	public FileStore(File dir, String urlPrefix)
	{
		this.rootDir = dir;
		this.urlPrefix = urlPrefix;
		this.rootDir.mkdirs();
	}

	public File getDir()
	{
		return rootDir;
	}
	
	//传入相对路径path（path=数据库message.storePath+message.img)，返回File
	public File getFile(String path)
	{
		rootDir.mkdirs();
		return new File(rootDir, path);
	}
	
	//传入相对路径path，返回URL
	public String getUrl(String path)
	{
		return urlPrefix + path;
	}
	
	//根据URL，找到相对路径
	public String pathOfUrl(String url)
	{
		return url.substring(urlPrefix.length());
	}
	
	//根据URL，找到相应的文件
	public File fileOfUrl(String url)
	{
		return new File(rootDir, pathOfUrl(url));
	}
	
}
