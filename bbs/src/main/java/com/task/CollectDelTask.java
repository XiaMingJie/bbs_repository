package com.task;

import com.db.MyBatis;
import org.apache.ibatis.session.SqlSession;

public class CollectDelTask implements Task {
    @Override
    public void execute() throws Exception {
        System.out.println("清理收藏表(delFalg=1)...");
        try(SqlSession session = MyBatis.factory.openSession())
        {
            session.delete("mapper.collect.delete");
            session.commit(true);
        }
    }
}
