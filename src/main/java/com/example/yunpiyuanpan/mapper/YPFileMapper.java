package com.example.yunpiyuanpan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yunpiyuanpan.pojo.YPFile;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface YPFileMapper extends BaseMapper<YPFile> {

    @Delete("DELETE FROM tb_file WHERE file_name = #{filename}")
    void deleteOne(String filename);

    @Select("SELECT physical_path FROM tb_file WHERE user_id = #{userId}")
    List<String> selectPhysicalPathListByUserId(Long userId);


}


