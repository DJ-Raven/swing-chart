package raven.chart.spline;

import raven.chart.line.LineChart;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spline {

    public SplinePoint[] copyPoints() {
        SplinePoint[] ps = new SplinePoint[points.length];
        for (int i = 0; i < ps.length; i++) {
            ps[i] = new SplinePoint(points[i].getX(), points[i].getY());
        }
        return ps;
    }

    public SplinePoint[] getPoints() {
        return points;
    }

    public void setPoints(SplinePoint[] points) {
        this.points = points;
    }

    private SplinePoint[] points;

    public Spline() {
    }

    public List<SplinePoint> createSpline(LineChart.ChartType type, float f, SplinePoint... point) {
        this.points = point;
        float length;
        float increase;
        if (type == LineChart.ChartType.CURVE) {
            length = point.length - 3f;
            increase = 0.09f;
        } else {
            return Arrays.asList(point);
        }
        List<SplinePoint> list = new ArrayList<>();
        for (float t = 0f; t < length * f; t += increase) {
            list.add(getSpline(type, t));
        }
        return list;
    }

    public void render(Graphics2D g2, LineChart.ChartType type, SplinePoint... point) {
        SplinePoint pointAdded[] = new SplinePoint[point.length + 2];
        for (int i = 0; i < point.length; i++) {
            pointAdded[i + 1] = point[i];
        }
        pointAdded[0] = pointAdded[1];
        pointAdded[pointAdded.length - 1] = pointAdded[pointAdded.length - 2];
        List<SplinePoint> list = createSpline(type, 1f, pointAdded);
        Path2D.Double p2 = new Path2D.Double();
        for (int i = 0; i < list.size(); i++) {
            SplinePoint p = list.get(i);
            if (i == 0) {
                p2.moveTo(p.getX(), p.getY());
            } else {
                p2.lineTo(p.getX(), p.getY());
            }
        }
        g2.draw(p2);
    }

    public SplinePoint getSpline(LineChart.ChartType type, float t) {
        if (type == LineChart.ChartType.LINE) {
            return points[(int) t];
        }
        int p0, p1, p2, p3;
        p1 = (int) t + 1;
        p2 = p1 + 1;
        p3 = p2 + 1;
        p0 = p1 - 1;
        t = t - (int) t;
        float tt = t * t;
        float ttt = tt * t;
        float q1 = -ttt + 2.0f * tt - t;
        float q2 = 3.0f * ttt - 5.0f * tt + 2.0f;
        float q3 = -3.0f * ttt + 4.0f * tt + t;
        float q4 = ttt - tt;
        int length = points.length - 1;
        p0 = Math.min(p0, length);
        p1 = Math.min(p1, length);
        p2 = Math.min(p2, length);
        p3 = Math.min(p3, length);
        double tx = 0.5f * (points[p0].getX() * q1 + points[p1].getX() * q2 + points[p2].getX() * q3 + points[p3].getX() * q4);
        double ty = 0.5f * (points[p0].getY() * q1 + points[p1].getY() * q2 + points[p2].getY() * q3 + points[p3].getY() * q4);
        return new SplinePoint(tx, ty);
    }
}
