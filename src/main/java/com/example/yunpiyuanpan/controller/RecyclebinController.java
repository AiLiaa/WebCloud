package com.example.yunpiyuanpan.controller;

import com.example.yunpiyuanpan.exception.FileException;
import com.example.yunpiyuanpan.exception.FileStatus;
import com.example.yunpiyuanpan.pojo.YPFile;
import com.example.yunpiyuanpan.pojo.YPRecyclebin;
import com.example.yunpiyuanpan.pojo.YPUsersize;
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
@Api("文件回收站接口")
@RestController
public class RecyclebinController {

    @Autowired
    private FileQueryService fileQueryService;

    @Autowired
    private RecyclebinService recyclebinService;

    @Value("${storage-lib}")
    private String storageLib;

    /**
     * 删除回收站文件，删除物理文件
     * @param userId
     * @param fileId
     * @return
     */
    @ApiOperation(value = "删除回收站文件", httpMethod = "DELETE")
    @DeleteMapping("/{id}/deleteBinFile")
    public R deleteFile(@PathVariable("id") Long userId, Long fileId){
        YPRecyclebin ypBinFile = recyclebinService.getBinFileById(fileId);
        if(ypBinFile == null || !userId.equals(ypBinFile.getUserId())){
            log.error(String.format("userID=%d, binFile not fount:%d", userId, fileId));
            throw new FileException(FileStatus.FILE_NOT_FOUND);
        }
        double fileSize = ypBinFile.getFileSize();
        if (recyclebinService.deleteBinFileById(ypBinFile.getId()) == 1){
                        try {
                // 物理文件删除
                String physicalPath = String.format("%s%d/%s", storageLib, userId, ypBinFile.getPhysicalPath());
                File file = new File(physicalPath);
                boolean delete = file.delete();
                if(delete){
                    // 用户已使用内存减少
                    YPUsersize userSize = fileQueryService.getUserSize(userId);
                    double userUsedsize = userSize.getUserUsedsize();
                    double newSize = userUsedsize - fileSize;
                    userSize.setUserUsedsize(newSize);
                    fileQueryService.updateUserUsedSize(newSize, userId);
                }
            }catch (Exception e){
                log.error(String.format("userID=%d,fileId=%d 打开或删除文件异常", userId, fileId));
                throw new RuntimeException();
            }
        }else {
            log.error(String.format("userID=%d, delete error:%d", userId, fileId));
            throw new FileException(FileStatus.OTHER);
        }
        return R.ok();
    }

