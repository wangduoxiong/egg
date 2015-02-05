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
import edu.xiyou.andrew.Egg.persistence.BerkeleyWrite;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by andrew on 15-2-2.
 */
public class TestDemo {

    private Lock lock = new ReentrantLock();
    private Condition echo = lock.newCondition();

    private void createThread(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    int i = 0;
                        System.out.println("---->  " + (i++));

                } finally {
                    lock.unlock();
                }
            }
        });
        thread.start();
    }

    private void other(){
        lock.lock();
        try {
            int i = 0;
            while (i < 12) {
                System.out.println("   xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx  " + i);
                i++;
                if (i % 3 == 0){
                    createThread();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        TestDemo demo = new TestDemo();
        demo.other();
    }
}
