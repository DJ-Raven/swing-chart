package raven.chart.pie;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.chart.ChartColor;
import raven.chart.ChartUtils;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.simple.SimpleDataBarChart;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PieChart extends JPanel {

    private NumberFormat format = new DecimalFormat("#,##0.##");
    private ChartType chartType = ChartType.DEFAULT;
    private int donutSize = -1;
    private DefaultPieDataset<String> dataset = new SimpleDataBarChart();
    private int selectedIndex = -1;

    private ChartColor chartColor;

    public PieChart() {
        init();
    }

    private void init() {
        chartColor = new ChartColor();
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new MigLayout("wrap 1,fill", "fill", "[grow 0][fill][grow 0]"));
        setLayout(new BorderLayout());
        add(layeredPane);

        panelRender = new PanelRender();
        panelHeader = new JPanel(new BorderLayout());
        panelFooter = new JPanel(new BorderLayout());

        panelRender.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null;"
                + "border:10,10,10,10");
        panelHeader.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null");
        panelFooter.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null");

        labelNoData = new JLabel("Empty Data", new FlatSVGIcon("com/raven/chart/empty.svg"), JLabel.CENTER);
        labelNoData.setHorizontalTextPosition(SwingConstants.CENTER);
        labelNoData.setVerticalTextPosition(SwingConstants.BOTTOM);
        layeredPane.add(panelHeader);
        layeredPane.add(panelRender, "width 150:300,height 150:300");
        layeredPane.add(panelFooter);
        updateDataset();
    }

    public DefaultPieDataset<String> getDataset() {
        return dataset;
    }

    public void setDataset(DefaultPieDataset<String> dataset) {
        this.dataset = dataset;
        updateDataset();
    }

    private void updateDataset() {
        int count = dataset.getItemCount();
        panelRender.clear();
        if (count > 0) {
            noData(false);
            double maxValue = 0;
            for (int i = 0; i < count; i++) {
                maxValue += dataset.getValue(i).doubleValue();
            }
            for (int i = 0; i < count; i++) {
                double value = dataset.getValue(i).doubleValue();
                float percent = (float) (value / maxValue);
                panelRender.addItem(dataset.getKey(i), value, percent);
            }
        } else {
            noData(true);
        }
        panelRender.repaint();
    }

    private void updateImageRender() {
        panelRender.imageUpdated = false;
        repaint();
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

    public ChartColor getChartColor() {
        return chartColor;
    }

    public void setChartColor(ChartColor chartColor) {
        this.chartColor = chartColor;
        updateImageRender();
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        if (this.chartType != chartType) {
            this.chartType = chartType;
            updateImageRender();
        }
    }

    public int getDonutSize() {
        return donutSize;
    }

    public void setDonutSize(int donutSize) {
        if (this.donutSize != donutSize) {
            this.donutSize = donutSize;
            updateImageRender();
        }
    }

    private class PanelRender extends JPanel {

        private List<Item> items;
        private BufferedImage imageRender;
        private boolean imageUpdated;
        private int oldWidth;
        private int oldHeight;

        public PanelRender() {
            init();
        }

        private void init() {
            items = new ArrayList<>();
        }

        public void addItem(String key, double value, float percent) {
            items.add(new Item(key, value, percent));
        }

        public void clear() {
            items.clear();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Insets insets = getInsets();
            int x = insets.left;
            int y = insets.top;
            int width = getWidth() - (insets.left + insets.right);
            int height = getHeight() - (insets.top + insets.bottom);
            Graphics2D g2 = (Graphics2D) g;
            ChartUtils.registerRenderingHin(g2);
            //  Create data
            if (dataset != null) {
                if (imageUpdated == false || width != oldWidth || height != oldHeight) {
                    imageRender = createImageRender(0, 0, width, height);
                    imageUpdated = true;
                    oldWidth = width;
                    oldHeight = height;
                }
                if (imageRender != null) {
                    g2.drawImage(imageRender, x, y, null);
                }
                g2.translate(x, y);
                createSelectedIndex(g2, height);
            }
            g2.dispose();
        }

        private BufferedImage createImageRender(int x, int y, int width, int height) {
            BufferedImage buffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = buffImage.createGraphics();
            ChartUtils.registerRenderingHin(g2);
            g2.translate(x, y);
            int border = UIScale.scale(10);
            createPie(g2, width, height, border);
            // createRows(g2, width, height);
            g2.dispose();
            System.out.println("Create");
            return buffImage;
        }

        private void createPie(Graphics2D g2, int width, int height, int broder) {
            int size = Math.min(width, height) - broder;
            Area areaCut = chartType == ChartType.DEFAULT ? null : createAreaCut(width, height, size * 0.5f);
            int x = (width - size) / 2;
            int y = (height - size) / 2;
            float start = 90;
            ChartUtils.registerRenderingHinStrokePure(g2);
            g2.setStroke(new BasicStroke(UIScale.scale(1.5f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            float angles[] = new float[items.size()];
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                float angle = -(item.percent * 360f);
                Area area = new Area(new Arc2D.Double(x, y, size, size, start, angle, Arc2D.PIE));
                if (areaCut != null) {
                    area.subtract(areaCut);
                }
                g2.setColor(chartColor.getColor(i));
                g2.fill(area);
                g2.setColor(getParent().getBackground());
                g2.draw(area);
                start += angle;
                angles[i] = start - (angle / 2);
            }
            g2.setFont(getParent().getFont());
            g2.setColor(Color.decode("#FAFAFA"));
            for (int i = 0; i < angles.length; i++) {
                drawString(g2, format.format(items.get(i).percent * 100) + "%", x, y, size, (360 - angles[i]));
            }
        }

        private void drawString(Graphics2D g2, String text, int x, int y, int size, float angle) {
            int centerX = x + size / 2;
            int centerY = y + size / 2;
            float dis = (size / 2) * 0.7f;
            double lx = centerX + Math.cos(Math.toRadians(angle)) * dis;
            double ly = centerY + Math.sin(Math.toRadians(angle)) * dis;
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D rec = fm.getStringBounds(text, g2);
            lx -= rec.getWidth() / 2;
            ly -= rec.getHeight() / 2;
            g2.drawString(text, (float) lx, (float) (ly + fm.getAscent()));
        }

        private Area createAreaCut(int width, int height, float size) {
            float x = (width - size) / 2;
            float y = (height - size) / 2;
            return new Area(new Ellipse2D.Double(x, y, size, size));
        }

        private void createSelectedIndex(Graphics2D g2, int height) {

        }

        private class Item {

            public Item(String key, double value, float percent) {
                this.key = key;
                this.value = value;
                this.percent = percent;
            }

            protected String key;
            protected double value;
            protected float percent;
        }
    }

    public static enum ChartType {
        DEFAULT, DONUT_CHART
    }
}
