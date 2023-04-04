package com.example.yunpiyuanpan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yunpiyuanpan.pojo.YPUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface YPUserMapper extends BaseMapper<YPUser> {

    @Select("SELECT id FROM tb_user GROUP BY id")
    List<Long> selectAllUserId();
}
