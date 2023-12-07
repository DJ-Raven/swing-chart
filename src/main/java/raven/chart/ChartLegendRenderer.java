package raven.chart;

import javax.swing.*;
import java.awt.*;

public class ChartLegendRenderer {

    public Component getLegendComponent(Object legend, int index) {
        return new JLabel(legend.toString());
    }
}
