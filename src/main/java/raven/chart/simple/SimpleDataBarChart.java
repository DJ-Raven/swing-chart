package raven.chart.simple;

import raven.chart.data.pie.DefaultPieDataset;

public class SimpleDataBarChart extends DefaultPieDataset<String> {

    public SimpleDataBarChart() {
        init();
    }

    private void init() {
        addValue("July (ongoing)", 4.5);
        addValue("June", 59.64);
        addValue("May", 76.79);
        addValue("April", 63.60);
        addValue("March", 57.61);
        addValue("February", 46.25);
    }
}
