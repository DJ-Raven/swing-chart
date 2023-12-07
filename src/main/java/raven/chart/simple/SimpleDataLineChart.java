package raven.chart.simple;

import raven.chart.data.category.DefaultCategoryDataset;

public class SimpleDataLineChart extends DefaultCategoryDataset<String, String> {

    public SimpleDataLineChart() {
        init();
    }

    private void init() {
        addValue(400, "Income", "Jun 7, 2023");
        addValue(250, "Income", "Jun 8, 2023");
        addValue(500, "Income", "Jun 9, 2023");
        addValue(300, "Income", "Jun 10, 2023");
        addValue(1000, "Income", "Jun 11, 2023");
        addValue(650, "Income", "Jun 12, 2023");
        addValue(410, "Income", "Jun 13, 2023");

        addValue(50, "Expense", "Jun 8, 2023");
        addValue(80, "Expense", "Jun 9, 2023");
        addValue(400, "Expense", "Jun 10, 2023");
        addValue(200, "Expense", "Jun 11, 2023");
    }
}
