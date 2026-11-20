package com.k2636474.smartstay.ui;

/**
 * Administrator dashboard: overview, room management, all reservations and guests.
 */
public class K2636474AdminDashboard extends K2636474DashboardShell {

    private static final String OVERVIEW = "overview";
    private static final String ROOMS = "rooms";
    private static final String RESERVATIONS = "reservations";
    private static final String GUESTS = "guests";

    /**
     * Creates the admin dashboard and opens the overview.
     *
     * @param context  UI context
     * @param onLogout called when the admin logs out
     */
    public K2636474AdminDashboard(K2636474UiContext context, Runnable onLogout) {
        super(context, onLogout);
        addPage(OVERVIEW, "Overview", "dashboard", new K2636474OverviewPage(context));
        addPage(ROOMS, "Rooms", "door", new K2636474RoomsPage(context));
        addPage(RESERVATIONS, "Reservations", "calendar", new K2636474ReservationsPage(context, "Reservations",
                "Every booking in the hotel. Search by booking ID, guest name or check-in date."));
        addPage(GUESTS, "Guests", "users", new K2636474GuestsPage(context));
        showPage(OVERVIEW);
    }
}
