package com.xdmy.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class JSONReturn {
    private JSONObject json = new JSONObject();
    private String type;
    private String msg;
    private Integer code;
    private String rtnString;
    private List objList;


    public String getRtnString() {
        return rtnString;
    }

    public void setRtnString(String rtnString) {
        this.rtnString = rtnString;
    }

    public JSONReturn() {
        this.code = 1;
        this.type = "success";
        this.msg = "操作成功！";
    }

    public JSONReturn(String type, String msg, Integer code) {
        this.type = type;
        this.msg = msg;
        this.code = code;
    }

    public JSONReturn(ErrorCode errorCode) {
        this.code = errorCode.code;
        this.type = "error";
        this.msg = errorCode.message;
    }

    public JSONReturn(Integer code) {
        this.code = code;
    }

    public JSONReturn(String type) {
        this.type = type;
    }

    public JSONReturn(JSONObject data) {
        this.code = 1;
        this.type = "success";
        try {
            this.json = data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.msg = "操作成功！";
    }

    public JSONReturn(Integer code, String msg, List objList) {
        this.code = code;
        this.msg = msg;
        this.objList = objList;
    }

    public JSONReturn(String type, String msg, Integer code, String rtnStirng, List objList) {
        this.type = type;
        this.msg = msg;
        this.code = code;
        this.rtnString = rtnStirng;
        this.objList = objList;
    }

    public JSONReturn(String type, String msg, String rtnString) {
        this.type = type;
        this.msg = msg;
        this.rtnString = rtnString;
    }

    public JSONReturn(String type, String msg, String rtnString, List objList) {
        this.type = type;
        this.msg = msg;
        this.rtnString = rtnString;
        this.objList = objList;
    }

    public JSONReturn(Integer code, String msg, String rtnString) {
        this.code = code;
        this.msg = msg;
        this.rtnString = rtnString;
    }

    public JSONReturn(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public JSONReturn(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public JSONReturn(Integer code, String msg, String rtnString, List objList) {
        this.msg = msg;
        this.code = code;
        this.rtnString = rtnString;
        this.objList = objList;
    }

    public JSONReturn(String type, String msg, List objList) {
        this.type = type;
        this.msg = msg;
        this.objList = objList;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List getObjList() {
        return objList;
    }

    public void setObjList(List objList) {
        this.objList = objList;
    }

    public JSONObject toJson() {
        try {
            if (type != null && type.trim().length() > 0)
                json.put("type", type);
            if (code != null)
                json.put("code", code);
            if (msg != null && msg.trim().length() > 0)
                json.put("msg", msg);
            if (rtnString != null && rtnString.trim().length() > 0) {
                json.put("rtnString", rtnString);
            }
            if (objList != null && objList.size() > 0) {
                JSONArray array = new JSONArray(objList);
                json.put("objList", array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public String toString() {
        try {
            if (code != null) {
                json.put("code", code);
            }
            if (msg != null && msg.trim().length() > 0)
                json.put("msg", msg);
            if (rtnString != null && rtnString.trim().length() > 0)
                json.put("rtnString", rtnString);
            if (objList != null && objList.size() > 0) {
                JSONArray array = new JSONArray(objList);
                json.put("objList", array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public String toJsonp(String callback) {
        return callback + "(" + this.json.toString() + ")";
    }
}
