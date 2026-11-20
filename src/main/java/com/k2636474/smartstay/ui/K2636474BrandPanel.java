package com.k2636474.smartstay.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/**
 * Teal brand half of the login and register screens: logo, name, tagline and a
 * subtle geometric pattern painted with Java2D.
 */
public class K2636474BrandPanel extends JPanel {

    private static final int PATTERN_STEP = 56;
    private static final Color PATTERN_LINE = new Color(255, 255, 255, 16);
    private static final Color RING = new Color(201, 162, 39, 55);

    /**
     * Creates the brand panel.
     */
    public K2636474BrandPanel() {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 56, 32, 56));

        JPanel centre = new JPanel(new GridBagLayout());
        centre.setOpaque(false);
        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));

        JComponent mark = new JComponent() {
            @Override
            protected void paintComponent(Graphics graphics) {
                K2636474UiTheme.paintBrandMark((Graphics2D) graphics, 2, 2, 52);
            }
        };
        mark.setPreferredSize(new Dimension(56, 56));
        mark.setMaximumSize(new Dimension(56, 56));
        JLabel name = K2636474UiTheme.label("SmartStay", K2636474UiTheme.font(Font.BOLD, 44), Color.WHITE);
        JLabel tagline = K2636474UiTheme.label("Stays made simple across Sri Lanka",
                K2636474UiTheme.font(Font.PLAIN, 17), K2636474UiTheme.SIDEBAR_TEXT_MUTED);
        JLabel highlights = K2636474UiTheme.label("Standard  ·  Deluxe  ·  Suite  ·  Family Suite",
                K2636474UiTheme.semibold(12), K2636474UiTheme.ACCENT);
        for (JComponent component : new JComponent[]{mark, name, tagline, highlights}) {
            component.setAlignmentX(LEFT_ALIGNMENT);
        }
        stack.add(mark);
        stack.add(Box.createVerticalStrut(20));
        stack.add(name);
        stack.add(Box.createVerticalStrut(6));
        stack.add(tagline);
        stack.add(Box.createVerticalStrut(28));
        stack.add(highlights);
        centre.add(stack);

        add(centre, BorderLayout.CENTER);
        add(K2636474UiTheme.label("SmartStay Hotels · Colombo · Kandy · Galle",
                K2636474UiTheme.font(Font.PLAIN, K2636474UiTheme.CAPTION_SIZE),
                K2636474UiTheme.SIDEBAR_TEXT_MUTED), BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(new GradientPaint(0, 0, K2636474UiTheme.PRIMARY, getWidth(), getHeight(),
                K2636474UiTheme.PRIMARY_HOVER));
        g.fillRect(0, 0, getWidth(), getHeight());
        paintDiamondLattice(g);
        paintRings(g);
        g.dispose();
    }

    private void paintDiamondLattice(Graphics2D g) {
        g.setColor(PATTERN_LINE);
        g.setStroke(new BasicStroke(1f));
        int half = PATTERN_STEP / 2;
        for (int y = -PATTERN_STEP; y < getHeight() + PATTERN_STEP; y += PATTERN_STEP) {
            for (int x = -PATTERN_STEP; x < getWidth() + PATTERN_STEP; x += PATTERN_STEP) {
                Path2D diamond = new Path2D.Float();
                diamond.moveTo(x + half, y);
                diamond.lineTo(x + PATTERN_STEP, y + half);
                diamond.lineTo(x + half, y + PATTERN_STEP);
                diamond.lineTo(x, y + half);
                diamond.closePath();
                g.draw(diamond);
            }
        }
    }

    private void paintRings(Graphics2D g) {
        g.setColor(RING);
        g.setStroke(new BasicStroke(1.5f));
        int centreX = getWidth();
        int centreY = getHeight();
        for (int radius = 120; radius <= 360; radius += 60) {
            g.draw(new Ellipse2D.Float(centreX - radius, centreY - radius, radius * 2f, radius * 2f));
        }
    }
}
