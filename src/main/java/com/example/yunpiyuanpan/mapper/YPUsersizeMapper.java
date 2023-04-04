package com.example.yunpiyuanpan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.yunpiyuanpan.pojo.YPUsersize;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

//UsersizeMapper类不含主键，使用BaseMapper中的很多方法会报错（无主键）
//因此直接采用mybatis注解/配置开发模式

@Mapper
public interface YPUsersizeMapper{

    @Select("select * from tb_usersize where user_id = ${id}")
    public YPUsersize selectByUserId(int id);

    /**
     * 更新已使用空间
     */
    @Update("update tb_usersize set user_usedsize = ${newSize} where user_id = ${userId}")
    public Integer updateUsedSize(double newSize, Long userId);


}
