package raven.chart.bar;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.simple.SimpleDataBarChart;
import raven.chart.utils.ChartAnimator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class HorizontalBarChart extends JPanel {

    protected NumberFormat valuesFormat = new DecimalFormat("$ #,##0.00");
    private DefaultPieDataset<String> dataset = new SimpleDataBarChart();
    private Color barColor = new Color(40, 139, 78);
    protected ChartAnimator animator;

    public HorizontalBarChart() {
        init();
    }

    private void init() {
        initAnimator();
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new MigLayout("wrap 1,fill", "fill", "[grow 0][fill][grow 0]"));
        setLayout(new BorderLayout());
        add(layeredPane);

        panelRender = new PanelRender();
        panelHeader = new JPanel(new BorderLayout());
        panelFooter = new JPanel(new BorderLayout());

        panelRender.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null");
        panelHeader.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null");
        panelFooter.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null");

        labelNoData = new JLabel("Empty Data", new FlatSVGIcon("com/raven/chart/empty.svg"), JLabel.CENTER);
        labelNoData.setHorizontalTextPosition(SwingConstants.CENTER);
        labelNoData.setVerticalTextPosition(SwingConstants.BOTTOM);
        layeredPane.add(panelHeader);
        layeredPane.add(panelRender);
        layeredPane.add(panelFooter);
        updateDataset();
    }

    private void initAnimator() {
        animator = new ChartAnimator() {
            @Override
            public BufferedImage createImage(BufferedImage image, float animate) {
                return null;
            }

            @Override
            public void animatorChanged(float animator) {
                repaint();
            }
        };
    }

    public DefaultPieDataset<String> getDataset() {
        return dataset;
    }

    public void setDataset(DefaultPieDataset<String> dataset) {
        this.dataset = dataset;
        updateDataset();
    }

    public void startAnimation() {
        animator.start();
    }

    private void updateDataset() {
        panelRender.removeAll();
        panelRender.revalidate();
        int count = dataset.getItemCount();
        if (count > 0) {
            noData(false);
            double maxValue = dataset.getMaxValues().doubleValue();
            for (int i = 0; i < count; i++) {
                double value = dataset.getValue(i).doubleValue();
                float percent = (float) (value / maxValue);
                panelRender.addItem(dataset.getKey(i), value, percent);
            }
        } else {
            noData(true);
        }
        panelRender.repaint();
        panelRender.revalidate();
    }

    private void noData(boolean noData) {
        if (noData) {
            layeredPane.remove(labelNoData);
            layeredPane.add(labelNoData, 0);
        } else {
            layeredPane.remove(labelNoData);
        }
    }

    protected JLayeredPane layeredPane;
    private PanelRender panelRender;
    private JPanel panelHeader;
    private JPanel panelFooter;
    private JLabel labelNoData;

    public void setHeader(Component component) {
        panelHeader.removeAll();
        panelHeader.add(component);
        panelHeader.revalidate();
        panelHeader.repaint();
    }

    public void setFooter(Component component) {
        panelFooter.removeAll();
        panelFooter.add(component);
        panelFooter.revalidate();
        panelFooter.repaint();
    }

    public Color getBarColor() {
        return barColor;
    }

    public void setBarColor(Color barColor) {
        this.barColor = barColor;
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        if (panelRender != null)
            panelRender.setOpaque(isOpaque);
        if (panelHeader != null)
            panelHeader.setOpaque(isOpaque);
        if (panelFooter != null)
            panelFooter.setOpaque(isOpaque);
    }

    public NumberFormat getValuesFormat() {
        return valuesFormat;
    }

    public void setValuesFormat(NumberFormat valuesFormat) {
        this.valuesFormat = valuesFormat;
        if (dataset != null) {
            updateDataset();
        }
    }

    private class PanelRender extends JPanel {

        private List<Item> items;

        public PanelRender() {
            init();
        }

        private void init() {
            items = new ArrayList<>();
            setLayout(new MigLayout("wrap 3,fill", "[grow 0][fill,100::400]50[grow 0,trailing]"));
        }

        public void addItem(String key, double value, float Percent) {
            Item item = new Item(new JLabel(key), new LabelBar(HorizontalBarChart.this, LabelBar.LabelType.HORIZONTAL, Percent), new JLabel(valuesFormat.format(value)));
            items.add(item);
            add(item.label);
            add(item.bar, "height 8");
            add(item.value);
        }

        @Override
        public void removeAll() {
            super.removeAll();
            items.clear();
        }

        private class Item {

            public Item(JLabel label, LabelBar bar, JLabel value) {
                this.label = label;
                this.bar = bar;
                this.value = value;
            }

            protected JLabel label;
            protected LabelBar bar;
            protected JLabel value;
        }
    }
}
