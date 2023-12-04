package raven.chart.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.chart.ChartColor;
import raven.chart.ChartUtils;
import raven.chart.data.category.CategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CategoryLabelPopup extends JPanel {

    public CategoryLabelPopup() {
        init();
    }

    private void init() {
        setOpaque(false);
        setLayout(new MigLayout("wrap,insets 8,gap 2,fillx", "fill"));
        putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20");
        details = new ArrayList<>();
        labelName = new JLabel();
        labelName.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:bold");
        add(labelName);
    }

    public void setDetail(CategoryDataset data, String columnKey, NumberFormat format, ChartColor chartColor) {
        for (Component detail : details) {
            remove(detail);
        }
        details.clear();
        int i = 0;
        for (Object row : data.getRowKeys()) {
            Component com = createComponent(i++, row, format.format(data.getValue(row, columnKey).doubleValue()), chartColor);
            details.add(com);
            add(com);
        }
        labelName.setText(columnKey);
        revalidate();
    }

    private Component createComponent(int rowIndex, Object row, String value, ChartColor chartColor) {
        JPanel panel = new JPanel(new MigLayout("insets 1,fill", "[]push[]"));
        panel.setOpaque(false);
        JLabel labelRow = new JLabel(row.toString());
        labelRow.setIcon(new ColorIcon(chartColor.getColor(rowIndex)));
        JLabel labelValue = new JLabel(value, JLabel.RIGHT);
        labelRow.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:-1");
        labelValue.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:0,30,0,0;"
                + "font:-1");
        panel.add(labelRow);
        panel.add(labelValue);
        panel.applyComponentOrientation(getComponentOrientation());
        return panel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (getComponentCount() > 1) {
            Graphics2D g2 = (Graphics2D) g.create();
            ChartUtils.registerRenderingHin(g2);
            g2.setColor(getColor());
            g2.setComposite(AlphaComposite.SrcOver.derive(0.8f));
            int arc = UIScale.scale(15);
            FlatUIUtils.paintComponentBackground(g2, 0, 0, getWidth(), getHeight(), 0, arc);
            g2.dispose();
        }
        super.paintComponent(g);
    }

    private Color getColor() {
        Color color = FlatUIUtils.getUIColor("Chart.background", getBackground());
        if (FlatLaf.isLafDark()) {
            return ColorFunctions.lighten(color, 0.05f);
        } else {
            return ColorFunctions.darken(color, 0.05f);
        }
    }

    private JLabel labelName;
    private List<Component> details;
}
