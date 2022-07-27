package com.xdmy.dao.inter;

import com.xdmy.domain.Incoming;

import java.util.List;

public interface IIncomingDao {
    List<Incoming> findAllIncoming(int pageNum, int pageSize, String producerName, String productName, String bizStartDate, String bizEndDate);

    int getAllTotalSize(String producerName, String productName, String bizStartDate, String bizEndDate);

    int addIncoming(Incoming Incoming);

    int deleteIncomingById(int id);

    int updateIncoming(Incoming Incoming);

    int updatePaystatusIncomingById(int id);
}
