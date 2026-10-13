package bstmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {  // Makes sure K has a compareTo method
    private BSTNode tree;

    public void printInOrder() {
        printHelper(tree);
    }

    private void printHelper(BSTNode node) {
        if (node != null) {
            printHelper(node.left);
            System.out.println(node.key.toString() + ": " + node.val.toString());
            printHelper(node.right);
        }
    }

    @Override
    public void clear() {
        tree = null;
    }

    @Override
    public boolean containsKey(K key) {
        return containsHelper(tree, key);
    }

    private boolean containsHelper(BSTNode node, K key) {
        if (node == null) {
            return false;
        }
        if (node.key.equals(key)) {
            return true;
        }
        if (node.key.compareTo(key) > 0) {  // root key larger than target key
            return containsHelper(node.left, key);
        } else {  // root key smaller than target key
            return containsHelper(node.right, key);
        }
    }

    @Override
    public V get(K key) {
        return getHelper(tree, key);
    }

    private V getHelper(BSTNode node, K key) {
        if (node == null) {
            return null;
        }
        if (node.key.equals(key)) {
            return node.val;
        }
        if (node.key.compareTo(key) > 0) {  // root key larger than target key
            return getHelper(node.left, key);
        } else {  // root key smaller than target key
            return getHelper(node.right, key);
        }
    }

    @Override
    public int size() {
        return sizeHelper(tree);
    }

    private int sizeHelper(BSTNode node) {
        if (node == null) {
            return 0;
        }
        return sizeHelper(node.left) + sizeHelper(node.right) + 1;
    }

    @Override
    public void put(K key, V value) {
        if (tree == null) {
            tree = new BSTNode(key, value);
        } else {
            putHelper(tree, key, value);
        }
    }

    private void putHelper(BSTNode node, K key, V value) {
        // Note that node argument must not be null
        if (node.key.equals(key)) {
            node.val = value;
            return;
        }
        if (node.key.compareTo(key) > 0) {  // root key larger than target key
            if (node.left == null) {
                node.left = new BSTNode(key, value);
            } else {
                putHelper(node.left, key, value);
            }
        } else {  // root key smaller than target key
            if (node.right == null) {
                node.right = new BSTNode(key, value);
            } else {
                putHelper(node.right, key, value);
            }
        }

    }

    @Override
    public Set<K> keySet() {
        return keySetHelper(tree, new HashSet<>());
    }

    private Set<K> keySetHelper(BSTNode node, Set<K> keys) {
        if (node == null) {
            return keys;
        } else {
            keys.add(node.key);
            return keySetHelper(node.left, keySetHelper(node.right, keys));
        }
    }

    @Override
    public V remove(K key) {
        if (tree == null) {
            return null;
        }
        if (tree.key.equals(key)) { // root node
            V ans = tree.val;
            if (tree.left == null) {
                if (tree.right == null) {
                    tree = null;
                } else {
                    tree = tree.right;
                }
            } else {
                if (tree.right == null) {
                    tree = tree.left;
                } else {
                    // Find the rightmost element on the left of root as new root
                    BSTNode parent = tree.left;
                    if (parent.right == null) {
                        // the rightmost element is parent
                        tree.left = parent.left;
                        tree.key = parent.key;
                        tree.val = parent.val;
                    } else {
                        while (parent.right.right != null) {
                            parent = parent.right;
                        }
                        // the rightmost element is parent.right
                        K k = parent.right.key;
                        V v = parent.right.val;
                        parent.right = parent.right.left;
                        tree.key = k;
                        tree.val = v;
                    }
                }
            }
            return ans;
        }
        // For non-root cases,
        // first locate the node and its parent
        BSTNode parent = tree;
        while (true) {
            // Note that parent.key can NEVER equal value because it is a parent.
            // While loop should be broken and node be found before parent proceeds deeper.
            if (parent.key.compareTo(key) > 0) { // go left
                if (parent.left == null) {
                    return null; // not found
                } else if (parent.left.key.equals(key)) {
                    // found! parent.left
                    V ans = parent.left.val;
                    if (parent.left.left == null) {
                        if (parent.left.right == null) {
                            parent.left = null;
                        } else {
                            parent.left = parent.left.right;
                        }
                    } else {
                        if (parent.left.right == null) {
                            parent.left = parent.left.left;
                        } else {
                            // Consider a new tree with node as root
                            // Find the rightmost element('s parent) on the left of root as new root
                            BSTNode parent2 = parent.left.left;
                            if (parent2.right == null) {
                                // the rightmost element is parent2
                                parent.left.left = parent2.left;
                                parent.left.key = parent2.key;
                                parent.left.val = parent2.val;
                            } else {
                                while (parent2.right.right != null) {
                                    parent2 = parent2.right;
                                }
                                // the rightmost element is parent2.right
                                K k = parent2.right.key;
                                V v = parent2.right.val;
                                parent2.right = parent2.right.left;
                                parent.left.key = k;
                                parent.left.val = v;
                            }
                        }
                    }
                    return ans;

                } else {
                    parent = parent.left;
                }
            } else {
                if (parent.right == null) {
                    return null; // not found
                } else if (parent.right.key.equals(key)) {
                    // found! parent.right
                    V ans = parent.right.val;
                    if (parent.right.left == null) {
                        if (parent.right.right == null) {
                            parent.right = null;
                        } else {
                            parent.right = parent.right.right;
                        }
                    } else {
                        if (parent.right.right == null) {
                            parent.right = parent.right.left;
                        } else {
                            // Consider a new tree with node as root
                            // Find the rightmost element on the left of root as new root
                            BSTNode parent2 = parent.right.left;
                            if (parent2.right == null) {
                                // the rightmost element is parent
                                parent.right.left = parent2.left;
                                parent.right.key = parent2.key;
                                parent.right.val = parent2.val;
                            } else {
                                while (parent2.right.right != null) {
                                    parent2 = parent2.right;
                                }
                                // the rightmost element is parent2.right
                                K k = parent2.right.key;
                                V v = parent2.right.val;
                                parent2.right = parent2.right.left;
                                parent.right.key = k;
                                parent.right.val = v;
                            }
                        }
                    }
                    return ans;
                } else {
                    parent = parent.right;
                }
            }
        }
    }


    @Override
    public V remove(K key, V value) {
        if (tree == null) {
            return null;
        }
        if (tree.key.equals(key)) { // root node
            if (!tree.val.equals(value)) {
                return null;
            }

            V ans = tree.val;
            if (tree.left == null) {
                if (tree.right == null) {
                    tree = null;
                } else {
                    tree = tree.right;
                }
            } else {
                if (tree.right == null) {
                    tree = tree.left;
                } else {
                    // Find the rightmost element on the left of root as new root
                    BSTNode parent = tree.left;
                    if (parent.right == null) {
                        // the rightmost element is parent
                        tree.left = parent.left;
                        tree.key = parent.key;
                        tree.val = parent.val;
                    } else {
                        while (parent.right.right != null) {
                            parent = parent.right;
                        }
                        // the rightmost element is parent.right
                        K k = parent.right.key;
                        V v = parent.right.val;
                        parent.right = parent.right.left;
                        tree.key = k;
                        tree.val = v;
                    }
                }
            }
            return ans;
        }
        // For non-root cases,
        // first locate the node and its parent
        BSTNode parent = tree;
        while (true) {
            // Note that parent.key can NEVER equal value because it is a parent.
            // While loop should be broken and node be found before parent proceeds deeper.
            if (parent.key.compareTo(key) > 0) { // go left
                if (parent.left == null) {
                    return null; // not found
                } else if (parent.left.key.equals(key)) {
                    // found! parent.left
                    if (!parent.left.val.equals(value)) {
                        return null;
                    }

                    V ans = parent.left.val;
                    if (parent.left.left == null) {
                        if (parent.left.right == null) {
                            parent.left = null;
                        } else {
                            parent.left = parent.left.right;
                        }
                    } else {
                        if (parent.left.right == null) {
                            parent.left = parent.left.left;
                        } else {
                            // Consider a new tree with node as root
                            // Find the rightmost element('s parent) on the left of root as new root
                            BSTNode parent2 = parent.left.left;
                            if (parent2.right == null) {
                                // the rightmost element is parent2
                                parent.left.left = parent2.left;
                                parent.left.key = parent2.key;
                                parent.left.val = parent2.val;
                            } else {
                                while (parent2.right.right != null) {
                                    parent2 = parent2.right;
                                }
                                // the rightmost element is parent2.right
                                K k = parent2.right.key;
                                V v = parent2.right.val;
                                parent2.right = parent2.right.left;
                                parent.left.key = k;
                                parent.left.val = v;
                            }
                        }
                    }
                    return ans;

                } else {
                    parent = parent.left;
                }
            } else {
                if (parent.right == null) {
                    return null; // not found
                } else if (parent.right.key.equals(key)) {
                    // found! parent.right
                    if (!parent.right.val.equals(value)) {
                        return null;
                    }

                    V ans = parent.right.val;
                    if (parent.right.left == null) {
                        if (parent.right.right == null) {
                            parent.right = null;
                        } else {
                            parent.right = parent.right.right;
                        }
                    } else {
                        if (parent.right.right == null) {
                            parent.right = parent.right.left;
                        } else {
                            // Consider a new tree with node as root
                            // Find the rightmost element on the left of root as new root
                            BSTNode parent2 = parent.right.left;
                            if (parent2.right == null) {
                                // the rightmost element is parent
                                parent.right.left = parent2.left;
                                parent.right.key = parent2.key;
                                parent.right.val = parent2.val;
                            } else {
                                while (parent2.right.right != null) {
                                    parent2 = parent2.right;
                                }
                                // the rightmost element is parent2.right
                                K k = parent2.right.key;
                                V v = parent2.right.val;
                                parent2.right = parent2.right.left;
                                parent.right.key = k;
                                parent.right.val = v;
                            }
                        }
                    }
                    return ans;
                } else {
                    parent = parent.right;
                }
            }
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTIterator();
    }

    private class BSTIterator implements Iterator<K> {
        ArrayList<BSTNode> pending = new ArrayList<>();  // do not add null into this

        public BSTIterator() {
            if (tree != null) {
                pending.add(tree);
            }
        }

        @Override
        public boolean hasNext() {
            return !pending.isEmpty();
        }

        @Override
        public K next() {
            BSTNode processing = pending.remove(pending.size() - 1);
            if (processing.left != null) {
                pending.add(processing.left);
            }
            if (processing.right != null) {
                pending.add(processing.right);
            }
            return processing.key;
        }
    }

    private class BSTNode {
        K key;
        V val;
        BSTNode left;
        BSTNode right;

        public BSTNode(K k, V v) {
            key = k;
            val = v;
        }
    }
}
