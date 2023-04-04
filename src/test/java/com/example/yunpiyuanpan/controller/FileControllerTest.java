package com.example.yunpiyuanpan.controller;

import com.example.yunpiyuanpan.util.R;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
@SpringBootTest
public class FileControllerTest {

    @Autowired
    FileController fileController;

    @Test
    void uploadTest(){
        File file = new File("E:\\whujiaxy\\paper-explorer\\README.md");
        try {
            FileInputStream inputStream = new FileInputStream(file);
            MultipartFile mockMultipartFile = new MockMultipartFile(file.getName(), file.getName(), "Image", inputStream);
            R r = fileController.upload( "/whujiaxy/", mockMultipartFile);
            Assertions.assertTrue(r.getSuccess());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
