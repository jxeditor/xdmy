package com.xdmy.dao.inter;


import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface IScreenDao {
    List<JSONObject> getShipment1ChartData(String customerName, String bizStartDate, String bizEndDate);

    List<JSONObject> getShipment2ChartData();
}
