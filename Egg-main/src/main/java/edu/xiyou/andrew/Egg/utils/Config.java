package edu.xiyou.andrew.Egg.utils;

/**
 * 爬虫的全局配置
 * Created by andrew on 15-1-16.
 */
public interface Config {
    /**
     * 是否备份抓取的文件
     */
    public static boolean isBack = false;

    /**
     * 存储抓取信息的目录
     */
    public static String storePath = "~/EggData/";

    /**
     * 备份抓取信息的目录
     */
    public static String backPath = "~/EggDataBack/";

    /**
     * 相同链接抓取的最少间隔时间 , -1 代表间隔时间无限大
     */
    public static long interval = -1;

    /**
     * 抓取的网页的最大数量
     */
    public static long maxSize = 1000 * 1000;

    /**
     * 是否支持断点抓取
     */
    public static boolean supportResume = false;

    /**
     * 线程数量
     */
    public static int threadNum = 10;

    public static int retry = 3;

    public static long WAIT_THREAD_END_TIME=1000*60;
}
