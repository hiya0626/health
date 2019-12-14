package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.dao.UserDao;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

//用户服务
@Transactional
@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    RoleDao roleDao;

    @Autowired
    PermissionDao permissionDao;

    //根据用户名查询数据库获取用户信息和关联的角色信息,同时需要查询角色关联的权限信息
    @Override
    public User findByUsername(String username) {
        User user = userDao.findByUsername(username);//查询用户基本信息,不包含用户角色信息
        if (user == null) {
            //无用户信息
            return null;
        }
        //根据用户ID查询对应的角色
        Integer id = user.getId();
        Set<Role> roles = roleDao.findByUserId(id);
        user.setRoles(roles);
        for (Role role : roles) {
            Integer roleId = role.getId();
            //根据ID查询关联的权限
            Set<Permission> permissions = permissionDao.findByRoleId(roleId);
            role.setPermissions(permissions);//让角色关联权限
        }
        user.setRoles(roles);//让用户关联角色
        return user;
    }
}
