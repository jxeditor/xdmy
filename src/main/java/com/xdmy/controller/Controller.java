package com.xdmy.controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;

public class Controller {
    // 将param转换成map格式
    public HashMap<String, String> getRequestParam(HttpServletRequest request) {
        Enumeration eObj = request.getParameterNames();
        HashMap<String, String> returnMap = null;
        if (eObj != null) {
            returnMap = new HashMap<>();
            while (eObj.hasMoreElements()) {
                Object obj = eObj.nextElement();
                if (obj == null || obj.toString().trim().length() < 1 || obj.toString().trim().equals("m") || obj.toString().equals(request.getQueryString()))
                    continue;
                Object val = request.getParameter(obj.toString());
                returnMap.put(obj.toString(), val.toString());
            }
        }
        return returnMap;
    }
}
