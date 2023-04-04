package com.example.yunpiyuanpan.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.yunpiyuanpan.mapper.YPFileMapper;
import com.example.yunpiyuanpan.mapper.YPUserMapper;
import com.example.yunpiyuanpan.pojo.YPFile;
import com.example.yunpiyuanpan.pojo.YPUser;
import com.example.yunpiyuanpan.pojo.secondLevel.QRDecodeFileInfo;
import com.example.yunpiyuanpan.qrcodeutil.CreateQRCodeWithBG;
import com.example.yunpiyuanpan.qrcodeutil.ReadQRCode;
import com.example.yunpiyuanpan.service.FileService;
import com.example.yunpiyuanpan.util.R;
import com.example.yunpiyuanpan.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;
//RestController
@RestController
@Api("文件分享接口")
@RequestMapping("/fileShare")
public class FileShareController {

    @Value("${storage-lib}")
    private String path;

    @Value("${qrcodebgPath}")
    private String qrcodebgPath;

    @Value("${qrcodewithbgPath}")
    private String qrcodewithbgPath;

    @Value("${ipConfig}")
    private String ipConfig;

    @Autowired
    private FileService fileService;
    
    @Autowired
    private YPFileMapper ypFileMapper;

    @Autowired
    private YPUserMapper ypUserMapper;

    //生成二维码，使用其他平台识别二维码时，可以直接下载
//    @ApiOperation(value = "获取二维码", httpMethod = "GET")
//    @GetMapping(value = "/createQRCode",produces = MediaType.IMAGE_PNG_VALUE)
//    public BufferedImage createQRCode(@RequestParam("fileId")Long fileId) throws IOException {//测试成功
//
//        //是否已经登录
//        if(!StpUtil.isLogin()){
//            //return R.fail().message("尚未登录");
//            System.out.println("尚未登录");
//        }
//
//        //根据userId和fileId生成access_token
//        Long userId = StpUtil.getLoginIdAsLong();
//        if(!fileService.checkOwnership(userId, fileId)){
//            System.out.println("您没有分享权限，请先转存");
//            //return R.fail().message("您没有分享权限，请先转存");
//        }
//        String token= TokenUtil.sign(userId, fileId);
//
//        //将access_token信息存入二维码
//        // TODO: 2022/11/24 记得在服务器上放置要作为背景的图片并配置好图片路径
//        File bgImgFile = new File(qrcodebgPath);//背景图片
//        File qrCodeFile = new File(qrcodewithbgPath);//生成图片位置
//        //todo: 需要修改路径配置，适合本地调试和远端调试
//        String url = "http://"+ipConfig+":8989/publicDownload?access_token=" + token;
//        String note = "";
//        String tui = "";
//        CreateQRCodeWithBG.CreatQRCode(qrCodeFile,bgImgFile, 200, 200,    url,      note,    tui, 38, 408, 508, 0, 0, 410, 210);
//        if(!qrCodeFile.exists()){//如果要生成的分享文件不存在
//            System.out.println("二维码生成失败");
//            //return R.fail().message("二维码生成失败");
//        }
//        BufferedImage bufferedImage = ImageIO.read(qrCodeFile);
//
//        //返回二维码
//        System.out.println("创建成功，内容为：" + url);
//        return bufferedImage;

//    }

    //平台扫描二维码，显示文件信息并返回下载路径
    @PostMapping("/readQRCode")
    @ApiOperation(value = "读二维码", httpMethod = "POST")
    public R readQRCode(@RequestParam("file") MultipartFile file) throws IOException {//测试通过

        //判断文件是否为空
        if(file.isEmpty()){
            return R.fail().message("文件为空");
        }

        //将内容保存为QRCode临时文件
        byte[] fileByte = file.getBytes();//将文件转化为字节数组
        String tempFilePath = path + "tmp/";//设置临时文件路径
        String fileName = "QRTemp.png";//设置临时文件名
        File uploadQRCodeDir = new File(tempFilePath);
        if(!uploadQRCodeDir.exists()){//若没有这个文件夹，创建之
            uploadQRCodeDir.mkdirs();
        }
        File qrcodeFile = new File(tempFilePath + fileName);
        file.transferTo(qrcodeFile);
        if (!qrcodeFile.exists()){
            System.out.println("二维码文件为空");
            return R.fail().message("二维码无效");
        }

        //调用扫描工具
        String qrCodeResult = ReadQRCode.decode(qrcodeFile);//读取QRCode内容
        System.out.println("二维码内容：" + qrCodeResult);
        if(qrCodeResult==""||qrCodeResult==null){
            return R.fail().message("二维码无效");
        }
        //todo: 弄明白access_token的工作原理，在此进行提取和解析
        String separatorChar = "=";
        int index = qrCodeResult.lastIndexOf(separatorChar);
        String accessToken = qrCodeResult.substring(index+1);

        //判断access_token是否合法
        System.out.println(TokenUtil.verify(accessToken));
        if(!TokenUtil.verify(accessToken)){
            System.out.println("提取到的token为：" + accessToken);
            return R.fail().message("token不合法或已过期");
        }

        //将提取到的access_token进行解析
        Map<String,Object> map = TokenUtil.parseToken(accessToken);
        Long userId = (Long) map.get("userId");
        Long fileId = (Long) map.get("fileId");
        System.out.println("获取到的分享人id：" + userId);
        System.out.println("获取到的文件的id：" + fileId);

        //获取文件信息
        YPFile ypFile = ypFileMapper.selectById(fileId);
        if(ypFile==null){
            return R.fail().message("该分享文件已经失效");
        }

        //查询用户名
        String ownerName = "";
        YPUser ypUser = ypUserMapper.selectById(ypFile.getUserId());
        ownerName = ypUser.getUsername();

        //将下载路径和文件信息放入二级pojo中
        QRDecodeFileInfo qrDecodeFileInfo = new QRDecodeFileInfo();
        qrDecodeFileInfo.setFileName(ypFile.getFileName());
        qrDecodeFileInfo.setFileSize(ypFile.getFileSize());
        qrDecodeFileInfo.setFileType(ypFile.getFileType());
        qrDecodeFileInfo.setOwnerName(ownerName);
        qrDecodeFileInfo.setDownloadTimes(ypFile.getDownloadTimes());
        qrDecodeFileInfo.setFileImg(ypFile.getFileImg());
        qrDecodeFileInfo.setDownloadUrl(qrCodeResult);


        //先直接返回二维码内容，和文件具体信息
        //return R.ok().data("downloadUrl",qrCodeResult).data("fileInfo",ypFile).message("成功！");
        return R.ok().data("fileInfo",qrDecodeFileInfo).message("成功！");
    }

