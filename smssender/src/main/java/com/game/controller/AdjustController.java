package com.game.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.beans.ResultBean;
import com.game.entity.AdjustData;
import com.game.repository.AdjustDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("adjust")
public class AdjustController extends BaseController{

    @Autowired
    AdjustDataRepository adjustDataRepository;
    Logger logger = LoggerFactory.getLogger(AdjustController.class);

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    @RequestMapping(value = "callBack", method = {RequestMethod.GET, RequestMethod.POST})
    public void callBack(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String event = request.getParameter("event");
        String eventName = request.getParameter("event_name");
        String activityKind = request.getParameter("activity_kind");

        AdjustData adjustData = new AdjustData();
        adjustData.setActivityKind(activityKind);
        adjustData.setEvent(event);
        adjustData.setEventName(eventName);
        adjustData.setCreatedTime(new Date());
        logger.info(JSON.toJSONString(adjustData));

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    adjustDataRepository.save(adjustData);
                }catch (Exception e){
                    logger.info("exception e " + e);
                    logger.info("message " + e.getMessage());
                }

            }
        });
        PrintWriter printWriter = response.getWriter();
        printWriter.write("1");
        printWriter.flush();
    }
}
