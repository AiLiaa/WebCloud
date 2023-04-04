package com.example.yunpiyuanpan.controller;

import com.example.yunpiyuanpan.exception.FileException;
import com.example.yunpiyuanpan.exception.FileStatus;
import com.example.yunpiyuanpan.pojo.YPFile;
import com.example.yunpiyuanpan.pojo.YPRecyclebin;
import com.example.yunpiyuanpan.service.FileQueryService;
import com.example.yunpiyuanpan.service.RecyclebinService;
import com.example.yunpiyuanpan.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@Slf4j
@Api("文件查询接口")
@RestController
public class FileQueryController {

    @Autowired
    private FileQueryService fileQueryService;

    @Autowired
    private RecyclebinService recyclebinService;

    @Value("${storage-lib}")
    private String storageLib;


    /**
     * 删除单个文件，将文件放入回收站，删除的数据存入YPRecyclebin对象中，写入数据库
     * @param userId
     * @param fileId
     * @return
     */
    @ApiOperation(value = "删除单个文件", httpMethod = "DELETE")
    @DeleteMapping("/{id}/deleteFile")
    public R deleteFile(@PathVariable("id") Long userId, Long fileId){
        YPFile ypFile = fileQueryService.getFileById(fileId);
        if(ypFile == null || !userId.equals(ypFile.getUserId())){
            log.error(String.format("userID=%d, file not fount:%d", userId, fileId));
            throw new FileException(FileStatus.FILE_NOT_FOUND);
        }
        YPRecyclebin ypRecyclebin = new YPRecyclebin(ypFile.getId(), ypFile.getFileName(), ypFile.getFileSize(),
                ypFile.getFileType(), ypFile.getFileSuffix(), ypFile.getPhysicalPath(),
                ypFile.getVirtualPath(), ypFile.getUserId(), ypFile.getUploadTime(),
                ypFile.getDownloadTimes(), ypFile.getFileImg(), ypFile.getIsfolder());

        // 数据库删除
        if (fileQueryService.deleteFileById(fileId) == 1){
            // 存入回收站
            recyclebinService.addRecycleBinFile(ypRecyclebin);
        }else {
            log.error(String.format("userID=%d, delete error:%d", userId, fileId));
            throw new FileException(FileStatus.OTHER);
        }
        return R.ok();
    }


    /**
     * 文件重命名
     * @param userId
     * @param newFileName
     * @param fileId
     * @return
     */
    @ApiOperation(value = "文件重命名", httpMethod = "PUT")
    @PutMapping("/{id}/renameFile")
    public R renameFile(@PathVariable("id") Long userId, String newFileName, Long fileId){
        YPFile ypFile = fileQueryService.getFileById(fileId);
        String oldFilePhysicalPath = ypFile.getPhysicalPath();
        // 用户传入的newName不带后缀
        newFileName = newFileName + "." + ypFile.getFileSuffix();

        if(ypFile == null || !userId.equals(ypFile.getUserId())){
            log.error(String.format("userID=%d, file not fount:%d", userId, fileId));
            throw new FileException(FileStatus.FILE_NOT_FOUND);
        }
        //判断同级虚拟目录里的文件是否有同名
        List<YPFile> files = fileQueryService.getFileListBySameLevelVirtualPath(ypFile.getVirtualPath(), userId);
        for (YPFile y : files ) {
            if(newFileName.equals(y.getFileName())){
                log.error(String.format("userID=%d, fileId=%d  file:%s rename fail", userId, fileId, newFileName));
                return R.fail().message("重命名失败，文件名" + newFileName + "已存在" );
            }
        }

        // 修改数据库字段fileName
        Integer integer = fileQueryService.updateFileNameAndPhysicalPathByFileId(ypFile,newFileName);
        if(integer == 1){

            // 重命名前的文件对象
            String physicalPath = String.format("%s%d/%s", storageLib, userId, oldFilePhysicalPath);
            File file = new File(physicalPath);

            // 根路径/uid/时间戳.新文件名.类型
            // 文件重命名
            try {
                String pathName = physicalPath.substring(0, physicalPath.indexOf(".")+1) + newFileName;
                File renameFile = new File(pathName);
                file.renameTo(renameFile);

                log.info("physicalPath: " + physicalPath);
                log.info("pathName: " + pathName);
            }catch (Exception e){
                log.error(String.format("userID=%d, rename error:%d", userId, fileId));
                throw new RuntimeException();
            }

            log.info(String.format("userID=%d, rename success:%d", userId, fileId));

        }else {
            log.error(String.format("userID=%d, rename error:%d", userId, fileId));
            throw new FileException(FileStatus.OTHER);
        }
        return R.ok();
    }

