package com.example.yunpiyuanpan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yunpiyuanpan.mapper.YPUserMapper;
import com.example.yunpiyuanpan.pojo.YPUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private YPUserMapper ypUserMapper;

    //登录
    //检查账号密码是否正确
    //允许用户使用邮箱+密码、手机号+密码的形式登录
    public boolean isLoginLegal(String account,String password){
        //创建条件构造器
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("email",account);//根据邮箱
        wrapper.or();
        wrapper.eq("phone",account);//用户名
        YPUser ypUser = ypUserMapper.selectOne(wrapper);
        System.out.println(ypUser);
        if(ypUser==null){
            return false;
        }
        if(password.equals(ypUser.getPassword())){
            return true;
        }
        else{
            return false;
        }

    }
}
