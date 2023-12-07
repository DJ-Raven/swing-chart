package raven.chart.blankchart;

import javax.swing.*;
import java.awt.*;

public class PanelChartRender extends JPanel {

    private final ChartRender chartRender;

    public PanelChartRender(ChartRender chartRender) {
        this.chartRender = chartRender;
        init();
    }

    private void init() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        chartRender.render(this, g.create());
    }
}
