package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //1. dateList:[begin,end]
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //2. turnover list
        //select sum(amounts) from orders where status = completed and time > and time <
        List<Double>turnoverList = new ArrayList<>();
        dateList.forEach(date->{
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("begin",beginTime);map.put("end",endTime);map.put("status", Orders.COMPLETED);
            Double v = orderMapper.sumByMap(map);
            if(v==null) v = 0.0;
            turnoverList.add(v);
        });

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //1. dateList:[begin,end]
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //2. newUser list
        //select count(id) from user where create_time > and create_time <
        List<Integer>newUserList = new ArrayList<>();
        //select count(id) from user where create_time <
        List<Integer>totalUserList = new ArrayList<>();
        dateList.forEach(date->{
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("end",endTime);
            Integer total = userMapper.countByMap(map);
            totalUserList.add(total);

            map.put("begin",beginTime);
            Integer newu = userMapper.countByMap(map);
            newUserList.add(newu);
        });

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //1. dateList:[begin,end]
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //2. orderCountList & validOrderCountList
        //select count(id) from order where order_time > and order_time <
        List<Integer>orderCountList = new ArrayList<>();
        //select count(id) from user where status = completed and order_time > and order_time <
        List<Integer>validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("begin",beginTime);map.put("end",endTime);
            Integer total = orderMapper.countByMap(map);
            orderCountList.add(total);

            map.put("status",Orders.COMPLETED);
            Integer valid = orderMapper.countByMap(map);
            validOrderCountList.add(valid);
        }

        Integer totalCnt = orderCountList.stream().reduce(Integer::sum).get();
        Integer validCnt =validOrderCountList.stream().reduce(Integer::sum).get();
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalCnt)
                .validOrderCount(validCnt)
                .orderCompletionRate(totalCnt == 0? 0.0:validCnt.doubleValue()/totalCnt)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> list = orderMapper.getSalesTop10(beginTime,endTime);

        List<String>nameList = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer>numberList = list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //1. 查询数据库，获取数据
        LocalDate beginDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(beginDate,LocalTime.MIN), LocalDateTime.of(endDate,LocalTime.MAX));

        //2. 通过poi写入excel模板
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            //基于模板创建新的excel
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet = excel.getSheet("Sheet1");
        // 概览数据
            sheet.getRow(1).getCell(1).setCellValue("时间"+beginDate+"至"+endDate);
            //第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            //第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
        // 明细数据
            for (int i =0; i < 30; ++i){
                LocalDate date = beginDate.plusDays(i);
                BusinessDataVO vo = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7+i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(vo.getTurnover());
                row.getCell(3).setCellValue(vo.getValidOrderCount());
                row.getCell(4).setCellValue(vo.getOrderCompletionRate());
                row.getCell(5).setCellValue(vo.getUnitPrice());
                row.getCell(6).setCellValue(vo.getNewUsers());
            }
        //3. 通过输出流，将excel下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

        // 4. 关闭资源
            out.close();excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
