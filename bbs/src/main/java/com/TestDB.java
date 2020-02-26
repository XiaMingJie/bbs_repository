package com;

import com.util.MyUtil;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;

public class TestDB {
    public static void main(String[] args) {
        try{
            String text = "202cb962ac59075b964b07152d234b70";
            String pwd = "123";

            pwd = MyUtil.md5(pwd);

            System.out.println(pwd.equals(text));
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
