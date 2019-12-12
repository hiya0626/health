package com.itheima.controller;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.canstant.MessageConstant;
import com.itheima.canstant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.util.SMSUtils;
import com.itheima.util.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    JedisPool jedisPool;

    //用户在线体检预约发送验证码
    @RequestMapping("/send4Order")
    public Result send4Order(String telephone) {
        //随机生成四位数密码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(4);
        System.out.println(validateCode);

        //给用户发送验证码
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode.toString());
        } catch (ClientException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }

        //将验证码保存到redis(5分钟)
        jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_ORDER,300,validateCode.toString());
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

    //用户手机快速登录发送验证码
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone) {
        //随机生成六位数密码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(6);
        System.out.println(validateCode);

        //给用户发送验证码
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,validateCode.toString());
        } catch (ClientException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }

        //将验证码保存到redis(5分钟)
        jedisPool.getResource().setex(telephone+ RedisMessageConstant.SENDTYPE_LOGIN,300,validateCode.toString());
        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

}
