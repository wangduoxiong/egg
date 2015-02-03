/*
 *Copyright (c) 2015 Andrew-Wang.
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by andrew on 15-1-29.
 */
public class ThreadDemoFirst {
//    public static void main(String[] args){
//        final ExecutorService executorService = Executors.newFixedThreadPool(5);
//        final ReentrantLock lock = new ReentrantLock();
//        final Condition condition = lock.newCondition();
//        final int time = 5;
//        final Runnable add = new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("----> Pre: " + lock.toString());
//                lock.lock();
//
//                try{
//                    condition.await(time, TimeUnit.SECONDS);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }finally {
//                    System.out.println("----> Post: " + lock.toString());
//                    lock.unlock();
//                }
//            }
//        };
//
//        for (int i = 0; i < 4; i++){
//            executorService.submit(add);
//        }
//        executorService.shutdown();
//    }



    public ThreadDemoFirst(){
    }

    static class NumberWrapper{
        public int value = 0;
    }

    public static void main(String[ ] args){
        final Lock lock = new ReentrantLock();

        final Condition reachThreeLock = lock.newCondition();
        final Condition reachSixLock = lock.newCondition();

        final NumberWrapper num = new NumberWrapper();

        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    System.out.println("ThreadA start write");
                    while (num.value <= 3) {
                        System.out.println(num.value);
                        num.value++;
                    }
                    reachThreeLock.signal();
                }finally {
                    lock.unlock();
                }

                lock.lock();
                try {
                    reachSixLock.await();
                    System.out.println("ThreadA start write");

                    while (num.value <= 9){
                        System.out.println(num.value);
                        num.value++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }
            }
        });

        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    while (num.value <= 3){
                        reachThreeLock.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }

                try {
                    lock.lock();
                    System.out.println("ThreadB start write");
                    while (num.value <= 6){
                        System.out.println(num.value);
                        num.value++;
                    }
                    reachSixLock.signal();
                }finally {
                    lock.unlock();
                }
            }
        });

        threadA.start();
        threadB.start();
    }
}
