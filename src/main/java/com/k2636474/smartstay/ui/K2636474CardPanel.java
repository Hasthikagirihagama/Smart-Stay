package com.k2636474.smartstay.ui;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * White rounded panel with a 1px warm border, used for every card in the app.
 * It paints its own shape, so it looks the same under FlatLaf and the Nimbus fallback.
 */
public class K2636474CardPanel extends JPanel {

    /**
     * Creates a card with a layout manager.
     *
     * @param layout layout for the card's children
     */
    public K2636474CardPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBackground(K2636474UiTheme.SURFACE);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        float arc = K2636474UiTheme.CARD_ARC;
        g.setColor(getBackground());
        g.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1f, getHeight() - 1f, arc, arc));
        g.setColor(K2636474UiTheme.BORDER);
        g.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1.5f, getHeight() - 1.5f, arc, arc));
        g.dispose();
        super.paintComponent(graphics);
    }
}
