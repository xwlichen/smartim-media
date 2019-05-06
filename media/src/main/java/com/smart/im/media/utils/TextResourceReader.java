package com.smart.im.media.utils;

import android.content.Context;
import android.content.res.Resources;

import com.blankj.utilcode.util.LogUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @date : 2019/4/12 下午3:32
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class TextResourceReader {
    public static String readTxtFileFromResource(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String nextLine;
        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return body.toString();
    }

    public static String readTxtFileFromAsset(Context context, String filename) {
        return readTextFileFromAsset(context.getResources(), filename);
    }

    public static String readTextFileFromAsset(Resources res, String filename) {
        String result = null;
        try {
            InputStream in = res.getAssets().open(filename);
            int ch = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
            byte[] buff = baos.toByteArray();
            baos.close();
            in.close();
            result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
            LogUtils.d("read result is " + result);
        } catch (IOException e) {
            LogUtils.d("exception");
            LogUtils.e(e.getMessage(), e);
            e.printStackTrace();
        }
        return result;
    }
}
