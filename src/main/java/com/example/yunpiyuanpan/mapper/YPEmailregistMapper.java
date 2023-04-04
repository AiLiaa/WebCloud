package com.example.yunpiyuanpan.mapper;

import com.example.yunpiyuanpan.pojo.YPEmailRegist;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface YPEmailregistMapper {

    @Select("select * from tb_emailregist where email = #{email}")
    public YPEmailRegist selectByEmail(String email);

    @Insert("insert into tb_emailregist(email,code) values (#{email},#{code})")
    public int insertOne(String email,String code);

    @Delete("delete from tb_emailregist where email = #{email}")
    public int deleteOne(String email);

}
