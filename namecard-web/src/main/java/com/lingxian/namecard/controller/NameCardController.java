package com.lingxian.namecard.controller;

import cn.hutool.core.util.CreditCodeUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONUtil;
import com.lingxian.namecard.dto.Result;
import com.lingxian.namecard.param.NameCardParam;
import com.lingxian.namecard.service.EmailService;
import com.lingxian.namecard.utils.AESUtil;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author lingxian36158
 * @Date 2022-05-29 16:42
 * @Since 1.0
 */
@RestController
@Controller
@ResponseBody
@RequestMapping("/nameCard")
public class NameCardController {
    @Autowired
    EmailService emailService;
    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @PostMapping("getCode.json")
    public Result getCode(HttpServletRequest request, @RequestBody NameCardParam nameCardParam){
        String email = nameCardParam.getEmail();
        String remoteHost = request.getRemoteHost();
        String value= redisTemplate.opsForValue().get("email@" + email);
        String value2= redisTemplate.opsForValue().get("ip@" + remoteHost);
        if(true||value==null&&value2==null){
            String s=CreditCodeUtil.randomCreditCode();
            try {
                String code = s.subSequence(0, 6).toString();
                emailService.sendCode(email, code);
                redisTemplate.opsForValue().set("email@" + email,code,3000, TimeUnit.SECONDS);
                redisTemplate.opsForValue().set("ip@" + remoteHost,"1",3000, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                return Result.Failed("发送失败:"+e.getMessage());
            }
        }else {
           return Result.Failed("验证码属于有效时间,稍后重试");
        }
        //  emailService.sendCode();
        return Result.Success();
    }
    @PostMapping("getEnCyptData.json")
    public Result getCyptData(HttpServletRequest request, @RequestBody NameCardParam nameCardParam){
        String email = nameCardParam.getEmail();
        String name = nameCardParam.getName();
        String publicKey = nameCardParam.getPublicKey();
        String code=nameCardParam.getCode();
        String address=nameCardParam.getAddress();
        String v = redisTemplate.opsForValue().get("email@" + email);
        if(code.equals(v)){
            Map<String,String> result= new HashMap<>();
            result.put("email",email);
            result.put("name",name);
            byte[] decrypt = AESUtil.encrypt(publicKey,JSONUtil.toJsonStr(result));
            redisTemplate.opsForHash().put("address-key",address,publicKey);
            return Result.Success("0x"+new  String(HexUtil.encodeHex(decrypt)));
        }else {
            return Result.Failed("验证码失效或错误");
        }
    }
    @PostMapping("getDeCyptData.json")
    public Result getDeCyptData(HttpServletRequest request, @RequestBody NameCardParam nameCardParam){
        String publicKey= (String) redisTemplate.opsForHash().get("address-key",nameCardParam.getAddress());
        String cryptData = nameCardParam.getEncryptData();
        if(cryptData.startsWith("0x")){
            cryptData=cryptData.replace("0x","");
        }
        if(publicKey!=null){
            String decrypt = AESUtil.decrypt(publicKey,HexUtil.decodeHex(cryptData));
            return Result.Success(JSONUtil.toBean(decrypt,HashMap.class));
        }else {
            return Result.Failed("无公钥");
        }
    }
}
