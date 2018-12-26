package com.game.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.game.entity.ActivityRegister;
import com.game.repository.ActivityRegisterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 阿里云发送短信
 * 目前使用账号
 * Created by IntelliJ IDEA.
 * User: Jason
 * Date: 2018/11/35
 */
@Service
public class AliyunMessageSendTemplateImpl {

    private static final Logger logger = LoggerFactory.getLogger(AliyunMessageSendTemplateImpl.class);
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    //产品名称:云通信短信API产品,开发者无需替换
    private static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    private static final String domain = "dysmsapi.aliyuncs.com";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    private static final String accessKeyId = "LTAI8LQAhu0sywQZ";
    private static final String accessKeySecret = "TPRYxRusOEEjOvXCWJMeaznOYq8A9j";
    private static IAcsClient acsClient;
    @Autowired
    private ActivityRegisterRepository activityRegisterRepository;
    @Autowired
    private Environment environment;

    static {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        try {
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            acsClient = new DefaultAcsClient(profile);
        } catch (ClientException e) {
            e.printStackTrace();
            logger.error("Aliyun短信初始化失败");
        }
    }

    public String send(String phone, String templateCode, JSONObject templateParam) {

        logger.info("待发送短信: {}, {}", new Object[]{phone, templateCode});
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("三国志M");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为"{\"name\":\"Tom\", \"code\":\"123\"}"
        request.setTemplateParam(templateParam.toJSONString());

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        String outId = UUID.randomUUID().toString();
        request.setOutId(outId);
        logger.info("outId: {}", outId);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ActivityRegister activityRegister = new ActivityRegister();
                    String activityCode = environment.getProperty("activity.code", "c8pnadsghyov");
                    activityRegister.setActivityCode(activityCode);
                    activityRegister.setPhone(phone);
                    activityRegister.setTemplateCode(templateCode);
                    activityRegister.setContent(templateParam.toJSONString());
                    activityRegister.setOutId(outId);
                    activityRegister.setCreatedBy("system");
                    activityRegister.setModifiedBy("system");
                    activityRegister.setCreatedTime(new Date());
                    activityRegister.setModifiedTime(new Date());
                    activityRegister.setStatus(0);
                    activityRegisterRepository.save(activityRegister);

                    //hint 此处可能会抛出异常，注意catch
                    SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
                    Thread.sleep(3000L);
                    if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                        logger.info("阿里云接收到短信: {}", outId);
                    } else {
                        logger.error("短信发送失败: {}" + sendSmsResponse.getMessage());
                    }
                } catch (ClientException e) {
                    e.printStackTrace();
                    logger.error("ClientException " + e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logger.error("InterruptedException " + e);
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error("Exception " + e);
                }
            }
        });
        return outId;
    }
}
