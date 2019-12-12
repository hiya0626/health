package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.canstant.RedisConstant;
import com.itheima.dao.CheckGroupDao;
import com.itheima.dao.CheckItemDao;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.CheckItem;
import com.itheima.pojo.Setmeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealDao setmealDao;

    @Autowired
    CheckGroupDao checkGroupDao;

    @Autowired
    CheckItemDao checkItemDao;

    @Autowired
    JedisPool jedisPool;

    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        Integer setmealId = setmeal.getId();
        if (checkgroupIds != null && checkgroupIds.length > 0) {
            for (Integer checkgroupId : checkgroupIds) {
                Map<String, Integer> map = new HashMap<>();
                map.put("setmeal_id", setmealId);
                map.put("checkgroup_id", checkgroupId);
                setmealDao.setSetmealAndCheckGroup(map);
            }
        }
        String img = setmeal.getImg();
        System.out.println("service层图片名字" + img);
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES, setmeal.getImg());
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        //通过分页助手来分页
        PageHelper.startPage(currentPage, pageSize);
        //通过分页助手自带的page封装对象
        Page<Setmeal> page = setmealDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    @Override
    public Setmeal findById(int id) {
        //1.根据套餐ID查询套餐信息
        Setmeal setmeal = setmealDao.findById(id);

        //2.根据套餐id查询套餐_检查组表关联的检查组ids
        List<Integer> cGIds = setmealDao.findCGidsById_new2(id);

        //存放每个检查套餐中,所有检查组信息的list
        List<CheckGroup> cGList = new ArrayList<>();

        for (Integer cgId : cGIds) {

            //存放每个检查组中,所有检查项信息的list
            List<CheckItem> cIList = new ArrayList<>();

            //3.根据检查组中,查询检查组信息
            CheckGroup cg = checkGroupDao.findById(cgId);

            //4.根据检查组id,查询检查组_检查项关联表的检查项ids
            List<Integer> ciIDList = checkGroupDao.findCheckItemIdsByCheckGroupId(cg.getId());

            for (Integer ciID : ciIDList) {
                //5根据检查项id,查询检查项信息
                CheckItem checkItem = checkItemDao.findById(ciID);
                cIList.add(checkItem);
            }
            cg.setCheckItems(cIList);
            cGList.add(cg);
        }
        setmeal.setCheckGroups(cGList);

        return setmeal;
    }
}
