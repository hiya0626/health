package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.canstant.MessageConstant;
import com.itheima.canstant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import com.itheima.util.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Reference
    SetmealService setmealService;

    @Autowired
    JedisPool jedisPool;

    //文件上传
    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){
        System.out.println("Controller层"+imgFile);
        //获得原始文件名
        String originalFilename = imgFile.getOriginalFilename();//例:xxxxxxxxxxx.jpg
        System.out.println("Controller层"+originalFilename);
        //1.通过剪切获取文件后缀名 .jpg
        int index = originalFilename.lastIndexOf(".");
        String extention = originalFilename.substring(index);
        //2.避免重复,通过UUID随机生成并且拼接.JPG
        String filename = UUID.randomUUID().toString()+extention;
        try {

            //3.上传文件
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),filename);

            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,filename);

        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.PIC_UPLOAD_FAIL);
        }
        //返回文件名
        return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS,filename);
    }

    //添加新建弹窗数据
    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds){
        try {
            setmealService.add(setmeal,checkgroupIds);
            return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.ADD_SETMEAL_FAIL);
        }
    }

    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        try {
            return setmealService.pageQuery(queryPageBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
