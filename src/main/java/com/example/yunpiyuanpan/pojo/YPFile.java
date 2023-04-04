package com.example.yunpiyuanpan.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.yunpiyuanpan.config.LongJsonDeserializer;
import com.example.yunpiyuanpan.config.LongJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_file")
public class YPFile {
    @TableId
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;
    private String fileName;
    private double fileSize;//以KB为单位
    private String fileType;
    private String fileSuffix;//文件后缀
    private String physicalPath;//文件存放的真实路径（包括真实文件名）
    private String virtualPath;//用户设置的虚拟路径
    private long userId;
    private String uploadTime;//上传时间
    private long downloadTimes;//下载次数
    private String fileImg;
    private int isfolder;//是否为文件夹 0表示文件，1表示文件夹
}
