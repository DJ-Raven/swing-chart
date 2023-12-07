package raven.chart.blankchart;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.chart.ChartLegendRenderer;
import raven.chart.data.ChartDataInfo2D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public abstract class PlotChart extends JPanel implements ChartRender {

    protected NumberFormat valuesFormat = new DecimalFormat("$ #,##0.##");
    protected NiceScale niceScale;

    private ChartLegendRenderer legendRenderer = new ChartLegendRenderer();

    public PlotChart() {
        init();
    }

    private void init() {
        setBorder(new EmptyBorder(15, 15, 15, 15));
        putClientProperty(FlatClientProperties.STYLE, ""
                + "border:15,15,15,15;"
                + "arc:10;"
                + "background:$Chart.background");
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new MigLayout("debug,fill", "[fill]", "[fill]"));
        setLayout(new BorderLayout());
        add(layeredPane);
        panelHeader = new JPanel(new BorderLayout());
        panelRender = new PanelChartRender(this);
        panelValues = new JPanel(new MigLayout("wrap,fill", "fill", "fill"));
        panelLegend = new JPanel(new MigLayout("fill,gapx 30", "fill", "fill"));
        panelValues.setBorder(new EmptyBorder(10, 0, 10, 0));
        panelRender.setBorder(new EmptyBorder(10, 0, 10, 0));
        panelValues.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:10,0,10,0;"
                + "background:$Chart.background");
        panelRender.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:10,10,10,10;"
                + "background:$Chart.background");
        panelHeader.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Chart.background");
        panelLegend.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Chart.background");
        layeredPane.add(panelHeader, "cell 0 0,span 2,grow 0");
        layeredPane.add(panelValues, "cell 0 1,grow 0");
        layeredPane.add(panelRender,"cell 1 1");
        layeredPane.add(panelLegend, "cell 1 2");
    }

    protected final void initValues(ChartDataInfo2D data) {
        double minValue = data.getMinValue().doubleValue();
        double maxValue = data.getMaxValues().doubleValue();
        if (niceScale == null) {
            niceScale = new NiceScale(minValue, maxValue);
        } else {
            niceScale.setMinMax(minValue, maxValue);
        }
        initPanelValues();
        initLegend(data);
        initPopupComponent(data);
        repaint();
        revalidate();
    }

    private void initPanelValues() {
        panelValues.removeAll();
        int maxTicks = niceScale.getMaxTicks();
        double values = niceScale.getNiceMin();
        for (int i = 0; i <= maxTicks; i++) {
            String text = valuesFormat.format(values);
            panelValues.add(new JLabel(text));
            values += niceScale.getTickSpacing();
        }
        panelValues.revalidate();
    }

    private void initLegend(ChartDataInfo2D data) {
        panelLegend.removeAll();
        int index = -1;
        for (Object legend : data.getColumnKeys()) {
            Component com = createLegend(legend, ++index);
            if (com != null) {
                panelLegend.add(com);
            }
        }
        panelLegend.repaint();
    }

    private void initPopupComponent(ChartDataInfo2D data) {
        if (popupComponents != null) {
            layeredPane.remove(popupComponents);
        }
        popupComponents = createPopupComponent(data);
        layeredPane.setLayer(popupComponents, JLayeredPane.POPUP_LAYER);
        layeredPane.add(popupComponents, "pos 0 0", 0);
    }

    protected Component createLegend(Object data, int index) {
        if (legendRenderer != null) {
            return legendRenderer.getLegendComponent(data, index);
        }
        return null;
    }

    protected Component createPopupComponent(ChartDataInfo2D row) {
        return new JLabel(row.toString());
    }

    protected void plotChartDoLayout() {
    }

    public ChartLegendRenderer getLegendRenderer() {
        return legendRenderer;
    }

    public void setLegendRenderer(ChartLegendRenderer legendRenderer) {
        this.legendRenderer = legendRenderer;
    }

    protected PanelChartRender panelRender;
    protected JLayeredPane layeredPane;
    protected JPanel panelValues;
    protected JPanel panelLegend;
    protected JPanel panelHeader;
    protected Component popupComponents;

    public void setHeader(Component component) {
        panelHeader.removeAll();
        panelHeader.add(component);
        panelHeader.revalidate();
        panelHeader.repaint();
    }
}
