package com.game.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.game.Application;
import com.game.activity.PhoneFrequency;
import com.game.entity.ActivityRegister;
import com.game.entity.ActivityResult;
import com.game.manager.SmsManager;
import com.game.repository.ActivityRegisterRepository;
import com.game.repository.ActivityResultRepository;
import com.game.sms.AliyunMessageSendTemplateImpl;
import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2018/11/24 14:13
 */
@Service
public class SmsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);
    @Autowired
    private SmsManager smsManager;
    @Autowired
    private Environment environment;
    @Autowired
    private AliyunMessageSendTemplateImpl aliyun;

    public String getPhoneCode(String phone) {
        return smsManager.getPhoneCode(phone);
    }

    public String delPhoneCode(String phone) {
        String activityCode = environment.getProperty("activity.code", "c8pnadsghyov");
        String code = smsManager.delPhoneCode(phone,activityCode);
        return code;
    }


    public void setPhoneCode(String phone, String code) {
        this.smsManager.setPhoneCode(phone, code);
    }
    public void sendMessage(String signName, String phone, String templateCode, JSONObject param) {
        String global = environment.getProperty("sms.global", "false");
        if(phone.startsWith("86-")){
            phone = phone.substring(phone.indexOf("-") + 1, phone.length());
        }else if (phone.contains("-")) {
            phone = phone.replace("-", "");
        }
        try {
            aliyun.send(signName, phone, templateCode, param);
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("phone:{}, templateCode: {}, message:{}, reservation message", new Object[]{phone, templateCode, param.toJSONString()});
    }
    public void sendMessage(String phone, String templateCode, JSONObject param) {
        String global = environment.getProperty("sms.global", "false");
        if(phone.startsWith("86-")){
            phone = phone.substring(phone.indexOf("-") + 1, phone.length());
        }else if (phone.contains("-")) {
            phone = phone.replace("-", "");
        }
        aliyun.send(phone, templateCode, param);
        LOGGER.info("send sms phone:{}, templateCode: {}, message:{}", new Object[]{phone, templateCode, param});
    }

    public String getOutIdSendMessage(String phone, String templateCode, JSONObject param) {
        return aliyun.send(phone, templateCode, param);
    }

    /**
     * 每24小时每个IP限制访问20次
     * 每分钟访问一次.10次之后限制12小时
     *
     * @param ip
     * @return
     */
    public boolean isFrequencyAccess(String ip) {
        PhoneFrequency phoneFrequency = this.smsManager.getPhoneFrequency(ip);
        if (phoneFrequency == null) {
            return false;
        }
        if (phoneFrequency.getCount() >= 10) {
            return true;
        }
        if (System.currentTimeMillis() - phoneFrequency.getTimestamp() < 60 * 1000) {
            return true;
        }
        return false;
    }

    public void incrementFrequencyAccess(String ip) {
        PhoneFrequency phoneFrequency = this.smsManager.getPhoneFrequency(ip);
        if (phoneFrequency == null) {
            phoneFrequency = new PhoneFrequency();
            phoneFrequency.setCount(1);
            phoneFrequency.setTimestamp(System.currentTimeMillis());
            smsManager.setFrequencyAccess(ip, phoneFrequency);
        } else {
            phoneFrequency.setCount(phoneFrequency.getCount() + 1);
            phoneFrequency.setTimestamp(System.currentTimeMillis());
            smsManager.setFrequencyAccess(ip, phoneFrequency);
        }
    }

    public ActivityResult findByPhoneAndActivityCode(String activityCode, String phone) {
        return smsManager.findByPhoneAndActivityCode(activityCode, phone);
    }

    public int getTotal() {
        return smsManager.getTotal();
    }
}
