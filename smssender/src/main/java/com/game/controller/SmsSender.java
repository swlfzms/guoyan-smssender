package com.game.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.game.Application;
import com.game.beans.ResultBean;
import com.game.entity.ActivityRegister;
import com.game.entity.ActivityResult;
import com.game.repository.ActivityRegisterRepository;
import com.game.service.SmsService;
import com.game.util.PhoneFormatUtils;
import com.game.util.RandomUtils;
import com.game.util.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 *  短信发送类
 * @Description:
 * @Author: Jason
 * @CreateDate: 2018/11/24 11:42
 */
@RestController()
@RequestMapping("sms")
public class SmsSender extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsSender.class);

    @Autowired
    private SmsService smsService;

    @Autowired
    private Environment env;

    /**
     * 发送短信
     * @param params
     * @return
     */
    @RequestMapping("sendMessage")
    public @ResponseBody ResultBean sendMessage(@RequestBody JSONObject params){
        LOGGER.info("params "+ params);
        String phone = params.getString("phone");
        if(StringUtils.isAnyBlank(phone)){
            return new ResultBean("1002", "参数不能为空");
        }
        boolean isSupport = PhoneFormatUtils.isSupport(phone);
        if(!isSupport){
            return new ResultBean("1001", "手机号格式不对");
        }
        Boolean isTurnOn = env.getProperty("activity.turn.on", Boolean.class);
        if(!isTurnOn){
            return new ResultBean("1005", "活动结束了");
        }
        //检查频率
        String ip = WebUtils.getIP(request);
        LOGGER.info("ip: {}", ip);//223.73.196.38
        boolean frequency = smsService.isFrequencyAccess(ip);
        if(frequency){
            return new ResultBean("1004", "访问太频繁");
        }
        String activityCode = env.getProperty("activity.code", "c8pnadsghyov");

        ActivityResult activityResult = this.smsService.findByPhoneAndActivityCode(activityCode, phone);
        if(activityResult != null){
            return new ResultBean("1000", "已经参与过了");
        }
        String code = RandomUtils.getRandomString(6, RandomUtils.CharacterType.DIGIT);
        LOGGER.info("phone: {} 验证码获取成功:{}", new Object[]{phone, code});
        synchronized (smsService){
            smsService.setPhoneCode(phone, code);
            JSONObject content = new JSONObject();
            content.put("code", code);
            smsService.sendMessage(phone, "SMS_155275146", content);
        }
        smsService.incrementFrequencyAccess(ip);
        return new ResultBean("1000", "短信发送成功");
    }

    @RequestMapping("verifyCode")
    public @ResponseBody ResultBean verifyCode(@RequestBody JSONObject params){
        LOGGER.info("params "+ params);
        String phone = params.getString("phone");
        String code = params.getString("code");
        if(StringUtils.isAnyBlank(phone, code)){
            return new ResultBean("1002", "参数不能为空");
        }
        boolean isSupport = PhoneFormatUtils.isSupport(phone);
        if(!isSupport){
            return new ResultBean("1001", "手机号格式不对");
        }
        String vCode = smsService.getPhoneCode(phone);
        LOGGER.info("phone: {}, vCode: {} ", new Object[]{phone, vCode});
        if(StringUtils.isNotBlank(vCode) && vCode.equalsIgnoreCase(code)){
            String resultCode = smsService.delPhoneCode(phone);
            JSONObject reservationCode = new JSONObject();
            reservationCode.put("code", resultCode);
            smsService.sendMessage("三国志M", phone, "SMS_155275048", reservationCode);
            ResultBean resultBean =  new ResultBean("1000", "登记成功");
            resultBean.setData(resultCode);
            return resultBean;
        }else{
            LOGGER.info("phone: {} 验证码错误", new Object[]{phone});
            return new ResultBean("1000", "验证码错误");
        }
    }

    @RequestMapping("multiSendMsg")
    public @ResponseBody ResultBean multiSendMsg(@RequestBody JSONArray params){
        LOGGER.info("params: {}", params);
        String ipWhiteList = env.getProperty("sms.whiteList");
        String ip = WebUtils.getIP(request);
        if(StringUtils.isBlank(ip) || !ipWhiteList.contains(ip)){
            return new ResultBean("1006", "拒绝访问");
        }
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<params.size();i++){
            JSONObject result = new JSONObject();
            JSONObject msg = params.getJSONObject(i);
            String outId = smsService.getOutIdSendMessage(msg.getString("phone"), msg.getString("templateCode"), JSONObject.parseObject(msg.getString("content")));
            result.put(msg.getString("phone"), outId);
            jsonArray.add(result);
        }
        return new ResultBean("1000", jsonArray.toJSONString());
    }

    @RequestMapping("total")
    public @ResponseBody ResultBean total(){
        int count = smsService.getTotal();
        return new ResultBean("1000", ""+count);
    }

    @RequestMapping("reservation")
    public @ResponseBody
    ResultBean reservation(@RequestBody JSONObject params) {
        String phone = params.getString("phone");
        ResultBean resultBean = new ResultBean("1000", "查找成功");
        String activityCode = env.getProperty("activity.code", "c8pnadsghyov");
        ActivityResult activityResult = this.smsService.findByPhoneAndActivityCode(activityCode, phone);
        if(activityResult!=null){
            resultBean.setData(activityResult.getReservationCode());
        }else{
            resultBean = new ResultBean("1001", "未预约");
        }
        return resultBean;
    }
}
