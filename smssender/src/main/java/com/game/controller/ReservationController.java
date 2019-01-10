package com.game.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.game.Application;
import com.game.beans.ResultBean;
import com.game.entity.ActivityReservation;
import com.game.entity.ActivityResult;
import com.game.repository.ActivityResultRepository;
import com.game.service.ReservationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2019/1/9 22:43
 */
@RestController()
@RequestMapping("reservation")
public class ReservationController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationController.class);
    @Autowired
    private ReservationService reservationService;
    private ActivityResultRepository activityResultRepository;
    @Autowired
    private Environment environment;

    @RequestMapping("insertCode")
    public @ResponseBody
    ResultBean insertCode(@RequestBody JSONObject params) {

        ResultBean resultBean = new ResultBean("1000", "新增成功");
        String activityCode = environment.getProperty("activity.code", "c8pnadsghyov");
        JSONArray data = params.getJSONArray("data");
        String code = params.getString("code");
        if (StringUtils.isAnyBlank(data.toString(), code)) {
            resultBean.setCode("1001");
            resultBean.setMessage("空值異常");
            return resultBean;
        }
        List<ActivityReservation> list = new ArrayList<ActivityReservation>();

        for (int i = 0; i < data.size(); i++) {
            ActivityReservation activityReservation = new ActivityReservation();
            activityReservation.setActivityCode(activityCode);
            activityReservation.setReservationCode(data.getString(i));
            activityReservation.setCreatedBy("system");
            activityReservation.setCreatedTime(new Date());
            activityReservation.setDeleted(false);
            activityReservation.setModifiedBy("system");
            activityReservation.setModifiedTime(new Date());
            activityReservation.setStatus(false);
            list.add(activityReservation);
        }
        this.reservationService.save(list);
        return resultBean;
    }

    @RequestMapping("findValidCode")
    public @ResponseBody
    ResultBean findValidCode() {
        ResultBean resultBean = new ResultBean("1000", "查找成功");
        String activityCode = environment.getProperty("activity.code", "c8pnadsghyov");
        List<String> list = this.reservationService.findAll(activityCode);
        resultBean.setData(list);
        return resultBean;
    }


}