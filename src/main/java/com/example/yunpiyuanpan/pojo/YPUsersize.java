package com.example.yunpiyuanpan.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_usersize")
public class YPUsersize {
    private Long userId;
    private int user_level;//用户等级，1为普通用户（内存为4GB），2为2级会员（16GB），3为三级会员（32GB），我先随便设置了一下
    private double userMaxsize;//以KB为单位
    private double userUsedsize;//以KB为单位
}
