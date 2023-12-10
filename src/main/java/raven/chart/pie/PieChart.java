package raven.chart.pie;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.chart.ChartColor;
import raven.chart.ChartUtils;
import raven.chart.component.ColorIcon;
import raven.chart.component.PieLabelPopup;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.simple.SimpleDataBarChart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private int selectedBorderSize = 7;
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
        panelLegend = new JPanel(new MigLayout("fillx,wrap,al center center", "fill"));
        labelNoData = new JLabel("Empty Data", new FlatSVGIcon("com/raven/chart/empty.svg"), JLabel.CENTER);
        labelNoData.setHorizontalTextPosition(SwingConstants.CENTER);
        labelNoData.setVerticalTextPosition(SwingConstants.BOTTOM);
        layeredPane.add(panelHeader);
        layeredPane.add(panelRender, "width 150:250,height 150:250,split 2");
        layeredPane.add(panelLegend);
        layeredPane.add(panelFooter);
        initPopupComponent();
        updateDataset();
    }

    public void setSelectedIndex(int selectedIndex) {
        if (this.selectedIndex != selectedIndex) {
            this.selectedIndex = selectedIndex;
            if (selectedIndex >= 0 && selectedIndex < dataset.getItemCount()) {
                String title = dataset.getKey(selectedIndex);
                String value = dataset.getValue(selectedIndex) + " (" + format.format(getPercent(selectedIndex) * 100) + "%)";
                popupComponents.setValue(title, value);
                popupComponents.setVisible(true);
            } else {
                popupComponents.setVisible(false);
            }
            repaint();
        }
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
        panelLegend.removeAll();
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
                panelLegend.add(createLegend(i));
            }
        } else {
            noData(true);
        }
        panelLegend.revalidate();
        repaint();
    }

    private float getPercent(int index) {
        return panelRender.items.get(index).percent;
    }

    private Component createLegend(int index) {
        JLabel label = new JLabel(dataset.getKey(index));
        label.setIcon(new ColorIcon(chartColor.getColor(index)));
        return label;
    }

    private void initPopupComponent() {
        if (popupComponents != null) {
            layeredPane.remove(popupComponents);
        }
        popupComponents = new PieLabelPopup();
        popupComponents.setVisible(false);
        layeredPane.setLayer(popupComponents, JLayeredPane.POPUP_LAYER);
        layeredPane.add(popupComponents, "pos 0 0", 0);
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
    private JPanel panelLegend;
    protected PieLabelPopup popupComponents;
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

        protected List<Item> items;
        private BufferedImage imageRender;
        private boolean imageUpdated;
        private int oldWidth;
        private int oldHeight;

        public PanelRender() {
            init();
        }

        private void init() {
            items = new ArrayList<>();
            createMouseEvent();
        }

        private void createMouseEvent() {
            MouseAdapter mouseEvent = new MouseAdapter() {

                @Override
                public void mouseExited(MouseEvent e) {
                    if (selectedIndex != -1) {
                        setSelectedIndex(-1);
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    int index = getSelectedIndex(e.getPoint());
                    setSelectedIndex(index);
                }
            };
            addMouseListener(mouseEvent);
            addMouseMotionListener(mouseEvent);
        }

        private int getSelectedIndex(Point point) {
            float angle = getAngleOf(point);
            float start = 0;
            int index = -1;
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                float ag = (item.percent * 360f);
                if (angle >= (start) && angle <= (start + ag)) {
                    index = i;
                    break;
                }
                start += ag;
            }
            return index;
        }

        private float getAngleOf(Point point) {
            Insets insets = getInsets();
            int width = getWidth() - (insets.left + insets.right);
            int height = getHeight() - (insets.top + insets.bottom);
            float centerX = insets.left + width / 2;
            float centerY = insets.top + height / 2;
            float x = point.x - centerX;
            float y = point.y - centerY;
            double angle = Math.toDegrees(Math.atan2(y, x)) + 90;
            if (angle < 0) {
                angle += 360;
            }
            return (float) angle;
        }

        public void addItem(String key, double value, float percent) {
            items.add(new Item(key, value, percent));
        }

        public void clear() {
            items.clear();
        }

        @Override
        public void updateUI() {
            super.updateUI();
            imageUpdated = false;
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
                createSelectedIndex(g2, width, height);
            }
            g2.dispose();
        }

        private BufferedImage createImageRender(int x, int y, int width, int height) {
            BufferedImage buffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = buffImage.createGraphics();
            ChartUtils.registerRenderingHin(g2);
            g2.translate(x, y);
            int border = UIScale.scale(selectedBorderSize);
            createPie(g2, width, height, border);
            g2.dispose();
            return buffImage;
        }

        private void createPie(Graphics2D g2, int width, int height, int broder) {
            int size = Math.min(width, height) - (broder * 2);
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

        private void createSelectedIndex(Graphics2D g2, int width, int height) {
            if (selectedIndex != -1 && selectedIndex < items.size()) {
                int size = Math.min(width, height);
                int x = (width - size) / 2;
                int y = (height - size) / 2;
                float start = 90;
                for (int i = 0; i < items.size(); i++) {
                    Item item = items.get(i);
                    float angle = -(item.percent * 360f);
                    if (i == selectedIndex) {
                        float stroke = UIScale.scale(1.5f / 2f);
                        Area area = new Area(new Arc2D.Double(x, y, size, size, start - stroke / 2f, angle + stroke, Arc2D.PIE));
                        if (selectedBorderSize > 0) {
                            double border = UIScale.scale(selectedBorderSize) - stroke;
                            double s = size - (border * 2);
                            area.subtract(new Area(new Ellipse2D.Double(x + border, y + border, s, s)));
                        }
                        g2.setColor(chartColor.getColor(i));
                        g2.setComposite(AlphaComposite.SrcOver.derive(0.5f));
                        g2.fill(area);
                        break;
                    }
                    start += angle;
                }
                createPopupLabel();
            }
        }

        private void createPopupLabel() {
            // This code need update it is beta :)
            Component com = popupComponents;
            int cw = com.getPreferredSize().width;
            int ch = com.getPreferredSize().height;
            Insets insets = getInsets();
            int x = insets.left;
            int y = insets.top;
            int width = getWidth() - (insets.left + insets.right);
            int height = getHeight() - (insets.top + insets.bottom);
            int size = Math.min(width, height) / 2;
            int centerX = getX() + x + size;
            int centerY = getY() + y + size;
            float angle = getAngleOfIndex(selectedIndex);

            int lx = (int) (centerX + Math.cos(Math.toRadians(angle)) * (size));
            int ly = (int) (centerY + Math.sin(Math.toRadians(angle)) * (size));
            lx -= cw / 2;
            ly -= ch / 2;
            lx = Math.min(Math.max(lx, 0), panelRender.getWidth());
            ly = Math.min(Math.max(ly, 0), panelRender.getHeight());
            com.setBounds(lx, ly, cw, ch);
        }

        private float getAngleOfIndex(int index) {
            float start = -90;
            float angle = 0;
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                float g = (item.percent * 360f);
                if (i == index) {
                    angle = start + g / 2f;
                    break;
                }
                start += g;
            }
            return angle;
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
