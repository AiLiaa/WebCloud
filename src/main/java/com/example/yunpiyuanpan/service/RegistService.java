package com.example.yunpiyuanpan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.yunpiyuanpan.mapper.YPEmailregistMapper;
import com.example.yunpiyuanpan.mapper.YPUserMapper;
import com.example.yunpiyuanpan.pojo.YPEmailRegist;
import com.example.yunpiyuanpan.pojo.YPUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RegistService {

    //引入Spring mail依赖之后，会自动装配到IOC容器中
    @Autowired(required = false)
    private JavaMailSender sender;

    @Autowired
    private YPEmailregistMapper ypEmailregistMapper;

    @Autowired
    private YPUserMapper ypUserMapper;

    //判断用户注册使用的邮箱格式是否正确，然后判断是否已经存在
    public boolean isEmailLegal(String email){//测试成功
        //邮箱格式正则表达式
        String format = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        if(!email.matches(format)){//不符合规范
            System.out.println("邮箱格式不符合规范");
            return false;
        }
        System.out.println("邮箱格式符合规范");
        //执行到这说明邮箱格式正确
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("email",email);
        if(ypUserMapper.selectOne(wrapper)!=null){
            return false;
        }
        return true;
    }

    //判断用户注册使用的用户名、手机号是否已经存在
    public List<String> isRegistInfoLegal(String username,String phone){//测试通过
        List<String> list = new ArrayList<>();
        //用户名
        QueryWrapper usernameWrapper = new QueryWrapper();
        usernameWrapper.eq("username",username);
        if(ypUserMapper.selectOne(usernameWrapper)!=null){
            System.out.println("此用户名已存在");
            list.add("此用户名已存在");
        }
        //手机号
        QueryWrapper phoneWrapper = new QueryWrapper();
        phoneWrapper.eq("phone",phone);
        if(ypUserMapper.selectOne(phoneWrapper)!=null){
            System.out.println("此手机号已存在");
            list.add("此手机号已存在");
        }
        return list;
    }

    //在这个方法里，应该给目标邮件地址发送邮件，将邮件和code存入数据库
    public boolean sendCodeToEmailAddress(String email){//测试通过

        String code = achieveCode();//生成验证码
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("云批原盘的注册邮件");//发送邮件的标题
        message.setText("您的验证码为：" + code + ",验证码仅在5分钟内有效，嘿嘿！");
        message.setTo(email);//收件人
        message.setFrom("435851735@qq.com");//寄件人
        sender.send(message);
        //如果数据库中没有此邮箱的验证码信息，直接插入数据
        if(ypEmailregistMapper.selectByEmail(email) == null){
            ypEmailregistMapper.insertOne(email,code);//将数据存入
        }else{
            ypEmailregistMapper.deleteOne(email);//先删
            ypEmailregistMapper.insertOne(email,code);//再加
        }
        return true;

    }

    //在这个方法里，先验证code是否过期，再验证email和code是否能匹配的上
    public boolean isCodeLegal(String email,String code){//测试通过

        YPEmailRegist ypEmailRegist = new YPEmailRegist();
        ypEmailRegist = ypEmailregistMapper.selectByEmail(email);
        //如果code过期了，返回false
        try{
            Date codeDate = null;
            codeDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(ypEmailRegist.getCreateTime());
//            System.out.println(codeDate.getTime());
            Date nowDate = new Date();
//            System.out.println(nowDate.getTime());
            long result = nowDate.getTime() - codeDate.getTime();
//            System.out.println(result);
            long oneMinute = 60000;
//            System.out.println(result/oneMinute);
            if(result/oneMinute > 5 ){//已经过了5分钟
                System.out.println("验证码已过期");
                return false;
            }
            System.out.println("验证码未过期");
        }catch (ParseException e){
            e.printStackTrace();
        }
        //执行到这说明还未过期，可以进行比对
        if(code.equals(ypEmailRegist.getCode())){//如果相同
            System.out.println("验证码正确");
            return true;
        }else{
            System.out.println("验证码错误");
            return false;
        }

    }

    //注册成功之后，应当删除注册表中信息，创建用户表中信息
    public void registSuccess(String email,String username,String password,String phone){//测试成功
        ypEmailregistMapper.deleteOne(email);
        YPUser ypUser = new YPUser();
        ypUser.setUsername(username);
        ypUser.setPassword(password);
        ypUser.setEmail(email);
        ypUser.setPhone(phone);
        ypUserMapper.insert(ypUser);
    }

    // 随机生成6位验证码的函数
    private String achieveCode() {
        String[] beforeShuffle= new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F",
                "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a",
                "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z" };
        List list = Arrays.asList(beforeShuffle);
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = afterShuffle.substring(3, 9);
        System.out.print(result);
        return result;
    }

}
