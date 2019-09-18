package de.comparus.opensource.longmap;

import de.comparus.opensource.longmap.LongMap;
import de.comparus.opensource.longmap.LongMapImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class LongMapImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private LongMapImpl<Double> longMap = new LongMapImpl<>();

    @Test
    public void initializingLongMapTest() {
        LongMap<String> longMap = new LongMapImpl<>();
        assertNull(longMap.keys());
        assertNull(longMap.values());
        assertEquals(longMap.size(), 0);
        assertTrue(longMap.isEmpty());
    }

    @Test
    public void putNullValueTest() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Value cannot be null!!!");
        longMap.put(1212, null);
    }

    @Test
    public void putExistKeyTest() {
        longMap = new LongMapImpl<>();
        double expectedOldValue = 42.;
        longMap.put(33, 42.);
        double actualOldValue = longMap.put(33, 66.);
        assertEquals(expectedOldValue, actualOldValue);
    }

    @Test
    public void putNotExistKeyTest() {
        longMap = new LongMapImpl<>();
        Double expectedOldValue = null;
        longMap.put(33, 42.);
        Double actualOldValue = longMap.put(66, 66.);
        assertEquals(expectedOldValue, actualOldValue);
    }

    @Test
    public void putCapacityIncreaseTest() {
        longMap = new LongMapImpl<>();
        for (int i = 0; i < 12; i++) {
            longMap.put(i, (double) i);
        }
        int oldCapacity = longMap.getCapacity();
        int expectedCapacity = 32;
        longMap.put(33, 42.);
        assertEquals(expectedCapacity, oldCapacity * 2);
    }

    @Test
    public void mapContainEntryTest() {
        longMap = new LongMapImpl<>();
        longMap.put(33, 42.);
        assertTrue(longMap.containsKey(33));
        assertTrue(longMap.containsValue(42.));
    }

    @Test
    public void testGetValues() {
        longMap = new LongMapImpl<>();
        longMap.put(123, 123d);
        assertEquals(123d, longMap.get(123));
    }

    @Test
    public void mapPutResizeTest() {
        longMap = new LongMapImpl<>();
        int sizeBefore = longMap.keys() != null ? longMap.keys().length : 0;
        longMap.put(42, 42d);
        assertEquals(sizeBefore + 1, longMap.size());
    }

    @Test
    public void mapPutContainOldEntriesTest() {
        longMap = new LongMapImpl<>();
        int beforeResize = longMap.keys() != null ? longMap.keys().length : 0;
        for (int i = 0; i < beforeResize; i++) {
            longMap.put(i, (double) i);
        }
        longMap.put(42, 42d);
        long[] oldKeys = longMap.keys();
        Object[] oldValues = longMap.values();
        if (oldKeys != null) {
            for (long key : oldKeys) {
                assertTrue(longMap.containsKey(key));
            }
        }
        if (oldValues != null) {
            for (Object value : oldValues) {
                assertTrue(longMap.containsValue((Double) value));
            }
        }
        assertTrue(longMap.containsKey(42));
        assertTrue(longMap.containsValue(42d));
    }

    @Test
    public void removeEntryTest() {
        longMap = new LongMapImpl<>();
        long key = 35;
        double value = 19.1923111333;
        longMap.put(key, value);
        double oldValue = longMap.remove(key);
        assertEquals(oldValue, value, 0.00000001);
        assertFalse(longMap.containsValue(value));
        assertFalse(longMap.containsKey(key));
    }

    @Test
    public void removeEntryFromTheMiddleTest() {
        LongMap<String> longMap = new LongMapImpl<>();
        long key = 19;
        String expectedValue = "String5";
        longMap.put(1, "String1");
        longMap.put(2, "String2");
        longMap.put(3, "String3");
        longMap.put(4, "String4");
        longMap.put(19, "String5");
        long sizeBeforeRemove = longMap.size();
        String oldValue = longMap.remove(key);
        assertEquals(expectedValue, oldValue);
        assertFalse(longMap.containsValue(expectedValue));
        assertFalse(longMap.containsKey(key));
        assertEquals(sizeBeforeRemove - 1, longMap.size());
        assertArrayEquals(new long[]{1, 2, 3, 4}, longMap.keys());
    }

    @Test
    public void clearTest() {
        LongMap<Double> map = new LongMapImpl<>();
        map.put(22, 35.);
        map.put(141, 3231.2);
        map.clear();
        assertEquals(0, map.size());
        assertNull(map.keys());
        assertNull(map.values());
        assertTrue(map.isEmpty());
    }

}