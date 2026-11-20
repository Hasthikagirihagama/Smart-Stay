package com.k2636474.smartstay.service;

import com.k2636474.smartstay.exception.K2636474BookingException;
import com.k2636474.smartstay.exception.K2636474NotFoundException;
import com.k2636474.smartstay.model.K2636474Booking;
import com.k2636474.smartstay.model.K2636474Room;
import com.k2636474.smartstay.model.enums.K2636474BedType;
import com.k2636474.smartstay.model.enums.K2636474RoomStatus;
import com.k2636474.smartstay.model.enums.K2636474RoomType;
import com.k2636474.smartstay.model.room.K2636474RoomPackageFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Room management for administrators and availability search for guests.
 */
public class K2636474RoomService {

    private final K2636474DataStore dataStore;
    private final Clock clock;

    /**
     * Creates the service.
     *
     * @param dataStore shared data store
     * @param clock     source of "today" (injectable so tests can fix the date)
     */
    public K2636474RoomService(K2636474DataStore dataStore, Clock clock) {
        this.dataStore = dataStore;
        this.clock = clock;
    }

    /**
     * Adds a room with the next free room ID.
     *
     * @param type    room type
     * @param bedType bed configuration
     * @param status  initial status
     * @return the new room
     */
    public K2636474Room addRoom(K2636474RoomType type, K2636474BedType bedType, K2636474RoomStatus status) {
        K2636474Room room = new K2636474Room(dataStore.getIdGenerator().nextRoomId(),
                K2636474RoomPackageFactory.getPackage(type), bedType, status);
        dataStore.saveRoom(room);
        return room;
    }

    /**
     * Edits a room. The type cannot change while the room has upcoming bookings,
     * because that would silently change their price and capacity.
     *
     * @param roomId  room to edit
     * @param type    new room type
     * @param bedType new bed type
     * @param status  new status
     * @return the updated room
     * @throws K2636474NotFoundException if the room does not exist
     * @throws K2636474BookingException  if the type changes while upcoming bookings exist
     */
    public K2636474Room updateRoom(String roomId, K2636474RoomType type, K2636474BedType bedType,
                                   K2636474RoomStatus status) {
        K2636474Room room = findById(roomId);
        if (type != room.getRoomType() && hasActiveFutureBookings(room)) {
            throw new K2636474BookingException("Room " + roomId
                    + " has upcoming bookings, so its type cannot be changed.");
        }
        room.setRoomPackage(K2636474RoomPackageFactory.getPackage(type));
        room.setBedType(bedType);
        room.setStatus(status);
        return room;
    }

    /**
     * Removes a room. Its ID is never reused.
     *
     * @param roomId room to remove
     * @throws K2636474NotFoundException if the room does not exist
     * @throws K2636474BookingException  if the room has upcoming confirmed bookings
     */
    public void removeRoom(String roomId) {
        K2636474Room room = findById(roomId);
        if (hasActiveFutureBookings(room)) {
            throw new K2636474BookingException("Room " + roomId
                    + " has upcoming confirmed bookings and cannot be removed. Cancel or move them first.");
        }
        dataStore.deleteRoom(roomId);
    }

    /**
     * Changes a room's operational status.
     *
     * @param roomId room to change
     * @param status new status
     * @return the updated room
     * @throws K2636474NotFoundException if the room does not exist
     */
    public K2636474Room changeStatus(String roomId, K2636474RoomStatus status) {
        K2636474Room room = findById(roomId);
        room.setStatus(status);
        return room;
    }

    /**
     * Returns every room in the order added.
     *
     * @return rooms
     */
    public List<K2636474Room> getAllRooms() {
        return dataStore.getRooms();
    }

    /**
     * Finds a room by ID.
     *
     * @param roomId room ID
     * @return the room
     * @throws K2636474NotFoundException if no room has that ID
     */
    public K2636474Room findById(String roomId) {
        return dataStore.findRoom(roomId)
                .orElseThrow(() -> new K2636474NotFoundException("Room " + roomId + " does not exist."));
    }

    /**
     * Counts rooms with a given status.
     *
     * @param status status to count
     * @return number of rooms
     */
    public long countByStatus(K2636474RoomStatus status) {
        return dataStore.getRooms().stream().filter(room -> room.getStatus() == status).count();
    }

    /**
     * Finds rooms a guest can book. Filters are applied cheapest first:
     * type → status AVAILABLE → capacity → no overlapping confirmed booking.
     *
     * @param type   room type, or null for any type
     * @param in     check-in date
     * @param out    check-out date
     * @param guests number of guests
     * @return bookable rooms in room-ID order
     * @throws K2636474BookingException if the dates or guest count are invalid
     */
    public List<K2636474Room> searchAvailable(K2636474RoomType type, LocalDate in, LocalDate out, int guests) {
        if (in == null || out == null || !out.isAfter(in)) {
            throw new K2636474BookingException("Check-out must be at least one night after check-in.");
        }
        if (guests < 1) {
            throw new K2636474BookingException("At least one guest is required.");
        }
        return dataStore.getRooms().stream()
                .filter(room -> type == null || room.getRoomType() == type)
                .filter(K2636474Room::isBookable)
                .filter(room -> guests <= room.getMaxOccupancy())
                .filter(room -> isRoomFreeFor(room, in, out, null))
                .toList();
    }

    /**
     * Checks that no confirmed booking for the room overlaps the given stay.
     * Only bookings that check in before {@code out} can overlap, so the TreeMap
     * {@code headMap} narrows the scan before the overlap test is applied.
     *
     * @param room             room to check
     * @param in               check-in date
     * @param out              check-out date
     * @param excludeBookingId booking to ignore (the one being modified), or null
     * @return true if the room is free for every night of the stay
     */
    public boolean isRoomFreeFor(K2636474Room room, LocalDate in, LocalDate out, String excludeBookingId) {
        return dataStore.getBookingsCheckingInBefore(out).stream()
                .filter(booking -> booking.getRoom() == room)
                .filter(K2636474Booking::isActive)
                .filter(booking -> !booking.getBookingId().equals(excludeBookingId))
                .noneMatch(booking -> booking.overlaps(in, out));
    }

    private boolean hasActiveFutureBookings(K2636474Room room) {
        LocalDate today = LocalDate.now(clock);
        // A stay that has not checked out yet still needs the room.
        return dataStore.getBookingsSortedByCheckIn().stream()
                .anyMatch(booking -> booking.getRoom() == room
                        && booking.isActive()
                        && booking.getCheckOutDate().isAfter(today));
    }
}
