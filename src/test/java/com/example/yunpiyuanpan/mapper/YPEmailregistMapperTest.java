package com.example.yunpiyuanpan.mapper;

import com.example.yunpiyuanpan.pojo.YPEmailRegist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class YPEmailregistMapperTest {

    @Autowired
    private YPEmailregistMapper ypEmailregistMapper;

    @Test
    void test1(){
        System.out.println(ypEmailregistMapper.selectByEmail("100@qq.com"));
    }

}
