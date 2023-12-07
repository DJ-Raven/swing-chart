package raven.chart.data;

import java.util.List;

public interface KeyedValues<K> extends Values {

    public K getKey(int index);

    public int getIndex(K key);

    public List<K> getKeys();

    public Number getValue(K key);
}
