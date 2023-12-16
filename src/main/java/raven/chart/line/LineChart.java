package raven.chart.line;

import com.formdev.flatlaf.util.UIScale;
import raven.chart.ChartColor;
import raven.chart.utils.ChartAnimator;
import raven.chart.utils.ChartUtils;
import raven.chart.component.CategoryLabelPopup;
import raven.chart.data.ChartDataInfo2D;
import raven.chart.blankchart.PanelChartRender;
import raven.chart.blankchart.PlotChart;
import raven.chart.data.category.CategoryDataset;
import raven.chart.simple.SimpleDataLineChart;
import raven.chart.spline.Spline;
import raven.chart.spline.SplinePoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class LineChart extends PlotChart {

    private ChartType chartType = ChartType.CURVE;
    private CategoryDataset<String, String> categoryDataset = new SimpleDataLineChart();
    private Spline spline;
    private ChartColor chartColor;
    private Color selectedColor = new Color(150, 150, 150);
    private int selectedIndex = -1;
    private Map<Integer, Spline> mapSpline;
    private BufferedImage imageRender;
    private boolean imageUpdated;
    private int oldWidth;
    private int oldHeight;

    private ChartAnimator animator;

    public LineChart() {
        init();
    }

    private void init() {
        initAnimator();
        chartColor = new ChartColor();
        mapSpline = new HashMap<>();
        MouseAdapter mouseEvent = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = ChartUtils.getIndexOf(panelRender, categoryDataset.getColumnCount(), e.getPoint());
                if (index != -1 && index != selectedIndex) {
                    selectedIndex = index;
                    columnChanged(index);
                    panelRender.repaint();
                }
            }
        };
        panelRender.addMouseMotionListener(mouseEvent);
        chartColor.addColor(new Color(60, 155, 75), new Color(204, 66, 66));
        updateDataset();
    }

    private void initAnimator() {
        animator = new ChartAnimator() {
            @Override
            public BufferedImage createImage(BufferedImage image, float animate) {
                float width = image.getWidth() * animate;
                if (width <= 1) {
                    return null;
                }
                BufferedImage img = new BufferedImage((int) width, image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = img.createGraphics();
                g2.drawImage(image, 0, 0, null);
                g2.dispose();
                return img;
            }

            @Override
            public void animatorChanged(float animator) {
                repaint();
            }
        };
    }

    public void startAnimation() {
        animator.start();
    }

    public CategoryDataset<String, String> getCategoryDataset() {
        return categoryDataset;
    }

    public void setCategoryDataset(CategoryDataset<String, String> categoryDataset) {
        this.categoryDataset = categoryDataset;
        updateDataset();
    }

    private void updateDataset() {
        selectedIndex = -1;
        mapSpline.clear();
        initValues(categoryDataset);
        imageUpdated = false;
    }

    private void updateImageRender() {
        imageUpdated = false;
        repaint();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        imageUpdated = false;
    }

    @Override
    protected Component createPopupComponent(ChartDataInfo2D row) {
        return new CategoryLabelPopup();
    }

    @Override
    public void render(PanelChartRender chartRender, Graphics g) {
        Insets insets = chartRender.getInsets();
        int x = insets.left;
        int y = insets.top;
        int width = chartRender.getWidth() - (insets.left + insets.right);
        int height = chartRender.getHeight() - (insets.top + insets.bottom);
        Graphics2D g2 = (Graphics2D) g;
        ChartUtils.registerRenderingHin(g2);
        //  Create data
        if (categoryDataset != null) {
            if (imageUpdated == false || width != oldWidth || height != oldHeight) {
                imageRender = createImageRender(chartRender, x, y, width, height);
                imageUpdated = true;
                oldWidth = width;
                oldHeight = height;
            }
            if (imageRender != null) {
                animator.renderImage(g2, imageRender);
            }
            g2.translate(x, y);
            createSelectedIndex(g2, height);
        }
        g2.dispose();
    }

    private BufferedImage createImageRender(PanelChartRender chartRender, int x, int y, int width, int height) {
        BufferedImage buffImage = new BufferedImage(chartRender.getWidth(), chartRender.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buffImage.createGraphics();
        ChartUtils.registerRenderingHin(g2);
        g2.translate(x, y);
        createLine(g2, width, height);
        createRows(g2, width, height);
        g2.dispose();
        return buffImage;
    }

    private void columnChanged(int index) {
        Component com = popupComponents;
        CategoryLabelPopup popup = (CategoryLabelPopup) com;
        popup.setDetail(categoryDataset, categoryDataset.getColumnKey(index), valuesFormat, chartColor);
    }

    private void createRows(Graphics2D g2, int width, int height) {
        if (chartType == ChartType.CURVE) {
            ChartUtils.registerRenderingHinStrokePure(g2);
        }
        int rowCount = categoryDataset.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            g2.setColor(chartColor.getColor(i));
            createColumn(g2, i, width, height);
        }
    }

    private void createColumn(Graphics2D g2, int row, int width, int height) {
        int columnCount = categoryDataset.getColumnCount();
        double maxValue = niceScale.getMaxValue();

        double space = width / (double) columnCount;
        SplinePoint point[] = new SplinePoint[columnCount];
        boolean ltr = getComponentOrientation().isLeftToRight();
        int index = ltr ? -1 : columnCount;
        for (int i = 0; i < columnCount; i++) {
            if (ltr) {
                index++;
            } else {
                index--;
            }
            double value = categoryDataset.getValue(row, index).doubleValue();
            double x = (space * i) + (space / 2);
            double y = height - ((value / maxValue) * height);
            point[i] = new SplinePoint(x, y);
        }
        g2.setStroke(new BasicStroke(UIScale.scale(1.5f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        if (mapSpline.containsKey(row)) {
            mapSpline.get(row).render(g2, chartType, point);
        } else {
            spline = new Spline();
            spline.render(g2, chartType, point);
            mapSpline.put(row, spline);
        }
        g2.setStroke(new BasicStroke(1));
    }

    private void createLine(Graphics2D g2, int width, int height) {
        double lineCount = niceScale.getMaxTicks();
        double space = height / lineCount;
        g2.setColor(UIManager.getColor("Component.borderColor"));
        float dash[] = {UIScale.scale(3f)};
        float f = UIScale.scale(1f);
        for (int i = 0; i <= lineCount; i++) {
            double y = space * i;
            g2.setStroke(new BasicStroke(f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, dash, 2f));
            g2.draw(new Line2D.Double(0, y, width, y));
        }
    }

    private void createSelectedIndex(Graphics2D g2, int height) {
        if (selectedIndex >= 0) {
            int index = getComponentOrientation().isLeftToRight() ? selectedIndex : (categoryDataset.getColumnCount() - 1 - selectedIndex);
            if (mapSpline.containsKey(0)) {
                g2.setColor(UIManager.getColor("Component.borderColor"));
                float size = UIScale.scale(1.5f);
                SplinePoint point = mapSpline.get(0).getSpline(chartType, index);
                double x = point.getX() - size / 2;
                double y = 0;
                g2.fill(new RoundRectangle2D.Double(x, y, size, height, size, size));
            }

            int rows = categoryDataset.getRowCount();
            float s = UIScale.scale(15f);
            float s1 = UIScale.scale(8f);
            List<SplinePoint> points = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                if (mapSpline.containsKey(i)) {
                    SplinePoint point = mapSpline.get(i).getSpline(chartType, index);
                    g2.setColor(chartColor.getColor(i));
                    g2.setComposite(AlphaComposite.SrcOver.derive(.5f));
                    g2.fill(new Ellipse2D.Double(point.getX() - s / 2, point.getY() - s / 2, s, s));
                    g2.setComposite(AlphaComposite.SrcOver);
                    g2.fill(new Ellipse2D.Double(point.getX() - s1 / 2, point.getY() - s1 / 2, s1, s1));
                    points.add(point);
                }
            }
            createPopupLabel(points);
        }
    }

    private void createPopupLabel(List<SplinePoint> points) {
        if (!points.isEmpty()) {
            SplinePoint p = points.get(0);
            Component com = popupComponents;
            int cw = com.getPreferredSize().width;
            int ch = com.getPreferredSize().height;
            int x = (int) (panelRender.getX() + p.getX()) - cw / 2;
            int y = (int) (panelRender.getY() + p.getY()) - ch;
            x = Math.max(x, panelRender.getX());
            x = Math.min(layeredPane.getWidth() - cw, x);
            y = Math.max(y, 0);
            com.setBounds(x, y, cw, ch);
        }
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
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

    public static enum ChartType {
        LINE, CURVE
    }
}
