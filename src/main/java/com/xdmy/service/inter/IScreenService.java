package com.xdmy.service.inter;


import com.alibaba.fastjson.JSONObject;

public interface IScreenService {
    JSONObject getShipment1ChartData(String customerName, String bizStartDate, String bizEndDate);

    JSONObject getShipment2ChartData();
}
