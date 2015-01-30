package scheduler;

import com.sleepycat.je.Environment;
import edu.xiyou.andrew.Egg.fetcher.Fetcher;
import edu.xiyou.andrew.Egg.generator.Generator;
import edu.xiyou.andrew.Egg.generator.SegmentWrite;
import edu.xiyou.andrew.Egg.generator.StandardGenerator;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.CrawlDatum;
import edu.xiyou.andrew.Egg.thread.ThreadPool;
import edu.xiyou.andrew.Egg.utils.BloomFilter;
import edu.xiyou.andrew.Egg.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by andrew on 15-1-30.
 */
public class QueueScheduler implements Scheduler{
    private static final int QUEUE_SIZE = 200;
    private BlockingQueue<CrawlDatum> queue = new ArrayBlockingQueue<CrawlDatum>(QUEUE_SIZE);
    private Environment environment;
    private SegmentWrite segmentWrite;
    private static Generator generator = null;
    private static ThreadPool pool = new ThreadPool(Config.CPU_COUNT * 2);

    private static final Logger LOG = LoggerFactory.getLogger(QueueScheduler.class);

    public QueueScheduler(Environment environment){
        this.environment = environment;
        this.segmentWrite = new SegmentWrite(environment);
        generator = new StandardGenerator(environment);
    }

    @Override
    public synchronized void push(CrawlDatum datum) {
        if ((datum == null) || (datum.getUrl() == null)){
            return;
        }
        try {
            if (!segmentWrite.contains(datum.getUrl())) {
                segmentWrite.writeLink(datum.getUrl());
            }
        } catch (Exception e) {
            LOG.info("Exception: " + e);
        }
    }

    @Override
    public CrawlDatum poll() {
        if (queue.size() > 0){
            if (queue.size() < 5){
                Runnable[] threads = new FillQueue[Config.CPU_COUNT * 2];
                for (int i = 0; i < threads.length; i++){
                    threads[i] = new FillQueue(generator, queue);
                    pool.execute(threads[i]);
                }
            }
            try {
                return queue.take();
            } catch (InterruptedException e) {
                LOG.info("Exception: " + e);
            }
        }
        return null;
    }

    private static class FillQueue implements Runnable{
        private BlockingQueue<CrawlDatum> queue;
        private Generator generator;
        public FillQueue(Generator generator, BlockingQueue<CrawlDatum> queue){
            this.generator = generator;
            this.queue = queue;
        }

        @Override
        public void run(){
            boolean hasMore = true;
            int feed;
            CrawlDatum datum = null;

            while (hasMore){
                feed = QUEUE_SIZE - queue.size();

                while (feed < 0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                }

                while (feed > 0 && hasMore){
                    try {
                        datum = generator.nextDatum();

                        hasMore = (datum != null);

                        if (hasMore){
                            queue.add(datum);
                            feed --;
                        }
                    } catch (Exception e) {
                        LOG.info("Exception: " + e);
                    }
                }
            }
        }
    }

    public void close(){
        if ((generator != null) && (generator instanceof StandardGenerator)){
            ((StandardGenerator) generator).close();
        }
    }
}
