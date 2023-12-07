package raven.chart.data.category;

import raven.chart.data.DefaultKeyedValues2D;

import java.util.List;

public class DefaultCategoryDataset<R, C> implements CategoryDataset<R, C> {

    private final DefaultKeyedValues2D<R, C> data;

    public DefaultCategoryDataset() {
        data = new DefaultKeyedValues2D<>();
    }

    @Override
    public int getRowCount() {
        return data.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return data.getColumnCount();
    }

    @Override
    public R getRowKey(int row) {
        return data.getRowKey(row);
    }

    @Override
    public int getRowIndex(R key) {
        return data.getRowIndex(key);
    }

    @Override
    public List<R> getRowKeys() {
        return data.getRowKeys();
    }

    @Override
    public C getColumnKey(int column) {
        return data.getColumnKey(column);
    }

    @Override
    public int getColumnIndex(C key) {
        return data.getColumnIndex(key);
    }

    @Override
    public List<C> getColumnKeys() {
        return data.getColumnKeys();
    }

    @Override
    public Number getValue(int row, int column) {
        return data.getValue(row, column);
    }

    @Override
    public Number getValue(R rowKey, C columnKey) {
        return data.getValue(rowKey, columnKey);
    }

    public void addValue(Number value, R rowKey, C columnKey) {
        data.addValue(value, rowKey, columnKey);
    }

    public void setValue(Number value, R rowKey, C columnKey) {
        data.setValue(value, rowKey, columnKey);
    }

    @Override
    public Number getMinValue() {
        return 0;
    }

    @Override
    public Number getMaxValues() {
        return data.getMaxValue();
    }
}
