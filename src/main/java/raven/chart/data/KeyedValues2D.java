package raven.chart.data;

import java.util.List;

public interface KeyedValues2D<R, C> extends Values2D {

    public R getRowKey(int row);

    public int getRowIndex(R key);

    public List<R> getRowKeys();

    public C getColumnKey(int column);

    public int getColumnIndex(C key);

    public List<C> getColumnKeys();

    public Number getValue(R rowKey, C columnKey);
}
