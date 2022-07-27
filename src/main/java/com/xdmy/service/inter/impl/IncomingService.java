package com.xdmy.service.inter.impl;


import com.xdmy.datasource.DBContextHolder;
import com.xdmy.domain.Incoming;
import com.xdmy.service.inter.IIncomingService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomingService extends BaseService implements IIncomingService {
    @Override
    public JSONObject findAllIncoming(int pageNum, int pageSize, String producerName, String productName, String bizStartDate, String bizEndDate) {
        DBContextHolder.setDbType("primary");
        List<Incoming> incomingList = daoFacade.getIncomingDao().findAllIncoming(pageNum, pageSize, producerName, productName, bizStartDate, bizEndDate);
        int total = daoFacade.getIncomingDao().getAllTotalSize(producerName, productName, bizStartDate, bizEndDate);
        return toJSONObject(incomingList).put("total", total);
    }

    @Override
    public int addIncoming(Incoming incoming) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getIncomingDao().addIncoming(incoming);
    }

    @Override
    public int deleteIncomingById(int id) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getIncomingDao().deleteIncomingById(id);
    }

    @Override
    public int updateIncoming(Incoming incoming) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getIncomingDao().updateIncoming(incoming);
    }

    @Override
    public int updatePaystatusIncomingById(int id) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getIncomingDao().updatePaystatusIncomingById(id);
    }

    public JSONObject toJSONObject(List<Incoming> incomingList) {
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        try {
            if (incomingList != null) {
                for (Incoming incoming : incomingList) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", incoming.getId());
                    obj.put("odd", incoming.getOdd());
                    obj.put("producer", incoming.getProducer());
                    obj.put("product", incoming.getProduct());
                    obj.put("billdate", incoming.getBilldate());
                    obj.put("amount", incoming.getAmount());
                    obj.put("unitprice", incoming.getUnitprice());
                    obj.put("money", incoming.getMoney());
                    obj.put("paystatus", incoming.getPaystatus());
                    data.put(obj);
                }
            }
            result.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
