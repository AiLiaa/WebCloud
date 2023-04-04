package com.example.yunpiyuanpan.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LoginServiceTest {

    @Autowired
    private LoginService loginService;

    @Test
    void test1(){
        System.out.println(loginService.isLoginLegal("林宇豪","123456"));
    }

}