    /**
     * 文件夹或文件重命名
     * @param userId
     * @param folderId
     * @param newFolderName
     * @return
     */
    @ApiOperation(value = "文件夹或文件重命名", httpMethod = "PUT")
    @PutMapping("/{id}/rename")
    public R renameFolder(@PathVariable("id") Long userId, Long folderId, String newFolderName){
        //需要匹配出所有该文件夹下的file并修改其virtualPath
        YPFile ypFolder = fileQueryService.getFolderById(folderId);
        //文件
        if(ypFolder == null){
            YPFile ypFile = fileQueryService.getFileById(folderId);
            String oldFilePhysicalPath = ypFile.getPhysicalPath();
            // 用户传入的newName不带后缀
            newFolderName = newFolderName + "." + ypFile.getFileSuffix();

            if(ypFile == null || !userId.equals(ypFile.getUserId())){
                log.error(String.format("userID=%d, file not fount:%d", userId, folderId));
                throw new FileException(FileStatus.FILE_NOT_FOUND);
            }
            //判断同级虚拟目录里的文件是否有同名
            List<YPFile> files = fileQueryService.getFileListBySameLevelVirtualPath(ypFile.getVirtualPath(), userId);
            for (YPFile y : files ) {
                if(newFolderName.equals(y.getFileName())){
                    log.error(String.format("userID=%d, fileId=%d  file:%s rename fail", userId, folderId, newFolderName));
                    return R.fail().message("重命名失败，文件名" + newFolderName + "已存在" );
                }
            }
            // 修改数据库字段fileName
            Integer integer = fileQueryService.updateFileNameAndPhysicalPathByFileId(ypFile,newFolderName);
            if(integer == 1){
                // 重命名前的文件对象
                String physicalPath = String.format("%s%d/%s", storageLib, userId, oldFilePhysicalPath);
                File file = new File(physicalPath);
                // 根路径/uid/时间戳.新文件名.类型
                // 文件重命名
                try {
                    String pathName = physicalPath.substring(0, physicalPath.indexOf(".")+1) + newFolderName;
                    File renameFile = new File(pathName);
                    file.renameTo(renameFile);
                    log.info("physicalPath: " + physicalPath);
                    log.info("pathName: " + pathName);
                }catch (Exception e){
                    log.error(String.format("userID=%d, rename error:%d", userId, folderId));
                    throw new RuntimeException();
                }
                log.info(String.format("userID=%d, rename success:%d", userId, folderId));
            }else {
                log.error(String.format("userID=%d, rename error:%d", userId, folderId));
                throw new FileException(FileStatus.OTHER);
            }
            return R.ok().message("重命名成功");
        }else {
            //文件夹
            //判断同级虚拟目录里的文件夹是否有同名
            List<YPFile> folders = fileQueryService.getFolderListBySameLevelVirtualPath(ypFolder.getVirtualPath(), userId);
            for (YPFile y : folders ) {
                if(newFolderName.equals(y.getFileName())){
                    log.error(String.format("userID=%d, folderId=%d  folder:%s rename fail", userId, folderId, newFolderName));
                    return R.fail().message("重命名失败，文件夹名" + newFolderName + "已存在" );
                }
            }

            // 更改文件夹名
            String oldname = ypFolder.getFileName();
            ypFolder.setFileName(newFolderName);
            fileQueryService.updateFolderNameByFolderId(ypFolder);

            // 修改文件夹下所有文件及文件夹路径
            String folderPath = ypFolder.getVirtualPath();
            fileQueryService.updateFolderChildrenPath(folderPath+oldname+"/", folderPath+ypFolder.getFileName()+"/", userId);

//
//        // 获取匹配文件的虚拟路径
//        String oldFolderName = ypFolder.getFileName();
//        String oldFolderVirtualPath = ypFolder.getVirtualPath() + oldFolderName + "/";
//
//        ypFolder.setFileName(newFolderName);
//        Integer integer = fileQueryService.updateFolderNameByFolderId(ypFolder);


//        if (integer == 1){
//            List<YPFile> files = fileQueryService.getFileListByVirtualPath(oldFolderVirtualPath, userId);
//            String newVirtualPath =  ypFolder.getVirtualPath() + newFolderName + "/";
//            for (YPFile y : files){
//                // 数据库like模糊查询,剔除其他文件夹的可能搜索结果
//                String substring = y.getVirtualPath().substring(0, oldFolderVirtualPath.length());
//                log.info("substring-----:" + substring);
//                log.info("oldFolderVirtualPath-----:" + oldFolderVirtualPath);
//                if(!substring.equals(oldFolderVirtualPath)){
//                    files.remove(y);
//                    continue;
//                }
//                fileQueryService.updateFileVirtualPathByFileId(y, newVirtualPath, oldFolderVirtualPath);
//            }
//        }else {
//            log.error(String.format("userID=%d, rename error:%d", userId, folderId));
//            throw new FileException(FileStatus.OTHER);
//        }
            return R.ok().message("重命名成功");
        }
    }

