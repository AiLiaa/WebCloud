package com.example.yunpiyuanpan.exception;

import lombok.Getter;

@Getter
public enum FileStatus {

    OTHER(-100, "其他错误"),
    FILE_NOT_FOUND(-101, "文件不存在"),
    FILE_GOT_LOST(-102, "文件丢失"),
    FILE_OPEN_FAIL(-103, "文件打开失败");


    private static final FileStatus[] VALUES = values();
    private final int code;
    private final String message;

    FileStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
