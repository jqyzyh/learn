package jqyzyh.iee.commen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author jqyzyh
 * 文件操作工具类
 */
public class FileUtils {
    static final String LOG_TAG = "String";

    public static String copyFile(File srcFile, File desFile){
        if(srcFile == null){
            LogUtils.e(LOG_TAG, "copyFile 源文件为null");
            return null;
        }

        if(!srcFile.exists()){
            LogUtils.e(LOG_TAG, "copyFile 源文件不存在 path:" + srcFile.getPath());
            return null;
        }

        if(desFile == null){
            return null;
        }

        FileInputStream is = null;
        FileOutputStream os = null;

        try{
            if(desFile.exists()){
                desFile.delete();
            }
            desFile.createNewFile();

            is = new FileInputStream(srcFile);
            os = new FileOutputStream(desFile);

            byte[] buffer = new byte[10 * 1024];
            int len;
            while((len = is.read(buffer)) != -1){
                os.write(buffer, 0, len);
                os.flush();
            }

        }catch (Exception e){
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

        return null;
    }

    public String moveFile(File srcFile, File desFile){
        String ret = copyFile(srcFile, desFile);
        if(ret != null){
            try{
                srcFile.delete();
            }catch (Exception e){
                LogUtils.e(LOG_TAG, e);
            }
        }
        return ret;
    }

    public String copyFile(String srcPath, String desPath){
        if(srcPath == null || desPath == null){
            return null;
        }
        return copyFile(new File(srcPath), new File(desPath));
    }

    public String copyFileToDir(File srcFile, File desDir){
        if(srcFile == null){
            LogUtils.e(LOG_TAG, "copyFileToDir 源文件为null");
            return null;
        }

        if(desDir == null){
            LogUtils.e(LOG_TAG, "copyFileToDir 目标文件夹为null");
            return null;
        }

        if(!desDir.exists()){
            LogUtils.e(LOG_TAG, "copyFileToDir 目标文件夹不存在");
            return null;
        }

        return copyFile(srcFile, new File(desDir, srcFile.getName()));
    }


    public String moveFile(String srcPath, String desPath){
        if(srcPath == null || desPath == null){
            return null;
        }
        return moveFile(new File(srcPath), new File(desPath));
    }

    public String moveFileToDir(File srcFile, File desDir){
        if(srcFile == null){
            LogUtils.e(LOG_TAG, "copyFileToDir 源文件为null");
            return null;
        }

        if(desDir == null){
            LogUtils.e(LOG_TAG, "copyFileToDir 目标文件夹为null");
            return null;
        }

        if(!desDir.exists()){
            LogUtils.e(LOG_TAG, "copyFileToDir 目标文件夹不存在");
            return null;
        }

        return moveFile(srcFile, new File(desDir, srcFile.getName()));
    }
}

