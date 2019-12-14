package com.itheima.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component//交给spring管理
public class SpringSecurityUserService implements UserDetailsService {

    //使用dubbo通过网络远程调用服务提供方获取数据库中的用户信息
    @Reference
    UserService userService;

    //根据用户名查询数据获取用户名
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            //用户名不存在
            return null;
        }

        List<GrantedAuthority> list = new ArrayList<>();
        //为用户进行动态授权
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            //遍历角色,为用户赋予角色
            list.add(new SimpleGrantedAuthority(role.getKeyword()));
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                //遍历权限集合,为用户授权
                list.add(new SimpleGrantedAuthority(permission.getKeyword()));
            }
        }
        org.springframework.security.core.userdetails.User security = new org.springframework.security.core.userdetails.User(username, user.getPassword(), list);
        return security;
    }
}
