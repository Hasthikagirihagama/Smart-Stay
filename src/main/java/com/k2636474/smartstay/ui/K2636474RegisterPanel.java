package com.k2636474.smartstay.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.K2636474Guest;
import com.k2636474.smartstay.model.enums.K2636474IdType;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Guest registration screen. All invalid fields are reported at once, each with an
 * inline red message under the field.
 */
public class K2636474RegisterPanel extends JPanel {

    private static final int CARD_WIDTH = 460;

    private final K2636474UiContext context;
    private final Consumer<K2636474Guest> onRegistered;
    private final K2636474FormPanel form = new K2636474FormPanel();
    private final Map<String, K2636474FormPanel> columns = new HashMap<>();
    private final JComboBox<K2636474IdType> idTypeBox = new JComboBox<>(K2636474IdType.values());
    private final JTextField idNumberField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JTextField contactField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmField = new JPasswordField();

    /**
     * Creates the registration screen.
     *
     * @param context      UI context with the services
     * @param onRegistered called with the new guest after a successful registration
     * @param onBack       called when the user returns to the login screen
     */
    public K2636474RegisterPanel(K2636474UiContext context, Consumer<K2636474Guest> onRegistered, Runnable onBack) {
        super(new GridLayout(1, 2));
        this.context = context;
        this.onRegistered = onRegistered;
        add(new K2636474BrandPanel());

        JPanel formSide = new JPanel(new GridBagLayout());
        formSide.setBackground(K2636474UiTheme.BACKGROUND);
        K2636474CardPanel card = K2636474UiTheme.card();
        card.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        form.addRow(K2636474UiTheme.pageTitle("Create your account"), 0);
        form.addRow(K2636474UiTheme.subtitle("Register once, then book any room in seconds."), 6);
        configurePlaceholders();
        idTypeBox.addActionListener(event -> updateIdPlaceholder());

        JPanel idRow = new JPanel(new BorderLayout(K2636474UiTheme.GRID, 0));
        idRow.setOpaque(false);
        idTypeBox.setPreferredSize(new Dimension(120, idTypeBox.getPreferredSize().height));
        idRow.add(idTypeBox, BorderLayout.WEST);
        idRow.add(idNumberField, BorderLayout.CENTER);
        form.addField("idNumber", "Identity document", idRow);
        form.addField("fullName", "Full name", fullNameField);

        JPanel contactRow = twoColumns(fieldColumn("contactNumber", "Contact number", contactField),
                fieldColumn("email", "Email", emailField));
        form.addRow(contactRow, 0);
        JPanel passwordRow = twoColumns(fieldColumn("password", "Password", passwordField),
                fieldColumn("confirmPassword", "Confirm password", confirmField));
        form.addRow(passwordRow, 0);

        JButton createButton = K2636474UiTheme.primaryButton("Create account", null);
        createButton.setToolTipText("Register (Enter)");
        createButton.addActionListener(event -> attemptRegister());
        form.addRow(createButton, K2636474UiTheme.GRID);

        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        backRow.setOpaque(false);
        backRow.add(K2636474UiTheme.subtitle("Already registered?"));
        JButton backLink = K2636474UiTheme.linkButton("Back to login");
        backLink.setToolTipText("Return to the login screen");
        backLink.addActionListener(event -> onBack.run());
        backRow.add(backLink);
        form.addRow(backRow, K2636474UiTheme.GAP);

        card.add(form, BorderLayout.CENTER);
        // Fix the width only after the form is added, so the height still follows the content.
        card.setPreferredSize(new Dimension(CARD_WIDTH, card.getPreferredSize().height));
        formSide.add(card);
        add(formSide);

        for (JTextField field : new JTextField[]{idNumberField, fullNameField, contactField, emailField,
                passwordField, confirmField}) {
            field.addActionListener(event -> attemptRegister());
        }
    }

    // The two-column rows use a nested form per column; errors are routed to it by field name.
    private K2636474FormPanel fieldColumn(String fieldName, String label, JTextField field) {
        K2636474FormPanel column = new K2636474FormPanel();
        column.addField(fieldName, label, field);
        columns.put(fieldName, column);
        return column;
    }

    private static JPanel twoColumns(JPanel left, JPanel right) {
        JPanel row = new JPanel(new GridLayout(1, 2, K2636474UiTheme.GAP - 4, 0));
        row.setOpaque(false);
        row.add(left);
        row.add(right);
        return row;
    }

    private void configurePlaceholders() {
        fullNameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. Nimal Perera");
        contactField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0771234567");
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "you@example.com");
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "At least 6 characters");
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        confirmField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Type it again");
        updateIdPlaceholder();
    }

    private void updateIdPlaceholder() {
        String hint = idTypeBox.getSelectedItem() == K2636474IdType.PASSPORT
                ? "6-9 letters/digits, e.g. N1234567" : "912345678V or 199512345678";
        idNumberField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, hint);
        idNumberField.repaint();
    }

    /**
     * Clears the form for a fresh registration.
     */
    public void reset() {
        for (JTextField field : new JTextField[]{idNumberField, fullNameField, contactField, emailField,
                passwordField, confirmField}) {
            field.setText("");
        }
        idTypeBox.setSelectedItem(K2636474IdType.NIC);
        clearErrors();
        SwingUtilities.invokeLater(idNumberField::requestFocusInWindow);
    }

    private void attemptRegister() {
        clearErrors();
        K2636474IdType idType = (K2636474IdType) idTypeBox.getSelectedItem();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        Map<String, String> errors = context.getAuthService().validateRegistration(idType, idNumberField.getText(),
                fullNameField.getText(), contactField.getText(), emailField.getText(), password, confirm);
        if (!errors.isEmpty()) {
            errors.forEach(this::showError);
            return;
        }
        try {
            K2636474Guest guest = context.getAuthService().registerGuest(idType, idNumberField.getText(),
                    fullNameField.getText(), contactField.getText(), emailField.getText(), password, confirm);
            onRegistered.accept(guest);
        } catch (K2636474ValidationException error) {
            showError(error.getFieldName(), error.getMessage());
        }
    }

    private void showError(String fieldName, String message) {
        K2636474FormPanel column = columns.get(fieldName);
        (column != null ? column : form).showError(fieldName, message);
    }

    private void clearErrors() {
        form.clearErrors();
        columns.values().forEach(K2636474FormPanel::clearErrors);
    }
}
