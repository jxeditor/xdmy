package com.xdmy.service.inter;

import com.alibaba.fastjson.JSONObject;
import com.xdmy.domain.Incoming;

public interface IIncomingService {
    JSONObject findAllIncoming(int pageNum, int pageSize, String producerName, String productName, String bizStartDate, String bizEndDate);

    int addIncoming(Incoming Incoming);

    int deleteIncomingById(int id);

    int updateIncoming(Incoming Incoming);

    int updatePaystatusIncomingById(int id);

    JSONObject getIncomingStatement(String producerName, String bizStartDate, String bizEndDate);

    JSONObject findProducerNamesByPrefix(String prefix, int pageNum, int pageSize);
}
