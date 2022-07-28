package com.xdmy.service.inter.impl;


import com.xdmy.datasource.DBContextHolder;
import com.xdmy.domain.Turnover;
import com.xdmy.service.inter.ITurnoverService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnoverService extends BaseService implements ITurnoverService {
    @Override
    public JSONObject findAllTurnover(int pageNum, int pageSize, String payerName, String payeeName, String bizStartDate, String bizEndDate) {
        DBContextHolder.setDbType("primary");
        List<Turnover> turnoverList = daoFacade.getTurnoverDao().findAllTurnover(pageNum, pageSize, payerName, payeeName, bizStartDate, bizEndDate);
        int total = daoFacade.getTurnoverDao().getAllTotalSize(payerName, payeeName, bizStartDate, bizEndDate);
        return toJSONObject(turnoverList).put("total", total);
    }

    @Override
    public int addTurnover(Turnover turnover) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getTurnoverDao().addTurnover(turnover);
    }

    @Override
    public int deleteTurnoverById(int id) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getTurnoverDao().deleteTurnoverById(id);
    }

    @Override
    public int updateTurnover(Turnover turnover) {
        DBContextHolder.setDbType("primary");
        return daoFacade.getTurnoverDao().updateTurnover(turnover);
    }

    public JSONObject toJSONObject(List<Turnover> turnoverList) {
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        try {
            if (turnoverList != null) {
                for (Turnover turnover : turnoverList) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", turnover.getId());
                    obj.put("payer", turnover.getPayer());
                    obj.put("payee", turnover.getPayee());
                    obj.put("billdate", turnover.getBilldate());
                    obj.put("money", turnover.getMoney());
                    obj.put("tax", turnover.getTax());
                    obj.put("paid", turnover.getPaid());
                    obj.put("remark", turnover.getRemark());
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
