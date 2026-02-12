package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Incoming;

public interface IIncomingService {
    JSONObject findAllIncoming(int pageNum, int pageSize, String producerName, String productName, String bizStartDate, String bizEndDate, String companyName);

    int addIncoming(Incoming Incoming, String materialRelationsStr, String companyName);

    int deleteIncomingById(int id, String companyName);

    int updateIncoming(Incoming Incoming, String materialRelationsStr, String companyName);

    int updatePaystatusIncomingById(int id);

    JSONObject getIncomingStatement(String producerName, String bizStartDate, String bizEndDate, String companyName);

    JSONObject findProducerNamesByPrefix(String prefix, int pageNum, int pageSize, String companyName);

    JSONObject getIncomingMaterialOperations(int id);

    JSONObject findIncomingById(int id);
}
