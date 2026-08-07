package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private int size;
    private T[] items;
    // index of next addFirst
    private int nextFirst;
    // index of next addLast
    private int nextLast;

    public ArrayDeque() {
        size = 0;
        items = (T[]) new Object[8]; // circular array
        nextFirst = 8 - 1;
        nextLast = 0;
    }

    private void resize(int target) {
        T[] newArray = (T[]) new Object[target];
        for (int i = 0; i < size; i++) {
            int realIndex = Math.floorMod(i + nextFirst + 1, items.length);
            newArray[i] = items[realIndex];
        }
        items = newArray; // 原始数据重新放在newArray开头，即index0到size-1
        nextFirst = target - 1;
        nextLast = size;
    }

    // 各种API
    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        nextFirst = Math.floorMod(nextFirst - 1, items.length);
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        nextLast = Math.floorMod(nextLast + 1, items.length);
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            int realIndex = Math.floorMod(i + nextFirst + 1, items.length);
            System.out.print(items[realIndex] + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        } else {
            double usageFactor = (double) (size - 1) / items.length;
            if (items.length >= 16 && usageFactor < 0.25) {
                resize(items.length / 2);
            }
            int firstIndex = Math.floorMod(nextFirst + 1, items.length);
            T val = items[firstIndex];
            items[firstIndex] = null;
            nextFirst = firstIndex;
            size -= 1;
            return val;
        }
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        } else {
            double usageFactor = (double) (size - 1) / items.length;
            if (items.length >= 16 && usageFactor < 0.25) {
                resize(items.length / 2);
            }
            int lastIndex = Math.floorMod(nextLast - 1, items.length);
            T val = items[lastIndex];
            items[lastIndex] = null;
            nextLast = lastIndex;
            size -= 1;
            return val;
        }
    }

    @Override
    public T get(int index) {
        if (size == 0) {
            return null;
        } else {
            int realIndex = Math.floorMod(index + nextFirst + 1, items.length);
            return items[realIndex];
        }
    }

    // Support Iteration
    @Override
    public Iterator<T> iterator() {
        return new ArrDequeIterator();
    }

    private class ArrDequeIterator implements Iterator<T> {
        private int nextIndex = 0;

        @Override
        public boolean hasNext() {
            return nextIndex < size();
        }

        @Override
        public T next() {
            T nextItem = get(nextIndex);
            nextIndex += 1;
            return nextItem;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> obj = (Deque<T>) o;
        if (!(obj.size() == this.size())) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (!(this.get(i).equals(obj.get(i)))) {
                return false;
            }
        }
        return true;
    }
}
