package com.mall.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/register")
public class RegisterController {
    @RequestMapping(method = RequestMethod.GET)
    public String loginPage() {
        return "register";
    }

    @RequestMapping(value="/readProtocol",method = RequestMethod.GET)
    public String readProtocol() {
        return "readProtocol";
    }
}
