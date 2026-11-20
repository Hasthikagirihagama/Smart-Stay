package com.k2636474.smartstay.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.k2636474.smartstay.app.K2636474DataSeeder;
import com.k2636474.smartstay.exception.K2636474AuthenticationException;
import com.k2636474.smartstay.model.K2636474User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.function.Consumer;

/**
 * Login screen: brand on the left, login card on the right. Enter submits.
 */
public class K2636474LoginPanel extends JPanel {

    private static final int CARD_WIDTH = 400;

    private final K2636474UiContext context;
    private final Consumer<K2636474User> onLoggedIn;
    private final JTextField loginField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel messageLabel = new JLabel(" ");
    private final JButton loginButton = K2636474UiTheme.primaryButton("Log in", null);

    /**
     * Creates the login screen.
     *
     * @param context      UI context with the services
     * @param onLoggedIn   called with the user after a successful login
     * @param onRegister   called when the user wants to create an account
     */
    public K2636474LoginPanel(K2636474UiContext context, Consumer<K2636474User> onLoggedIn, Runnable onRegister) {
        super(new GridLayout(1, 2));
        this.context = context;
        this.onLoggedIn = onLoggedIn;
        add(new K2636474BrandPanel());

        JPanel formSide = new JPanel(new GridBagLayout());
        formSide.setBackground(K2636474UiTheme.BACKGROUND);
        K2636474CardPanel card = K2636474UiTheme.card();
        card.setBorder(BorderFactory.createEmptyBorder(36, 36, 32, 36));

        K2636474FormPanel form = new K2636474FormPanel();
        form.addRow(K2636474UiTheme.pageTitle("Welcome back"), 0);
        form.addRow(K2636474UiTheme.subtitle("Log in to book and manage your stays."), 6);
        loginField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "you@example.com or admin username");
        loginField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                K2636474UiTheme.icon("user", 16, K2636474UiTheme.TEXT_SECONDARY));
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Your password");
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        form.addField("loginId", "Email or username", loginField);
        form.addField("password", "Password", passwordField);
        messageLabel.setFont(K2636474UiTheme.font(Font.PLAIN, 12));
        form.addRow(messageLabel, 0);
        loginButton.setToolTipText("Log in (Enter)");
        loginButton.addActionListener(event -> attemptLogin());
        form.addRow(loginButton, K2636474UiTheme.GRID + 4);

        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerRow.setOpaque(false);
        registerRow.add(K2636474UiTheme.subtitle("New to SmartStay?"));
        JButton registerLink = K2636474UiTheme.linkButton("Create an account");
        registerLink.setToolTipText("Register as a guest");
        registerLink.addActionListener(event -> onRegister.run());
        registerRow.add(registerLink);
        form.addRow(registerRow, K2636474UiTheme.GAP);
        form.addRow(new JSeparator(), K2636474UiTheme.GAP);
        form.addRow(K2636474UiTheme.caption("Demo guest: nimal@example.com / " + K2636474DataSeeder.GUEST_PASSWORD),
                K2636474UiTheme.GRID + 4);
        form.addRow(K2636474UiTheme.caption("Demo admin: " + K2636474DataSeeder.ADMIN_USERNAME + " / "
                + K2636474DataSeeder.ADMIN_PASSWORD), 4);

        card.add(form, BorderLayout.CENTER);
        // Fix the width only after the form is added, so the height still follows the content.
        card.setPreferredSize(new Dimension(CARD_WIDTH, card.getPreferredSize().height));
        formSide.add(card);
        add(formSide);

        // Enter in either field submits, so the user never has to reach for the mouse.
        loginField.addActionListener(event -> attemptLogin());
        passwordField.addActionListener(event -> attemptLogin());
    }

    /**
     * Resets the form and optionally shows a message (e.g. after registering).
     *
     * @param message success message, or null
     * @param loginId login to pre-fill, or null
     */
    public void reset(String message, String loginId) {
        passwordField.setText("");
        loginField.setText(loginId == null ? "" : loginId);
        showMessage(message, false);
        SwingUtilities.invokeLater(() -> (loginId == null ? loginField : passwordField).requestFocusInWindow());
    }

    private void attemptLogin() {
        try {
            K2636474User user = context.getAuthService().login(loginField.getText(),
                    new String(passwordField.getPassword()));
            passwordField.setText("");
            showMessage(null, false);
            onLoggedIn.accept(user);
        } catch (K2636474AuthenticationException error) {
            showMessage(error.getMessage(), true);
            passwordField.selectAll();
            passwordField.requestFocusInWindow();
        }
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message == null ? " " : message);
        messageLabel.setForeground(isError ? K2636474UiTheme.ERROR : K2636474UiTheme.SUCCESS);
    }
}
