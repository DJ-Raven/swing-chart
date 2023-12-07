package raven.chart.component;

import com.formdev.flatlaf.icons.FlatAbstractIcon;

import java.awt.*;

public class ColorIcon extends FlatAbstractIcon {

    public ColorIcon(Color color) {
        super(14, 14, color);
    }

    @Override
    protected void paintIcon(Component com, Graphics2D g) {
        g.setColor(color);
        g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5);
    }
}
