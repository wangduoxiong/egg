import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by andrew on 15-9-4.
 */
public class TestTimerExecutor {
    private ScheduledExecutorService executorService;
    private long start;

    public TestTimerExecutor() {
        executorService = Executors.newScheduledThreadPool(2);
        start = System.currentTimeMillis();
    }

    public void timerOne(){
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("timerOne,the time: " + (System.currentTimeMillis() - start));
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }

    public void timerTwo(){
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("TimerTwo, the time: " + (System.currentTimeMillis() - start));
            }
        }, 2000, TimeUnit.MILLISECONDS);
    }

    public void timerThree(){
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        }, 1000, TimeUnit.MILLISECONDS);
    }

    public void timerFour(){
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("is running.....");
            }
        }, 4000, TimeUnit.MILLISECONDS);
    }

    private long rateFirst = System.currentTimeMillis();
    private long ratePreTime = rateFirst;
    private AtomicLong rateNum = new AtomicLong(0);
    public void timeFive(){
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("scheduleAtFixedRate is running... " + (System.currentTimeMillis() - ratePreTime)
                        + "   \t\t\ttotalTime: " + (System.currentTimeMillis() - rateFirst) + " num: " + rateNum.getAndIncrement());
                try {
                    ratePreTime = System.currentTimeMillis();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private AtomicLong delayNum = new AtomicLong(0);
    private long delayPreTime = System.currentTimeMillis();
    private long delayFirstTime = delayPreTime;
    public void timeSix(){
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println("scheduleWithFixedDelay  Delay is running.... " + (System.currentTimeMillis() - delayPreTime)
                        + "   totalTime :" + (System.currentTimeMillis() - delayFirstTime) + "  num: " + delayNum.getAndIncrement());
                try {
                    delayPreTime = System.currentTimeMillis();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        TestTimerExecutor testTimerExecutor = new TestTimerExecutor();
//        testTimerExecutor.timerThree();
//        testTimerExecutor.timerFour();
        testTimerExecutor.timeFive();
        testTimerExecutor.timeSix();

        List<String> list = new ArrayList<String>();
        list.toArray();
    }
}
