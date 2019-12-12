package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.canstant.MessageConstant;
import com.itheima.canstant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.itheima.util.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    JedisPool jedisPool;

    @Reference
    OrderService orderService;

    //在线体检预约
    @RequestMapping("submit")
    public Result submit(@RequestBody Map map) {
        //取出携带过来的手机号手机号
        String telephone = (String) map.get("telephone");

        //1.根据手机号从redis里把保存的验证码调出来进行验证
        String validateCodeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        //取出map里面的验证码
        String validateCode = (String) map.get("validateCode");

        if (validateCode != null && validateCodeInRedis != null && validateCode.equals(validateCodeInRedis)) {
            map.put("orderType", Order.ORDERTYPE_WEIXIN);
            //验证通过则调用服务完成预约处理
            Result result = null;
            try {
                result = orderService.order(map);
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
            if (result.isFlag()) {
                //预约成功发送短信通知
                try {
                    SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE, telephone, (String) map.get("orderDate"));
                } catch (ClientException e) {
                    e.printStackTrace();
                }
            }
            return result;

        } else {
            //验证失败,返回错误信息给页面
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }

    //根据ID查询预约成功后的用户信息
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            Map map = orderService.findById(id);
            return new Result(true,MessageConstant.QUERY_ORDER_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
