package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
