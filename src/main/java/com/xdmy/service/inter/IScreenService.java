package com.xdmy.service.inter;

import org.json.JSONObject;


public interface IScreenService {
    JSONObject getShipment1ChartData(String customerName, String bizStartDate, String bizEndDate);
}