    //非本平台下载文件(暂时不用)
    @GetMapping("/download/{fileId}")
    @ApiOperation(value = "非本平台下载分享文件（暂时不用）", httpMethod = "GET")
    public String downloadSharedFile(@PathVariable Long fileId,HttpServletResponse response){

        //根据路径中的文件id找到文件
        File file = fileService.getFileByFileId(fileId);

        if(!file.exists()){
            return "文件不存在或分享已失效";
        }

        //当文件存在时，将文件传回
        if(file.exists()){
            response.setContentType("application/force-download");//设置强制下载不打开
            String newName = file.getName();//新文件名
            response.addHeader("Content-Disposition","attachment;fileName=" + newName);//设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fileInputStream = null;
            BufferedInputStream bufferedInputStream = null;
            try{
                fileInputStream = new FileInputStream(file);//创建InputStream
                bufferedInputStream = new BufferedInputStream(fileInputStream);//创建BufferedInputStream
                OutputStream outputStream = response.getOutputStream();
                int i = bufferedInputStream.read(buffer);
                while(i!=-1){
                    outputStream.write(buffer,0,1);
                    i = bufferedInputStream.read(buffer);
                }
                System.out.println("download success");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(bufferedInputStream != null){
                    try {
                        bufferedInputStream.close();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(fileInputStream != null){
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "";
    }

    //生成二维码，使用其他平台识别二维码时，可以直接下载
    @ApiOperation(value = "获取二维码1", httpMethod = "GET")
    @GetMapping(value = "/createQRCode1")
    public byte[] createQRCode1(@RequestParam("fileId")Long fileId) throws IOException {//测试成功

        //是否已经登录
        if(!StpUtil.isLogin()){
            //return R.fail().message("尚未登录");
            System.out.println("尚未登录");
        }

        //根据userId和fileId生成access_token
        Long userId = StpUtil.getLoginIdAsLong();
        if(!fileService.checkOwnership(userId, fileId)){
            System.out.println("您没有分享权限，请先转存");
            //return R.fail().message("您没有分享权限，请先转存");
        }
        String token= TokenUtil.sign(userId, fileId);

        //将access_token信息存入二维码
        // TODO: 2022/11/24 记得在服务器上放置要作为背景的图片并配置好图片路径
        File bgImgFile = new File(qrcodebgPath);//背景图片
        File qrCodeFile = new File(qrcodewithbgPath);//生成图片位置
        //todo: 需要修改路径配置，适合本地调试和远端调试
        String url = "http://"+ipConfig+":8989/publicDownload?access_token=" + token;
        String note = "";
        String tui = "";
        CreateQRCodeWithBG.CreatQRCode(qrCodeFile,bgImgFile, 200, 200,    url,      note,    tui, 38, 408, 508, 0, 0, 410, 210);
        if(!qrCodeFile.exists()){//如果要生成的分享文件不存在
            System.out.println("二维码生成失败");
            //return R.fail().message("二维码生成失败");
        }
        BufferedImage bufferedImage = ImageIO.read(qrCodeFile);

        //返回二维码
        System.out.println("创建成功，内容为：" + url);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ImageIO.write(bufferedImage,"png",byteArrayOutputStream);
        ImageIO.write(bufferedImage,"png",byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byte[] imageInByte = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return imageInByte;

    }


}
