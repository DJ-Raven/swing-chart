package raven.chart.data;

import java.util.ArrayList;
import java.util.List;

public class DefaultKeyedValues<K> implements KeyedValues<K> {

    private final List<K> keys;
    private final List<Number> values;

    public DefaultKeyedValues() {
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    @Override
    public K getKey(int index) {
        return keys.get(index);
    }

    @Override
    public int getIndex(K key) {
        return keys.indexOf(key);
    }

    @Override
    public List<K> getKeys() {
        return keys;
    }

    @Override
    public Number getValue(K key) {
        int index = getIndex(key);
        if (index < 0) {
            return 0;
        }
        return getValue(index);
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    @Override
    public Number getValue(int index) {
        return values.get(index);
    }

    public void setValue(K key, Number value) {
        int keyIndex = keys.indexOf(key);
        if (keyIndex >= 0) {
            this.keys.set(keyIndex, key);
            this.values.set(keyIndex, value);
        } else {
            this.keys.add(key);
            this.values.add(value);
        }
    }

    public Number getMinValue() {
        double min = 0;
        for (Number value : values) {
            Math.min(min, value.doubleValue());
        }
        return min;
    }

    public Number getMaxValue() {
        double max = 0f;
        for (Number value : values) {
            max = Math.max(max, value.doubleValue());
        }
        return max;
    }
}
