package com.k2636474.smartstay.model;

import com.k2636474.smartstay.exception.K2636474ValidationException;
import com.k2636474.smartstay.model.enums.K2636474BedType;
import com.k2636474.smartstay.model.enums.K2636474RoomStatus;
import com.k2636474.smartstay.model.enums.K2636474RoomType;
import com.k2636474.smartstay.model.room.K2636474RoomPackage;
import com.k2636474.smartstay.util.K2636474Validator;

/**
 * A physical hotel room. Price, breakfast and capacity are not stored here: they come
 * from the shared {@link K2636474RoomPackage} for the room's type, so changing a
 * room's type is just swapping its package.
 */
public class K2636474Room {

    private final String roomId;
    private K2636474BedType bedType;
    private K2636474RoomStatus status;
    private K2636474RoomPackage roomPackage;

    /**
     * Creates a room.
     *
     * @param roomId      unique room ID (e.g. R001)
     * @param roomPackage the shared package for the room's type
     * @param bedType     bed configuration
     * @param status      operational status
     * @throws K2636474ValidationException if any argument is missing
     */
    public K2636474Room(String roomId, K2636474RoomPackage roomPackage, K2636474BedType bedType,
                        K2636474RoomStatus status) {
        if (roomId == null || roomId.isBlank()) {
            throw new K2636474ValidationException("roomId", "Room ID is required.");
        }
        this.roomId = roomId;
        setRoomPackage(roomPackage);
        setBedType(bedType);
        setStatus(status);
    }

    /**
     * Returns the room's room type (delegates to the package).
     *
     * @return room type
     */
    public K2636474RoomType getRoomType() {
        return roomPackage.getRoomType();
    }

    /**
     * Returns the nightly rate (delegates to the package).
     *
     * @return daily rate in LKR
     */
    public double getDailyRate() {
        return roomPackage.getDailyRate();
    }

    /**
     * Returns the maximum number of guests, base capacity plus extra guests (delegates to the package).
     *
     * @return maximum occupancy
     */
    public int getMaxOccupancy() {
        return roomPackage.getMaxOccupancy();
    }

    /**
     * Returns whether the room is currently open for new bookings.
     *
     * @return true when the status is AVAILABLE
     */
    public boolean isBookable() {
        return status == K2636474RoomStatus.AVAILABLE;
    }

    /**
     * Returns the unique room ID.
     *
     * @return room ID
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Returns the bed configuration.
     *
     * @return bed type
     */
    public K2636474BedType getBedType() {
        return bedType;
    }

    /**
     * Sets the bed configuration.
     *
     * @param bedType new bed type, required
     * @throws K2636474ValidationException if null
     */
    public void setBedType(K2636474BedType bedType) {
        this.bedType = K2636474Validator.requireNotNull("bedType", bedType, "Bed type");
    }

    /**
     * Returns the operational status.
     *
     * @return status
     */
    public K2636474RoomStatus getStatus() {
        return status;
    }

    /**
     * Sets the operational status.
     *
     * @param status new status, required
     * @throws K2636474ValidationException if null
     */
    public void setStatus(K2636474RoomStatus status) {
        this.status = K2636474Validator.requireNotNull("status", status, "Room status");
    }

    /**
     * Returns the shared package that prices this room.
     *
     * @return room package
     */
    public K2636474RoomPackage getRoomPackage() {
        return roomPackage;
    }

    /**
     * Sets the package, which also changes the room type.
     *
     * @param roomPackage new package, required
     * @throws K2636474ValidationException if null
     */
    public void setRoomPackage(K2636474RoomPackage roomPackage) {
        this.roomPackage = K2636474Validator.requireNotNull("roomType", roomPackage, "Room type");
    }

    @Override
    public String toString() {
        return roomId + " · " + getRoomType().getDisplayName() + " · " + bedType.getDisplayName();
    }
}