    /**
     * 删除回收站文件夹或文件
     * @param userId
     * @param folderId
     * @return
     */
    @ApiOperation(value = "删除回收站文件夹或文件", httpMethod = "DELETE")
    @DeleteMapping("/{id}/deleteBinFolder")
    public R deleteFolder(@PathVariable("id") Long userId, Long folderId){

        YPRecyclebin ypFolder = recyclebinService.getBinFolderById(folderId);
        //文件
        if(ypFolder == null){
            YPRecyclebin ypBinFile = recyclebinService.getBinFileById(folderId);
            if(ypBinFile == null || !userId.equals(ypBinFile.getUserId())){
                log.error(String.format("userID=%d, binFile not fount:%d", userId, folderId));
                throw new FileException(FileStatus.FILE_NOT_FOUND);
            }
            double fileSize = ypBinFile.getFileSize();
            if (recyclebinService.deleteBinFileById(ypBinFile.getId()) == 1){
                try {
                    // 物理文件删除
                    String physicalPath = String.format("%s%d/%s", storageLib, userId, ypBinFile.getPhysicalPath());
                    File file = new File(physicalPath);
                    boolean delete = file.delete();
                    if(delete){
                        // 用户已使用内存减少
                        YPUsersize userSize = fileQueryService.getUserSize(userId);
                        double userUsedsize = userSize.getUserUsedsize();
                        double newSize = userUsedsize - fileSize;
                        userSize.setUserUsedsize(newSize);
                        fileQueryService.updateUserUsedSize(newSize, userId);
                    }
                }catch (Exception e){
                    log.error(String.format("userID=%d,fileId=%d 打开或删除文件异常", userId, folderId));
                    throw new RuntimeException();
                }
            }else {
                log.error(String.format("userID=%d, delete error:%d", userId, folderId));
                throw new FileException(FileStatus.OTHER);
            }
            return R.ok().message("彻底删除成功");
        }else {
            //文件夹
            String virtualPath = ypFolder.getVirtualPath() + ypFolder.getFileName() + "/";
            List<YPRecyclebin> files = recyclebinService.getBinFileListByVirtualPath(virtualPath, userId);
            if(files.size()!=0){
                for (YPRecyclebin y : files){
                    // 数据库like模糊查询,剔除其他文件夹的可能搜索结果
                    String substring = y.getVirtualPath().substring(0, virtualPath.length());
                    if(!substring.equals(virtualPath)){
                        files.remove(y);
                    }
                }
            }

            List<YPRecyclebin> folders = recyclebinService.getBinFolderListByVirtualPath(virtualPath, userId);
            if(folders.size()!=0){
                for (YPRecyclebin y : folders){
                    // 数据库like模糊查询,剔除其他文件夹的可能搜索结果
                    String substring = y.getVirtualPath().substring(0, virtualPath.length());
                    if(!substring.equals(virtualPath)){
                        files.remove(y);
                    }
                }
            }

            if(files.size() == 0 && folders.size() == 0){ // 空文件夹
                if (recyclebinService.deleteBinFolderById(folderId) == 1){
                    return R.ok().message("彻底删除成功");
                }
                else
                    return R.error().message("未知异常，删除失败");
            }else {
                // 删除该文件夹里的所有文件
                if(files.size() != 0){
                    double subSize = 0;//删除的文件总空间大小
                    for(YPRecyclebin y : files){
                        try {
                            String physicalPath = String.format("%s%d/%s", storageLib, userId, y.getPhysicalPath());
                            File file = new File(physicalPath);
                            boolean delete = file.delete();
                            if(delete){
                                subSize += y.getFileSize();
                                recyclebinService.deleteBinFileById(y.getId());
                            }else {
                                return R.error().message("删除失败,未找到文件");
                            }
                        }catch (Exception e){
                            log.error(String.format("userID=%d,fileId=%d 打开或删除文件异常", userId, y.getId()));
                            throw new RuntimeException();
                        }
                    }
                    YPUsersize userSize = fileQueryService.getUserSize(userId);
                    double userUsedsize = userSize.getUserUsedsize();
                    double newUsedSize = userUsedsize - subSize;
                    fileQueryService.updateUserUsedSize(newUsedSize, userId);
                }
                // 删除该文件夹里的所有文件夹
                if(folders.size() != 0){
                    for(YPRecyclebin y : folders){
                        recyclebinService.deleteBinFolderById(y.getId());
                    }
                }
                //删除该文件夹
                recyclebinService.deleteBinFolderById(folderId);
                return R.ok().message("彻底删除成功");
            }
        }

    }

    /**
     * 陈列文件，文件夹
     * @param userId
     * @param path
     * @return
     */
    @ApiOperation(value = "根据路径查询路径下所有文件和文件夹", httpMethod = "GET")
    @GetMapping("/{id}/getBinFilesAndFolders")
    public R getFilesByFolderPath(@PathVariable("id") Long userId, @RequestParam String path){
        List<YPRecyclebin> list = recyclebinService.getBinFilesByPath(path, userId);
        return R.ok().data("fileList", list);
    }

    /**
     * bin根路径陈列文件，文件夹
     * @param userId
     * @param path
     * @return
     */
    @ApiOperation(value = "bin根路径陈列", httpMethod = "GET")
    @GetMapping("/{id}/getFilesByBinPath")
    public R getFilesByBinPath(@PathVariable("id") Long userId, @RequestParam String path){
        List<YPRecyclebin> list = recyclebinService.getBinFilesByBinPath(path, userId);
        return R.ok().data("fileList", list);
    }

    /**
     * 从回收站中恢复文件
     * @param userId
     * @param fileId
     * @return
     */
    @ApiOperation(value = "恢复文件", httpMethod = "PUT")
    @PutMapping("/{id}/recoverFile")
    public R recoverFile(@PathVariable("id") Long userId, Long fileId){
        YPRecyclebin ypBinFile = recyclebinService.getBinFileById(fileId);
        if(ypBinFile == null || !userId.equals(ypBinFile.getUserId())){
            log.error(String.format("userID=%d, binFile not fount:%d", userId, fileId));
            throw new FileException(FileStatus.FILE_NOT_FOUND);
        }
        YPFile ypFile = new YPFile(ypBinFile.getId(), ypBinFile.getFileName(), ypBinFile.getFileSize(),
                ypBinFile.getFileType(), ypBinFile.getFileSuffix(), ypBinFile.getPhysicalPath(),
                ypBinFile.getVirtualPath(), ypBinFile.getUserId(), ypBinFile.getUploadTime(),
                ypBinFile.getDownloadTimes(), ypBinFile.getFileImg(), ypBinFile.getIsfolder());
        if (recyclebinService.deleteBinFileById(fileId) == 1){
            recyclebinService.recoverFile(ypFile);
            return R.ok();
        }else
            return R.error();
    }