    /**
     * 删除文件夹或文件
     * @param userId
     * @param fileId
     * @return
     */
    @ApiOperation(value = "删除文件夹或文件", httpMethod = "DELETE")
    @DeleteMapping("/{uid}/delete")
    public R deleteFolder(@PathVariable("uid") Long userId, Long fileId){

        YPFile ypFolder = fileQueryService.getFolderById(fileId);

        // 文件
        if(ypFolder == null ){
            YPFile ypFile = fileQueryService.getFileById(fileId);
            if(ypFile == null || !userId.equals(ypFile.getUserId())){
                log.error(String.format("userID=%d, file not fount:%d", userId, fileId));
                throw new FileException(FileStatus.FILE_NOT_FOUND);
            }
            YPRecyclebin ypRecyclebin = new YPRecyclebin(ypFile.getId(), ypFile.getFileName(), ypFile.getFileSize(),
                    ypFile.getFileType(), ypFile.getFileSuffix(), ypFile.getPhysicalPath(),
                    ypFile.getVirtualPath(), ypFile.getUserId(), ypFile.getUploadTime(),
                    ypFile.getDownloadTimes(), ypFile.getFileImg(), ypFile.getIsfolder());
            ypRecyclebin.setBinPath("/");

            // 数据库删除
            if (fileQueryService.deleteFileById(fileId) == 1){
                // 存入回收站
                recyclebinService.addRecycleBinFile(ypRecyclebin);
            }else {
                log.error(String.format("userID=%d, delete error:%d", userId, fileId));
                throw new FileException(FileStatus.OTHER);
            }
            return R.ok().message("删除成功");
        }else {//文件夹
            String folderVirtualPath = ypFolder.getVirtualPath();

            if(!userId.equals(ypFolder.getUserId())){
                log.error(String.format("userID=%d, folder not fount:%d", userId, fileId));
                throw new FileException(FileStatus.FILE_NOT_FOUND);
            }
            String virtualPath = ypFolder.getVirtualPath() + ypFolder.getFileName() + "/";
            List<YPFile> files = fileQueryService.getFileListByVirtualPath(virtualPath, userId);
            for (YPFile y : files){
                // 数据库like模糊查询,剔除其他文件夹的可能搜索结果
                String substring = y.getVirtualPath().substring(0, virtualPath.length());
                if(!substring.equals(virtualPath)){
                    files.remove(y);
                }
            }
            List<YPFile> folders = fileQueryService.getFolderListByVirtualPath(virtualPath, userId);
            for (YPFile y : folders){
                // 数据库like模糊查询,剔除其他文件夹的可能搜索结果
                String substring = y.getVirtualPath().substring(0, virtualPath.length());
                if(!substring.equals(virtualPath)){
                    files.remove(y);
                }
            }
            YPRecyclebin ypRecyclebin;
            if(files.size() == 0 && folders.size() == 0){ // 空文件夹
                ypRecyclebin = new YPRecyclebin(ypFolder.getId(), ypFolder.getFileName(), ypFolder.getFileSize(),
                        ypFolder.getFileType(), ypFolder.getFileSuffix(), ypFolder.getPhysicalPath(),
                        ypFolder.getVirtualPath(), ypFolder.getUserId(), ypFolder.getUploadTime(),
                        ypFolder.getDownloadTimes(), ypFolder.getFileImg(), ypFolder.getIsfolder());
                ypRecyclebin.setBinPath("/");
                if (fileQueryService.deleteFolderById(fileId) == 1){
                    // 添加到回收站
                    recyclebinService.addRecycleBinFile(ypRecyclebin);
                    return R.ok().message("删除成功");
                }
                else
                    return R.error().message("未知异常，删除失败");
            }else {
                // 删除该文件夹里的所有文件
                if(files.size() != 0){
                    for(YPFile y : files){
                        ypRecyclebin = new YPRecyclebin(y.getId(), y.getFileName(), y.getFileSize(),
                                y.getFileType(), y.getFileSuffix(), y.getPhysicalPath(),
                                y.getVirtualPath(), y.getUserId(), y.getUploadTime(),
                                y.getDownloadTimes(), y.getFileImg(), y.getIsfolder());
                        String virtualPath1 = y.getVirtualPath();
                        String binPath = virtualPath1.substring(folderVirtualPath.length()-1, virtualPath1.length());
//                        System.out.println("fileBinPath:"+binPath);
                        ypRecyclebin.setBinPath(binPath);
                        if (fileQueryService.deleteFileById(y.getId()) == 1){
                            recyclebinService.addRecycleBinFile(ypRecyclebin);
                        }
                    }
                }
                // 删除该文件夹里的所有文件夹
                if(folders.size() != 0){
                    for(YPFile y : folders){
                        ypRecyclebin = new YPRecyclebin(y.getId(), y.getFileName(), y.getFileSize(),
                                y.getFileType(), y.getFileSuffix(), y.getPhysicalPath(),
                                y.getVirtualPath(), y.getUserId(), y.getUploadTime(),
                                y.getDownloadTimes(), y.getFileImg(), y.getIsfolder());
                        String virtualPath1 = y.getVirtualPath();
                        String binPath = virtualPath1.substring(folderVirtualPath.length()-1, virtualPath1.length());
//                        System.out.println("folderBinPath:"+binPath);
                        ypRecyclebin.setBinPath(binPath);
                        if (fileQueryService.deleteFolderById(y.getId()) == 1){
                            recyclebinService.addRecycleBinFile(ypRecyclebin);
                        }

                    }
                }
                //删除该文件夹
                ypRecyclebin = new YPRecyclebin(ypFolder.getId(), ypFolder.getFileName(), ypFolder.getFileSize(),
                        ypFolder.getFileType(), ypFolder.getFileSuffix(), ypFolder.getPhysicalPath(),
                        ypFolder.getVirtualPath(), ypFolder.getUserId(), ypFolder.getUploadTime(),
                        ypFolder.getDownloadTimes(), ypFolder.getFileImg(), ypFolder.getIsfolder());
                ypRecyclebin.setBinPath("/");
                if (fileQueryService.deleteFolderById(fileId) == 1){
                    recyclebinService.addRecycleBinFile(ypRecyclebin);
                }
                return R.ok().message("删除成功");
            }
        }
    }


