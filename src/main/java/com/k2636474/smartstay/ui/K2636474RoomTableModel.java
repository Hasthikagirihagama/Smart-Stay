package com.k2636474.smartstay.ui;

import com.k2636474.smartstay.model.K2636474Room;
import com.k2636474.smartstay.model.enums.K2636474RoomStatus;
import com.k2636474.smartstay.model.enums.K2636474RoomType;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model listing rooms with their package details.
 */
public class K2636474RoomTableModel extends AbstractTableModel {

    private static final String[] COLUMNS =
            {"Room", "Type", "Bed", "Daily rate", "Capacity", "Breakfast", "Status"};
    private static final Class<?>[] TYPES = {String.class, K2636474RoomType.class, String.class, Double.class,
            String.class, String.class, K2636474RoomStatus.class};
    /** Column index of the room type, used for filtering. */
    public static final int TYPE_COLUMN = 1;
    /** Column index of the daily rate, used to attach the money renderer. */
    public static final int RATE_COLUMN = 3;
    /** Column index of the status, used for filtering and the pill renderer. */
    public static final int STATUS_COLUMN = 6;

    private final List<K2636474Room> rows = new ArrayList<>();

    /**
     * Replaces the rows.
     *
     * @param rooms rooms to show
     */
    public void setRooms(List<K2636474Room> rooms) {
        rows.clear();
        rows.addAll(rooms);
        fireTableDataChanged();
    }

    /**
     * Returns the room on a model row.
     *
     * @param modelRow row index in the model (convert view indexes first)
     * @return room
     */
    public K2636474Room getRoomAt(int modelRow) {
        return rows.get(modelRow);
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return TYPES[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        K2636474Room room = rows.get(row);
        return switch (column) {
            case 0 -> room.getRoomId();
            case 1 -> room.getRoomType();
            case 2 -> room.getBedType().getDisplayName();
            case 3 -> room.getDailyRate();
            case 4 -> room.getRoomPackage().getBaseCapacity() + " (max " + room.getMaxOccupancy() + ")";
            case 5 -> room.getRoomPackage().includesBreakfast() ? "Included" : "—";
            default -> room.getStatus();
        };
    }
}
