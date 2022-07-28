package com.xdmy.service.inter;

import com.xdmy.domain.Turnover;
import org.json.JSONObject;


public interface ITurnoverService {
    JSONObject findAllTurnover(int pageNum, int pageSize, String payerName, String payeeName, String bizStartDate, String bizEndDate);

    int addTurnover(Turnover turnover);

    int deleteTurnoverById(int id);

    int updateTurnover(Turnover turnover);
}
