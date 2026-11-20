package com.k2636474.smartstay.model.room;

import com.k2636474.smartstay.model.enums.K2636474RoomType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Factory that hands out the package for a room type. Packages hold no per-room
 * state, so one shared instance per type is cached (Flyweight-style): fifty Deluxe
 * rooms all point at the same Deluxe package object.
 */
public final class K2636474RoomPackageFactory {

    /** EnumMap: array-backed, so lookup by enum key is O(1) and very compact. */
    private static final Map<K2636474RoomType, K2636474RoomPackage> PACKAGES =
            new EnumMap<>(K2636474RoomType.class);

    private K2636474RoomPackageFactory() {
    }

    /**
     * Returns the shared package for a room type, creating it on first use.
     *
     * @param roomType the room type; must not be null
     * @return the single shared package instance for that type
     * @throws IllegalArgumentException if {@code roomType} is null
     */
    public static synchronized K2636474RoomPackage getPackage(K2636474RoomType roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("Room type is required.");
        }
        return PACKAGES.computeIfAbsent(roomType, K2636474RoomPackageFactory::createPackage);
    }

    private static K2636474RoomPackage createPackage(K2636474RoomType roomType) {
        return switch (roomType) {
            case STANDARD -> new K2636474StandardPackage();
            case DELUXE -> new K2636474DeluxePackage();
            case SUITE -> new K2636474SuitePackage();
            case FAMILY_SUITE -> new K2636474FamilySuitePackage();
        };
    }
}
