package raven.chart.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
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
        setLayout(new LabelLayout());
        putClientProperty(FlatClientProperties.STYLE, ""
                + "border:10,10,10,10;"
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel labelRow = new JLabel(row.toString());
        labelRow.setIcon(new ColorIcon(chartColor.getColor(rowIndex)));
        JLabel labelValue = new JLabel(value, JLabel.RIGHT);
        labelRow.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:-1");
        labelValue.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:0,30,0,0;"
                + "font:-1");
        panel.add(labelRow, BorderLayout.CENTER);
        panel.add(labelValue, BorderLayout.LINE_END);
        return panel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ChartUtils.registerRenderingHin(g2);
        g2.setColor(getBackground());
        g2.setComposite(AlphaComposite.SrcOver.derive(0.8f));
        FlatUIUtils.paintComponentBackground(g2, 0, 0, getWidth(), getHeight(), 0, 10);
        g2.dispose();
        super.paintComponent(g);
    }

    private JLabel labelName;
    private List<Component> details;

    private class LabelLayout implements LayoutManager {

        private final int gap = 1;

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int g = UIScale.scale(gap);
                int width = insets.left + insets.right + getMaxWidth();
                int height = insets.top + insets.bottom + labelName.getPreferredSize().height;
                for (Component com : details) {
                    height += com.getPreferredSize().height;
                }
                if (details.size() > 1) {
                    height += (g * details.size());
                }
                return new Dimension(width, height);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.right;
                int g = UIScale.scale(gap);
                labelName.setBounds(x, y, labelName.getPreferredSize().width, labelName.getPreferredSize().height);
                int size = details.size();
                if (size > 0) {
                    int maxWidth = getMaxWidth();
                    y += labelName.getPreferredSize().height + g;
                    for (int i = 0; i < size; i++) {
                        Component com = details.get(i);
                        com.setBounds(x, y, maxWidth, com.getPreferredSize().height);
                        y += com.getPreferredSize().height + g;
                    }
                }
            }
        }

        private int getMaxWidth() {
            int max = labelName.getPreferredSize().width;
            for (Component com : details) {
                max = Math.max(max, com.getPreferredSize().width);
            }
            return max;
        }
    }
}
