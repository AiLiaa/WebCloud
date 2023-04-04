package com.example.yunpiyuanpan;

import cn.dev33.satoken.SaManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class YunpiyuanpanApplication {

    public static void main(String[] args) {
        SpringApplication.run(YunpiyuanpanApplication.class, args);
        log.info("启动成功：Sa-Token配置如下：" + SaManager.getConfig());
    }

}
