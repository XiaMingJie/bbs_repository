package com;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.util.MyUtil;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.util.Random;

public class TestDB {
    public static void randomNumber()
    {
        Random random = new Random();
        int num = random.nextInt(4);
        System.out.println(num);
    }

    public static void jsonTest()
    {
        String str = "{\"client_id\":\"101810266\",\"openid\":\"F89F25F0C6EAC7003E586D8AB860D555\"}";
        JsonObject json = (JsonObject) new Gson().fromJson(str, JsonObject.class);
        if(json.has("openid"))
            System.out.println(json.get("openid").getAsString());
    }

    public static void main(String[] args) {
        try{
            jsonTest();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
