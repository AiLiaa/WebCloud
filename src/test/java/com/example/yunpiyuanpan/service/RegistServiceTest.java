package com.example.yunpiyuanpan.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegistServiceTest {

    @Autowired
    private RegistService registService;

    @Test
    void test1(){
        registService.sendCodeToEmailAddress("1005772685@qq.com");
    }

    @Test
    void test2(){
        registService.isCodeLegal("1005772685@qq.com","111111");
    }

    @Test
    void test3(){
        registService.registSuccess("100@qq.com","芜湖","123456","110");
    }

    @Test
    void test4(){
        registService.isRegistInfoLegal("林宇豪","15623728663");
    }

    @Test
    void test5(){
        registService.isEmailLegal("1005772685@qq.com");
    }
}
