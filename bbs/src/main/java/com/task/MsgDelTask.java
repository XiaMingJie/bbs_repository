package com.task;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;

import com.controller.MsgImgController;
import com.db.Message;
import com.db.MyBatis;
import com.util.TmpFile;

public class MsgDelTask implements Task
{
	
	@Override
	public void execute() throws Exception
	{
		System.out.println("** 清理帖子(delFlag=1)...");
		
		try(SqlSession session = MyBatis.factory.openSession())
		{
			List<Message> msgList = session.selectList("mapper.message.delRow");
			
			for(Message msg: msgList)
			{
				clearMessage(msg);
			}
		}
		
	}
	
	private void clearMessage(Message msg) throws Exception
	{
		//删除附件图片目录
		File storeDir = MsgImgController.store.getFile(msg.storePath);
		
		if(storeDir.exists())
		{
			try {
				FileUtils.deleteQuietly(storeDir);
			}catch(Exception e) {
			
			}
		}
		
		try(SqlSession db = MyBatis.factory.openSession())
		{
			//删除数据库记录
			db.delete("mapper.message.execDel", msg.id);
			db.commit(true);
		}
	}

}
