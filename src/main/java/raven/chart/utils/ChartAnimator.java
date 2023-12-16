package raven.chart.utils;

import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.CubicBezierEasing;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ChartAnimator {

    private Animator animator;
    private float animate;

    private void initAnimator() {
        if (animator == null) {
            animator = new Animator(1000, new Animator.TimingTarget() {
                @Override
                public void timingEvent(float v) {
                    animate = v;
                    animatorChanged(animate);
                }
            });
            animator.setInterpolator(CubicBezierEasing.EASE);
        }
    }

    public void renderImage(Graphics2D g2, BufferedImage image) {
        if (animator != null && animator.isRunning()) {
            g2.drawImage(createImage(image, animate), 0, 0, null);
        } else {
            g2.drawImage(image, 0, 0, null);
        }
    }

    public abstract BufferedImage createImage(BufferedImage image, float animate);

    public abstract void animatorChanged(float animator);

    public void start() {
        initAnimator();
        if (animator.isRunning()) {
            animator.stop();
        }
        animate = 0;
        animator.start();
    }

    public boolean isRunning() {
        return animator != null && animator.isRunning();
    }
}
