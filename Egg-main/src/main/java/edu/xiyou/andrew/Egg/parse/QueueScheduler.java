package edu.xiyou.andrew.Egg.parse;

import com.sleepycat.je.Environment;
import edu.xiyou.andrew.Egg.generator.Generator;
import edu.xiyou.andrew.Egg.generator.StardardGenerator;
import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.Egg.parse.filter.BloomFilter;
import edu.xiyou.andrew.Egg.persistence.BerkeleyWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by andrew on 15-2-2.
 */
public class QueueScheduler implements Scheduler{
    private static int queueSize = 100;
    private static long maxSize = 1000 * 1000;
    protected static AtomicLong pushNum = new AtomicLong(0);
    protected static AtomicLong pullNum = new AtomicLong(0);
    private BlockingQueue<CrawlDatum> queue = new ArrayBlockingQueue<CrawlDatum>(queueSize);
    private static BloomFilter<String> bloomFilter = new BloomFilter<String>(7, (int)maxSize);
    private BerkeleyWrite write;
    private Generator generator;
    private Environment environment;

    protected final Lock lock = new ReentrantLock();
    protected Condition empty = lock.newCondition();
    protected Condition full = lock.newCondition();

    private static Logger logger = LoggerFactory.getLogger(QueueScheduler.class);

    public QueueScheduler(Environment environment){
        this.environment = environment;
        write = new BerkeleyWrite(environment);
        write.init();
        generator = new StardardGenerator(environment);
    }

    @Override
    public void push(CrawlDatum datum) {
        if (datum == null){
            return;
        }
        if (pushNum.get() > maxSize){
            logger.info("push crawldatum too more");
            return;
        }
        if (!bloomFilter.contains(datum.getUrl())){
            bloomFilter.add(datum.getUrl());
            try {
                write.writeLink(datum);
            }catch (Exception e){
                logger.info("Exception: " + e);
                return;
            }
            pushNum.incrementAndGet();
        }
    }

    {
        Thread putThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        putToQueue();
                    } catch (InterruptedException e) {
                        logger.info("InterruptedException" + e);
                    }
                }
            }
        });
        putThread.start();
    }

    private void putToQueue() throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() >= queueSize){
                full.await();
            }
            queue.add(generator.next());
            empty.signal();
        }finally {
            lock.unlock();
        }
    }

    private CrawlDatum takeFromQueue() throws InterruptedException {
        CrawlDatum datum = null;
        lock.lock();
        try {
            while (queue.isEmpty()){
                empty.await();
            }
            datum = queue.take();
            full.signal();
        }finally {
            lock.unlock();
        }
        return datum;
    }

    @Override
    public CrawlDatum pull() {
        CrawlDatum datum = null;
        lock.lock();
        try {
            while (queue.isEmpty()){
                try {
                    empty.await();
                } catch (InterruptedException e) {
                    logger.info("InterruptedException" + e);
                }
            }
            datum = queue.take();
            full.signal();
        } catch (InterruptedException e) {
            logger.info("InterruptedException" + e);
        }finally {
            lock.unlock();
        }
        return datum;
    }
}
