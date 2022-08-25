package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Turnover;


public interface ITurnoverService {
    JSONObject findAllTurnover(int pageNum, int pageSize, String payerName, String payeeName, String bizStartDate, String bizEndDate);

    int addTurnover(Turnover turnover);

    int deleteTurnoverById(int id);

    int updateTurnover(Turnover turnover);
}
