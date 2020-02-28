package com;

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

    public static void main(String[] args) {
        try{
            randomNumber();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
