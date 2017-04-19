package com.org.ehome.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import Decoder.BASE64Decoder;

/**
 * Created by liuwei on 2016/7/28.
 */
public class Util {
    /**
     * 保存到本地文件
     *
     * @param context
     * @param buf
     * @param fileName
     */
    public static void SaveToFile(Context context, byte[] buf, String fileName) {
        // TODO Auto-generated method stub
        String filename = fileName;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            //fileOutputStream = new FileOutputStream(filename);
            fileOutputStream.write(buf);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null)
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }

    /**
     * 从本地读取
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String ReadFromFile(Context context, String fileName) {
        // TODO Auto-generated method stub
        String filename = fileName;
        FileInputStream fileinputStream = null;
        String line;
        StringBuffer stringBuffer = null;
        try {
            fileinputStream = context.openFileInput(filename);
            //fileOutputStream = new FileOutputStream(filename);

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(fileinputStream, "UTF-8"));
            stringBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\r\n");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fileinputStream != null)
                try {

                    fileinputStream.close();
                    return stringBuffer.toString();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
            return null;
        }
    }

    public static void setConfig(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static String getConfig(Context context, String key, String defValue) {
        SharedPreferences preferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return preferences.getString(key, defValue);
    }

    //base64字符串转化成图片
    public static Bitmap GenerateImage(String imgStr, int dstWidth, int dsyHeight) {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {//调整异常数据
                    b[i] += 256;
                }
            }
            //生成jpeg图片
            return Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(b, 0, b.length), dstWidth, dsyHeight, true);
        } catch (Exception e) {
        }
        return null;
    }


}
