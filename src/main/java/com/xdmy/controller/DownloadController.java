package com.xdmy.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author xz
 * @Date 2022/8/25 18:25
 * @Description 下载出货&入货对账单
 */
@RestController
@RequestMapping("/download")
public class DownloadController extends BaseController {

    @GetMapping("/downloadShipmentStatement")
    public void downloadShipmentStatement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, String> params = getRequestParam(request);
        String customerName = params.getOrDefault("customerName", "");
        String bizStartDate = params.getOrDefault("bizStartDate", "");
        String bizEndDate = params.getOrDefault("bizEndDate", "");

        JSONObject result = serviceFacade.getShipmentService().getShipmentStatement(customerName, bizStartDate, bizEndDate, getCompanyName(request));

        InputStream templateFileName = this.getClass().getClassLoader().getResourceAsStream("出货对账单模板.xlsx");
        // String templateFileName = FileUtil.getPath() + "出货对账单模板.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        Map<String, Object> map = MapUtils.newHashMap();
        map.put("year", bizStartDate.substring(0, 4));
        map.put("month", bizStartDate.substring(5, 7));
        map.put("customer", customerName);
        map.put("currentdate", DateUtils.format(new Date(), "yyyy-MM-dd"));
        map.put("total", result.getInteger("total"));
        map.put("sumpay", result.getDouble("sumpay"));

        String fileName = URLEncoder.encode(customerName + bizStartDate.substring(0, 7).replace("-", "") + "出货对账单", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Access-Control-Expose-Headers", "Content-disposition,filename");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        response.setHeader("filename", fileName + ".xlsx");
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.FALSE).build();
            excelWriter.fill(result.getJSONArray("data"), fillConfig, writeSheet);
            excelWriter.fill(map, writeSheet);
        }
    }

    @GetMapping("/downloadIncomingStatement")
    public void downloadIncomingStatement(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, String> params = getRequestParam(request);
        String producerName = params.getOrDefault("producerName", "");
        String bizStartDate = params.getOrDefault("bizStartDate", "");
        String bizEndDate = params.getOrDefault("bizEndDate", "");

        JSONObject result = serviceFacade.getIncomingService().getIncomingStatement(producerName, bizStartDate, bizEndDate, getCompanyName(request));

        InputStream templateFileName = this.getClass().getClassLoader().getResourceAsStream("入货对账单模板.xlsx");
        // String templateFileName = FileUtil.getPath() + "出货对账单模板.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        Map<String, Object> map = MapUtils.newHashMap();
        map.put("year", bizStartDate.substring(0, 4));
        map.put("month", bizStartDate.substring(5, 7));
        map.put("producer", producerName);
        map.put("currentdate", DateUtils.format(new Date(), "yyyy-MM-dd"));
        map.put("total", result.getInteger("total"));
        map.put("sumpay", result.getDouble("sumpay"));

        String fileName = URLEncoder.encode(producerName + bizStartDate.substring(0, 7).replace("-", "") + "入货对账单", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Access-Control-Expose-Headers", "Content-disposition,filename");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        response.setHeader("filename", fileName + ".xlsx");
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.FALSE).build();
            excelWriter.fill(result.getJSONArray("data"), fillConfig, writeSheet);
            excelWriter.fill(map, writeSheet);
        }
    }
}
