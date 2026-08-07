package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    // 一些简单的Comparator，都调用了Integer/String自带的compareTo
    private static class SimpleIntegerComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    }

    private static class ReversedIntegerComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2.compareTo(o1);
        }
    }

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    // 需要实例化才能使用这些comparator
    Comparator<Integer> simpleIntegerComparator = new SimpleIntegerComparator();
    Comparator<Integer> reversedIntegerComparator = new ReversedIntegerComparator();
    Comparator<String> stringComparator = new StringComparator();

    // 测试MaxArrayDeque.max()
    @Test
    public void maxTest() {
        // 创建一个简单的maxArrayDeque
        MaxArrayDeque<Integer> maxDeque1 = new MaxArrayDeque<>(simpleIntegerComparator);
        maxDeque1.addLast(1);
        maxDeque1.addLast(2);
        maxDeque1.addLast(3);

        int result1 = maxDeque1.max();
        assertEquals(3, result1);
        int result2 = maxDeque1.max(reversedIntegerComparator);
        assertEquals(1, result2);

        // 创建一个简单的maxArrayDeque
        MaxArrayDeque<String> maxDeque2 = new MaxArrayDeque<>(stringComparator);
        maxDeque2.addLast("Jeremy");
        maxDeque2.addLast("once");
        maxDeque2.addLast("loved");
        maxDeque2.addLast("Lucinda");

        String result3 = maxDeque2.max();
        assertEquals("once", result3);
    }


    // 测试Deque.equals()
    @Test
    public void equalsTest() {
        Deque<Integer> maxDeque = new MaxArrayDeque<>(simpleIntegerComparator);
        Deque<Integer> arrDeque = new ArrayDeque<>();
        Deque<Integer> llDeque = new LinkedListDeque<>();
        maxDeque.addLast(1);
        maxDeque.addLast(2);
        maxDeque.addLast(3);
        arrDeque.addLast(1);
        arrDeque.addLast(2);
        arrDeque.addLast(3);
        llDeque.addLast(1);
        llDeque.addLast(2);
        llDeque.addLast(3);

        // assertEquals实际上调用的就是.equals()
        assertEquals(maxDeque, arrDeque);
        assertEquals(maxDeque, llDeque);
    }
}
