package com.xdmy.service.inter;

import com.xdmy.domain.Incoming;
import org.json.JSONObject;

public interface IIncomingService {
    JSONObject findAllIncoming(int pageNum, int pageSize, String producerName, String productName, String bizStartDate, String bizEndDate);

    int addIncoming(Incoming Incoming);

    int deleteIncomingById(int id);

    int updateIncoming(Incoming Incoming);

    int updatePaystatusIncomingById(int id);
}
