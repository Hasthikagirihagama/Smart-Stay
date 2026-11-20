package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Room;
import com.k2636474.smartstay.model.enums.K2636474BedType;
import com.k2636474.smartstay.model.enums.K2636474RoomStatus;
import com.k2636474.smartstay.model.enums.K2636474RoomType;
import com.k2636474.smartstay.model.room.K2636474RoomPackageFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.function.Consumer;

/**
 * Admin dialog for adding a new room or editing an existing one. Shows the
 * selected type's package (rate, breakfast, capacity, tax) so the admin can see
 * what the type means before saving.
 */
public class K2636474RoomFormDialog extends JDialog {

    private final K2636474UiContext context;
    private final K2636474Room room;
    private final Consumer<K2636474Room> onSaved;
    private final JComboBox<K2636474RoomType> typeBox = new JComboBox<>(K2636474RoomType.values());
    private final JComboBox<K2636474BedType> bedBox = new JComboBox<>(K2636474BedType.values());
    private final JComboBox<K2636474RoomStatus> statusBox = new JComboBox<>(K2636474RoomStatus.values());
    private final JLabel packageLabel = K2636474UiTheme.label(" ", K2636474UiTheme.font(Font.PLAIN, 12),
            K2636474UiTheme.TEXT_SECONDARY);

    /**
     * Creates the dialog.
     *
     * @param context UI context
     * @param room    room to edit, or null to add a new one
     * @param onSaved called with the saved room
     */
    public K2636474RoomFormDialog(K2636474UiContext context, K2636474Room room, Consumer<K2636474Room> onSaved) {
        super(context.getWindow(), room == null ? "Add room" : "Edit room " + room.getRoomId(),
                ModalityType.APPLICATION_MODAL);
        this.context = context;
        this.room = room;
        this.onSaved = onSaved;
        if (room != null) {
            typeBox.setSelectedItem(room.getRoomType());
            bedBox.setSelectedItem(room.getBedType());
            statusBox.setSelectedItem(room.getStatus());
        }
        typeBox.addActionListener(event -> updatePackageInfo());

        K2636474FormPanel form = new K2636474FormPanel();
        form.addRow(K2636474UiTheme.pageTitle(room == null ? "Add a room" : "Edit room " + room.getRoomId()), 0);
        form.addRow(K2636474UiTheme.subtitle(room == null ? "The room ID is assigned automatically."
                : "Type changes are blocked while the room has upcoming bookings."), 6);
        form.addField("roomType", "Room type", typeBox);
        form.addRow(packageLabel, 0);
        form.addField("bedType", "Bed type", bedBox);
        form.addField("status", "Status", statusBox);

        JButton saveButton = K2636474UiTheme.primaryButton(room == null ? "Add room" : "Save changes", "check");
        saveButton.setToolTipText("Save (Enter)");
        saveButton.addActionListener(event -> save());
        JButton cancelButton = K2636474UiTheme.secondaryButton("Cancel", null);
        cancelButton.setToolTipText("Close without saving (Esc)");
        cancelButton.addActionListener(event -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, K2636474UiTheme.GRID + 4, 0));
        buttons.setOpaque(false);
        buttons.add(cancelButton);
        buttons.add(saveButton);
        form.addRow(buttons, K2636474UiTheme.GAP);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(K2636474UiTheme.SURFACE);
        content.setBorder(BorderFactory.createEmptyBorder(K2636474UiTheme.PAGE_PADDING, 28,
                K2636474UiTheme.PAGE_PADDING, 28));
        content.add(form, BorderLayout.NORTH);
        setContentPane(content);
        updatePackageInfo();

        K2636474UiTheme.installDialogKeys(this, saveButton);
        pack();
        setSize(Math.max(getWidth(), 520), getHeight());
        setResizable(false);
        setLocationRelativeTo(context.getWindow());
    }

    private void updatePackageInfo() {
        K2636474RoomType type = (K2636474RoomType) typeBox.getSelectedItem();
        packageLabel.setText("<html><div style='width:420px'>"
                + K2636474RoomPackageFactory.getPackage(type).describe() + "</div></html>");
    }

    private void save() {
        K2636474RoomType type = (K2636474RoomType) typeBox.getSelectedItem();
        K2636474BedType bedType = (K2636474BedType) bedBox.getSelectedItem();
        K2636474RoomStatus status = (K2636474RoomStatus) statusBox.getSelectedItem();
        try {
            K2636474Room saved = room == null
                    ? context.getRoomService().addRoom(type, bedType, status)
                    : context.getRoomService().updateRoom(room.getRoomId(), type, bedType, status);
            dispose();
            onSaved.accept(saved);
        } catch (RuntimeException error) {
            K2636474UiTheme.showError(this, error);
        }
    }
}
