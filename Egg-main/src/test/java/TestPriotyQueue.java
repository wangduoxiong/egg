import org.junit.Test;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by andrew on 15-9-4.
 */
public class TestPriotyQueue {
    @Test
    public void order() throws InterruptedException {
        PriorityQueue<Num> queue = new PriorityQueue<Num>();
        Num num1 = new Num(1);
        Num num2 = new Num(2);
        Num num3 = new Num(3);
        Num num4 = new Num(4);
        Num num0 = new Num(0);
        queue.add(num3);
        queue.add(num2);
        queue.add(num0);
        queue.add(num4);
        queue.add(num1);
        Num num;
        int i = 3;
        while ((num  = queue.poll()) != null) {
            System.out.println(num);
        }
    }

    static class Num implements Comparable{
        private int num;

        public Num(int num){
            this.num = num;
        }

        @Override
        public int compareTo(Object o) {
            return this.num - ((Num)o).num;
        }

        @Override
        public String toString() {
            return "Num{" +
                    "num=" + num +
                    '}';
        }
    }
}
