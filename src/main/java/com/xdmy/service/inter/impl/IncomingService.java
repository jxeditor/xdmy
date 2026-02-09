package com.xdmy.service.inter.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xdmy.datasource.DBContextHolder;
import com.xdmy.domain.Incoming;
import com.xdmy.domain.Shipment;
import com.xdmy.service.inter.IIncomingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomingService extends BaseService implements IIncomingService {
    @Override
    public JSONObject findAllIncoming(int pageNum, int pageSize, String producerName, String productName, String bizStartDate, String bizEndDate) {
        DBContextHolder.setDbType("primary");
        List<Incoming> incomingList = daoFacade.getIncomingDao().findAllIncoming(pageNum, pageSize, producerName, productName, bizStartDate, bizEndDate);
        int total = daoFacade.getIncomingDao().getAllTotalSize(producerName, productName, bizStartDate, bizEndDate);
        JSONObject result = toJSONObject(incomingList);
        result.put("total", total);
        return result;
    }

    @Override
    public JSONObject getIncomingStatement(String producerName, String bizStartDate, String bizEndDate) {
        DBContextHolder.setDbType("primary");
        List<Incoming> incomingList = daoFacade.getIncomingDao().getIncomingStatement(producerName, bizStartDate, bizEndDate);
        int total = daoFacade.getIncomingDao().getDistinctSize(producerName, bizStartDate, bizEndDate);
        double sumpay = daoFacade.getIncomingDao().getSumPay(producerName, bizStartDate, bizEndDate);
        JSONObject result = toJSONObject(incomingList);
        result.put("total", total);
        result.put("sumpay", sumpay);
        return result;
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

    @Override
    public JSONObject findProducerNamesByPrefix(String prefix, int pageNum, int pageSize) {
        DBContextHolder.setDbType("primary");
        List<String> producerNames = daoFacade.getIncomingDao().findProducerNamesByPrefix(prefix, pageNum, pageSize);
        int total = daoFacade.getIncomingDao().getProducerNamesCount(prefix);
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        try {
            if (producerNames != null) {
                for (String producerName : producerNames) {
                    data.add(producerName);
                }
            }
            result.put("data", data);
            result.put("total", total);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
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
                    obj.put("remark", incoming.getRemark());
                    data.add(obj);
                }
            }
            result.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
