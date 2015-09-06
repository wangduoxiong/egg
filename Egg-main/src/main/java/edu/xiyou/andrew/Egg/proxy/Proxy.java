package edu.xiyou.andrew.Egg.proxy;

import com.google.common.collect.Lists;
import edu.xiyou.andrew.Egg.utils.ToStringUtils;
import org.apache.http.HttpHost;

import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrew on 15-9-6.
 */
public class Proxy extends ToStringUtils implements Delayed{
    private static final int ERROR_404 = 404;
    private static final int ERROR_BANNED = 10000;
    private static final int ERROR_PROXY = 10001;

    private final HttpHost httpHost;

    private int reuseTimeinterval =  1500;
    private Long canUseTime = 0L;
    private Long lastBorrowTime = System.nanoTime();
    private Long responseTime = 0L;

    private int failNum = 0;
    private int successNum = 0;

    private List<Integer> failedErrorType = Lists.newArrayList();

    public Proxy(HttpHost host, int reuseTimeinterval){
        this.httpHost = host;
        this.reuseTimeinterval = reuseTimeinterval;
        this.canUseTime = System.nanoTime() + reuseTimeinterval;
    }

    public Proxy(HttpHost host){
        httpHost = host;
        canUseTime = System.nanoTime() + reuseTimeinterval;
    }

    public void recordResponse(){
        this.responseTime = (System.nanoTime() - lastBorrowTime)/2;
        this.lastBorrowTime = System.nanoTime();
    }

    public void setFailType(int type){
        this.failNum++;
        failedErrorType.add(type);
    }

    public void success(){
        successNum++;
    }

    public int getReuseTimeinterval() {
        return reuseTimeinterval;
    }

    public void setReuseTimeinterval(int reuseTimeinterval) {
        this.reuseTimeinterval = reuseTimeinterval;
    }

    public Long getCanUseTime() {
        return canUseTime;
    }

    public void setCanUseTime(Long canUseTime) {
        this.canUseTime = canUseTime;
    }

    @Override
    public long getDelay(TimeUnit timeUnit) {
        return timeUnit.convert(canUseTime - System.nanoTime(), timeUnit);
    }

    @Override
    public int compareTo(Delayed delayed) {
        if (delayed == this){
            return 0;
        }
        long canUseTime = delayed.getDelay(TimeUnit.NANOSECONDS);
        return this.canUseTime > canUseTime ? 1 : (this.canUseTime == canUseTime ? 0 : -1);
    }

    public Long getLastBorrowTime() {
        return lastBorrowTime;
    }

    public int getFailNum() {
        return failNum;
    }

    public int getSuccessNum() {
        return successNum;
    }

    public List<Integer> getFailedErrorType() {
        return failedErrorType;
    }

    public Long getResponseTime() {
        return responseTime;
    }
}
