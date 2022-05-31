package core.basesyntax;

public class MyHashMap<K, V> implements MyMap<K, V> {
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int INDEX_NULL_KEY = 0;
    private Node<K, V>[] table;
    private int size;
    private int modCount;
    private int capacity;

    public MyHashMap() {
        capacity = DEFAULT_INITIAL_CAPACITY;
        modCount = (int) (capacity * DEFAULT_LOAD_FACTOR);
        table = (Node<K, V>[]) new Node[capacity];
    }

    @Override
    public void put(K key, V value) {
        if (isNeedResize()) {
            resizeTable();
            putAfter(key, value, table);
        } else if (key == null) {
            if (table[INDEX_NULL_KEY] == null) {
                size++;
            }
            table[INDEX_NULL_KEY] = new Node<K, V>(key, value, null);
        } else {
            putAfter(key, value, table);
        }
    }

    @Override
    public V getValue(K key) {
        if (key == null) {
            return table[INDEX_NULL_KEY].value;
        }
        V value = null;
        for (int i = 1; i < capacity; i++) {
            for (Node<K, V> current = table[i]; current != null; current = current.next) {
                if (current.key.equals(key)) {
                    value = current.value;
                }
            }
        }
        return value;
    }

    @Override
    public int getSize() {
        return size;
    }

    private void resizeTable() {
        Node<K, V>[] oldTable = table;
        int oldCapacity = capacity;
        modCount = (int) ((capacity *= 2) * DEFAULT_LOAD_FACTOR);
        table = (Node<K, V>[]) new Node[capacity];
        int oldSize = size;
        for (int i = 0; i < oldCapacity; i++) {
            for (Node<K, V> current = oldTable[i]; current != null; current = current.next) {
                put(current.key, current.value);
            }
        }
        size = oldSize;
    }

    private int getHash(Object key) {
        int hash = key == null ? 0 : key.hashCode() % capacity;
        hash += hash == 0 ? 1 : 0;
        return hash < 0 ? hash * (-1) : hash;
    }

    private boolean isNeedResize() {
        return modCount == size;
    }

    private boolean isEmptyBucket(K key, Node<K, V>[] node) {
        int index = getHash(key) == 0 ? 1 : getHash(key);
        return node[index] == null;
    }

    private void putAfter(K key, V value, Node<K, V>[] node) {
        int index = getHash(key) == 0 ? 1 : getHash(key);
        if (isEmptyBucket(key, node)) {
            node[index] = new Node<K, V>(key, value, null);
        } else {
            Node<K, V> current = node[index];
            while (current.next != null) {
                if (current.key.equals(key)) {
                    current.value = value;
                    return;
                }
                current = current.next;
            }
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current.next = new Node<K, V>(key, value, null);
        }
        size++;
    }

    private static class Node<K, V> {
        private final K key;
        private V value;
        private Node<K, V> next;

        public Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
