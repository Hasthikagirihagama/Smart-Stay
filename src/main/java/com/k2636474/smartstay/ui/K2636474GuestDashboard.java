package com.k2636474.smartstay.ui;

/**
 * Guest dashboard: find and book rooms, manage own bookings, search own reservations.
 */
public class K2636474GuestDashboard extends K2636474DashboardShell {

    private static final String FIND_ROOM = "find";
    private static final String MY_BOOKINGS = "bookings";
    private static final String SEARCH = "search";

    /**
     * Creates the guest dashboard and opens the room search.
     *
     * @param context  UI context
     * @param onLogout called when the guest logs out
     */
    public K2636474GuestDashboard(K2636474UiContext context, Runnable onLogout) {
        super(context, onLogout);
        addPage(FIND_ROOM, "Find a room", "search", new K2636474FindRoomPage(context));
        addPage(MY_BOOKINGS, "My bookings", "bed", new K2636474MyBookingsPage(context));
        addPage(SEARCH, "Search reservations", "invoice", new K2636474ReservationsPage(context,
                "Search reservations", "Find your reservations by booking ID, name or check-in date."));
        showPage(FIND_ROOM);
    }
}
