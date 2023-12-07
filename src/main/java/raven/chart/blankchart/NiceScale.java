package raven.chart.blankchart;

public class NiceScale {

    private double min;
    private double max;
    private int maxTicks = 5;
    private double tickSpacing;
    private double range;
    private double niceMin;
    private double niceMax;

    public NiceScale(double min, double max) {
        this.min = min;
        this.max = checkMax(max);
        calculate();
    }

    private double checkMax(double max) {
        return Math.max(max, 100);
    }

    private void calculate() {
        range = niceNum(max - min, false);
        tickSpacing = niceNum(range / (maxTicks - 1), true);
        niceMin = Math.floor(min / tickSpacing) * tickSpacing;
        niceMax = Math.ceil(max / tickSpacing) * tickSpacing;
    }

    private double niceNum(double range, boolean round) {
        double exponent;     // exponent of RANGE
        double fraction;     // fractional part of RANGE
        double niceFraction; // nice, rounded fraction

        exponent = Math.floor(Math.log10(range));
        fraction = range / Math.pow(10, exponent);

        if (round) {
            if (fraction < 1.5) {
                niceFraction = 1;
            } else if (fraction < 3) {
                niceFraction = 2;
            } else if (fraction < 7) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (fraction <= 1) {
                niceFraction = 1;
            } else if (fraction <= 2) {
                niceFraction = 2;
            } else if (fraction <= 5) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }
        return niceFraction * Math.pow(10, exponent);
    }

    public void setMinMax(double min, double max) {
        this.min = min;
        this.max = checkMax(max);
        calculate();
    }

    public void setMaxTicks(int maxTicks) {
        this.maxTicks = maxTicks;
        calculate();
    }

    public double getTickSpacing() {
        return tickSpacing;
    }

    public double getNiceMin() {
        return niceMin;
    }

    public double getNiceMax() {
        return niceMax;
    }

    public int getMaxTicks() {
        return maxTicks;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
        calculate();
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.checkMax(max);
        calculate();
    }

    public double getMaxValue() {
        return tickSpacing * maxTicks;
    }
}
