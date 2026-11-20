package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474User;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shared dashboard layout: a teal sidebar (logo, navigation, user, logout) and a
 * content area that swaps pages with a {@link CardLayout}. Admin and guest
 * dashboards extend this and only declare which pages they have.
 */
public abstract class K2636474DashboardShell extends JPanel {

    private final K2636474UiContext context;
    private final CardLayout pageCards = new CardLayout();
    private final JPanel pageArea = new JPanel(pageCards);
    private final JPanel navList = new JPanel();
    private final Map<String, K2636474Page> pages = new LinkedHashMap<>();
    private final Map<String, K2636474NavItem> navItems = new LinkedHashMap<>();

    /**
     * Builds the shell for the logged-in user.
     *
     * @param context  UI context
     * @param onLogout called when the user logs out
     */
    protected K2636474DashboardShell(K2636474UiContext context, Runnable onLogout) {
        super(new BorderLayout());
        this.context = context;
        add(buildSidebar(context.getCurrentUser(), onLogout), BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(K2636474UiTheme.BACKGROUND);
        pageArea.setOpaque(false);
        content.add(pageArea, BorderLayout.CENTER);
        K2636474Toast toast = new K2636474Toast();
        content.add(toast, BorderLayout.SOUTH);
        context.setToast(toast);
        add(content, BorderLayout.CENTER);
    }

    private JPanel buildSidebar(K2636474User user, Runnable onLogout) {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(K2636474UiTheme.PRIMARY);
        sidebar.setPreferredSize(new Dimension(K2636474UiTheme.SIDEBAR_WIDTH, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(K2636474UiTheme.PAGE_PADDING, 0, K2636474UiTheme.GAP, 0));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(buildLogo());
        top.add(Box.createVerticalStrut(K2636474UiTheme.PAGE_PADDING + 8));
        JLabel menuLabel = K2636474UiTheme.label("MENU", K2636474UiTheme.semibold(10),
                K2636474UiTheme.SIDEBAR_TEXT_MUTED);
        menuLabel.setBorder(BorderFactory.createEmptyBorder(0, 24, K2636474UiTheme.GRID, 0));
        JPanel menuRow = new JPanel(new BorderLayout());
        menuRow.setOpaque(false);
        menuRow.add(menuLabel);
        menuRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        top.add(menuRow);
        navList.setOpaque(false);
        navList.setLayout(new BoxLayout(navList, BoxLayout.Y_AXIS));
        top.add(navList);
        sidebar.add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.add(buildUserBox(user));
        bottom.add(Box.createVerticalStrut(K2636474UiTheme.GRID));
        bottom.add(new K2636474NavItem("Log out", "logout", onLogout));
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private static JComponent buildLogo() {
        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logo.setOpaque(false);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        JComponent mark = new JComponent() {
            @Override
            protected void paintComponent(Graphics graphics) {
                K2636474UiTheme.paintBrandMark((Graphics2D) graphics, 2, 3, 24);
            }
        };
        mark.setPreferredSize(new Dimension(28, 28));
        logo.add(mark);
        logo.add(K2636474UiTheme.label("SmartStay", K2636474UiTheme.font(Font.BOLD, 20), Color.WHITE));
        logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return logo;
    }

    private static JComponent buildUserBox(K2636474User user) {
        JPanel box = new JPanel(new BorderLayout(12, 0));
        box.setOpaque(false);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 30)),
                BorderFactory.createEmptyBorder(K2636474UiTheme.GAP, 22, 4, 12)));
        box.add(new JLabel(K2636474UiTheme.icon("user", 28, K2636474UiTheme.ACCENT)), BorderLayout.WEST);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(K2636474UiTheme.label(user.getFullName(), K2636474UiTheme.semibold(13), Color.WHITE));
        text.add(K2636474UiTheme.label(user.getRole().getDisplayName(),
                K2636474UiTheme.font(Font.PLAIN, K2636474UiTheme.CAPTION_SIZE), K2636474UiTheme.SIDEBAR_TEXT_MUTED));
        box.add(text, BorderLayout.CENTER);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        return box;
    }

    /**
     * Adds a page and its sidebar entry.
     *
     * @param key      unique page key
     * @param label    sidebar label
     * @param iconName sidebar icon
     * @param page     the page
     */
    protected void addPage(String key, String label, String iconName, K2636474Page page) {
        pages.put(key, page);
        pageArea.add(page, key);
        K2636474NavItem item = new K2636474NavItem(label, iconName, () -> showPage(key));
        navItems.put(key, item);
        navList.add(item);
        navList.add(Box.createVerticalStrut(4));
    }

    /**
     * Shows a page, refreshing its data first, and highlights its sidebar entry.
     *
     * @param key page key
     */
    public void showPage(String key) {
        pages.get(key).refresh();
        navItems.forEach((itemKey, item) -> item.setActive(itemKey.equals(key)));
        pageCards.show(pageArea, key);
    }

    /**
     * Returns the UI context for subclasses.
     *
     * @return UI context
     */
    protected K2636474UiContext getContext() {
        return context;
    }
}
