package com.game.manager;

import com.alibaba.fastjson.JSON;
import com.game.Application;
import com.game.activity.PhoneFrequency;
import com.game.entity.ActivityRegister;
import com.game.entity.ActivityResult;
import com.game.repository.ActivityRegisterRepository;
import com.game.repository.ActivityResultRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2018/11/24 14:12
 */
@Service
public class SmsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ActivityResultRepository activityResultRepository;

    public String getPhoneCode(String phone){
        String key = getPhoneRegisterKey(phone);
        LOGGER.info("key:{}", new Object[]{key});
        return redisTemplate.opsForValue().get(key);
    }

    public void setPhoneCode(String phone, String code){
        String key = getPhoneRegisterKey(phone);
        redisTemplate.opsForValue().set(key, code);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
        LOGGER.info("设置缓存 phone: {}, code: {}, key:{}", new Object[]{phone, code, key});
    }

    private String getPhoneRegisterKey(String phone){
        return "PhoneRegisterKey_" + phone;
    }

    private String getPhoneFrequencyKey(String ip){
        return "PhoneFrequency_" + ip;
    }

    public PhoneFrequency getPhoneFrequency(String ip){
        String key = this.getPhoneFrequencyKey(ip);
        String cache = redisTemplate.opsForValue().get(key);
        if(StringUtils.isNoneBlank(cache)){
            return JSON.parseObject(cache, PhoneFrequency.class);
        }
        return null;
    }

    public void setFrequencyAccess(String ip, PhoneFrequency phoneFrequency) {
        String key = this.getPhoneFrequencyKey(ip);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(phoneFrequency));
        redisTemplate.expire(key, 12, TimeUnit.HOURS);
    }

    public void delPhoneCode(String phone, String activityCode) {
        String key = getPhoneRegisterKey(phone);

        ActivityResult activityResult = new ActivityResult();
        activityResult.setActivityCode(activityCode);
        activityResult.setPhone(phone);
        activityResult.setCreatedTime(new Date());
        try {
            activityResultRepository.save(activityResult);
        }catch (Exception e){
            LOGGER.error("error: {}", e);
        }
        redisTemplate.delete(key);
    }


    private String getActivityResult(String activityCode, String phone){
        return "ActivityResult_" + activityCode+"_"+phone;
    }

    public ActivityResult findByPhoneAndActivityCode(String activityCode, String phone) {
        String key = getActivityResult(activityCode, phone);
        String value = redisTemplate.opsForValue().get(key);
        if(StringUtils.isNotBlank(value)){
            return JSON.parseObject(value, ActivityResult.class);
        }
        ActivityResult result = activityResultRepository.findByPhoneAndActivityCode(phone, activityCode);
        if(result != null){
            redisTemplate.opsForValue().set(key, JSON.toJSONString(result));
            redisTemplate.expire(key, 30, TimeUnit.DAYS);
        }
        return result;
    }

    public int getTotal() {
        List<ActivityResult> list = activityResultRepository.findAll();
        return list.size();
    }
}
