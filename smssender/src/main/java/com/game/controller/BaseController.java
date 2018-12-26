package com.game.controller;

import com.game.beans.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2018/11/25 0:52
 */
@Component
public class BaseController {
    @Autowired
    protected HttpServletRequest request;

    @RequestMapping("error")
    public ResultBean error(){
        return new ResultBean("1000", "请求错误");
    }
}
