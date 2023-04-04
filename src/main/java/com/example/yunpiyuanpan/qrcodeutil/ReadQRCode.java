package com.example.yunpiyuanpan.qrcodeutil;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReadQRCode {

    //仅用于测试
    public static void main(String[] args) throws IOException {
        File file = new File("F:/qrcodejwithbg.png");
        System.out.println(decode(file));
    }


    public static String decode2(File file) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(file);
        BufferedImage image;
        try {
            if (null == inputStream) {
                return "";
            }
            image = ImageIO.read(inputStream);
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Map<DecodeHintType,Object> hints = new LinkedHashMap<DecodeHintType,Object>();
            // 解码设置编码方式为：utf-8，
            hints.put(DecodeHintType.CHARACTER_SET,"utf-8");
            //优化精度
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            //复杂模式，开启PURE_BARCODE模式
            //hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            Result result = new MultiFormatReader().decode(bitmap, hints);
            String txt = result.getText();
            return txt;
        } catch (Exception e) {
            return "";
        }
    }

    public static String decode(File file){
        BufferedImage image;
        String resultStr =null;
        try{
            image = ImageIO.read(file);
            if (image == null) {
                return null;
            }
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(
                    image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();
            com.google.zxing.Result result = reader.decode(bitmap);
            resultStr = result.getText();
        }catch (Exception e){
            return resultStr;
        }
        return resultStr;
    }


    public static String readQRCode(File file) throws IOException {

        try {
            //MultiFormatReader 多格式读取
            MultiFormatReader formatReader = new MultiFormatReader();
            //读取图片buffer中
            BufferedImage bufferedImage = ImageIO.read(file);
            /*
             * BinaryBitmap			二进制位图
             * HybridBinarizer		混合二值化器
             * BufferedImageLuminanceSource   图像缓存区 亮度 资源
             * */
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
            //定义二维码参数
            HashMap hashMap = new HashMap();
            hashMap.put(EncodeHintType.CHARACTER_SET, "utf-8");//编码方式
            hashMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);//优化精度
            hashMap.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);//复杂模式，开启PURE_BARCODE模式
            Result result = formatReader.decode(binaryBitmap,hashMap);
            System.out.println("解析结果："+result.toString());
            System.out.println("二维码格式类型："+result.getBarcodeFormat());//BarcodeFormat   条形码格式
            System.out.println("二维码文本内容："+result.getText());
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
