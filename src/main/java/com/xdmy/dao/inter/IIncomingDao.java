package com.xdmy.dao.inter;

import com.xdmy.domain.Incoming;

import java.util.List;
import java.util.Map;

public interface IIncomingDao {
    List<Incoming> findAllIncoming(int pageNum, int pageSize, String producerName, String productName, String bizStartDate, String bizEndDate, String companyName);

    int getAllTotalSize(String producerName, String productName, String bizStartDate, String bizEndDate, String companyName);

    int addIncoming(Incoming Incoming, String materialRelationsStr, String companyName);

    int addIncoming(Incoming Incoming, String companyName);

    int deleteIncomingById(int id, String companyName);

    int updateIncoming(Incoming Incoming, String materialRelationsStr, String companyName);

    int updateIncoming(Incoming Incoming);

    int updatePaystatusIncomingById(int id);

    List<Incoming> getIncomingStatement(String producerName, String bizStartDate, String bizEndDate, String companyName);

    int getDistinctSize(String producerName, String bizStartDate, String bizEndDate, String companyName);

    double getSumPay(String producerName, String bizStartDate, String bizEndDate, String companyName);

    List<String> findProducerNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);

    int getProducerNamesCount(String prefix, String companyName);

    List<Map<String, Object>> getIncomingMaterialOperations(int id);

    Incoming findIncomingById(int id);
}
