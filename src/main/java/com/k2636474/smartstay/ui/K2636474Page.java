package com.k2636474.smartstay.ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Base class for every sidebar page: a title, a short subtitle and a body area.
 * The dashboard calls {@link #refresh()} each time the page is shown, so data is
 * always current; each page decides how to reload itself (polymorphism).
 */
public abstract class K2636474Page extends JPanel {

    private final JPanel header = new JPanel(new BorderLayout());
    private final JPanel body = new JPanel(new BorderLayout(K2636474UiTheme.GAP, K2636474UiTheme.GAP));

    /**
     * Creates the page frame.
     *
     * @param title    page title
     * @param subtitle one-line description under the title
     */
    protected K2636474Page(String title, String subtitle) {
        super(new BorderLayout(0, K2636474UiTheme.PAGE_PADDING - K2636474UiTheme.GRID));
        setBackground(K2636474UiTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(K2636474UiTheme.PAGE_PADDING, K2636474UiTheme.PAGE_PADDING,
                K2636474UiTheme.GRID, K2636474UiTheme.PAGE_PADDING));
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(K2636474UiTheme.pageTitle(title));
        titles.add(javax.swing.Box.createVerticalStrut(4));
        titles.add(K2636474UiTheme.subtitle(subtitle));
        header.setOpaque(false);
        header.add(titles, BorderLayout.WEST);
        body.setOpaque(false);
        add(header, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }

    /**
     * Returns the area below the title where the page puts its content.
     *
     * @return body panel (BorderLayout)
     */
    protected JPanel getBody() {
        return body;
    }

    /**
     * Reloads the page's data from the services. Called whenever the page is shown.
     */
    public abstract void refresh();
}
