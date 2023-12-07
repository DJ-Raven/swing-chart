package raven.chart.data.pie;

import raven.chart.data.DefaultKeyedValues;

import java.util.List;

public class DefaultPieDataset<K> implements PieDataset<K> {

    private final DefaultKeyedValues<K> data;

    public DefaultPieDataset() {
        data = new DefaultKeyedValues<>();
    }

    @Override
    public K getKey(int index) {
        return data.getKey(index);
    }

    @Override
    public int getIndex(K key) {
        return data.getIndex(key);
    }

    @Override
    public List<K> getKeys() {
        return data.getKeys();
    }

    @Override
    public Number getValue(K key) {
        return data.getValue(key);
    }

    @Override
    public int getItemCount() {
        return this.data.getItemCount();
    }

    @Override
    public Number getValue(int index) {
        return data.getValue(index);
    }

    public void addValue(K key, Number value) {
        data.setValue(key, value);
    }

    public void setValue(K key, Number value) {
        data.setValue(key, value);
    }

    public Number getMaxValues() {
        return data.getMaxValue();
    }
}
