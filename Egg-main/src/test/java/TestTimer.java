//import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by andrew on 15-9-4.
 */
public class TestTimer {
    Timer timer;

    public TestTimer(){
    }

//    public TestTimer consume(){
//        timer = new Timer();
//        return this;
//    }
//
//    public <E extends TimerTask>TestTimer setTimerTask(E timerTask, int time){
//        timer.schedule(timerTask, time);
//        return this;
//    }
//
//    @Test
//    public void testTimer(){
//        System.out.println("TimerTask is begin....");
//        TestTimer testTimer = new TestTimer();
//        testTimer.consume().setTimerTask(new TestTimerTask(), 1 * 1000);
//    }
    public TestTimer(int time){
        timer = new Timer();
        timer.schedule(new TestTimerTask(), time * 1000, 1 * 1000);
    }

    public static void main(String[] args) {
        System.out.println("TimerTask is begin....");
        new TestTimer(1);
    }

    static class TestTimerTask extends TimerTask{
        public void run(){
            System.out.println("TestTimerTask is running");
        }
    }
}
