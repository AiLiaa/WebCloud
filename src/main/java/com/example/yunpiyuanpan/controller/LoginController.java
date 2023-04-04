package com.example.yunpiyuanpan.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yunpiyuanpan.mapper.YPUserMapper;
import com.example.yunpiyuanpan.pojo.YPUser;
import com.example.yunpiyuanpan.service.LoginService;
import com.example.yunpiyuanpan.util.R;
import com.example.yunpiyuanpan.util.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Api("用户登录接口")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private YPUserMapper ypUserMapper;

    @PostMapping("/login")
    @ApiOperation(value = "登录", httpMethod = "POST")
    public R login(@Param("account") String account, @Param("password") String password){//测试成功
        System.out.println(account + password);
        if(loginService.isLoginLegal(account, password)){//用户名密码满足条件
            //登录成功，返回用户表内容
            QueryWrapper wrapper1 = new QueryWrapper();
            wrapper1.eq("email",account);
            YPUser ypUser = ypUserMapper.selectOne(wrapper1);
            if(ypUser!=null){
                StpUtil.login(ypUser.getId());//创建登录凭证
                return R.ok().data("user", ypUser).data("satoken",StpUtil.getTokenValue());
            }else{
                QueryWrapper wrapper2 = new QueryWrapper();
                wrapper2.eq("phone",account);
                ypUser = ypUserMapper.selectOne(wrapper2);
                StpUtil.login(ypUser.getId());//创建登录凭证
                return R.ok().data("user", ypUser).data("satoken",StpUtil.getTokenValue());
            }
        }
        return R.setResult(ResultCodeEnum.FAIL).message("邮箱/手机号 或 密码 不正确");
    }

    @GetMapping("/isLogin")
    @ApiOperation(value = "登录检测", httpMethod = "GET")
    public R isLogin(){
        return R.ok().data("isLogin",StpUtil.isLogin());
    }

}