    /**
     * 创建文件夹
     * @param userId
     * @param path
     * @param folderName
     * @return
     */
    @ApiOperation(value = "创建文件夹", httpMethod = "POST")
    @PostMapping("/{id}/createFolder")
    public R createFolder(@PathVariable("id") Long userId, String path, String folderName){
        //判断同级文件夹是否重名
        List<YPFile> folders = fileQueryService.getFolderListBySameLevelVirtualPath(path, userId);
        for (YPFile y : folders){
            if(folderName.equals(y.getFileName())){
                return R.fail().message("创建失败，文件夹重名");
            }
        }
        YPFile folder = new YPFile();
        folder.setVirtualPath(path);
        folder.setUserId(userId);
        folder.setFileName(folderName);
        folder.setIsfolder(1);
        folder.setPhysicalPath("");
        folder.setFileType("");
        folder.setFileSuffix("");
        if (fileQueryService.createFolder(folder) == 1){
            return R.ok().message("创建成功");
        }else {
            return R.error().message("数据读入失败");
        }
    }

    /**
     * 陈列文件，文件夹
     * @param userId
     * @param path
     * @return
     */
    @ApiOperation(value = "根据路径查询路径下所有文件和文件夹", httpMethod = "GET")
    @GetMapping("/{id}/getFilesAndFolders")
    public R getFilesByFolderPath(@PathVariable("id") Long userId, @RequestParam String path){
        List<YPFile> list = fileQueryService.getFilesByPath(path, userId);
        return R.ok().data("fileList", list);
    }

