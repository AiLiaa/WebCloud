package com.example.yunpiyuanpan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yunpiyuanpan.mapper.YPFileMapper;
import com.example.yunpiyuanpan.mapper.YPUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j
@Component
public class ScheduledService {

    @Value("${storage-lib}")
    String storageLib;

    @Autowired
    YPFileMapper fileMapper;

    @Autowired
    YPUserMapper userMapper;

    /**
     * 定时对文件仓库进行检查，删去不在数据库中的文件。
     * "0 0 12 ?" 表示每天中午12点执行
     */
    @Scheduled(cron = "0 0 12 ?")
    public void doClean(){
        List<Long> userIds = userMapper.selectAllUserId();
        for (Long id : userIds) {
            List<String> physicalPathList = fileMapper.selectPhysicalPathListByUserId(id);
            if(physicalPathList.isEmpty()){
                continue;
            }
            String fileDir = String.format("%s%d", storageLib, id);
            File dir = new File(fileDir);
            if(!dir.exists()){
                continue;
            }
            File[] files = dir.listFiles();
            if(files==null){
                continue;
            }
            for (File file : files) {
                if(!physicalPathList.contains(file.getName())){
                    log.info(id + "\\" + file.getName() + " not exists in db");
                    file.deleteOnExit();
                }
            }
        }
        log.info("Scheduled Task has run Successfully");
    }

}
