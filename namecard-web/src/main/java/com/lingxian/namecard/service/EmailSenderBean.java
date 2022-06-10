package com.lingxian.namecard.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author lingxian36158
 * @Date 2022-05-29 13:15
 * @Since 1.0
 */
@Component
public class EmailSenderBean implements FactoryBean<HtmlEmail> {

    @Value("${email.hostName}")
    String hostName;
    @Value("${email.userName}")
    String userName;
    @Value("${email.password}")
    String password;
    List<Session> session= Collections.synchronizedList(new ArrayList<>());
    ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(8,16,3, TimeUnit.SECONDS,new LinkedBlockingQueue<>());
    AtomicInteger count=new AtomicInteger(0);
    @Override
    public HtmlEmail getObject() throws Exception {
        HtmlEmail sender=new HtmlEmail();
        sender.setFrom(userName);
        sender.setSubject("名片锻造-邮箱验证");
        sender.setCharset("UTF-8");
        HtmlEmail sessionItem = new HtmlEmail();
        if(session.size()==0){
            sessionItem.setHostName(hostName);
            sessionItem.setAuthentication(userName, password);
            try {
                session.add(sessionItem.getMailSession());
            } catch (EmailException e) {
                e.printStackTrace();
            }
        }
        if(session.size()<5){
            Runnable runnable= new Runnable() {
                @Override
                public void run() {
                    HtmlEmail sessionItem = new HtmlEmail();
                    sessionItem.setHostName(hostName);
                    sessionItem.setAuthentication(userName, password);
                    try {
                        session.add(sessionItem.getMailSession());
                    } catch (EmailException e) {
                        e.printStackTrace();
                    }
                }
            };
            threadPoolExecutor.execute(runnable);
        }
        if(session.size()>0){
            Session session = this.session.get(count.getAndAdd(1));
            if(count.get()>=5){
                count.set(0);
            }
            sender.setMailSession(session);
        }
        return sender;
    }

    @Override
    public Class<?> getObjectType() {
        return HtmlEmail.class;
    }
    @Override
    public boolean isSingleton(){
        return false;
    }

}
