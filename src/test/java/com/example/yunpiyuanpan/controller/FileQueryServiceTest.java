package com.example.yunpiyuanpan.controller;

import com.example.yunpiyuanpan.service.FileQueryService;
import com.example.yunpiyuanpan.util.R;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class FileQueryServiceTest {

    @Autowired
    private FileQueryController fileQueryController;

    @Autowired
    private FileQueryService fileQueryService;

    /**
     * 文件重命名
     */
    @Test
    public void renameFileTest(){
        try {
            R r = fileQueryController.renameFile(1l, "qiqi", 1l);
            System.out.println(r);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

//    /**
//     * 删除文件
//     */
//    @Test
//    public void deleteFileTest(){
//        try {
//            R r = fileQueryController.deleteFile(1l, 1l);
//            System.out.println(r);
//        }catch (Exception e){
//            throw new RuntimeException();
//        }
//    }

    /**
     * 文件夹重命名
     */
    @Test
    public void renameFolderTest(){
        try {
            R r = fileQueryController.renameFolder(1l, 1l, "refolder");
            System.out.println(r);
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    /**
     * 删除文件夹
     */
    @Test
    public void deleteFolderTest(){
        try {
            R r = fileQueryController.deleteFolder(1l, 2l);
            System.out.println(r);
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    /**
     * 创建文件夹
     */
    @Test
    public void createFolderTest(){
        try {
            R r = fileQueryController.createFolder(1l, "/test1/", "folder01");
            System.out.println(r);
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    /**
     * 获取一个文件夹里的文件
     */
    @Test
    public void getFilesByFolderPathTest(){
        try {
            R r = fileQueryController.getFilesByFolderPath(1l, "/path/");
            System.out.println(r);
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    /**
     * 移动文件
     */
    @Test
    public void moveFileTest(){
        try {
            R r = fileQueryController.moveFile(1l, 6l, "/test/folder/");
            System.out.println(r);
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    /**
     * 移动文件夹
     */
    @Test
    public void moveFolderTest(){
        try {
            R r = fileQueryController.moveFolder(1l, 1l, "/lj/test/folder1/");
            System.out.println(r);
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    @Test
    public void updateVPath(){
        fileQueryService.updateFolderChildrenPath("/whujiaxy/", "/cpr/", 1l);
    }

}
