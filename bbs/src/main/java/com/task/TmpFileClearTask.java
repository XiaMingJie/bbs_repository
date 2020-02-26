package com.task;

import java.io.File;

import org.apache.commons.io.FileUtils;


/**
 * 根据文件的最近修改时间来清理临时文件
 * */
public class TmpFileClearTask implements Task
{
	File tmpDir;
	int RANGE = 1000 * 3600 * 24; //超过24小时
	
	//传入临时文件目录
	public TmpFileClearTask(File tmpDir)
	{
		this.tmpDir = tmpDir;
	}
	
	@Override
	public void execute() throws Exception
	{
		if(tmpDir == null) return;
		if(! tmpDir.exists()) return;
		
		//获取目录下的文件列表
		File[] files = tmpDir.listFiles();
		if(files == null || files.length == 0)
			return;
		
		System.out.println("tmp目录文件数量: " + files.length);
		
		//当前时间戳
		long now = System.currentTimeMillis();
		
		for(File f: files)
		{
			//若当前时间减去文件最后修改时间大于24小时，则删除
			if(now - tmpDir.lastModified() > RANGE)
			{
				try {
					System.out.println("** 清理过期文件:" + f.getAbsolutePath());
					FileUtils.deleteQuietly(f);
				}catch(Exception e) {}
			}
		}
		
		
	}

}
