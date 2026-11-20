package com.k2636474.smartstay.ui;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Vertical form layout: each field gets a label above it and an inline red error
 * label below it. Errors are looked up by field name, matching the field names in
 * {@code K2636474ValidationException}, so service errors land under the right field.
 */
public class K2636474FormPanel extends JPanel {

    private final Map<String, JLabel> errorLabels = new LinkedHashMap<>();
    private final Map<String, JComponent> fields = new LinkedHashMap<>();
    private int nextRow;

    /**
     * Creates an empty transparent form.
     */
    public K2636474FormPanel() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    /**
     * Adds any full-width component.
     *
     * @param component component to add
     * @param topGap    space above it in pixels
     */
    public void addRow(JComponent component, int topGap) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = nextRow++;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(topGap, 0, 0, 0);
        add(component, constraints);
    }

    /**
     * Adds a labelled field with an (initially empty) inline error label underneath.
     *
     * @param fieldName name used to report errors for this field
     * @param labelText label shown above the field
     * @param field     the input component
     */
    public void addField(String fieldName, String labelText, JComponent field) {
        addRow(K2636474UiTheme.fieldLabel(labelText), K2636474UiTheme.GAP - 4);
        addRow(field, 6);
        JLabel error = K2636474UiTheme.label(" ", K2636474UiTheme.font(Font.PLAIN, K2636474UiTheme.CAPTION_SIZE),
                K2636474UiTheme.ERROR);
        addRow(error, 3);
        errorLabels.put(fieldName, error);
        fields.put(fieldName, field);
    }

    /**
     * Shows an error under a field and outlines the field in red.
     *
     * @param fieldName field name
     * @param message   error to show
     */
    public void showError(String fieldName, String message) {
        JLabel error = errorLabels.get(fieldName);
        if (error != null) {
            error.setText(message);
            fields.get(fieldName).putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);
        }
    }

    /**
     * Clears every inline error.
     */
    public void clearErrors() {
        // A single space keeps each error row's height, so the form does not jump around.
        errorLabels.values().forEach(label -> label.setText(" "));
        fields.values().forEach(field -> field.putClientProperty(FlatClientProperties.OUTLINE, null));
    }
}
