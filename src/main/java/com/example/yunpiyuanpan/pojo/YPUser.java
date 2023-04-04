package com.example.yunpiyuanpan.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_user")
public class YPUser {
    @TableId
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
}
