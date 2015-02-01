package edu.xiyou.andrew.Egg.utils;

import java.io.*;
/**
 * Created by andrew on 15-2-1.
 */
public class FileUtils {
    /**
     * 递归删除文件/目录
     * @param dir 删除的目录/文件
     */
    public static void deleteDir(File dir){
        File[] files = dir.listFiles();
        for (File file : files){
            if (file.isFile()){
                file.delete();
            }else{
                deleteDir(file);
            }
        }
        dir.delete();
    }

    /**
     * 拷贝文件
     * @param oldFile
     * @param newFile
     * @throws IOException
     */
    public static void copy(File oldFile, File newFile) throws IOException {
        if (!newFile.getParentFile().exists()){
            newFile.getParentFile().mkdirs();
        }

        FileInputStream fis = new FileInputStream(oldFile);
        FileOutputStream fos = new FileOutputStream(newFile);

        byte[] buf = new byte[4096];
        int read;
        while ((read = fis.read(buf)) != -1){
            fos.write(buf, 0, read);
        }
        fis.close();
        fos.close();
    }

    /**
     * 将指定内容写入/追加文件
     * @param file 文件
     * @param context 内容
     * @param append 是否追加
     * @throws IOException
     */
    public static void writeFile(File file, byte[] context, boolean append) throws IOException {
        FileOutputStream fos = null;

        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }else if (append){
            fos = new FileOutputStream(file, append);
        }
        if (fos == null) {
            fos = new FileOutputStream(file);
        }
        fos.write(context);
        fos.close();
    }

    /**
     * 将指定内容写入文件
     * @param file 文件
     * @param context 内容
     * @throws IOException
     */
    public static void writeFile(File file, byte[] context) throws IOException {
        writeFile(file, context, false);
    }

    /**
     * 读取文件内容
     * @param file 文件
     * @return 存入内容的byte数组
     * @throws IOException
     */
    public static byte[] readFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buf = new byte[4096];
        int read;

        while ((read = fis.read(buf)) != -1){
            baos.write(buf, 0, read);
        }

        fis.close();
        baos.close();

        return baos.toByteArray();
    }

    /**
     * 获取当前工作路径
     * @return 当前工作路径
     */
    public static String getPath(){
        return System.getProperty("user.dir");
    }

}
