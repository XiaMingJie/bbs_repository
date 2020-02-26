package com.task;

import org.apache.ibatis.session.SqlSession;

import com.db.MyBatis;

public class UserAbilityInitTask implements Task
{

	@Override
	public void execute() throws Exception
	{
		// TODO Auto-generated method stub
		try(SqlSession db = MyBatis.factory.openSession())
		{
			db.update("mapper.userAbility.initialize");
			db.commit(true);
		}
	}

}
