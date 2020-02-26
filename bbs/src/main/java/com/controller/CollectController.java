package com.controller;

import com.db.Collect;
import com.db.Message;
import com.db.MyBatis;
import com.db.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import xspring.RestData;
import xspring.RestError;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CollectController {
    @PostMapping("/u/collect/addCollect.do")
    public Object add(@SessionAttribute User user,
                      @RequestBody String sreq) throws Exception
    {
        try(SqlSession db = MyBatis.factory.openSession())
        {
            Gson gson = new Gson();
            JsonObject jreq = gson.fromJson(sreq, JsonObject.class);

            long msgId = jreq.get("msgId").getAsLong();

            Message msg = db.selectOne("mapper.message.check", msgId);
            if(msg == null)
                return new RestError("该帖子不存在!");

            //判断当前用户是否已收藏，已收藏则删除，未收藏则添加
            Map<String, Object> parm = new HashMap<>();
            parm.put("collector", user.id);
            parm.put("msgId", msgId);
            Collect isCollector = db.selectOne("mapper.collect.collector", parm);

            //未收藏
            if(isCollector == null)
            {
                Collect row = new Collect();
                row.msgId = msgId;
                row.name = "默认收藏夹";
                row.collector = user.id;
                row.time = new Date();
                row.delFlag = false;

                db.insert("mapper.collect.insert", row);
                db.commit(true);
            }
            //已收藏
            else
            {
                db.update("mapper.collect.delCollect", isCollector.id);
                db.commit(true);
            }


            return new RestData("");
        }
    }
}
