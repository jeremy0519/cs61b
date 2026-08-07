package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    // store the comparator
    private final Comparator<T> defaultComparator;

    public MaxArrayDeque(Comparator<T> c) {
        defaultComparator = c;
    }

    // 各种API
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T result = get(0);
        for (T item : this) {
            if (c.compare(item, result) > 0) { // 正数说明item更大
                result = item;
            }
        }
        return result;
    }

    public T max() {
        return max(defaultComparator);
    }
}
