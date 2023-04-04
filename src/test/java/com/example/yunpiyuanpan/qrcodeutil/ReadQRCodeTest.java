package com.example.yunpiyuanpan.qrcodeutil;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
public class ReadQRCodeTest {
    @Test
    void test1() throws IOException {
        File file = new File("F:/","bg.jpg");
        System.out.println(ReadQRCode.readQRCode(file));
        if(ReadQRCode.readQRCode(file).equals("")){
            System.out.println("啥也没读出来");
        }
    }
}
