package com.k2636474.smartstay.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import java.awt.CardLayout;

/**
 * Card holding a table that switches to a friendly empty-state message when the
 * table has no rows.
 */
public class K2636474TableCard extends K2636474CardPanel {

    private static final String TABLE_VIEW = "table";
    private static final String EMPTY_VIEW = "empty";

    private final CardLayout views = new CardLayout();
    private final JTable table;

    /**
     * Creates the card.
     *
     * @param table        table to show
     * @param emptyIcon    icon for the empty state
     * @param emptyTitle   empty-state headline
     * @param emptyMessage empty-state hint
     */
    public K2636474TableCard(JTable table, String emptyIcon, String emptyTitle, String emptyMessage) {
        super(null);
        this.table = table;
        setLayout(views);
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        K2636474UiTheme.styleTable(table);
        add(K2636474UiTheme.scroll(table), TABLE_VIEW);
        JPanel empty = K2636474UiTheme.emptyState(emptyIcon, emptyTitle, emptyMessage);
        add(empty, EMPTY_VIEW);
    }

    /**
     * Shows the table, or the empty state if the table (after filtering) has no rows.
     */
    public void updateView() {
        views.show(this, table.getRowCount() == 0 ? EMPTY_VIEW : TABLE_VIEW);
    }
}
