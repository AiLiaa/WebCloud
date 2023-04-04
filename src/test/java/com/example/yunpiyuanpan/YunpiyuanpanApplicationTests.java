package com.example.yunpiyuanpan;

import com.example.yunpiyuanpan.mapper.YPUserMapper;
import com.example.yunpiyuanpan.mapper.YPUsersizeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YunpiyuanpanApplicationTests {

    @Autowired
    private YPUserMapper ypUserMapper;

    @Autowired
    private YPUsersizeMapper ypUsersizeMapper;

    @Test
    void contextLoads() {
        System.out.println(ypUserMapper.selectById(1));
        System.out.println(ypUsersizeMapper.selectByUserId(1));
    }

}
