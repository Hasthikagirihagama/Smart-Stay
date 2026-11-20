package com.k2636474.smartstay.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Small rounded badge: tinted background with text in the full colour.
 * Used for statuses and the breakfast badge.
 */
public class K2636474PillLabel extends JLabel {

    private static final int TINT_ALPHA = 30;
    private Color pillColor = K2636474UiTheme.STATUS_MAINTENANCE;

    /**
     * Creates an empty pill.
     */
    public K2636474PillLabel() {
        setFont(K2636474UiTheme.semibold(K2636474UiTheme.CAPTION_SIZE));
        setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        setOpaque(false);
    }

    /**
     * Sets the pill text and colour.
     *
     * @param text  text to show
     * @param color base colour for text and tint
     */
    public void setPill(String text, Color color) {
        this.pillColor = color;
        setText(text);
        setForeground(color);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(pillColor.getRed(), pillColor.getGreen(), pillColor.getBlue(), TINT_ALPHA));
        float height = getHeight();
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), height, height, height));
        g.dispose();
        super.paintComponent(graphics);
    }
}
