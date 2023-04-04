package com.example.yunpiyuanpan.pojo.secondLevel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QRDecodeFileInfo {
    private String fileName;
    private double fileSize;//以KB为单位
    private String fileType;
    private String ownerName;
    private long downloadTimes;//下载次数
    private String fileImg;
    private String downloadUrl;
}
