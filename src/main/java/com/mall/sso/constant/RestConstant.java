package com.mall.sso.constant;

public interface RestConstant {
    String SUCCESS = "1";
    String FAILED = "0";
    public interface Login {
        String COOKIE="cookie已失效，请重新登陆";
    }

    public interface RegisterType {
        Integer USERNAME = 1;
        Integer PHONE = 2;
    }
}
