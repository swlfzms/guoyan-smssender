package com.game.beans;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2018/11/24 14:09
 */
public class ResultBean implements Serializable{

    private String code;
    private String message;
    private Object data;

    public ResultBean(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
