package com.example.yunpiyuanpan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yunpiyuanpan.mapper.YPFileMapper;
import com.example.yunpiyuanpan.mapper.YPUsersizeMapper;
import com.example.yunpiyuanpan.pojo.YPFile;
import com.example.yunpiyuanpan.pojo.YPUsersize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileQueryService {
    @Autowired
    private YPFileMapper ypFileMapper;

    @Autowired
    private YPUsersizeMapper ypUsersizeMapper;

    /**
     * 根据id获取单个文件
     * @param fileId
     * @return YPFile
     */
    public YPFile getFileById(Long fileId){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("id",fileId);
        wrapper.eq("isfolder",0);
        return ypFileMapper.selectOne(wrapper);
    }

    /**
     * 根据id获取单个文件夹
     * @param folderId
     * @return
     */
    public YPFile getFolderById(Long folderId){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("id",folderId);
        wrapper.eq("isfolder",1);
        return ypFileMapper.selectOne(wrapper);
    }


    /**
     * 根据文件id删除单个文件
     * @param fileId
     * @return
     */
    public Integer deleteFileById(Long fileId){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("id",fileId);
        wrapper.eq("isfolder",0);
        return ypFileMapper.delete(wrapper);
    }

    /**
     * 根据文件id删除单个文件夹
     * @param folderId
     * @return
     */
    public Integer deleteFolderById(Long folderId){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("id",folderId);
        wrapper.eq("isfolder",1);
        return ypFileMapper.delete(wrapper);
    }

    /**
     * 查找虚拟路径下所有的文件和文件夹
     * @param path 虚拟路径，根目录为"/"
     * @param userId userId
     * @return 包括所有文件和文件夹
     */
    public List<YPFile> getFilesByPath(String path, Long userId){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("virtual_path",path);
        wrapper.eq("user_id",userId);
        return ypFileMapper.selectList(wrapper);
    }

    /**
     * 修改目录下所有的文件路路径，参数均对应于virtual_path字段
     * @param oldPath 旧的路径
     * @param newPath 新的路径，包括当前目录的名称
     */
    public void updateFolderChildrenPath(String oldPath, String newPath, Long userId){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.likeRight("virtual_path", oldPath);
        List<YPFile> list = ypFileMapper.selectList(wrapper);


        for (YPFile file: list) {
            String suffix = file.getVirtualPath()
                    .substring(file.getVirtualPath().indexOf(oldPath)+oldPath.length());
            file.setVirtualPath(newPath+suffix);
            ypFileMapper.updateById(file);
        }

    }

    /**
     * 更新文件夹大小
     * @param folder
     * @param userId
     */
    public void updateFolderSize(YPFile folder, Long userId){
        ypFileMapper.updateById(folder);

    }


    /**
     * 查找同级用户目录下的所有文件
     * @param virtualPath
     * @param userId
     * @return
     */
    public List<YPFile> getFileListBySameLevelVirtualPath(String virtualPath, Long userId){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("virtual_path",virtualPath);
        wrapper.eq("user_id",userId);
        wrapper.eq("isfolder",0);
        return ypFileMapper.selectList(wrapper);
    }

    /**
     * 查找同级用户目录下的所有文件夹
     * @param virtualPath
     * @param userId
     * @return
     */
    public List<YPFile> getFolderListBySameLevelVirtualPath(String virtualPath, Long userId){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("virtual_path",virtualPath);
        wrapper.eq("user_id",userId);
        wrapper.eq("isfolder",1);
        return ypFileMapper.selectList(wrapper);
    }

    /**
     * 根据文件id重命名文件: fileName,physicalPath
     * @param file, newName
     * @return 0,1
     */
    public Integer updateFileNameAndPhysicalPathByFileId(YPFile file, String newName) {

        // 物理路径字符串处理
        String physicalPath = file.getPhysicalPath();
        physicalPath = physicalPath.substring(0, physicalPath.indexOf(".") + 1) + newName;
        file.setPhysicalPath(physicalPath);
        file.setFileName(newName);

        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("id",file.getId());
        wrapper.eq("isfolder",0);
        return ypFileMapper.update(file, wrapper);
    }

    /**
     * 根据文件夹id重命名文件夹: folderName
     * @param folder
     * @return
     */
    public Integer updateFolderNameByFolderId(YPFile folder){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("id",folder.getId());
        wrapper.eq("isfolder",1);
        return ypFileMapper.update(folder, wrapper);
    }

    /**
     * 获取一个文件夹下的所有文件
     * @param virtualPath
     * @param userId
     * @return
     */
    public List<YPFile> getFileListByVirtualPath(String virtualPath, Long userId){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.like("virtual_path",virtualPath);
        wrapper.eq("user_id",userId);
        wrapper.eq("isfolder",0);
        return ypFileMapper.selectList(wrapper);
    }

    /**
     * 获取一个文件夹里的所有文件夹
     * @param virtualPath
     * @param userId
     * @return
     */
    public List<YPFile> getFolderListByVirtualPath(String virtualPath, Long userId){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.like("virtual_path",virtualPath);
        wrapper.eq("user_id",userId);
        wrapper.eq("isfolder",1);
        return ypFileMapper.selectList(wrapper);
    }

    /**
     * 根据虚拟路径获取文件夹，判断移动的目的文件夹是否存在
     * @param virtual_path
     * @return
     */
    public YPFile getFolderByPath(String virtual_path){
        String sub = virtual_path.substring(0, virtual_path.length() - 1);
        System.out.println("sub:"+sub);
        int index = 0;
        while((sub.indexOf("/",index)) >- 1)
        {
            index = sub.indexOf("/",index) + 1;
        }

        String name = sub.substring(index,sub.length());
        String path = sub.substring(0, index);

        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("virtual_path",path);
        wrapper.eq("isfolder",1);
        wrapper.eq("file_name",name);
        return ypFileMapper.selectOne(wrapper);
    }

    /**
     * 修改文件虚拟路径
     * @param ypFile
     * @param newVirtualPath
     * @param oldVirtualPath
     * @return
     */
    // todo 废弃
    public Integer updateFileVirtualPathByFileId(YPFile ypFile, String newVirtualPath, String oldVirtualPath){
        String virtualPath = ypFile.getVirtualPath();
        String replace = virtualPath.replace(oldVirtualPath, newVirtualPath);
        ypFile.setVirtualPath(replace);

        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("id",ypFile.getId());
        wrapper.eq("isfolder",0);

        return ypFileMapper.update(ypFile, wrapper);
    }

    /**
     * 创建文件夹
     * @param folder
     * @return
     */
    public Integer createFolder(YPFile folder){
        return ypFileMapper.insert(folder);
    }

    /**
     * 移动文件
     * @param ypFile
     * @return
     */
    public Integer updateFileVirtualPath(YPFile ypFile){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("id",ypFile.getId());
        wrapper.eq("isfolder",0);
        return ypFileMapper.update(ypFile, wrapper);
    }

    /**
     * 移动文件夹
     * @param folder
     * @return
     */
    public Integer updateFolderVirtualPath(YPFile folder){
        QueryWrapper<YPFile> wrapper = new QueryWrapper<>();
        wrapper.eq("id",folder.getId());
        wrapper.eq("isfolder",1);
        return ypFileMapper.update(folder, wrapper);
    }

    /**
     * 获取用户已使用空间
     * @param userId
     * @return
     */
    public YPUsersize getUserSize(Long userId){
        return ypUsersizeMapper.selectByUserId(userId.intValue());
    }

    /**
     * 更新用户已使用空间
     * @param newSize
     * @param userId
     * @return
     */
    public Integer updateUserUsedSize(double newSize, Long userId){
        return ypUsersizeMapper.updateUsedSize(newSize, userId);
    }


}
