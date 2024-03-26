package raven.chart.blankchart;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import raven.chart.ChartLegendRenderer;
import raven.chart.data.ChartDataInfo2D;

import javax.swing.*;
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
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new MigLayout("fill", "[grow 0][fill]", "[grow 0][fill][grow 0]"));
        setLayout(new BorderLayout());
        add(layeredPane);
        panelHeader = new JPanel(new BorderLayout());
        panelRender = new PanelChartRender(this);
        panelValues = new JPanel(new MigLayout("wrap,fill,btt,insets 2 5 2 5", "trailing", "fill"));
        panelLegend = new JPanel(new MigLayout("fill,gapx 30", "fill", "fill"));

        panelValues.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null");
        panelRender.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:10,10,10,10;"
                + "background:null");
        panelHeader.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null");
        panelLegend.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:null");
        layeredPane.add(panelHeader, "cell 0 0,span 2,grow 0");
        layeredPane.add(panelValues, "cell 0 1,grow 0");
        layeredPane.add(panelRender, "cell 1 1");
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

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        if (panelRender != null)
            panelRender.setOpaque(isOpaque);
        if (panelValues != null)
            panelValues.setOpaque(isOpaque);
        if (panelLegend != null)
            panelLegend.setOpaque(isOpaque);
        if (panelHeader != null)
            panelHeader.setOpaque(isOpaque);
    }
}
