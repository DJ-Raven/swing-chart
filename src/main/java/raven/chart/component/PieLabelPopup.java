package raven.chart.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.MigLayout;
import raven.chart.utils.ChartUtils;

import javax.swing.*;
import java.awt.*;

public class PieLabelPopup extends JPanel {

    public PieLabelPopup() {
        init();
    }

    private void init() {
        setOpaque(false);
        setLayout(new MigLayout("wrap,insets 8,gap 2,fillx", "fill"));
        putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20");
        labelTitle = new JLabel();
        labelValue = new JLabel();
        labelValue.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +1");
        add(labelTitle);
        add(labelValue);
    }

    public void setValue(String title, String value) {
        labelTitle.setText(title);
        labelValue.setText(value);
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ChartUtils.registerRenderingHin(g2);
        g2.setColor(getColor());
        g2.setComposite(AlphaComposite.SrcOver.derive(0.8f));
        int arc = UIScale.scale(15);
        FlatUIUtils.paintComponentBackground(g2, 0, 0, getWidth(), getHeight(), 0, arc);
        g2.dispose();
        super.paintComponent(g);
    }

    private Color getColor() {
        Color color = getBackground();
        if (FlatLaf.isLafDark()) {
            return ColorFunctions.lighten(color, 0.05f);
        } else {
            return ColorFunctions.darken(color, 0.05f);
        }
    }

    private JLabel labelTitle;
    private JLabel labelValue;
}
