package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.canstant.MessageConstant;
import com.itheima.canstant.RedisConstant;
import com.itheima.canstant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    JedisPool jedisPool;

    @Reference
    MemberService memberService;

    //手机号快速验证
    @RequestMapping("/login")
    public Result login(HttpServletResponse response, @RequestBody Map map) {
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");

        //从redis中获取验证码
        String validateCodeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);

        //判断输入的验证码和redis中存的验证码是否相同
        if (validateCode != null && validateCodeInRedis != null && validateCode.equals(validateCodeInRedis)) {
            //验证通过
            Member member = memberService.findByTelephone(telephone);
            if (member == null) {
                //会员表没有信息,自动注册用户信息
                member = new Member();
                member.setRegTime(new Date());
                member.setPhoneNumber(telephone);
                memberService.add(member);
            }
            //向浏览器写入cookie值,内容为手机号
            Cookie cookie = new Cookie("login_member_telephone", telephone);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 30);
            response.addCookie(cookie);

            //将会员信息存到redis
            String json = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(telephone, 60 * 5, json);

            return new Result(true, MessageConstant.LOGIN_SUCCESS);
        } else {
            //验证失败
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }
    }
}
