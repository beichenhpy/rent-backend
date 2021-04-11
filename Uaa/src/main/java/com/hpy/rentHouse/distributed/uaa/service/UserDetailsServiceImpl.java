package com.hpy.rentHouse.distributed.uaa.service;

import DO.AuthsDo;
import com.hpy.rentHouse.distributed.uaa.dao.UserMapper;
import DO.UserDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author beichenhpy
 * 在数据库中查询用户的信息对应查找到用户是否存在，如果存在则查找到她的用户信息，并将用户名和密码权限放入验证信息中
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDo bean = userMapper.findByName(username);
        if (bean == null) {
            throw new UsernameNotFoundException("此账号不存在");
        }
        //查询用户的权限
        List<AuthsDo> authList = userMapper.findPerm(bean.getUid());
        List<String> permissions = new ArrayList<String>();
        for (AuthsDo auth : authList) {
            permissions.add(auth.getAuthors());
        }
        String[] permissionArray = new String[permissions.size()];
        permissions.toArray(permissionArray);
        //将用户的uid和权限放入User中
        return User.withUsername(bean.getUid()).password(bean.getPassword()).authorities(permissionArray).build();
    }
}