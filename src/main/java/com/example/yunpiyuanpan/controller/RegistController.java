package com.example.yunpiyuanpan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yunpiyuanpan.mapper.YPEmailregistMapper;
import com.example.yunpiyuanpan.mapper.YPUserMapper;
import com.example.yunpiyuanpan.pojo.YPUser;
import com.example.yunpiyuanpan.service.RegistService;
import com.example.yunpiyuanpan.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/regist")
@Api("用户注册接口")
public class RegistController {

    @Autowired
    private RegistService registService;

    @Autowired
    private YPUserMapper ypUserMapper;

    @GetMapping("/sendEmail")
    @ApiOperation(value = "发送邮件验证码", httpMethod = "GET")
    public ResponseEntity<Result> sendEmail(@RequestParam("email") String email){//已完成
        Result result = new Result();
        result.setStatus(200);
        result.setMessage("ok");
        //判断邮箱是否正确且没被注册过
        boolean isEmailLegal = registService.isEmailLegal(email);
        if(!isEmailLegal){//若邮箱格式不正确或被注册过
            result.setMessage("邮箱格式错误或已被注册");
            return ResponseEntity.ok(result);
        }
        //邮箱正确，可以发送邮件，并将创建注册表信息
        registService.sendCodeToEmailAddress(email);
        result.setMessage("发送成功！");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/byemail")
    @ApiOperation(value = "邮箱注册", httpMethod = "POST")
    public ResponseEntity<Result> registByEmail(
            @RequestParam("email") String email,
            @RequestParam("code") String code,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("phone") String phone){//已完成
        Result result = new Result();
        result.setStatus(200);
        result.setMessage("ok");
        //要求前端判断手机号输入是否正确
        //判断用户名和手机号是否被注册过
        List<String> stringList = registService.isRegistInfoLegal(username, phone);
        if(!stringList.isEmpty()){//不为空代表被注册过咯
            int i = 1;
            for (String s:
                 stringList) {
                result.putData("错误" + i + ": ",s);
                i++;
            }
            result.setMessage("手机号已被注册");
            return ResponseEntity.ok(result);
        }
        //判断验证码是否合法
        boolean isCodeLegal = registService.isCodeLegal(email, code);
        if(!isCodeLegal){//验证码不合法
            result.setMessage("验证码错误或已失效");
            return ResponseEntity.ok(result);
        }
        //验证码合法，删除注册表中内容，创建用户表内容
        registService.registSuccess(email, username, password, phone);
        //创建成功，应当返回用户表内容
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("email",email);
        YPUser ypUser = ypUserMapper.selectOne(wrapper);
        result.putData("user",ypUser);
        result.setMessage("注册成功！");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/byPhone")
    @ApiOperation(value = "手机号注册", httpMethod = "POST")
    public ResponseEntity<Result> registByPhone(){//待完成
        Result result = new Result();
        result.setStatus(200);
        result.setMessage("ok");
        return ResponseEntity.ok(result);
    }

}
