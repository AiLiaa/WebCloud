package com.example.yunpiyuanpan.exception;

public class FileException extends RuntimeException{

    FileStatus fileStatus;

    public FileException(FileStatus fileStatus){
        super();
        this.fileStatus=fileStatus;
    }

    public FileException(String msg){
        super(msg);
        this.fileStatus=FileStatus.OTHER;
    }

    public FileException(FileStatus fileStatus, String msg) {
        super(msg);
        this.fileStatus=fileStatus;
    }

    public FileStatus getFileStatus() {
        return fileStatus;
    }
}
