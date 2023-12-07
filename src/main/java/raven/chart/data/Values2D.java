package raven.chart.data;

public interface Values2D {

    public int getRowCount();

    public int getColumnCount();

    public Number getValue(int row, int column);
}
