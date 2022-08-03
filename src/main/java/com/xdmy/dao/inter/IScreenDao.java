package com.xdmy.dao.inter;

import org.json.JSONObject;

import java.util.List;

public interface IScreenDao {
    List<JSONObject> getShipment1ChartData(String customerName, String bizStartDate, String bizEndDate);
}
