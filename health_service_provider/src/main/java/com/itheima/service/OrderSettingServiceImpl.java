package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    OrderSettingDao orderSettingDao;

    @Override
    public void add(List<OrderSetting> list) {
        if (list != null && list.size() > 0) {
            for (OrderSetting orderSetting : list) {
                //判断当前日期是否已经进行了预约设置
                long countByOrderDate = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if (countByOrderDate > 0) {
                    //已经进行了预约设置,执行更新操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                } else {
                    //没有进行预约设置,执行插入操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    //根据月份查询对应的预约设置数据
    @Override
    public List<Map> getOrderSettingByMonth(String date) {
        String begin = date + "-1";//2019-12-1
        String end = date + "-31";//2019-12-31
        Map<String, String> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        //调用DAO,根据日期范围查询预约设置数据
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        List<Map> result = new ArrayList<>();
        for (OrderSetting orderSetting : list) {
            Map<String,Object> m=new HashMap<>();
            m.put("date",orderSetting.getOrderDate().getDate());//获取日期数字(几号)
            m.put("number",orderSetting.getNumber());
            m.put("reservations",orderSetting.getReservations());
            result.add(m);
        }
        return result;
    }
    //根据日期查询对应的预约设置数据
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        Date orderDate = orderSetting.getOrderDate();
        //根据日期查询是否已进行了预约设置
        long count = orderSettingDao.findCountByOrderDate(orderDate);
        if (count>0){
            //已经预约过,需要更新数据
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else {
            //没有预约,需要插入信息
            orderSettingDao.add(orderSetting);
        }
    }
}
