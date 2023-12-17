package raven.chart.bar;

import com.formdev.flatlaf.util.ColorFunctions;
import raven.chart.utils.ChartUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LabelBar extends JLabel {

    private final HorizontalBarChart barChart;
    private final LabelType type;
    private final float percent;

    public LabelBar(HorizontalBarChart barChart, LabelType type, float percent) {
        this.barChart = barChart;
        this.type = type;
        this.percent = percent;
    }

    private Color getBarChartColor() {
        return barChart.getBarColor();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ChartUtils.registerRenderingHin(g2);
        Shape shape = initShape(g2);
        g2.fill(shape);
        g2.dispose();
        super.paintComponent(g);
    }

    private Shape initShape(Graphics2D g2) {
        int width = getWidth();
        int height = getHeight();
        int arc = Math.min(width, height);
        float animate = barChart.animator.isRunning() ? barChart.animator.getAnimate() : 1f;
        Color gradientColor = ColorFunctions.lighten(getBarChartColor(), 0.05f);
        Shape shape;
        if (getComponentOrientation().isLeftToRight()) {
            if (type == LabelType.HORIZONTAL) {
                shape = new RoundRectangle2D.Double(0, 0, (width * percent) * animate, height, arc, arc);
                g2.setPaint(new GradientPaint(0, 0, getBarChartColor(), width, 0, gradientColor));
            } else {
                double v = height * percent;
                shape = new RoundRectangle2D.Double(0, height - v, width, v, arc, arc);
                g2.setPaint(new GradientPaint(0, height, getBarChartColor(), 0, 0, gradientColor));
            }
        } else {
            if (type == LabelType.HORIZONTAL) {
                float size = (width * percent) * animate;
                float x = width - size;
                shape = new RoundRectangle2D.Double(x, 0, size, height, arc, arc);
                g2.setPaint(new GradientPaint(0, 0, gradientColor, width, 0, getBarChartColor()));
            } else {
                double v = height * percent;
                shape = new RoundRectangle2D.Double(0, height - v, width, v, arc, arc);
                g2.setPaint(new GradientPaint(0, height, gradientColor, 0, 0, getBarChartColor()));
            }
        }
        return shape;
    }

    public static enum LabelType {
        HORIZONTAL, VERTICAL
    }
}
