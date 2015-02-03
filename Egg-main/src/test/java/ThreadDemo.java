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
/**
 * Created by andrew on 15-1-29.
 */
public class ThreadDemo {

    class ThreadTest extends Thread{
        public void run(){
            System.out.println("Thread Runnig");
        }
    }

//    public static void main(String[] args){
//        ThreadDemo demo = new ThreadDemo();
//        ThreadTest test = demo.new ThreadTest();
//        test.start();
//    }

//    public static void main(String[] args){
//        Thread thread = new Thread(){
//            public void run(){
//                System.out.println("Thread Running");
//            }
//        };
//        thread.start();
//    }

    static class RunnableDemo implements Runnable{

        @Override
        public void run() {
            System.out.println("Runnable running");
        }
    }

//    public static void main(String[] args){
//        Thread thread = new Thread(new RunnableDemo());
//        thread.start();
//    }

//    public static void main(String[] args){
//        Thread thread = new Thread(new RunnableDemo(), "new Runnable");
//        thread.start();
//        System.out.println(thread.getName());
//    }

    static class RunnableDemo1 implements Runnable{
        @Override
        public void run() {
            System.out.println("Thread name: " + Thread.currentThread().getName());
        }
    }

    public static void main(String[] args){
        Thread thread = new Thread(new RunnableDemo1(), "demo1");
        thread.start();
    }


}