    /**
     * 刷新时更新文件夹大小
     * @param userId
     * @param path
     * @return
     */
    @ApiOperation(value = "计算文件夹大小", httpMethod = "GET")
    @GetMapping("/{id}/getFolderSize")
    public R getFolderSize(@PathVariable("id") Long userId, @RequestParam String path){
        List<YPFile> folders = fileQueryService.getFolderListBySameLevelVirtualPath(path, userId);
//        System.out.println(folders);
        double size;
        for (YPFile folder : folders){
            size = 0;
            List<YPFile> files = fileQueryService.getFileListByVirtualPath(folder.getVirtualPath() + folder.getFileName() + "/", userId);
            for (YPFile file : files){
                size += file.getFileSize();
            }
//            System.out.println(size);
            if(size != folder.getFileSize()){
                folder.setFileSize(size);
                fileQueryService.updateFolderSize(folder,userId);
            }
        }
        return R.ok();
    }

    /**
     * 移动文件
     * @param userId
     * @param fileId
     * @param newPath
     * @return
     */
    @ApiOperation(value = "移动文件", httpMethod = "PUT")
    @PutMapping("/{id}/moveFile")
    public  R moveFile(@PathVariable("id") Long userId, Long fileId, String newPath){
        YPFile ypFile = fileQueryService.getFileById(fileId);
        if(ypFile == null || !userId.equals(ypFile.getUserId())){
            log.error(String.format("userID=%d, file not fount:%d", userId, fileId));
            throw new FileException(FileStatus.FILE_NOT_FOUND);
        }
        // 判断新文件夹里有无同名文件
        List<YPFile> files = fileQueryService.getFileListBySameLevelVirtualPath(newPath, userId);
        if(files.size() != 0){
            for (YPFile y : files){
                if (ypFile.getFileName().equals(y.getFileName())){
                    return R.fail().message("移动失败，目标文件夹存在同名文件");
                }
            }
        }

        ypFile.setVirtualPath(newPath);
        if(fileQueryService.updateFileVirtualPath(ypFile) == 1){
            return R.ok();
        }else {
            return R.error().message("移动失败");
        }
    }

