package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.canstant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    MemberService memberService;

    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){
        //使用模拟数据测试对象格式是否能够转为echarts所需的数据格式
        Map<String,Object> map = new HashMap<>();
        List<String> months= new ArrayList<>();
        months.add("2018.05");
        months.add("2018.06");
        months.add("2018.07");
        months.add("2018.08");
        months.add("2018.09");
        map.put("months",months);

        List<Integer> memberCount = new ArrayList<>();
        memberCount.add(200);
        memberCount.add(157);
        memberCount.add(321);
        memberCount.add(249);
        memberCount.add(170);
        map.put("memberCount",memberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
    }
}
