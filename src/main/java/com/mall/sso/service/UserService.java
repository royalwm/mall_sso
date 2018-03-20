package com.mall.sso.service;

import com.mall.sso.constant.RestConstant;
import com.mall.sso.mapper.UserMapper;
import com.mall.sso.pojo.User;
import com.mall.sso.pojo.UserExample;
import com.mall.sso.pojo.UserExample.Criteria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public boolean checkRegisterUser(String content, Integer type) {
        UserExample userExample = new UserExample();
        Criteria createCriteria = userExample.createCriteria();
        if (RestConstant.RegisterType.USERNAME.equals(type)) {
            createCriteria.andUsernameEqualTo(content);
        }
        if (RestConstant.RegisterType.PHONE.equals(type)) {
            createCriteria.andPhoneEqualTo(content);
        }
        List<User> userList = userMapper.selectByExample(userExample);
        if (userList.size() > 0) {
            return true;
        }
        return false;
    }

    public void add(User user) {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setUpdated(new Date());
        user.setCreated(new Date());
        userMapper.insertSelective(user);
    }

    public List<User> selectUser(User user) {
        UserExample userExample = new UserExample();
        Criteria createCriteria = userExample.createCriteria();
        createCriteria.andUsernameEqualTo(user.getUsername());
        createCriteria.andPasswordEqualTo(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        return userMapper.selectByExample(userExample);
    }

}
