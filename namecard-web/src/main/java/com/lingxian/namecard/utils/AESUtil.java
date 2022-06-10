package com.lingxian.namecard.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.symmetric.AES;

import java.nio.charset.StandardCharsets;

/**
 * @Author:lx
 * @Date:2022/6/2
 * @Description:
 */
public class AESUtil {
    public static byte[] encrypt(String key,String data){
        byte[] keys = padding(key);
        AES aes = new AES(keys);
        return aes.encrypt(data.getBytes(StandardCharsets.UTF_8));
    }
    public static String decrypt(String key,byte[] data){

        byte[] keys = padding(key);
        AES aes = new AES(keys);
        return aes.decryptStr(data);
    }
    private static byte[] padding(String key){
        byte[] keyb=null;
        if(key.startsWith("0x")){
            key=key.replace("0x","");
            keyb = HexUtil.decodeHex(key);
        }else {
            keyb =Base64.decode(key);
        }
        byte[] keys=new byte[32];
        for (int i = 0; i < 32; i++) {
            if(keyb.length>i){
                keys[i]=keyb[i];
            }else {
                keys[i]=0;
            }
        }
        return keys;
    }

}
