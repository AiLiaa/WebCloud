package com.example.yunpiyuanpan.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ScheduledServiceTest {

    @Autowired
    ScheduledService service;

    @Test
    public void cleanTest() {
        try {
            service.doClean();
        }
        catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}
