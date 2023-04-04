package com.example.yunpiyuanpan.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.yunpiyuanpan.util.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class ATestController {

    @RequestMapping("/return")
    public ResponseEntity<Result> test(@RequestParam(value = "bad",required = false,defaultValue = "false") boolean bad){

        Result result = new Result(200,"ok");
        if(bad){
            result.setStatus(400);
            result.setMessage("Bad request");
            //ResponseEntity是响应实体泛型，通过它可以设置http相应的状态值
            return new ResponseEntity<Result>(result, HttpStatus.BAD_REQUEST);
        }
        //将返回值结果进行封装
        result.putData("word","testSuccess");
        return ResponseEntity.ok(result);

    }

    @GetMapping("/satokenLogin")
    public String satokenLogin(){
        System.out.println("登录的token为" + StpUtil.getTokenValue());
        return StpUtil.isLogin()+"";
    }

}
