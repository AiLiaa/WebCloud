package com.example.yunpiyuanpan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yunpiyuanpan.mapper.YPFileMapper;
import com.example.yunpiyuanpan.mapper.YPRecyclebinMapper;
import com.example.yunpiyuanpan.pojo.YPFile;
import com.example.yunpiyuanpan.pojo.YPRecyclebin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecyclebinService {
    @Autowired
    private YPRecyclebinMapper ypRecyclebinMapper;

    @Autowired
    private YPFileMapper ypFileMapper;

    /**
     * 将删除的文件先放入回收站
     * @param recycleBinFile
     * @return
     */
    public Integer addRecycleBinFile(YPRecyclebin recycleBinFile){
        return ypRecyclebinMapper.insert(recycleBinFile);
    }

    /**
     * 根据id获取单个文件
     * @param fileId
     * @return YPFile
     */
    public YPRecyclebin getBinFileById(Long fileId){
        QueryWrapper<YPRecyclebin> wrapper = new QueryWrapper<>();
        wrapper.eq("id",fileId);
        wrapper.eq("isfolder",0);
        return ypRecyclebinMapper.selectOne(wrapper);
    }

    /**
     * 根据文件id删除单个文件
     * @param fileId
     * @return
     */
    public Integer deleteBinFileById(Long fileId){
        QueryWrapper<YPRecyclebin> wrapper = new QueryWrapper<>();
        wrapper.eq("id",fileId);
        wrapper.eq("isfolder",0);
        return ypRecyclebinMapper.delete(wrapper);
    }

    /**
     * 根据id获取单个文件夹
     * @param folderId
     * @return
     */
    public YPRecyclebin getBinFolderById(Long folderId){
        QueryWrapper<YPRecyclebin> wrapper = new QueryWrapper<>();
        wrapper.eq("id",folderId);
        wrapper.eq("isfolder",1);
        return ypRecyclebinMapper.selectOne(wrapper);
    }
    /**
     * 获取一个文件夹下的所有文件
     * @param virtualPath
     * @param userId
     * @return
     */
    public List<YPRecyclebin> getBinFileListByVirtualPath(String virtualPath, Long userId){
        QueryWrapper<YPRecyclebin> wrapper = new QueryWrapper<>();
        wrapper.like("virtual_path",virtualPath);
        wrapper.eq("user_id",userId);
        wrapper.eq("isfolder",0);
        return ypRecyclebinMapper.selectList(wrapper);
    }

    /**
     * 获取一个文件夹里的所有文件夹
     * @param virtualPath
     * @param userId
     * @return
     */
    public List<YPRecyclebin> getBinFolderListByVirtualPath(String virtualPath, Long userId){
        QueryWrapper<YPRecyclebin> wrapper = new QueryWrapper<>();
        wrapper.like("virtual_path",virtualPath);
        wrapper.eq("user_id",userId);
        wrapper.eq("isfolder",1);
        return ypRecyclebinMapper.selectList(wrapper);
    }

    /**
     * 根据文件id删除单个文件夹
     * @param folderId
     * @return
     */
    public Integer deleteBinFolderById(Long folderId){
        QueryWrapper<YPRecyclebin> wrapper = new QueryWrapper<>();
        wrapper.eq("id",folderId);
        wrapper.eq("isfolder",1);
        return ypRecyclebinMapper.delete(wrapper);
    }

    /**
     * 查找同级用户目录下的所有文件
     * @param virtualPath
     * @param userId
     * @return
     */
    public List<YPRecyclebin> getBinFileListBySameLevelVirtualPath(String virtualPath, Long userId){
        QueryWrapper<YPRecyclebin> wrapper = new QueryWrapper<>();
        wrapper.eq("virtual_path",virtualPath);
        wrapper.eq("user_id",userId);
        wrapper.eq("isfolder",0);
        return ypRecyclebinMapper.selectList(wrapper);
    }

    /**
     * 查找同级用户目录下的所有文件夹
     * @param virtualPath
     * @param userId
     * @return
     */
    public List<YPRecyclebin> getBinFolderListBySameLevelVirtualPath(String virtualPath, Long userId){
        QueryWrapper<YPRecyclebin> wrapper = new QueryWrapper<>();
        wrapper.eq("virtual_path",virtualPath);
        wrapper.eq("user_id",userId);
        wrapper.eq("isfolder",1);
        return ypRecyclebinMapper.selectList(wrapper);
    }

    /**
     * 恢复到file数据
     * @param file
     * @return
     */
    public Integer recoverFile(YPFile file){
        return ypFileMapper.insert(file);
    }

    /**
     * 查找虚拟路径下所有的文件和文件夹
     * @param path 虚拟路径，根目录为"/"
     * @param userId userId
     * @return 包括所有文件和文件夹
     */
    public List<YPRecyclebin> getBinFilesByPath(String path, Long userId){
        QueryWrapper<YPRecyclebin> wrapper = new QueryWrapper<>();
        wrapper.eq("virtual_path",path);
        wrapper.eq("user_id",userId);
        return ypRecyclebinMapper.selectList(wrapper);
    }

    public List<YPRecyclebin> getBinFilesByBinPath(String path, Long userId){
        QueryWrapper<YPRecyclebin> wrapper = new QueryWrapper<>();
        wrapper.eq("bin_path",path);
        wrapper.eq("user_id",userId);
        return ypRecyclebinMapper.selectList(wrapper);
    }
}
