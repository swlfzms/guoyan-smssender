package com.game.sms;

import com.alibaba.fastjson.JSON;
import com.alicom.mns.tools.DefaultAlicomMessagePuller;
import com.alicom.mns.tools.MessageListener;
import com.aliyun.mns.model.Message;
import com.aliyuncs.exceptions.ClientException;
import com.game.entity.ActivityRegister;
import com.game.entity.ActivityResult;
import com.game.repository.ActivityRegisterRepository;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信发送状态接收器
 * 默认不启动,因为缺乏更新关联字段
 * Created by IntelliJ IDEA.
 * User: Jason
 * Date: 2018/9/18
 */
@Component
public class AliyunSmsReportWorker {

    @Autowired
    private ActivityRegisterRepository activityRegisterRepository;

    private static Logger logger = LoggerFactory.getLogger(AliyunSmsReportWorker.class);

    class MyMessageListener implements MessageListener {
        private Gson gson=new Gson();

        @Override
        public boolean dealMessage(Message message) {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //消息的几个关键值
//            logger.info("message receiver time from mns:" + format.format(new Date()));
//            logger.info("message handle: " + message.getReceiptHandle());
//            logger.info("message body: " + message.getMessageBodyAsString());
//            logger.info("message id: " + message.getMessageId());
//            logger.info("message dequeue count:" + message.getDequeueCount());
//            logger.info("Thread:" + Thread.currentThread().getName());
            try{
                Map<String,Object> contentMap=gson.fromJson(message.getMessageBodyAsString(), HashMap.class);

                //TODO 根据文档中具体的消息格式进行消息体的解析
                Boolean success = (Boolean) contentMap.get("success");
                String phoneNumber = (String) contentMap.get("phone_number");
                String errMsg = (String) contentMap.get("err_msg");
                //TODO 这里开始编写您的业务代码
                logger.info("Aliyun短信发送状态contentMap {}", JSON.toJSONString(contentMap));
                String outId = contentMap.get("out_id").toString();
                if("ignore".equalsIgnoreCase(outId)){
                    //忽略不保存结果的短信
                    return true;
                }
                ActivityRegister activityRegister = activityRegisterRepository.findByOutId(outId);
                activityRegister.setModifiedTime(new Date());
                if(success!=null && success == true){
                    activityRegister.setStatus(1);
                }else{
                    activityRegister.setStatus(2);
                    activityRegister.setComment(errMsg);
                }
                activityRegisterRepository.save(activityRegister);
            }catch(com.google.gson.JsonSyntaxException e){
                logger.error("error_json_format: {}",message.getMessageBodyAsString(),e);
                //理论上不会出现格式错误的情况，所以遇见格式错误的消息，只能先delete,否则重新推送也会一直报错
                return true;
            } catch (Throwable e) {
                //您自己的代码部分导致的异常，应该return false,这样消息不会被delete掉，而会根据策略进行重推
                e.printStackTrace();
                logger.error("内部逻辑错误: {}",e);
                return true;
            }

            //消息处理成功，返回true, SDK将调用MNS的delete方法将消息从队列中删除掉
            return true;
        }

    }

    /**
     * 默认启动,接收玩家短信状态,
     * 暂时不启动,没有相关字段可以更新短信状态
     */
    @PostConstruct
    public void init() throws ClientException, ParseException {
        DefaultAlicomMessagePuller puller=new DefaultAlicomMessagePuller();

        /**
         * 设置异步线程池大小及任务队列的大小，还有无数据线程休眠时间
         * 理论上需要接收回复的内容比较少,不需要开较大的线程池
         */
        puller.setConsumeMinThreadSize(1);
        puller.setConsumeMaxThreadSize(2);
        puller.setThreadQueueSize(2000);
        puller.setPullMsgThreadSize(1);
        //和服务端联调问题时开启,平时无需开启，消耗性能
        puller.openDebugLog(false);

        //TODO 此处需要替换成开发者自己的AK信息
        String accessKeyId="LTAI8LQAhu0sywQZ";
        String accessKeySecret="TPRYxRusOEEjOvXCWJMeaznOYq8A9j";

		/*
		* TODO 将messageType和queueName替换成您需要的消息类型名称和对应的队列名称
		*云通信产品下所有的回执消息类型:
		*1:短信回执：SmsReport，
		*2:短息上行：SmsUp
		*3:语音呼叫：VoiceReport
		*4:流量直冲：FlowReport
		*/
        //此处应该替换成相应产品的消息类型
        String messageType="SmsReport";
        //在云通信页面开通相应业务消息后，就能在页面上获得对应的queueName,格式类似Alicom-Queue-xxxxxx-SmsReport
        String queueName="Alicom-Queue-1384315406202856-SmsReport";
        puller.startReceiveMsg(accessKeyId,accessKeySecret, messageType, queueName, new MyMessageListener());
    }
}
