package raven.chart;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ChartColor {

    private final Map<Integer, Color> randomColor = new HashMap<>();
    private final List<Color> colors = new ArrayList<>();

    public void addNewColor(Color... colors) {
        this.colors.clear();
        randomColor.clear();
        addColor(colors);
    }

    public void addColor(Color... colors) {
        for (Color color : colors) {
            this.colors.add(color);
        }
    }

    public Color getColor(int index) {
        if (index >= 0 && index < colors.size()) {
            return colors.get(index);
        } else {
            if (randomColor.containsKey(index)) {
                return randomColor.get(index);
            } else {
                Color color = getRandomColor();
                randomColor.put(index, color);
                return color;
            }
        }
    }

    private Color getRandomColor() {
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat() / 2f;
        float b = rand.nextFloat() / 2f;
        return new Color(r, g, b);
    }
}
