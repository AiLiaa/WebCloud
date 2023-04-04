package com.example.yunpiyuanpan.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.yunpiyuanpan.exception.FileException;
import com.example.yunpiyuanpan.exception.FileStatus;
import com.example.yunpiyuanpan.pojo.YPFile;
import com.example.yunpiyuanpan.pojo.YPUsersize;
import com.example.yunpiyuanpan.service.FileQueryService;
import com.example.yunpiyuanpan.service.FileService;
import com.example.yunpiyuanpan.util.R;
import com.example.yunpiyuanpan.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@Api("文件传输接口")
// todo: 是否要加前缀 @RequestMapping(?)
public class FileController implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Value("${storage-lib}")
    String storageLib;

    @Autowired
    FileService fileService;

    @Autowired
    FileQueryService fileQueryService;

    @PostMapping("/upload")
    @ApiOperation(value = "上传文件", httpMethod = "POST")
    public R upload(@RequestParam("path") @Nullable String path, MultipartFile ifile){
        Long userId = StpUtil.getLoginIdAsLong();
        if(path==null || path.equals("")){
            path = "/";
        }
        if(ifile==null || ifile.isEmpty()){
            return R.fail().message("文件上传失败:文件不存在或文件为空");
        }


        // 记录相关数据
        YPFile ypFile = new YPFile();
        String filename = ifile.getOriginalFilename();
        ypFile.setFileName(filename);
        ypFile.setFileSuffix(filename.substring(filename.lastIndexOf(".") + 1));
        ypFile.setFileSize(ifile.getSize()/1000d);//getSize()返回值单位为Byte
        ypFile.setFileType(ifile.getContentType());
        ypFile.setUserId(userId);
        ypFile.setVirtualPath(path);

        // 确认用户空间
        YPUsersize userSize = fileQueryService.getUserSize(userId);
        if (userSize.getUserMaxsize() < userSize.getUserUsedsize() + ypFile.getFileSize()){
            return R.fail().message("空间不足");
        }

        // 文件原名附加时间戳前缀避免同名冲突
        String physicalPath = String.format("%d.%s",System.currentTimeMillis(), filename);
        String parentDir = String.format("%s/%s/",storageLib, userId);
        ypFile.setPhysicalPath(physicalPath);
        File userLib = new File(parentDir);
        if(!userLib.exists()){
            userLib.mkdirs();
        }
        try {
            ifile.transferTo(new File(parentDir+physicalPath));
        } catch (IOException e) {
            e.printStackTrace();
            return R.error().message("上传失败");
        }
        boolean isExist = fileService.checkFileExist(ypFile);
        fileService.saveYPFile(ypFile, isExist);

        // 更新用户空间大小
        fileQueryService.updateUserUsedSize(userSize.getUserUsedsize() + ypFile.getFileSize(), userId);
        //todo 返回值待讨论
        return R.ok();
    }

    @ApiOperation(value = "下载文件", httpMethod = "GET")
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("fileId") Long fileId, HttpServletRequest request){
        Long userId = StpUtil.getLoginIdAsLong();
        Resource resource = null;
        try {
            resource = fileService.loadFileAsResource(userId, fileId);
        } catch (FileException e){
            logger.error(e.getFileStatus().toString());
            if(e.getFileStatus().equals(FileStatus.FILE_GOT_LOST)){
                return ResponseEntity.internalServerError().build();
            }

            return ResponseEntity.badRequest().build();
        }

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            logger.error("无法获取文件类型", e);
        }
        if(contentType == null){
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename().substring(resource.getFilename().indexOf(".")+1) + "\"")
                .body(resource);


    }
    // "http://localhost:8989/publicDownload?access_token=guifggliutliugvuilfyluidyi"
    @ApiOperation(value = "下载公开的分享文件", httpMethod = "GET")
    @GetMapping("/publicDownload")
    public ResponseEntity<Resource> publicDownload(@RequestParam("access_token") String token, HttpServletRequest request) {
        if(null==token || !TokenUtil.verify(token)){
            return ResponseEntity.badRequest().build();
        }
        Map map = TokenUtil.parseToken(token);
        Long userId = (Long) map.get("userId");
        Long fileId = (Long) map.get("fileId");

        Resource resource = fileService.loadFileAsResource(userId, fileId, false);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            logger.error("无法获取文件类型", e);
        }
        if(contentType == null){
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename().substring(resource.getFilename().indexOf(".")+1) + "\"")
                .body(resource);
    }

    @ApiOperation(value = "生成分享码")
    @GetMapping("/getShareCode")
    public R getShareCode(@RequestParam Long fileId){
        //todo:文件所有权检验
        Long userId = StpUtil.getLoginIdAsLong();
        if(!fileService.checkOwnership(userId, fileId)){
            R.error();
        }
        String token= TokenUtil.sign(userId, fileId);
        return R.ok().data("access_token", token);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        File lib = new File(storageLib);
        if (!lib.exists()) {
            logger.info("库目录不存在，创建空的文件库");
            lib.mkdirs();
        }
        else if (!lib.isDirectory()){
            logger.error("库路径下存在与库同名的文件，请检查路径");
            throw new Exception();
        }
    }
}
