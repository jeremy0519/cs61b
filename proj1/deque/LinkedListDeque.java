package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    // A single Node, containing previous, value, and next
    private class Node {
        Node previous;
        T value;
        Node next;

        Node(Node previous, T value, Node next) {
            this.previous = previous;
            this.value = value;
            this.next = next;
        }
    }

    private int size;
    private final Node sentinel;

    // 初始化
    public LinkedListDeque() {
        size = 0;
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.previous = sentinel;
    }

    // 各种API
    @Override
    public void addFirst(T item) {
        Node temp = sentinel.next;
        sentinel.next = new Node(sentinel, item, temp);
        temp.previous = sentinel.next;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node temp = sentinel.previous;
        sentinel.previous = new Node(temp, item, sentinel);
        temp.next = sentinel.previous;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node current = sentinel.next;
        while (current.value != null) {
            System.out.print(current.value + " ");
            current = current.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        } else {
            T val = sentinel.next.value;
            sentinel.next = sentinel.next.next;
            sentinel.next.previous = sentinel;
            size -= 1;
            return val;
        }
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        } else {
            T val = sentinel.previous.value;
            sentinel.previous = sentinel.previous.previous;
            sentinel.previous.next = sentinel;
            size -= 1;
            return val;
        }
    }

    @Override
    // Iteration method
    public T get(int index) {
        if (index >= size) {
            return null;
        } else {
            Node current = sentinel.next;
            while (index > 0) {
                current = current.next;
                index -= 1;
            }
            return current.value;
        }
    }

    // Recursion method
    private T getRecursiveHelper(int index, Node start) {
        if (index == 0) {
            return start.value;
        } else {
            return getRecursiveHelper(index - 1, start.next);
        }
    }

    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        } else {
            return getRecursiveHelper(index, sentinel.next);
        }
    }

    // Support Iteration
    public Iterator<T> iterator() {
        return new LLDequeIterator();
    }

    private class LLDequeIterator implements Iterator<T> {
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
