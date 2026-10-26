package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author jeremy0519
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private final double maxLoad;
    private int size = 0;
    private HashSet<K> keySet = new HashSet<>();

    /** Constructors */
    public MyHashMap() {
        this(16, 0.75);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        this.maxLoad = maxLoad;
        for (int i = 0; i < initialSize; i++) {
            buckets[i] = createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    @Override
    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
        keySet = new HashSet<>();
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        // Check if key is already in the HashTable
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        // key is not in HashTable
        size += 1;
        double newLoad = (double) size / buckets.length;
        if (newLoad > maxLoad) {
            // Resize first
            Collection<Node>[] newBuckets = createTable(buckets.length * 2);
            for (int i = 0; i < newBuckets.length; i++) {
                newBuckets[i] = createBucket();
            }
            for (Collection<Node> bucket : buckets) {
                for (Node node : bucket) {
                    index = Math.floorMod(node.key.hashCode(), newBuckets.length);
                    newBuckets[index].add(node);
                }
            }
            buckets = newBuckets;
        }
        index = Math.floorMod(key.hashCode(), buckets.length);
        buckets[index].add(createNode(key, value));
        keySet.add(key);
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                V ans = node.value;
                buckets[index].remove(node);
                return ans;
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                if (node.value.equals(value)) {
                    V ans = node.value;
                    buckets[index].remove(node);
                    keySet.remove(key);
                    size -= 1;
                    return ans;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }
}
