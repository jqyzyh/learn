package jqyzyh.iee.commen;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author jqyzyh
 * 媒体相关工具
 */
public class MediaUtils {

    /**
     * 通过{@link #getContentUri}方法获取可以query的Uri，然后条用{@link #queryData(Context, Uri, String, String[])} 获得对应文件路径
     * @see #getContentUri(Uri)
     * @param uri 图片的uri
     * @return uri对应的文件路径
     */
    public static String uriToFilePath(Context context, Uri uri){
        if("file".equals(uri.getScheme())){
            /*如果scheme是"file"说明是文件路径直接输出即可*/
            return uri.toString().replace("file://","");
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            /*如果系统版本大于等于19（Android4.4）*/
            if(isExternalStorageDocument(uri)){
                String docId = DocumentsContract.getDocumentId(uri);
                String[] strs = docId.split(":");
                if("primary".equalsIgnoreCase(strs[0])){
                    return Environment.getExternalStorageDirectory() + "/" + strs[1];
                }

            }else if(isDownloadDocument(uri)){
                return queryData(context, getContentUri(uri), null, null);
            }else if(isMediaDocument(uri)){
                return queryData(context, getContentUri(uri), null, null);
            }
        }

        return queryData(context, uri, null, null);
    }

    /**
     *
     * 系统返回的uri会出现两种情况<br/>
     * <li style="list-style-type:decimal">
     * ep:"content://com.android.providers.media.documents/document/image:123"<br/>
     * 此时为4.4新加入的api，从相册中最近目录中选取后返回的uri，
     * 需要用先通过{@link Uri#getAuthority()}判断类型，
     * 再辅助{@link DocumentsContract}解析，最后获得可用的uri<br/>
     * </li>
     * <li style="list-style-type:decimal">
     * ep:"content://media/external/images/media/123"<br/>
     * 此时直接用{@link ContentResolver#query(Uri, String[], String, String[], String)}
     * 配合{@link MediaStore}查找即可
     * </li>
     * @param uri 原始uri
     * @return 文件实际的uri
     */
    public static Uri getContentUri(Uri uri){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            if(isDownloadDocument(uri)){
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return contentUri;
            }else if(isMediaDocument(uri)){
                String docId = DocumentsContract.getDocumentId(uri);
                String[] strs = docId.split(":");
                Uri contentUri = null;
                if("image".equals(strs[0])){
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }else if("video".equals(strs[0])){
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }else if("audio".equals(strs[0])){
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                contentUri = ContentUris.withAppendedId(contentUri, Long.valueOf(strs[1]));
                return contentUri;
            }
        }
        return uri;
    }

    /**
     * 用uri通过系统的ContentProvider查询文件路径
     * @param uri 已经的uri
     * @return uri对应的文件路径
     */
    public static String queryData(Context context, Uri uri, String selection, String[] selectionArgs){
        Cursor cursor = null;
        try{
            //用系统Resolver查询该uri的"_data"值
            cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, selection, selectionArgs, null);
            if(cursor != null && cursor.moveToFirst()){
                //如果cursor不为空 返回第一个有游标的"_data"值
                return cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            }
        }finally {
            //释放资源
            if(cursor != null){
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 保存图片到本地
     * @param context context
     * @param bitmap 要保存的图片
     * @param dir 保存的文件夹
     * @param fileName 保存的文件名
     * @return filePath 如果保存失败返回null
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static String saveBitmapToLocal(Context context, Bitmap bitmap, File dir, String fileName) {
        if(bitmap == null){
            return null;
        }
        try{
            File file = new File(dir, fileName);
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            FileOutputStream os = null;
            try{
                os = new FileOutputStream(file);
                bitmap.compress(fileName.endsWith(".png") ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 100, os);
            }finally {
                if(os != null){
                    os.close();
                }
            }
            if(context != null){
                /*通知系统刷新系统相册*/
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO){
                    MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 校验图片是否被旋转过，如果被旋转过就把它正过来
     * @param path 图片路径
     */
    public static void checkBitmapRotaDegree(String path){
        int degree = readPictureDegree(path);
        if(degree == 0){
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if(bitmap == null){
            return;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap tempBitmap = null;
        try {
            tempBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
            try {
                tempBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
                return;
            }
        }
        File file = new File(path);
        file.delete();
        FileOutputStream os= null;
        try {
            file.createNewFile();
            os = new FileOutputStream(file);
            tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取图片旋转角度
     * @param path 图片路径
     * @return 旋转角度
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static int readPictureDegree(String path){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ECLAIR){
            return 0;
        }
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation){
                case ExifInterface.ORIENTATION_NORMAL:
                    return 0;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //======================== 4.4版本加入的近期媒体类型 关键字Document===============================
    /**
     *
     * @param uri 校验的uri
     * @return 是否是ExternalStorage类型的uri
     */
    public static boolean isExternalStorageDocument(Uri uri){
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     *
     * @param uri 校验的uri
     * @return 是否是download类型的uri
     */
    public static boolean isDownloadDocument(Uri uri){
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     *
     * @param uri 校验的uri
     * @return 是否是media类型的uri
     */
    public static boolean isMediaDocument(Uri uri){
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
