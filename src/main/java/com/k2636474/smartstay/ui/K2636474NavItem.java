package com.k2636474.smartstay.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * One sidebar navigation entry: icon and label on the teal sidebar, with a hover
 * highlight and a gold bar on the left when it is the active page.
 */
public class K2636474NavItem extends JPanel {

    private static final int HEIGHT = 44;
    private static final int ACTIVE_BAR_WIDTH = 4;
    private static final Color HOVER = new Color(255, 255, 255, 18);
    private static final Color ACTIVE = new Color(255, 255, 255, 28);

    private final JLabel label;
    private final String iconName;
    private boolean active;
    private boolean hovered;

    /**
     * Creates a navigation item.
     *
     * @param text     label text
     * @param iconName icon name
     * @param onClick  action when clicked
     */
    public K2636474NavItem(String text, String iconName, Runnable onClick) {
        super(new BorderLayout());
        this.iconName = iconName;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 22, 0, 12));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
        setPreferredSize(new Dimension(K2636474UiTheme.SIDEBAR_WIDTH, HEIGHT));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(text);
        label = new JLabel(text);
        label.setIconTextGap(12);
        add(label, BorderLayout.CENTER);
        setActive(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                onClick.run();
            }

            @Override
            public void mouseEntered(MouseEvent event) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent event) {
                hovered = false;
                repaint();
            }
        });
    }

    /**
     * Marks the item as the current page or not.
     *
     * @param active true when this item's page is shown
     */
    public void setActive(boolean active) {
        this.active = active;
        Color color = active ? Color.WHITE : K2636474UiTheme.SIDEBAR_TEXT_MUTED;
        label.setForeground(color);
        label.setFont(active ? K2636474UiTheme.semibold(14) : K2636474UiTheme.font(java.awt.Font.PLAIN, 14));
        label.setIcon(K2636474UiTheme.icon(iconName, 18, active ? K2636474UiTheme.ACCENT : color));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (active || hovered) {
            g.setColor(active ? ACTIVE : HOVER);
            g.fill(new RoundRectangle2D.Float(10, 2, getWidth() - 20f, getHeight() - 4f, 10, 10));
        }
        if (active) {
            g.setColor(K2636474UiTheme.ACCENT);
            g.fill(new RoundRectangle2D.Float(0, 8, ACTIVE_BAR_WIDTH, getHeight() - 16f, 4, 4));
        }
        g.dispose();
        super.paintComponent(graphics);
    }
}
