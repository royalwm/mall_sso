package com.mall.sso.controller;

import com.mall.sso.config.RedisClient;
import com.mall.sso.constant.RestConstant;
import com.mall.sso.pojo.User;
import com.mall.sso.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisClient redisClinet;

    @RequestMapping(value = "/register/check/{content}/{type}", method = RequestMethod.GET)
    @ResponseBody
    public boolean check(@PathVariable("content") String content, @PathVariable("type") Integer type) {
        return userService.checkRegisterUser(content, type);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public String register(User user) {
        userService.add(user);
        return RestConstant.SUCCESS;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response, User user) {
        List<User> userList = userService.selectUser(user);
        if (userList.size() == 0) {
            return RestConstant.FAILED;
        }
        String token = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("token", token);
        JSONObject fromObject = JSONObject.fromObject(userList.get(0));
        try {
            redisClinet.set(token, fromObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        cookie.setMaxAge(5 * 60);
        redisClinet.setExpire(token,5*60);
        cookie.setPath("/");
        response.addCookie(cookie);
        return RestConstant.SUCCESS;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    redisClinet.setExpire(cookie.getValue(),0);
                    cookie.setValue(null);
                    cookie.setMaxAge(0);// 立即销毁cookie
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
        return "login";
    }

    @RequestMapping(value = "/token/{token}", method = RequestMethod.GET)
    @ResponseBody
    public Object validateSession(HttpServletRequest request, HttpServletResponse response,
                    @PathVariable("token") String token, String callback) {
        String string = null;
        try {
            string = redisClinet.get(token);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONObject fromObject = JSONObject.fromObject(string);
        User user =  (User) JSONObject.toBean(fromObject,User.class);
        if (user != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        cookie.setMaxAge(5 * 60);
                        cookie.setPath("/");
                    }
                }
            }
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(user);
            mappingJacksonValue.setJsonpFunction(callback);
            return mappingJacksonValue;
        }
        return null;
    }
}
