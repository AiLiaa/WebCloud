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
@TableName("tb_recyclebin")
public class YPRecyclebin {
    @TableId
    @JsonSerialize(using = LongJsonSerializer.class)
    @JsonDeserialize(using = LongJsonDeserializer.class)
    private Long id;
    private String fileName;
    private double fileSize;
    private String fileType;
    private String fileSuffix;//文件后缀
    private String physicalPath;//文件存放的真实路径（包括真实文件名）
    private String virtualPath;//用户设置的虚拟路径
    private long userId;
    private String uploadTime;//上传时间
    private long downloadTimes;//下载次数
    private String fileImg;
    private int isfolder;//是否为文件夹 0表示文件，1表示文件夹
    private String deleteTime;//删除时间
    private String binPath;//回收站虚拟路径

    public YPRecyclebin(Long id, String fileName, double fileSize, String fileType, String fileSuffix, String physicalPath, String virtualPath, long userId, String uploadTime, long downloadTimes, String fileImg, int isfolder) {
        this.id = id;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.fileSuffix = fileSuffix;
        this.physicalPath = physicalPath;
        this.virtualPath = virtualPath;
        this.userId = userId;
        this.uploadTime = uploadTime;
        this.downloadTimes = downloadTimes;
        this.fileImg = fileImg;
        this.isfolder = isfolder;
    }

}
