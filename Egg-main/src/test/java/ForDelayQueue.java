import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;

/**
 * Created by andrew on 15-9-4.
 */
public  class ForDelayQueue<T extends Delayed> {
    private DelayQueue<T> queue = new DelayQueue<T>();

}
