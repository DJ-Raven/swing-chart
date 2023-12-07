package raven.chart.data;

import java.util.ArrayList;
import java.util.List;

public class DefaultKeyedValues2D<R, C> {

    private final List<R> rowKeys;
    private final List<C> columnKeys;

    private final List<DefaultKeyedValues<C>> rows;

    public DefaultKeyedValues2D() {
        rowKeys = new ArrayList<>();
        columnKeys = new ArrayList<>();
        rows = new ArrayList<>();
    }

    public void addValue(Number value, R rowKey, C columnKey) {
        setValue(value, rowKey, columnKey);
    }

    public void setValue(Number value, R rowKey, C columnKey) {
        DefaultKeyedValues row;
        int rowIndex = this.rowKeys.indexOf(rowKey);
        if (rowIndex >= 0) {
            row = this.rows.get(rowIndex);
        } else {
            row = new DefaultKeyedValues();
            this.rowKeys.add(rowKey);
            this.rows.add(row);
        }
        row.setValue(columnKey, value);
        int columnIndex = this.columnKeys.indexOf(columnKey);
        if (columnIndex < 0) {
            this.columnKeys.add(columnKey);
        }
    }

    public int getRowCount() {
        return rowKeys.size();
    }

    public int getColumnCount() {
        return columnKeys.size();
    }

    public R getRowKey(int row) {
        return rowKeys.get(row);
    }

    public int getRowIndex(R key) {
        return rowKeys.indexOf(key);
    }

    public List<R> getRowKeys() {
        return rowKeys;
    }

    public C getColumnKey(int column) {
        return columnKeys.get(column);
    }

    public int getColumnIndex(C key) {
        return columnKeys.indexOf(key);
    }

    public List<C> getColumnKeys() {
        return columnKeys;
    }

    public Number getValue(int row, int column) {
        Number result = null;
        DefaultKeyedValues<C> rowData = rows.get(row);
        if (rowData != null) {
            C columnKey = columnKeys.get(column);
            result = rowData.getValue(columnKey);
        }
        return result;
    }

    public Number getValue(R rowKey, C columnKey) {
        int rowIndex = getRowIndex(rowKey);
        int columnIndex = getColumnIndex(columnKey);
        return getValue(rowIndex, columnIndex);
    }

    public Number getMinValue() {
        return 0;
    }

    public Number getMaxValue(int row) {
        Number max = 0;
        DefaultKeyedValues<C> rowData = rows.get(row);
        if (rowData != null) {
            max = rowData.getMaxValue();
        }
        return max;
    }

    public Number getMaxValue(R rowKey) {
        return getMaxValue(getRowIndex(rowKey));
    }

    public Number getMaxValue() {
        double max = 0;
        for (int i = 0; i < rows.size(); i++) {
            max = Math.max(max, getMaxValue(i).doubleValue());
        }
        return max;
    }
}
