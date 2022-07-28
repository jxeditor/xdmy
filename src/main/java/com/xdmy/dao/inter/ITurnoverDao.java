package com.xdmy.dao.inter;

import com.xdmy.domain.Turnover;

import java.util.List;

public interface ITurnoverDao {
    List<Turnover> findAllTurnover(int pageNum, int pageSize, String payerName, String payeeName, String bizStartDate, String bizEndDate);

    int getAllTotalSize(String payerName, String payeeName, String bizStartDate, String bizEndDate);

    int addTurnover(Turnover turnover);

    int deleteTurnoverById(int id);

    int updateTurnover(Turnover turnover);
}
