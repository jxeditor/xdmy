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

    List<Incoming> getIncomingStatement(String producerName, String bizStartDate, String bizEndDate);

    int getDistinctSize(String producerName, String bizStartDate, String bizEndDate);

    double getSumPay(String producerName, String bizStartDate, String bizEndDate);

    List<String> findProducerNamesByPrefix(String prefix, int pageNum, int pageSize);

    int getProducerNamesCount(String prefix);

}
