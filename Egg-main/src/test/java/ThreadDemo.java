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
