package com.example.yunpiyuanpan.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Test
    void test1(){
        fileService.getFileByFileId((long)1);
    }

}
