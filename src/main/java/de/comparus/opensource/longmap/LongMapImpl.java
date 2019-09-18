package de.comparus.opensource.longmap;

import java.util.Arrays;
import java.util.Iterator;

public class LongMapImpl<V> implements LongMap<V> {

    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private int capacity = INITIAL_CAPACITY;
    private Item[] entries = new Item[capacity];
    private int size;

    @SuppressWarnings("unchecked")
    public V put(long key, V value) {
        if (value == null) {
            throw new RuntimeException("Value cannot be null!!!");
        }
        V oldValue = null;
        Item item = getItem(key);
        if (item != null) {
            oldValue = (V) item.getData().value;
            item.getData().value = value;
        } else {
            checkCapacity();
            Item newItem = new Item(new Entry<V>(key, value));
            int bucketNumber = bucketNumber(key);
            item = entries[bucketNumber];
            checkCapHelper(entries, newItem, bucketNumber, item);
            size++;
        }
        return oldValue;
    }

    @SuppressWarnings("unchecked")
    public V get(long key) {
        Item item = getItem(key);
        return item != null ? (V) item.getData().value : null;
    }

    @SuppressWarnings("unchecked")
    public V remove(long key) {
        V oldValue = null;

        Item previous = null;
        int bucketNumber = bucketNumber(key);
        Item item = entries[bucketNumber];
        while (item != null) {
            if (item.getData().key == key) {
                oldValue = (V) item.getData().value;
                break;
            }
            previous = item;
            item = item.getNext();
        }
        if (oldValue != null) {
            if (previous == null) {
                entries[bucketNumber] = item.getNext();
            } else {
                previous.setNext(item.getNext());
            }
            size--;
        }
        return oldValue;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(long key) {
        return getItem(key) != null;
    }

    public boolean containsValue(V value) {
        if (value != null) {
            for (Item item : entries) {
                while (item != null) {
                    if (value.equals(item.getData().value)) {
                        return true;
                    }
                    item = item.getNext();
                }
            }
        }
        return false;
    }

    public long[] keys() {
        if (size > 0) {
            long[] keys = new long[size];
            int i = 0;
            for (Item item : entries) {
                while (item != null) {
                    keys[i++] = item.getData().key;
                    item = item.getNext();
                }
            }
            return keys;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public V[] values() {
        if (size > 0) {
            V[] values = (V[]) new Object[size];
            int i = 0;
            for (Item item : entries) {
                while (item != null) {
                    values[i++] = (V) item.getData().value;
                    item = item.getNext();
                }
            }
            return values;
        }
        return null;
    }

    public long size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        Item<V>[] tab = entries;
        if (tab != null && size > 0) {
            size = 0;
            Arrays.fill(tab, null);
        }
    }

    @SuppressWarnings("unchecked")
    private void checkCapacity() {
        if (size == capacity * LOAD_FACTOR) {
            Item[] tempEntries = new Item[capacity * 2];
            Iterator iter = iterator();
            while (iter.hasNext()) {
                Entry<V> entry = (Entry<V>) iter.next();
                Item newItem = new Item(entry);
                int bucketNumber = Math.abs(entry.hashCode()) % (capacity * 2);
                Item item = tempEntries[bucketNumber];
                checkCapHelper(tempEntries, newItem, bucketNumber, item);
            }
            entries = tempEntries;
            capacity = capacity * 2;
        }
    }

    private void checkCapHelper(Item[] tempEntries, Item newItem, int bucketNumber, Item item) {
        if (item == null) {
            tempEntries[bucketNumber] = newItem;
        } else {
            while (item.getNext() != null) {
                item = item.getNext();
            }
            item.setNext(newItem);
        }
    }

    private Item getItem(long key) {
        Item item = entries[bucketNumber(key)];
        while (item != null) {
            if (item.getData().key == 0 || key == (item.getData().key)) {
                break;
            }
            item = item.getNext();
        }
        return item;
    }

    private int bucketNumber(long key) {
        return (int) Math.abs(key) % capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    @SuppressWarnings("unchecked")
    private Iterator<Entry<V>> iterator() {
        return new Iterator<Entry<V>>() {
            private int currentIndex = -1;
            private Item currentItem;
            private ItemWithIndex next;

            private ItemWithIndex findNext() {
                int index = currentIndex;
                Item item = currentItem;
                if (item != null) {
                    item = item.getNext();
                }
                while (item == null) {
                    if (index == capacity - 1) {
                        break;
                    }
                    item = entries[++index];
                }
                return new ItemWithIndex(index, item);
            }

            @Override
            public boolean hasNext() {
                next = findNext();
                return next.getItem() != null;
            }

            @Override
            public Entry<V> next() {
                if (next == null) {
                    next = findNext();
                }
                currentIndex = next.getIndex();
                currentItem = next.getItem();
                next = null;
                return currentItem != null ? currentItem.getData() : null;
            }

            class ItemWithIndex {
                private int index;
                private Item item;

                ItemWithIndex(int index, Item item) {
                    this.index = index;
                    this.item = item;
                }

                int getIndex() {
                    return index;
                }

                Item getItem() {
                    return item;
                }
            }
        };
    }

    private static class Item<V> {
        private Item next;
        private Entry<V> data;

        Item(Entry<V> data) {
            this.data = data;
        }

        Item getNext() {
            return next;
        }

        void setNext(Item next) {
            this.next = next;
        }

        Entry<V> getData() {
            return data;
        }
    }

    static class Entry<V> {
        final long key;
        V value;

        Entry(long key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
