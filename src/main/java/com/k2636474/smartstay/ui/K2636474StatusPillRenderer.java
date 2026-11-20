package com.k2636474.smartstay.ui;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.FlowLayout;

/**
 * Table cell renderer that shows a room or booking status as a coloured pill badge
 * (green Available, amber Reserved, grey Maintenance, red Cancelled...).
 */
public class K2636474StatusPillRenderer extends JPanel implements TableCellRenderer {

    private final K2636474PillLabel pill = new K2636474PillLabel();

    /**
     * Creates the renderer.
     */
    public K2636474StatusPillRenderer() {
        super(new FlowLayout(FlowLayout.LEFT, 12, 8));
        add(pill);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        pill.setPill(value == null ? "" : value.toString(), K2636474UiTheme.statusColor(value));
        return this;
    }
}
