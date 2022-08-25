package com.xdmy.service.inter.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xdmy.datasource.DBContextHolder;
import com.xdmy.service.inter.IScreenService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScreenService extends BaseService implements IScreenService {
    @Override
    public JSONObject getShipment1ChartData(String customerName, String bizStartDate, String bizEndDate) {
        DBContextHolder.setDbType("primary");
        List<JSONObject> resultList = daoFacade.getScreenDao().getShipment1ChartData(customerName, bizStartDate, bizEndDate);
        return toShipment1ChartJSONObject(resultList);
    }

    @Override
    public JSONObject getShipment2ChartData() {
        DBContextHolder.setDbType("primary");
        List<JSONObject> resultList = daoFacade.getScreenDao().getShipment2ChartData();
        return toShipment2ChartJSONObject(resultList);
    }

    public JSONObject toShipment1ChartJSONObject(List<JSONObject> resultList) {
        JSONObject result = new JSONObject();
        JSONArray billdate = new JSONArray();
        JSONArray money = new JSONArray();
        JSONArray costmoney = new JSONArray();
        JSONArray profit = new JSONArray();
        JSONArray paymoney = new JSONArray();
        JSONArray payprofit = new JSONArray();

        try {
            if (resultList != null) {
                for (JSONObject obj : resultList) {
                    billdate.add(obj.getString("billdate"));
                    money.add(obj.getDouble("money"));
                    costmoney.add(obj.getDouble("costmoney"));
                    profit.add(obj.getDouble("profit"));
                    paymoney.add(obj.getDouble("paymoney"));
                    payprofit.add(obj.getDouble("payprofit"));
                }
            }
            result.put("billdate", billdate);
            result.put("money", money);
            result.put("costmoney", costmoney);
            result.put("profit", profit);
            result.put("paymoney", paymoney);
            result.put("payprofit", payprofit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject toShipment2ChartJSONObject(List<JSONObject> resultList) {
        JSONObject result = new JSONObject();
        JSONArray billdate = new JSONArray();
        JSONArray money = new JSONArray();
        JSONArray costmoney = new JSONArray();
        JSONArray profit = new JSONArray();
        JSONArray paymoney = new JSONArray();
        JSONArray payprofit = new JSONArray();

        try {
            if (resultList != null) {
                for (JSONObject obj : resultList) {
                    billdate.add(obj.getString("billdate"));
                    money.add(obj.getDouble("money"));
                    costmoney.add(obj.getDouble("costmoney"));
                    profit.add(obj.getDouble("profit"));
                    paymoney.add(obj.getDouble("paymoney"));
                    payprofit.add(obj.getDouble("payprofit"));
                }
            }
            result.put("billdate", billdate);
            result.put("money", money);
            result.put("costmoney", costmoney);
            result.put("profit", profit);
            result.put("paymoney", paymoney);
            result.put("payprofit", payprofit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
