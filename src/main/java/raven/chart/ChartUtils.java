package raven.chart;

import javax.swing.*;
import java.awt.*;

public class ChartUtils {

    public static void registerRenderingHin(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    public static void registerRenderingHinStrokePure(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    public static int getIndexOf(JComponent component, int column, Point point) {
        Insets insets = component.getInsets();
        int width = component.getWidth() - (insets.left + insets.right);
        int height = component.getHeight() - (insets.top + insets.bottom);
        double space = width / (double) column;
        int index = -1;
        for (int i = 0; i < column; i++) {
            double x = insets.left + space * i;
            double x1 = x + space;
            int y = insets.top;
            int y1 = insets.top + height;
            if (point.x >= x && point.x <= x1 && point.y >= y && point.y <= y1) {
                index = i;
                break;
            }
        }
        return index;
    }
}
