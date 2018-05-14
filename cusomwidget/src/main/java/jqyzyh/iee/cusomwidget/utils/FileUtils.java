package jqyzyh.iee.cusomwidget.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author yuhang
 */
public class FileUtils {
    static String LOG_TAG = "FileUtils";

    public static File getDir(Context context, String dir){
        File[] files = ContextCompat.getExternalFilesDirs(context, dir);
        if (files.length == 0) {
            return null;
        }
        File file = files[0];
        if(!file.exists()){
            file.mkdir();
        }
        return file;
    }

    public static File getCacheDir(Context context, String dir){
        File file = new File(context.getCacheDir(), dir);
        if(file.exists()){
            if(file.isDirectory()){
               return file;
            }
            file.delete();
        }
        file.mkdir();
        return file;
    }

    public static File getPubulicDir(Context context, String dir){
        File[] files = ContextCompat.getExternalFilesDirs(context, dir);
        try{
            File ret = files[0];
            if (!ret.exists()) {
                ret.mkdir();
            }
            return ret;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static File moveFile(File src, String dirPath, String fileName){
        if(src == null){
            LogUtils.e(LOG_TAG, "moveFile 源文件为null");
            return null;
        }

        if(!src.exists()){
            LogUtils.e(LOG_TAG, "moveFile 源文件不存在 path:" + src.getPath());
            return null;
        }

        File dir = new File(dirPath);
        if(!dir.exists()){
            LogUtils.e(LOG_TAG, "moveFile 目标文件夹不存在 path:" + dirPath);
            return null;
        }

        File ret = new File(dir, fileName);
        if(ret.exists()){
            ret.delete();
        }

        FileInputStream is = null;
        FileOutputStream os = null;

        try {
            ret.createNewFile();

            is = new FileInputStream(src);
            os = new FileOutputStream(ret);

            byte[] buffer = new byte[10 * 1024];
            int len;
            while((len = is.read(buffer)) != -1){
                os.write(buffer, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        src.delete();

        return ret;
    }

    public static File saveString(File dir, String name, String text){
        if (dir == null) {
            return null;
        }

        File file = new File(dir, name);
        if (!file.exists()) {
            file.delete();
        }
        FileOutputStream os = null;
        try {
            file.createNewFile();
            os = new FileOutputStream(file);

            try {
                os.write(text.getBytes("utf-8"));
                os.flush();
                os.close();
                os = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static long getFileSize(File file){
        if (file == null || !file.exists()) {
            return 0;
        }
        if (file.isDirectory()) {
            File[] paths = file.listFiles();
            int length = 0;
            if (paths != null) {
                for (File child : paths) {
                    length += getFileSize(child);
                }
            }
            return length;
        }else{
            return file.length();
        }
    }

    public static void deleteFile(File file){
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] paths = file.listFiles();
            if (paths != null) {
                for (File child : paths) {
                    deleteFile(child);
                }
            }
            file.delete();
        }else{
            file.delete();
        }
    }

    /**
     * 获取文件里的字符串
     * @return
     */
    public static String stringWithFile(File file){
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            return stringWithIO(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String stringWithIO(InputStream is){
        if (is == null) {
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