    /**
     * 移动文件夹或文件
     * @param userId
     * @param newPath
     * @return
     */
    @ApiOperation(value = "移动文件夹或文件", httpMethod = "PUT")
    @PutMapping("/{id}/move")
    public  R moveFolder(@PathVariable("id") Long userId, Long folderId, String newPath){
        YPFile folderByPath = fileQueryService.getFolderByPath(newPath);
        if (folderByPath == null && !newPath.equals("/")){
            return R.fail().message("目标文件夹不存在");
        }
        YPFile folder = fileQueryService.getFolderById(folderId);
        //文件
        if(folder == null){
            YPFile ypFile = fileQueryService.getFileById(folderId);
            if(ypFile == null || !userId.equals(ypFile.getUserId())){
                log.error(String.format("userID=%d, file not fount:%d", userId, folderId));
                throw new FileException(FileStatus.FILE_NOT_FOUND);
            }
            // 判断新文件夹里有无同名文件
            List<YPFile> files = fileQueryService.getFileListBySameLevelVirtualPath(newPath, userId);
            if(files.size() != 0){
                for (YPFile y : files){
                    if (ypFile.getFileName().equals(y.getFileName())){
                        return R.fail().message("移动失败，目标文件夹存在同名文件");
                    }
                }
            }
            ypFile.setVirtualPath(newPath);
            if(fileQueryService.updateFileVirtualPath(ypFile) == 1){
                return R.ok().message("移动成功");
            }else {
                return R.error().message("移动失败");
            }
        }else {
            //文件夹
            // 判断目标文件夹里面有无与该文件夹同名的文件夹
            List<YPFile> folders = fileQueryService.getFolderListBySameLevelVirtualPath(newPath, userId);
            if(folders.size() != 0){
                for (YPFile y : folders){
                    if (folder.getFileName().equals(y.getFileName())){
                        return R.fail().message("目标文件夹内存在同名文件夹");
                    }
                }
            }

            // 修改文件夹路径
            String oldPath = folder.getVirtualPath();
            folder.setVirtualPath(newPath);
            fileQueryService.updateFolderVirtualPath(folder);

            // 修改文件夹目录下子文件及文件夹的路径
            String folderPath = folder.getVirtualPath();
            fileQueryService.updateFolderChildrenPath(oldPath+folder.getFileName()+"/", newPath+folder.getFileName()+"/", userId);

            return R.ok().message("移动成功");
        }
    }

    /**
     * 逐层修改文件夹虚拟目录
     * @param folder
     * @param newPath
     */
    // todo 废弃的做法，更改为使用正则匹配路径前缀
//    public void modifyVirtualPath(YPFile folder, String newPath){
//        //该文件夹信息
//        long userId = folder.getUserId();
//        String virtualPath = folder.getVirtualPath() + folder.getFileName() + "/";
//
//        // 该文件夹下的所有文件
//        List<YPFile> files = fileQueryService.getFileListBySameLevelVirtualPath(virtualPath, userId);
//        if(files.size() != 0){
//            for (YPFile f : files){
//                String virtualPath_file = newPath + folder.getFileName() + "/";
//                f.setVirtualPath(virtualPath_file);
//                fileQueryService.updateFileVirtualPath(f);
//            }
//        }
//
//        // 该文件夹下的所有文件夹
//        List<YPFile> folders = fileQueryService.getFolderListBySameLevelVirtualPath(virtualPath, userId);
//        if (folders.size() != 0){
//            for (YPFile y : folders){
//                String newPathTemp = newPath + folder.getFileName()  + "/";
//                // 处理文件夹里的文件夹
//                modifyVirtualPath(y, newPathTemp);
//            }
//        }
//        // 修改该文件夹
//        folder.setVirtualPath(newPath);
//        fileQueryService.updateFolderVirtualPath(folder);
//    }

}