    /**
     * 恢复文件夹或文件
     * @param userId
     * @param folderId
     * @return
     */
    @ApiOperation(value = "恢复文件夹或文件", httpMethod = "PUT")
    @PutMapping("/{id}/recoverFolder")
    public R recoverFolder(@PathVariable("id") Long userId, Long folderId){
        YPRecyclebin folder = recyclebinService.getBinFolderById(folderId);
        //文件
        if(folder == null){
            YPRecyclebin ypBinFile = recyclebinService.getBinFileById(folderId);
            if(ypBinFile == null || !userId.equals(ypBinFile.getUserId())){
                log.error(String.format("userID=%d, binFile not fount:%d", userId, folderId));
                throw new FileException(FileStatus.FILE_NOT_FOUND);
            }
            YPFile ypFile = new YPFile(ypBinFile.getId(), ypBinFile.getFileName(), ypBinFile.getFileSize(),
                    ypBinFile.getFileType(), ypBinFile.getFileSuffix(), ypBinFile.getPhysicalPath(),
                    ypBinFile.getVirtualPath(), ypBinFile.getUserId(), ypBinFile.getUploadTime(),
                    ypBinFile.getDownloadTimes(), ypBinFile.getFileImg(), ypBinFile.getIsfolder());
            if (recyclebinService.deleteBinFileById(folderId) == 1){
                recyclebinService.recoverFile(ypFile);
                return R.ok().message("成功恢复");
            }else
                return R.error().message("错误");
        }else {
            //文件夹
            YPFile ypFile;
            String virtualPath = folder.getVirtualPath() + folder.getFileName() + "/";

            List<YPRecyclebin> files = recyclebinService.getBinFileListByVirtualPath(virtualPath, userId);
            if(files.size()!=0){
                for (YPRecyclebin ypBinFile : files){
                    // 数据库like模糊查询,剔除其他文件夹的可能搜索结果
                    String substring = ypBinFile.getVirtualPath().substring(0, virtualPath.length());
                    if(substring.equals(virtualPath)){
                        ypFile = new YPFile(ypBinFile.getId(), ypBinFile.getFileName(), ypBinFile.getFileSize(),
                                ypBinFile.getFileType(), ypBinFile.getFileSuffix(), ypBinFile.getPhysicalPath(),
                                ypBinFile.getVirtualPath(), ypBinFile.getUserId(), ypBinFile.getUploadTime(),
                                ypBinFile.getDownloadTimes(), ypBinFile.getFileImg(), ypBinFile.getIsfolder());
                        if(!(recyclebinService.deleteBinFileById(ypBinFile.getId()) == 1 && recyclebinService.recoverFile(ypFile) == 1)){
                            return R.error().message("error");
                        }
                    }
                }
            }

            List<YPRecyclebin> folders = recyclebinService.getBinFolderListByVirtualPath(virtualPath, userId);
            if(folders.size()!=0){
                for (YPRecyclebin ypBinFolder : folders){
                    // 数据库like模糊查询,剔除其他文件夹的可能搜索结果
                    String substring = ypBinFolder.getVirtualPath().substring(0, virtualPath.length());
                    if(substring.equals(virtualPath)){
                        ypFile = new YPFile(ypBinFolder.getId(), ypBinFolder.getFileName(), ypBinFolder.getFileSize(),
                                ypBinFolder.getFileType(), ypBinFolder.getFileSuffix(), ypBinFolder.getPhysicalPath(),
                                ypBinFolder.getVirtualPath(), ypBinFolder.getUserId(), ypBinFolder.getUploadTime(),
                                ypBinFolder.getDownloadTimes(), ypBinFolder.getFileImg(), ypBinFolder.getIsfolder());
                        if(!(recyclebinService.deleteBinFolderById(ypBinFolder.getId()) == 1 && recyclebinService.recoverFile(ypFile) == 1)){
                            return R.error().message("error");
                        }
                    }
                }
            }

            ypFile = new YPFile(folder.getId(), folder.getFileName(), folder.getFileSize(),
                    folder.getFileType(), folder.getFileSuffix(), folder.getPhysicalPath(),
                    folder.getVirtualPath(), folder.getUserId(), folder.getUploadTime(),
                    folder.getDownloadTimes(), folder.getFileImg(), folder.getIsfolder());
            if(!(recyclebinService.deleteBinFolderById(folder.getId()) == 1 && recyclebinService.recoverFile(ypFile) == 1)){
                return R.error().message("error");
            }
            return R.ok().message("成功恢复");
        }
        }


}
