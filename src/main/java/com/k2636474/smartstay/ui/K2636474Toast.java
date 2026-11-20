package com.k2636474.smartstay.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Non-blocking status message shown at the bottom of the content area. It hides
 * itself after {@link K2636474UiTheme#TOAST_MILLIS}, so success feedback never
 * interrupts the user with a dialog.
 */
public class K2636474Toast extends JPanel {

    private final JLabel messageLabel = new JLabel();
    private final Timer hideTimer;

    /**
     * Creates a hidden toast area.
     */
    public K2636474Toast() {
        super(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, K2636474UiTheme.GAP, 0));
        JPanel bubble = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(K2636474UiTheme.TEXT);
                g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
                g.dispose();
            }
        };
        bubble.setOpaque(false);
        bubble.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 20));
        messageLabel.setFont(K2636474UiTheme.semibold(K2636474UiTheme.BODY_SIZE));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setIconTextGap(K2636474UiTheme.GRID + 2);
        bubble.add(messageLabel);
        add(bubble);
        setVisible(false);
        hideTimer = new Timer(K2636474UiTheme.TOAST_MILLIS, event -> setVisible(false));
        hideTimer.setRepeats(false);
    }

    /**
     * Shows a success message; it disappears automatically.
     *
     * @param message text to show
     */
    public void showMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setIcon(K2636474UiTheme.icon("check", 16, K2636474UiTheme.ACCENT));
        setVisible(true);
        revalidate();
        hideTimer.restart();
    }
}
