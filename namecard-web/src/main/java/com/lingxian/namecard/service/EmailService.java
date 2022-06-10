package com.lingxian.namecard.service;

import cn.hutool.extra.spring.SpringUtil;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.util.IDNEmailAddressConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author lingxian36158
 * @Date 2022-05-29 11:38
 * @Since 1.0
 */
@Service
public class EmailService {
    @Autowired
    RedisTemplate redisTemplate;
    public void send(String to,String msg) throws EmailException, AddressException {
        HtmlEmail email = SpringUtil.getBean(HtmlEmail.class);
        List<InternetAddress> toAddresses = new ArrayList<>();
        InternetAddress address = new InternetAddress((new IDNEmailAddressConverter()).toASCII(to));
        toAddresses.add(address);
        email.setTo(toAddresses);
        email.setMsg(msg);
        email.send();
    }
    public void sendCode(String to,String code) throws EmailException, AddressException {
        String msg="您的验证码为："+ code;
        send(to,msg);
    }

}
