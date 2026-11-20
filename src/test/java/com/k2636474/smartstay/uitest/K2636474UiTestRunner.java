package com.k2636474.smartstay.uitest;

import com.k2636474.smartstay.app.K2636474DataSeeder;
import com.k2636474.smartstay.model.enums.K2636474BedType;
import com.k2636474.smartstay.model.enums.K2636474IdType;
import com.k2636474.smartstay.model.enums.K2636474RoomStatus;
import com.k2636474.smartstay.model.enums.K2636474RoomType;
import com.k2636474.smartstay.pricing.K2636474InvoiceCalculator;
import com.k2636474.smartstay.pricing.K2636474LongStayDiscountPolicy;
import com.k2636474.smartstay.service.K2636474AuthService;
import com.k2636474.smartstay.service.K2636474BookingService;
import com.k2636474.smartstay.service.K2636474DataStore;
import com.k2636474.smartstay.service.K2636474InvoiceService;
import com.k2636474.smartstay.service.K2636474RoomService;
import com.k2636474.smartstay.ui.K2636474DateField;
import com.k2636474.smartstay.ui.K2636474FindRoomPage;
import com.k2636474.smartstay.ui.K2636474FormPanel;
import com.k2636474.smartstay.ui.K2636474MainFrame;
import com.k2636474.smartstay.ui.K2636474MyBookingsPage;
import com.k2636474.smartstay.ui.K2636474NavItem;
import com.k2636474.smartstay.ui.K2636474OverviewPage;
import com.k2636474.smartstay.ui.K2636474Page;
import com.k2636474.smartstay.ui.K2636474PriceBreakdownPanel;
import com.k2636474.smartstay.ui.K2636474ReservationsPage;
import com.k2636474.smartstay.ui.K2636474RoomCard;
import com.k2636474.smartstay.ui.K2636474RoomsPage;
import com.k2636474.smartstay.ui.K2636474UiTheme;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.MenuSelectionManager;
import javax.swing.ToolTipManager;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.k2636474.smartstay.uitest.K2636474UiDriver.button;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.click;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.clickAsync;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.field;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.find;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.findAll;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.first;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.labelTexts;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.onEdt;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.optionMessage;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.rows;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.runOnEdt;
import static com.k2636474.smartstay.uitest.K2636474UiDriver.selectRow;

/**
 * Runs the 47 coursework test cases through the real Swing UI (real frame, panels,
 * dialogs and services), screenshots each result and writes test-evidence/results.csv
 * and results.md. Every "actual" value is read back from the screen.
 *
 * <p>Run with: {@code mvn -q test-compile exec:java -Dexec.classpathScope=test
 * -Dexec.mainClass=com.k2636474.smartstay.uitest.K2636474UiTestRunner}</p>
 */
public final class K2636474UiTestRunner {

    private static final String ADMIN = K2636474DataSeeder.ADMIN_USERNAME;
    private static final String ADMIN_PASSWORD = K2636474DataSeeder.ADMIN_PASSWORD;
    private static final String GUEST_PASSWORD = K2636474DataSeeder.GUEST_PASSWORD;
    private static final String NIMAL = "nimal@example.com";
    private static final String EMMA = "emma@example.com";
    private static final String ERROR_TITLE = "Cannot complete action";
    private static final String[] AMOUNT_NAMES =
            {"Room charge", "Extra guests", "Discount", "Tax", "Total", "Advance", "Final payable"};

    private final Path evidenceDir;
    private final K2636474UiDriver driver;
    private final List<K2636474UiTestResult> results = new ArrayList<>();
    private final LocalDate today = LocalDate.now();
    private K2636474MainFrame frame;
    private JDialog openInvoice;

    private K2636474UiTestRunner(Path evidenceDir) throws Exception {
        this.evidenceDir = evidenceDir;
        Path screenshots = evidenceDir.resolve("screenshots");
        Files.createDirectories(screenshots);
        try (Stream<Path> old = Files.list(screenshots)) {
            for (Path file : old.toList()) {
                Files.delete(file);
            }
        }
        this.driver = new K2636474UiDriver(screenshots);
    }

    /**
     * Runs every test case and writes the evidence files.
     *
     * @param args ignored
     * @throws Exception if the evidence folder cannot be written
     */
    public static void main(String[] args) throws Exception {
        runOnEdt(() -> {
            K2636474UiTheme.install();
            ToolTipManager.sharedInstance().setInitialDelay(0);
            ToolTipManager.sharedInstance().setDismissDelay(60_000);
        });
        K2636474UiTestRunner runner = new K2636474UiTestRunner(Path.of("test-evidence").toAbsolutePath());
        runner.runAll();
        runner.writeResults();
        runOnEdt(() -> runner.frame.dispose());
        long failed = runner.results.stream().filter(result -> !result.passed()).count();
        System.exit(failed == 0 ? 0 : 1);
    }

    private void runAll() {
        driver.parkMouse();
        registrationAndLoginTests();
        roomManagementTests();
        searchAndBookingTests();
        invoiceTests();
        bookingChangeTests();
        reservationSearchTests();
    }

    // ===== TC01-TC14: registration and login ==============================================

    private void registrationAndLoginTests() {
        test("TC01", "Register a local guest with an old-format NIC",
                "NIC 851234567V, Sunil Fernando, 0712223344, sunil@example.com, sunil123 / sunil123",
                "Account created; back on login with \"Account created. Welcome, Sunil Fernando — please log in.\"",
                () -> {
                    freshStart();
                    register(K2636474IdType.NIC, "851234567V", "Sunil Fernando", "0712223344",
                            "sunil@example.com", "sunil123", "sunil123");
                    return registeredOutcome("TC01", "Account created. Welcome, Sunil Fernando — please log in.");
                });
        test("TC02", "Register an international guest with a passport",
                "Passport P9876543, John Smith, +14155550123, john@example.com, john1234 / john1234",
                "Account created and returned to login with the welcome message",
                () -> {
                    register(K2636474IdType.PASSPORT, "P9876543", "John Smith", "+14155550123",
                            "john@example.com", "john1234", "john1234");
                    return registeredOutcome("TC02", "Account created. Welcome, John Smith — please log in.");
                });
        registerRejected("TC03", "Reject an invalid NIC", "NIC 12345, all other fields valid",
                K2636474IdType.NIC, "12345", "t1@example.com", "0771112233", "test123", "test123",
                "idNumber", "NIC must be 9 digits + V/X (e.g. 912345678V) or 12 digits.");
        registerRejected("TC04", "Reject an invalid passport number", "ID type Passport, number AB1, other fields valid",
                K2636474IdType.PASSPORT, "AB1", "t1@example.com", "0771112233", "test123", "test123",
                "idNumber", "Passport number must be 6-9 letters or digits.");
        registerRejected("TC05", "Reject an invalid email", "Email test.example, other fields valid",
                K2636474IdType.NIC, "861234567V", "test.example", "0771112233", "test123", "test123",
                "email", "Enter a valid email such as name@example.com.");
        registerRejected("TC06", "Reject an invalid phone number", "Contact number 12345, other fields valid",
                K2636474IdType.NIC, "861234567V", "t1@example.com", "12345", "test123", "test123",
                "contactNumber",
                "Enter a mobile like 0771234567, +94771234567 or an international number starting with +.");
        registerRejected("TC07", "Reject a duplicate email", "Email nimal@example.com, new NIC 861234567V",
                K2636474IdType.NIC, "861234567V", NIMAL, "0771112233", "test123", "test123",
                "email", "An account with this email already exists.");
        registerRejected("TC08", "Reject a duplicate NIC", "NIC 912345678V (Nimal's), new email t3@example.com",
                K2636474IdType.NIC, "912345678V", "t3@example.com", "0771112233", "test123", "test123",
                "idNumber", "This NIC number is already registered.");
        registerRejected("TC09", "Reject mismatched passwords", "Password test123, confirm test999",
                K2636474IdType.NIC, "861234567V", "t1@example.com", "0771112233", "test123", "test999",
                "confirmPassword", "Passwords do not match.");
        test("TC10", "Reject an empty form", "Press Create account with every field empty",
                "An error under each field: NIC number / Full name / Contact number / Email is required; "
                        + "password \"Use at least 6 characters.\"",
                () -> {
                    register(K2636474IdType.NIC, "", "", "", "", "", "");
                    driver.screenshot("TC10");
                    Map<String, String> errors = registerErrors();
                    boolean pass = "NIC number is required.".equals(errors.get("idNumber"))
                            && "Full name is required.".equals(errors.get("fullName"))
                            && "Contact number is required.".equals(errors.get("contactNumber"))
                            && "Email is required.".equals(errors.get("email"))
                            && "Use at least 6 characters.".equals(errors.get("password"));
                    return outcome(describe(errors), pass);
                });
        test("TC11", "Guest login", NIMAL + " / " + GUEST_PASSWORD,
                "Guest dashboard opens on Find a room; sidebar shows Nimal Perera · Guest",
                () -> {
                    login(NIMAL, GUEST_PASSWORD);
                    driver.screenshot("TC11");
                    List<String> sidebar = sidebarUser();
                    String page = currentPageTitle();
                    return outcome("Page: " + page + "; sidebar: " + String.join(" · ", sidebar),
                            "Find a room".equals(page) && sidebar.equals(List.of("Nimal Perera", "Guest")));
                });
        test("TC12", "Admin login", ADMIN + " / " + ADMIN_PASSWORD,
                "Admin dashboard opens on Overview with the stat cards",
                () -> {
                    login(ADMIN, ADMIN_PASSWORD);
                    driver.screenshot("TC12");
                    String page = currentPageTitle();
                    List<String> labels = labelTexts(currentPage());
                    List<String> cards = List.of("Total rooms", "Available", "Under maintenance", "Active bookings",
                            "Check-ins this week");
                    return outcome("Page: " + page + "; stat cards: "
                                    + cards.stream().filter(labels::contains).collect(Collectors.joining(", ")),
                            "Overview".equals(page) && labels.containsAll(cards));
                });
        test("TC13", "Reject a wrong password", NIMAL + " / wrong",
                "\"Incorrect email/username or password.\" Stays on login",
                () -> {
                    login(NIMAL, "wrong");
                    driver.screenshot("TC13");
                    String message = loginMessage();
                    return outcome(message + " (on login: " + onLoginScreen() + ")",
                            "Incorrect email/username or password.".equals(message) && onLoginScreen());
                });
        test("TC14", "Log out", "From any dashboard press Log out",
                "Back on login with \"You have been logged out.\"",
                () -> {
                    login(ADMIN, ADMIN_PASSWORD);
                    clickNav("Log out");
                    driver.screenshot("TC14");
                    String message = loginMessage();
                    return outcome(message + " (on login: " + onLoginScreen() + ")",
                            "You have been logged out.".equals(message) && onLoginScreen());
                });
    }

    private void registerRejected(String id, String name, String input, K2636474IdType idType, String idNumber,
                                  String email, String contact, String password, String confirm,
                                  String errorField, String expectedMessage) {
        test(id, name, input, "\"" + expectedMessage + "\"" + (id.equals("TC03") ? " No account created" : ""), () -> {
            register(idType, idNumber, "Test User", contact, email, password, confirm);
            driver.screenshot(id);
            Map<String, String> errors = registerErrors();
            boolean stillOnRegister = onEdt(() -> ((java.awt.Component) field(frame, "registerPanel")).isShowing());
            return outcome(describe(errors) + (stillOnRegister ? " (no account created)" : " (left register screen)"),
                    expectedMessage.equals(errors.get(errorField)) && stillOnRegister);
        });
    }

    private K2636474UiTestOutcome registeredOutcome(String id, String expectedMessage) {
        driver.screenshot(id);
        String message = loginMessage();
        return outcome(message + " (on login: " + onLoginScreen() + ")",
                expectedMessage.equals(message) && onLoginScreen());
    }

    // ===== TC15-TC20: room management =====================================================

    private void roomManagementTests() {
        test("TC15", "View all room records", "Fresh start, admin → Rooms",
                "12 rooms R001–R012 with type, bed, daily rate, capacity, breakfast and status; "
                        + "R009 Under Maintenance, R011 Reserved",
                () -> {
                    freshStart();
                    login(ADMIN, ADMIN_PASSWORD);
                    clickNav("Rooms");
                    driver.screenshot("TC15");
                    List<List<String>> rooms = roomRows();
                    List<String> ids = rooms.stream().map(row -> row.get(0)).toList();
                    List<String> expectedIds = Stream.iterate(1, n -> n + 1).limit(12)
                            .map(n -> String.format("R%03d", n)).toList();
                    return outcome(rooms.size() + " rooms " + ids.get(0) + "–" + ids.get(ids.size() - 1)
                                    + "; R009 " + roomRow("R009").get(6) + "; R011 " + roomRow("R011").get(6)
                                    + "; columns " + roomsTable().getColumnCount(),
                            ids.equals(expectedIds) && "Under Maintenance".equals(roomRow("R009").get(6))
                                    && "Reserved".equals(roomRow("R011").get(6)));
                });
        test("TC16", "Add a room", "Add room: Deluxe, King, Available",
                "R013 added: Deluxe, King, LKR 9,000.00, 2 (max 4), breakfast Included, Available",
                () -> {
                    clickAsync(button(roomsPage(), "Add room"));
                    JDialog form = driver.dialog("Add room");
                    fillRoomForm(form, K2636474RoomType.DELUXE, K2636474BedType.KING, K2636474RoomStatus.AVAILABLE);
                    driver.press(form, "Add room");
                    driver.screenshot("TC16");
                    List<String> row = roomRow("R013");
                    return outcome(String.join(", ", row), row.equals(List.of("R013", "Deluxe", "King",
                            "LKR 9,000.00", "2 (max 4)", "Included", "Available")));
                });
        test("TC17", "Update a room", "Edit R013 → type Suite, bed Queen",
                "R013 now Suite, Queen, LKR 14,000.00, 3 (max 5)",
                () -> {
                    selectRow(roomsTable(), "R013");
                    clickAsync(button(roomsPage(), "Edit"));
                    JDialog form = driver.dialog("Edit room R013");
                    fillRoomForm(form, K2636474RoomType.SUITE, K2636474BedType.QUEEN, null);
                    driver.press(form, "Save changes");
                    driver.screenshot("TC17");
                    List<String> row = roomRow("R013");
                    return outcome(String.join(", ", row),
                            row.subList(0, 5).equals(List.of("R013", "Suite", "Queen", "LKR 14,000.00", "3 (max 5)")));
                });
        test("TC18", "Change availability status", "Change status of R005 → Under Maintenance",
                "R005 shows a grey Under Maintenance badge",
                () -> {
                    selectRow(roomsTable(), "R005");
                    click(button(roomsPage(), "Change status"));
                    JMenuItem item = find(null, JMenuItem.class, menu -> "Under Maintenance".equals(menu.getText()));
                    click(item);
                    // doClick() fires the action only; a real click also closes the menu.
                    runOnEdt(() -> MenuSelectionManager.defaultManager().clearSelectedPath());
                    driver.settle();
                    driver.screenshot("TC18");
                    String status = roomRow("R005").get(6);
                    return outcome("R005 status: " + status, "Under Maintenance".equals(status));
                });
        test("TC19", "Remove a room", "Remove R013, confirm (capture the confirmation, then the result)",
                "Confirmation asked; R013 removed; 12 rooms remain",
                () -> {
                    selectRow(roomsTable(), "R013");
                    clickAsync(button(roomsPage(), "Remove"));
                    JDialog confirm = driver.dialog("Remove room");
                    driver.screenshot("TC19a");
                    String question = optionMessage(confirm);
                    driver.press(confirm, "Yes");
                    driver.screenshot("TC19");
                    List<String> ids = roomRows().stream().map(row -> row.get(0)).toList();
                    return outcome("Asked: \"" + question + "\"; " + ids.size() + " rooms remain; R013 listed: "
                            + ids.contains("R013"), question.startsWith("Remove room R013") && ids.size() == 12
                            && !ids.contains("R013"));
                });
        test("TC20", "Block removing a room with bookings", "Remove R004 (booked by BK0002)",
                "\"Room R004 has upcoming confirmed bookings and cannot be removed. Cancel or move them first.\"",
                () -> {
                    selectRow(roomsTable(), "R004");
                    clickAsync(button(roomsPage(), "Remove"));
                    driver.press(driver.dialog("Remove room"), "Yes");
                    JDialog error = driver.dialog(ERROR_TITLE);
                    driver.screenshot("TC20");
                    String message = optionMessage(error);
                    driver.press(error, "OK");
                    return outcome(message + " (R004 still listed: " + !roomRow("R004").isEmpty() + ")",
                            message.equals("Room R004 has upcoming confirmed bookings and cannot be removed. "
                                    + "Cancel or move them first.") && !roomRow("R004").isEmpty());
                });
    }

    // ===== TC21-TC34: search, booking and live pricing ====================================

    private void searchAndBookingTests() {
        test("TC21", "Search by room type and dates", "Fresh start, Nimal → Find a room: Deluxe, T+2 → T+4, 2 guests",
                "R004, R005, R006 shown as room cards",
                () -> {
                    freshStart();
                    login(NIMAL, GUEST_PASSWORD);
                    return searchOutcome("TC21", K2636474RoomType.DELUXE, 2, 4, 2, List.of("R004", "R005", "R006"));
                });
        test("TC22", "Hide rooms under maintenance", "Suite, T+2 → T+3, 2 guests", "R007, R008 shown; R009 not shown",
                () -> searchOutcome("TC22", K2636474RoomType.SUITE, 2, 3, 2, List.of("R007", "R008")));
        test("TC23", "Hide rooms marked Reserved", "Family Suite, T+2 → T+3, 2 guests",
                "R010, R012 shown; R011 not shown",
                () -> searchOutcome("TC23", K2636474RoomType.FAMILY_SUITE, 2, 3, 2, List.of("R010", "R012")));
        test("TC24", "Hide rooms booked for those dates", "Deluxe, T+8 → T+10, 2 guests",
                "R005, R006 shown; R004 not shown (BK0002 holds it T+7 → T+13)",
                () -> searchOutcome("TC24", K2636474RoomType.DELUXE, 8, 10, 2, List.of("R005", "R006")));
        test("TC25", "Respect room capacity", "Standard, T+2 → T+3, 5 guests", "No rooms; empty-state message shown",
                () -> {
                    search(K2636474RoomType.STANDARD, 2, 3, 5);
                    driver.screenshot("TC25");
                    List<String> rooms = roomCardIds();
                    boolean emptyState = !findAll(frame, JLabel.class,
                            label -> "No rooms match those dates".equals(label.getText())).isEmpty();
                    return outcome("Rooms: " + (rooms.isEmpty() ? "none" : rooms)
                            + (emptyState ? "; \"No rooms match those dates\" shown" : ""), rooms.isEmpty() && emptyState);
                });
        test("TC26", "Reject check-in less than 2 days ahead", "Any type, T+1 → T+2, 2 guests",
                "\"Bookings must be made at least 2 days in advance. The earliest check-in is …\" (shows T+2)",
                () -> searchErrorOutcome("TC26", 1, 2, "Bookings must be made at least 2 days in advance. "
                        + "The earliest check-in is " + com.k2636474.smartstay.util.K2636474Format
                        .friendlyDate(today.plusDays(2)) + "."));
        test("TC27", "Reject check-out not after check-in", "Any type, T+5 → T+5",
                "\"Check-out must be at least one night after check-in.\"",
                () -> searchErrorOutcome("TC27", 5, 5, "Check-out must be at least one night after check-in."));
        test("TC28", "Book exactly 2 days ahead (boundary)",
                "Nimal: Standard, T+2 → T+4, 2 guests → Book R003 → Confirm",
                "Booking BK0006 confirmed; LKR 10,000 advance shown; total 13,200.00; final payable 3,200.00",
                () -> {
                    search(K2636474RoomType.STANDARD, 2, 4, 2);
                    JDialog booking = openBookingDialog("R003");
                    driver.screenshot("TC28a");
                    List<String> amounts = breakdownAmounts(booking);
                    clickAsync(button(booking, "Confirm booking"));
                    JDialog saved = driver.dialog("Booking saved");
                    driver.screenshot("TC28");
                    String message = optionMessage(saved);
                    driver.press(saved, "Done");
                    return outcome("Breakdown: " + describeAmounts(amounts) + " — Confirmation: " + message,
                            amountsMatch(amounts, null, null, null, null, "LKR 13,200.00", "− LKR 10,000.00",
                                    "LKR 3,200.00")
                                    && message.contains("Booking BK0006 is confirmed!")
                                    && message.contains("Advance paid: LKR 10,000.00")
                                    && message.contains("Balance at the hotel: LKR 3,200.00"));
                });
        test("TC29", "Prevent double booking", "After TC28: Standard, T+3 → T+5, 2 guests", "R003 not listed",
                () -> {
                    search(K2636474RoomType.STANDARD, 3, 5, 2);
                    driver.screenshot("TC29");
                    List<String> rooms = roomCardIds();
                    return outcome("Rooms: " + rooms, !rooms.contains("R003"));
                });
        test("TC30", "Allow back-to-back stays", "After TC28: Standard, T+4 → T+5, 2 guests", "R003 listed",
                () -> {
                    search(K2636474RoomType.STANDARD, 4, 5, 2);
                    driver.screenshot("TC30");
                    List<String> rooms = roomCardIds();
                    return outcome("Rooms: " + rooms, rooms.contains("R003"));
                });
        priceTest("TC31", "Extra guest charge", "Booking dialog for R002, T+20 → T+22, 3 guests",
                "Base 12,000; extra guests 2,000 (1 × 1,000 × 2); tax 10% 1,400; gross 15,400; final 5,400",
                K2636474RoomType.STANDARD, "R002", 22, 3, null,
                "LKR 12,000.00", "LKR 2,000.00", null, "LKR 1,400.00", "LKR 15,400.00", null, "LKR 5,400.00");
        priceTest("TC32", "No discount at 4 nights", "R002, T+20 → T+24, 2 guests",
                "Base 24,000; discount none; tax 2,400; gross 26,400; final 16,400",
                K2636474RoomType.STANDARD, "R002", 24, 2, null,
                "LKR 24,000.00", null, "None", "LKR 2,400.00", "LKR 26,400.00", null, "LKR 16,400.00");
        priceTest("TC33", "12% discount at 5 nights (boundary)", "R002, T+20 → T+25, 2 guests",
                "Base 30,000; discount 12% −3,600; tax 2,640; gross 29,040; final 19,040",
                K2636474RoomType.STANDARD, "R002", 25, 2, null,
                "LKR 30,000.00", null, "− LKR 3,600.00", "LKR 2,640.00", "LKR 29,040.00", null, "LKR 19,040.00");
        String input34 = "1 night (T+20 → T+21) at base capacity: R002 Standard (2), R005 Deluxe (2), "
                + "R008 Suite (3), R012 Family Suite (4)";
        String expected34 = "Gross: Standard 6,600 (10%), Deluxe 10,080 (12%), Suite 16,100 (15%), "
                + "Family Suite 20,700 (15%)";
        test("TC34", "Tax rate by room type", input34, expected34, () -> {
            List<K2636474UiTestOutcome> parts = List.of(
                    priceCheck("TC34a", K2636474RoomType.STANDARD, "R002", 21, 2, "10%",
                            null, null, null, null, "LKR 6,600.00", null, null),
                    priceCheck("TC34b", K2636474RoomType.DELUXE, "R005", 21, 2, "12%",
                            null, null, null, null, "LKR 10,080.00", null, null),
                    priceCheck("TC34c", K2636474RoomType.SUITE, "R008", 21, 3, "15%",
                            null, null, null, null, "LKR 16,100.00", null, null),
                    priceCheck("TC34d", K2636474RoomType.FAMILY_SUITE, "R012", 21, 4, "15%",
                            null, null, null, null, "LKR 20,700.00", null, null));
            return outcome(parts.stream().map(K2636474UiTestOutcome::actual).collect(Collectors.joining(" || ")),
                    parts.stream().allMatch(K2636474UiTestOutcome::passed));
        });
    }

    private void priceTest(String id, String name, String input, String expected, K2636474RoomType type,
                           String roomId, int outDays, int guests, String taxRate, String... expectedAmounts) {
        test(id, name, input, expected,
                () -> priceCheck(id, type, roomId, outDays, guests, taxRate, expectedAmounts));
    }

    /** Opens the booking dialog for a T+20 stay, screenshots its live breakdown and closes it. */
    private K2636474UiTestOutcome priceCheck(String screenshot, K2636474RoomType type, String roomId, int outDays,
                                             int guests, String taxRate, String... expectedAmounts) {
        search(type, 20, outDays, guests);
        JDialog booking = openBookingDialog(roomId);
        driver.screenshot(screenshot);
        List<String> amounts = breakdownAmounts(booking);
        String taxLine = labelTexts(find(booking, K2636474PriceBreakdownPanel.class, panel -> true)).stream()
                .filter(text -> text.matches("\\d+% of LKR .*")).findFirst().orElse("");
        driver.press(booking, "Close");
        boolean taxOk = taxRate == null || taxLine.startsWith(taxRate + " of");
        return outcome(roomId + ": " + describeAmounts(amounts) + (taxRate == null ? "" : "; tax " + taxLine),
                amountsMatch(amounts, expectedAmounts) && taxOk);
    }

    // ===== TC35-TC36: invoice ==============================================================

    private void invoiceTests() {
        test("TC35", "Detailed invoice with every required field", "Admin → Reservations → BK0002 → View invoice",
                "Room R004 Deluxe, Double, 9,000; guest Kamala Silva; 6 nights; 4 guests; room 54,000; extra 18,000; "
                        + "discount −6,480; tax 12% 7,862.40; gross 73,382.40; advance −10,000; final 63,382.40",
                () -> {
                    login(ADMIN, ADMIN_PASSWORD);
                    clickNav("Reservations");
                    selectRow(reservationsTable(), "BK0002");
                    clickAsync(button(currentPage(), "View invoice"));
                    openInvoice = driver.dialog("Invoice INV");
                    driver.screenshot("TC35");
                    return invoiceOutcome(invoiceText(openInvoice), new String[][]{
                            {"Room R004", "Deluxe, Double bed"}, {"Daily rate", "LKR 9,000.00"},
                            {"Name", "Kamala Silva"}, {"NIC", "199512345678"}, {"Contact", "+94712345678"},
                            {"Email", "kamala@example.com"}, {"Check-in", today.plusDays(7).toString()},
                            {"Check-out", today.plusDays(13).toString()}, {"Nights", "6"}, {"Guests", "4 "},
                            {"Room charge", "LKR 54,000.00"}, {"Extra guests", "LKR 18,000.00"},
                            {"Discount (12%", "-LKR 6,480.00"}, {"Tax (12%)", "LKR 7,862.40"},
                            {"Gross total", "LKR 73,382.40"}, {"Advance deducted", "-LKR 10,000.00"},
                            {"FINAL PAYABLE", "LKR 63,382.40"}});
                });
        test("TC36", "Save the invoice", "In the invoice dialog press Save as .txt → test-evidence/TC36_invoice.txt",
                "A .txt file with the same invoice is saved",
                () -> {
                    JDialog invoice = openInvoice;
                    String shown = invoiceText(invoice);
                    Path target = evidenceDir.resolve("TC36_invoice.txt");
                    Files.deleteIfExists(target);
                    clickAsync(button(invoice, "Save as .txt"));
                    JDialog chooserDialog = driver.dialog("Save invoice");
                    JFileChooser chooser = find(chooserDialog, JFileChooser.class, fileChooser -> true);
                    runOnEdt(() -> chooser.setSelectedFile(target.toFile()));
                    driver.settle();
                    driver.screenshot("TC36a");
                    runOnEdt(chooser::approveSelection);
                    JDialog savedDialog = driver.dialog("Invoice saved");
                    driver.screenshot("TC36");
                    String message = optionMessage(savedDialog);
                    driver.press(savedDialog, "OK");
                    String saved = Files.exists(target) ? Files.readString(target, StandardCharsets.UTF_8) : "";
                    showSavedFile(invoice, target, saved);
                    driver.press(invoice, "Close");
                    return outcome(message + "; file " + saved.lines().count() + " lines, identical to dialog: "
                            + saved.equals(shown), saved.equals(shown) && !saved.isEmpty());
                });
    }

    /** Shows the saved file in a plain read-only window so its contents appear in the evidence. */
    private void showSavedFile(JDialog invoice, Path file, String contents) {
        JDialog viewer = onEdt(() -> {
            // Owned by the invoice dialog so it stays on top of it; offset so both are visible.
            JDialog window = new JDialog(invoice, "Saved file: " + file.getFileName(), false);
            JTextArea text = new JTextArea(contents);
            text.setEditable(false);
            text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            window.add(new JScrollPane(text));
            window.setSize(600, 720);
            window.setLocation(frame.getX() + frame.getWidth() - 620, frame.getY() + 50);
            window.setVisible(true);
            return window;
        });
        driver.settle();
        driver.screenshot("TC36b");
        runOnEdt(viewer::dispose);
    }

    // ===== TC37-TC40: modify and cancel ====================================================

    private void bookingChangeTests() {
        test("TC37", "Modify an eligible booking",
                "Fresh start, Emma → My bookings → BK0005 → Modify: check-out T+65 (5 nights); then view the invoice",
                "Booking updated; invoice regenerated with 12% discount: final payable 19,040",
                () -> {
                    freshStart();
                    login(EMMA, GUEST_PASSWORD);
                    clickNav("My bookings");
                    selectRow(myBookingsTable(), "BK0005");
                    clickAsync(button(currentPage(), "Modify"));
                    JDialog modify = driver.dialog("Modify booking");
                    runOnEdt(() -> ((K2636474DateField) field(modify, "checkOutField")).setDate(today.plusDays(65)));
                    driver.settle();
                    clickAsync(button(modify, "Save changes"));
                    JDialog saved = driver.dialog("Booking saved");
                    driver.screenshot("TC37");
                    String message = optionMessage(saved);
                    driver.press(saved, "View invoice");
                    JDialog invoice = driver.dialog("Invoice INV");
                    driver.screenshot("TC37b");
                    K2636474UiTestOutcome invoiceCheck = invoiceOutcome(invoiceText(invoice), new String[][]{
                            {"Check-out", today.plusDays(65).toString()}, {"Nights", "5"},
                            {"Discount (12%", "-LKR 3,600.00"}, {"FINAL PAYABLE", "LKR 19,040.00"}});
                    driver.press(invoice, "Close");
                    return outcome(message + " — Invoice: " + invoiceCheck.actual(),
                            message.contains("Booking BK0005 has been updated.") && invoiceCheck.passed());
                });
        tooltipTest("TC38", "Block changes within 24h of check-in",
                "Nimal → My bookings → select BK0001 (checks in today); hover Cancel",
                "Modify and Cancel disabled; tooltip \"Changes are only allowed more than 24 hours before check-in (…)\"",
                NIMAL, "BK0001", "Changes are only allowed more than 24 hours before check-in (");
        test("TC39", "Cancel an eligible booking", "Emma → BK0005 → Cancel booking → Yes",
                "Status Cancelled; \"Advance of LKR 10,000.00 refunded.\" Invoice marked cancelled with nothing payable",
                () -> {
                    login(EMMA, GUEST_PASSWORD);
                    clickNav("My bookings");
                    selectRow(myBookingsTable(), "BK0005");
                    clickAsync(button(currentPage(), "Cancel booking"));
                    JDialog confirm = driver.dialog("Cancel booking");
                    driver.screenshot("TC39a");
                    driver.press(confirm, "Yes");
                    JDialog done = driver.dialog("Booking cancelled");
                    driver.screenshot("TC39");
                    String message = optionMessage(done);
                    driver.press(done, "OK");
                    String status = bookingRow(myBookingsTable(), "BK0005").get(9);
                    selectRow(myBookingsTable(), "BK0005");
                    clickAsync(button(currentPage(), "View invoice"));
                    JDialog invoice = driver.dialog("Invoice INV");
                    driver.screenshot("TC39b");
                    K2636474UiTestOutcome invoiceCheck = invoiceOutcome(invoiceText(invoice), new String[][]{
                            {"Status", "Cancelled"}, {"Advance deducted", "LKR 0.00"}, {"FINAL PAYABLE", "LKR 0.00"},
                            {"Booking cancelled", "Booking cancelled - advance of LKR 10,000.00 refunded."}});
                    driver.press(invoice, "Close");
                    return outcome("Status " + status + "; " + message + " — Invoice: " + invoiceCheck.actual(),
                            "Cancelled".equals(status) && message.contains("Advance of LKR 10,000.00 refunded.")
                                    && invoiceCheck.passed());
                });
        tooltipTest("TC40", "Block changing a cancelled booking", "Nimal → select BK0004 (cancelled); hover Cancel",
                "Modify and Cancel disabled; tooltip \"This booking is cancelled and can no longer be changed.\"",
                NIMAL, "BK0004", "This booking is cancelled and can no longer be changed.");
    }

    private void tooltipTest(String id, String name, String input, String expected, String loginId,
                             String bookingId, String expectedTip) {
        test(id, name, input, expected, () -> {
            login(loginId, GUEST_PASSWORD);
            clickNav("My bookings");
            selectRow(myBookingsTable(), bookingId);
            driver.settle();
            K2636474MyBookingsPage page = (K2636474MyBookingsPage) currentPage();
            JButton modify = (JButton) field(page, "modifyButton");
            JButton cancel = (JButton) field(page, "cancelButton");
            boolean modifyEnabled = onEdt(modify::isEnabled);
            boolean cancelEnabled = onEdt(cancel::isEnabled);
            // Click the sidebar logo (inert) so the window is focused and Swing will show tooltips.
            driver.focus(frame, 120, 70);
            String tip = driver.hoverForTooltip(cancel);
            driver.screenshot(id);
            driver.parkMouse();
            return outcome("Modify enabled: " + modifyEnabled + "; Cancel enabled: " + cancelEnabled
                    + "; tooltip: \"" + tip + "\"", !modifyEnabled && !cancelEnabled && tip.startsWith(expectedTip));
        });
    }

    // ===== TC41-TC47: reservation search, guests and overview ==============================

    private void reservationSearchTests() {
        test("TC41", "Search by booking ID", "Admin → Reservations, search BK0002", "One result: BK0002, Kamala Silva, R004",
                () -> {
                    login(ADMIN, ADMIN_PASSWORD);
                    clickNav("Reservations");
                    List<List<String>> found = searchReservations("BK0002");
                    driver.screenshot("TC41");
                    return outcome(describeBookings(found), found.size() == 1
                            && found.get(0).subList(0, 3).equals(List.of("BK0002", "Kamala Silva", "R004")));
                });
        test("TC42", "Search by guest name", "Admin, search emma", "BK0003 and BK0005 (Emma Clarke)",
                () -> {
                    List<List<String>> found = searchReservations("emma");
                    driver.screenshot("TC42");
                    return outcome(describeBookings(found), found.stream().map(row -> row.get(0)).toList()
                            .equals(List.of("BK0003", "BK0005"))
                            && found.stream().allMatch(row -> "Emma Clarke".equals(row.get(1))));
                });
        test("TC43", "View reservations by check-in date", "Admin, check-in date filter T+4", "BK0003 (Emma Clarke, R010)",
                () -> {
                    K2636474ReservationsPage page = (K2636474ReservationsPage) currentPage();
                    runOnEdt(() -> ((JTextField) field(page, "searchField")).setText(""));
                    click((JCheckBox) field(page, "dateFilterBox"));
                    runOnEdt(() -> ((K2636474DateField) field(page, "dateField")).setDate(today.plusDays(4)));
                    driver.settle();
                    driver.screenshot("TC43");
                    List<List<String>> found = rows(reservationsTable());
                    return outcome(describeBookings(found), found.size() == 1
                            && found.get(0).subList(0, 3).equals(List.of("BK0003", "Emma Clarke", "R010")));
                });
        test("TC44", "Guests only see their own reservations", "Nimal → Search reservations, search Emma", "No results",
                () -> {
                    login(NIMAL, GUEST_PASSWORD);
                    clickNav("Search reservations");
                    List<List<String>> found = searchReservations("Emma");
                    driver.screenshot("TC44");
                    return outcome(found.isEmpty() ? "No results; " + emptyStateText() : describeBookings(found),
                            found.isEmpty());
                });
        test("TC45", "Unknown booking ID", "Admin, search BK9999", "Empty-state message, no results",
                () -> {
                    login(ADMIN, ADMIN_PASSWORD);
                    clickNav("Reservations");
                    List<List<String>> found = searchReservations("BK9999");
                    driver.screenshot("TC45");
                    String empty = emptyStateText();
                    return outcome(found.isEmpty() ? "No results; " + empty : describeBookings(found),
                            found.isEmpty() && empty.contains("No reservations found"));
                });
        test("TC46", "View registered guests", "Admin → Guests",
                "Nimal Perera, Kamala Silva, Emma Clarke with ID type/number, contact and email",
                () -> {
                    clickNav("Guests");
                    driver.screenshot("TC46");
                    List<List<String>> guests = rows(find(currentPage(), JTable.class, table -> true));
                    List<List<String>> expectedGuests = List.of(
                            List.of("Nimal Perera", "NIC", "912345678V", "0771234567", NIMAL),
                            List.of("Kamala Silva", "NIC", "199512345678", "+94712345678", "kamala@example.com"),
                            List.of("Emma Clarke", "Passport", "N1234567", "+447700900123", EMMA));
                    List<List<String>> shown = guests.stream().map(row -> row.subList(1, 6)).toList();
                    return outcome(shown.stream().map(row -> String.join(", ", row)).collect(Collectors.joining(" | ")),
                            shown.equals(expectedGuests));
                });
        test("TC47", "Overview statistics", "Fresh start, admin → Overview",
                "12 rooms, 10 available, 1 under maintenance, 4 active bookings, arrivals in the next 7 days listed",
                () -> {
                    freshStart();
                    login(ADMIN, ADMIN_PASSWORD);
                    driver.screenshot("TC47");
                    K2636474OverviewPage page = (K2636474OverviewPage) currentPage();
                    String total = statValue(page, "totalRoomsValue");
                    String available = statValue(page, "availableValue");
                    String maintenance = statValue(page, "maintenanceValue");
                    String active = statValue(page, "activeBookingsValue");
                    List<String> arrivals = rows(find(page, JTable.class, table -> true)).stream()
                            .map(row -> row.get(0)).toList();
                    return outcome(total + " rooms, " + available + " available, " + maintenance
                                    + " under maintenance, " + active + " active bookings; arrivals: " + arrivals,
                            "12".equals(total) && "10".equals(available) && "1".equals(maintenance)
                                    && "4".equals(active) && !arrivals.isEmpty());
                });
    }

    // ===== App lifecycle and navigation ====================================================

    /** Clears the data, re-seeds for today and opens a brand-new main window. */
    private void freshStart() {
        runOnEdt(() -> {
            if (frame != null) {
                frame.dispose();
            }
        });
        K2636474UiDriver.closeStrayWindows();
        K2636474DataStore dataStore = K2636474DataStore.getInstance();
        dataStore.clear();
        // Same wiring as K2636474SmartStayApp.main.
        Clock clock = Clock.systemDefaultZone();
        K2636474AuthService authService = new K2636474AuthService(dataStore);
        K2636474RoomService roomService = new K2636474RoomService(dataStore, clock);
        K2636474InvoiceService invoiceService = new K2636474InvoiceService(dataStore,
                new K2636474InvoiceCalculator(new K2636474LongStayDiscountPolicy()), clock);
        K2636474BookingService bookingService = new K2636474BookingService(dataStore, roomService,
                invoiceService, clock);
        new K2636474DataSeeder(authService, roomService, bookingService).seed(LocalDate.now(clock));
        frame = onEdt(() -> {
            K2636474MainFrame mainFrame = new K2636474MainFrame(authService, roomService, bookingService,
                    invoiceService);
            mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            mainFrame.setSize(1280, 800);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setAlwaysOnTop(true);
            mainFrame.setVisible(true);
            mainFrame.toFront();
            return mainFrame;
        });
        driver.settle();
    }

    private void login(String loginId, String password) {
        if (first(frame, K2636474NavItem.class, item -> "Log out".equals(item.getToolTipText())) != null) {
            clickNav("Log out");
        }
        Object loginPanel = field(frame, "loginPanel");
        runOnEdt(() -> {
            ((JTextField) field(loginPanel, "loginField")).setText(loginId);
            ((JPasswordField) field(loginPanel, "passwordField")).setText(password);
            ((JButton) field(loginPanel, "loginButton")).doClick();
        });
        driver.settle();
    }

    private void clickNav(String label) {
        K2636474NavItem item = find(frame, K2636474NavItem.class, nav -> label.equals(nav.getToolTipText()));
        runOnEdt(() -> item.dispatchEvent(new MouseEvent(item, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
                0, 10, 10, 1, false)));
        driver.settle();
    }

    private boolean onLoginScreen() {
        return onEdt(() -> ((java.awt.Component) field(frame, "loginPanel")).isShowing());
    }

    private String loginMessage() {
        return onEdt(() -> ((JLabel) field(field(frame, "loginPanel"), "messageLabel")).getText());
    }

    private K2636474Page currentPage() {
        return find(frame, K2636474Page.class, page -> true);
    }

    private String currentPageTitle() {
        return labelTexts(currentPage()).get(0);
    }

    private List<String> sidebarUser() {
        List<String> labels = labelTexts(frame);
        int index = labels.indexOf("Nimal Perera");
        return index < 0 ? labels : labels.subList(index, Math.min(index + 2, labels.size()));
    }

    // ===== Registration helpers ============================================================

    private void register(K2636474IdType idType, String idNumber, String fullName, String contact, String email,
                          String password, String confirm) {
        if (first(frame, JButton.class, b -> "Create an account".equals(b.getText())) == null) {
            click(button(frame, "Back to login"));
            if (first(frame, JButton.class, b -> "Create an account".equals(b.getText())) == null) {
                clickNav("Log out");
            }
        }
        click(button(frame, "Create an account"));
        Object panel = field(frame, "registerPanel");
        runOnEdt(() -> {
            ((JComboBox<?>) field(panel, "idTypeBox")).setSelectedItem(idType);
            ((JTextField) field(panel, "idNumberField")).setText(idNumber);
            ((JTextField) field(panel, "fullNameField")).setText(fullName);
            ((JTextField) field(panel, "contactField")).setText(contact);
            ((JTextField) field(panel, "emailField")).setText(email);
            ((JPasswordField) field(panel, "passwordField")).setText(password);
            ((JPasswordField) field(panel, "confirmField")).setText(confirm);
        });
        click(button(frame, "Create account"));
        driver.settle();
    }

    /** Inline error texts currently shown under the register form's fields, by field name. */
    @SuppressWarnings("unchecked")
    private Map<String, String> registerErrors() {
        return onEdt(() -> {
            Object panel = field(frame, "registerPanel");
            List<K2636474FormPanel> forms = new ArrayList<>();
            forms.add((K2636474FormPanel) field(panel, "form"));
            forms.addAll(((Map<String, K2636474FormPanel>) field(panel, "columns")).values());
            Map<String, String> errors = new LinkedHashMap<>();
            for (String name : List.of("idNumber", "fullName", "contactNumber", "email", "password",
                    "confirmPassword")) {
                for (K2636474FormPanel form : forms) {
                    JLabel label = ((Map<String, JLabel>) field(form, "errorLabels")).get(name);
                    if (label != null && !label.getText().isBlank()) {
                        errors.put(name, label.getText());
                    }
                }
            }
            return errors;
        });
    }

    private static String describe(Map<String, String> errors) {
        return errors.isEmpty() ? "No errors shown" : errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": \"" + entry.getValue() + "\"").collect(Collectors.joining("; "));
    }

    // ===== Rooms helpers ===================================================================

    private K2636474RoomsPage roomsPage() {
        return find(frame, K2636474RoomsPage.class, page -> true);
    }

    private JTable roomsTable() {
        return (JTable) field(roomsPage(), "table");
    }

    private List<List<String>> roomRows() {
        return rows(roomsTable());
    }

    private List<String> roomRow(String roomId) {
        return roomRows().stream().filter(row -> row.get(0).equals(roomId)).findFirst().orElse(List.of());
    }

    private void fillRoomForm(JDialog form, K2636474RoomType type, K2636474BedType bed, K2636474RoomStatus status) {
        runOnEdt(() -> {
            ((JComboBox<?>) field(form, "typeBox")).setSelectedItem(type);
            ((JComboBox<?>) field(form, "bedBox")).setSelectedItem(bed);
            if (status != null) {
                ((JComboBox<?>) field(form, "statusBox")).setSelectedItem(status);
            }
        });
        driver.settle();
    }

    // ===== Find-a-room and booking helpers =================================================

    private void search(K2636474RoomType type, int inDays, int outDays, int guests) {
        K2636474FindRoomPage page = find(frame, K2636474FindRoomPage.class, findPage -> true);
        runOnEdt(() -> {
            JComboBox<?> typeBox = (JComboBox<?>) field(page, "typeBox");
            if (type == null) {
                typeBox.setSelectedIndex(0);
            } else {
                typeBox.setSelectedItem(type);
            }
            ((K2636474DateField) field(page, "checkInField")).setDate(today.plusDays(inDays));
            ((K2636474DateField) field(page, "checkOutField")).setDate(today.plusDays(outDays));
            ((JSpinner) field(page, "guestsSpinner")).setValue(guests);
        });
        clickAsync(button(page, "Search"));
        driver.settle();
    }

    private K2636474UiTestOutcome searchOutcome(String id, K2636474RoomType type, int inDays, int outDays, int guests,
                                                List<String> expectedRooms) {
        search(type, inDays, outDays, guests);
        driver.screenshot(id);
        List<String> rooms = roomCardIds();
        return outcome("Room cards: " + String.join(", ", rooms), rooms.equals(expectedRooms));
    }

    private K2636474UiTestOutcome searchErrorOutcome(String id, int inDays, int outDays, String expectedMessage) {
        search(null, inDays, outDays, 2);
        JDialog error = driver.dialog(ERROR_TITLE);
        driver.screenshot(id);
        String message = optionMessage(error);
        driver.press(error, "OK");
        return outcome(message, message.equals(expectedMessage));
    }

    private List<String> roomCardIds() {
        List<String> ids = new ArrayList<>();
        for (K2636474RoomCard card : findAll(frame, K2636474RoomCard.class, roomCard -> true)) {
            labelTexts(card).stream().filter(text -> text.startsWith("ROOM ")).findFirst()
                    .ifPresent(text -> ids.add(text.substring("ROOM ".length())));
        }
        return ids;
    }

    private JDialog openBookingDialog(String roomId) {
        clickAsync(button(frame, "Book room " + roomId));
        return driver.dialog("Book a room");
    }

    /** The seven amounts on the price breakdown card, top to bottom, exactly as displayed. */
    private List<String> breakdownAmounts(JDialog booking) {
        return labelTexts(find(booking, K2636474PriceBreakdownPanel.class, panel -> true)).stream()
                .filter(text -> text.startsWith("LKR ") || text.startsWith("− LKR ") || text.equals("None"))
                .toList();
    }

    private static boolean amountsMatch(List<String> actual, String... expected) {
        if (actual.size() != expected.length) {
            return false;
        }
        for (int index = 0; index < expected.length; index++) {
            if (expected[index] != null && !expected[index].equals(actual.get(index))) {
                return false;
            }
        }
        return true;
    }

    private static String describeAmounts(List<String> amounts) {
        List<String> parts = new ArrayList<>();
        for (int index = 0; index < amounts.size(); index++) {
            parts.add((index < AMOUNT_NAMES.length ? AMOUNT_NAMES[index] : "?") + " " + amounts.get(index));
        }
        return String.join("; ", parts);
    }

    // ===== Bookings, reservations and invoice helpers ======================================

    private JTable myBookingsTable() {
        return (JTable) field(find(frame, K2636474MyBookingsPage.class, page -> true), "table");
    }

    private JTable reservationsTable() {
        return (JTable) field(find(frame, K2636474ReservationsPage.class, page -> true), "table");
    }

    private static List<String> bookingRow(JTable table, String bookingId) {
        return rows(table).stream().filter(row -> row.get(0).equals(bookingId)).findFirst().orElse(List.of());
    }

    private List<List<String>> searchReservations(String query) {
        K2636474ReservationsPage page = find(frame, K2636474ReservationsPage.class, reservations -> true);
        runOnEdt(() -> ((JTextField) field(page, "searchField")).setText(query));
        driver.settle();
        return rows(reservationsTable());
    }

    private String emptyStateText() {
        return String.join(" ", labelTexts(currentPage()).stream()
                .filter(text -> text.startsWith("No ") || text.startsWith("Try ")).toList());
    }

    private static String describeBookings(List<List<String>> bookings) {
        return bookings.isEmpty() ? "No results" : bookings.size() + " result(s): " + bookings.stream()
                .map(row -> String.join(", ", row.subList(0, 3))).collect(Collectors.joining(" | "));
    }

    private static String statValue(K2636474OverviewPage page, String fieldName) {
        return onEdt(() -> ((JLabel) field(page, fieldName)).getText());
    }

    private static String invoiceText(JDialog invoice) {
        return onEdt(() -> find(invoice, JTextArea.class, area -> true).getText());
    }

    /**
     * Checks invoice lines: each pair is {line prefix, text that line must contain}.
     * The actual value lists every matched line exactly as printed (spaces collapsed).
     */
    private static K2636474UiTestOutcome invoiceOutcome(String text, String[][] checks) {
        List<String> found = new ArrayList<>();
        boolean pass = true;
        for (String[] check : checks) {
            String line = text.lines().map(candidate -> candidate.trim().replaceAll("\\s+", " "))
                    .filter(candidate -> candidate.startsWith(check[0])).findFirst().orElse("");
            found.add(line.isEmpty() ? "[missing " + check[0] + "]" : line);
            pass &= line.contains(check[1]);
        }
        return outcome(String.join(" | ", found), pass);
    }

    // ===== Recording results ===============================================================

    private void test(String id, String name, String input, String expected, K2636474UiTestStep step) {
        K2636474UiTestOutcome outcome;
        try {
            outcome = step.run();
        } catch (Exception | AssertionError error) {
            driver.screenshot(id + "_error");
            outcome = outcome("ERROR: " + error.getMessage(), false);
            K2636474UiDriver.closeStrayWindows();
        }
        K2636474UiTestResult result = new K2636474UiTestResult(id, name, input, expected, outcome.actual(),
                outcome.passed());
        results.add(result);
        System.out.println(id + " " + (result.passed() ? "PASS" : "FAIL") + " — " + outcome.actual());
    }

    private static K2636474UiTestOutcome outcome(String actual, boolean passed) {
        return new K2636474UiTestOutcome(actual, passed);
    }

    private void writeResults() throws IOException {
        long passed = results.stream().filter(K2636474UiTestResult::passed).count();
        String runAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        StringBuilder csv = new StringBuilder("test_id,test_case,input,expected,actual,result\n");
        for (K2636474UiTestResult result : results) {
            csv.append(Stream.of(result.id(), result.name(), result.input(), result.expected(), result.actual(),
                            result.passed() ? "PASS" : "FAIL")
                    .map(value -> "\"" + value.replace("\"", "\"\"") + "\"").collect(Collectors.joining(",")))
                    .append('\n');
        }
        Files.writeString(evidenceDir.resolve("results.csv"), csv.toString(), StandardCharsets.UTF_8);

        StringBuilder markdown = new StringBuilder("# SmartStay — automated UI test results\n\n")
                .append("**").append(passed).append('/').append(results.size()).append(" passed** · run ")
                .append(runAt).append(" · T (today) = ").append(today).append("\n\n")
                .append("Screenshots: `screenshots/<test id>.png` (extra steps as a/b).\n\n")
                .append("| ID | Test case | Input | Expected | Actual | Result |\n|---|---|---|---|---|---|\n");
        for (K2636474UiTestResult result : results) {
            markdown.append("| ").append(Stream.of(result.id(), result.name(), result.input(), result.expected(),
                            result.actual(), result.passed() ? "PASS" : "**FAIL**")
                    .map(value -> value.replace("|", "\\|")).collect(Collectors.joining(" | "))).append(" |\n");
        }
        Files.writeString(evidenceDir.resolve("results.md"), markdown.toString(), StandardCharsets.UTF_8);
        System.out.println(passed + "/" + results.size() + " passed");
    }

    /** One test's body; returns what the UI actually showed and whether it matched. */
    @FunctionalInterface
    private interface K2636474UiTestStep {
        K2636474UiTestOutcome run() throws Exception;
    }

    /** What the UI showed for one test and whether it matched the expected result. */
    private static final class K2636474UiTestOutcome {
        private final String actual;
        private final boolean passed;

        K2636474UiTestOutcome(String actual, boolean passed) {
            this.actual = actual;
            this.passed = passed;
        }

        String actual() {
            return actual;
        }

        boolean passed() {
            return passed;
        }
    }

    /** One row of the results table. */
    private static final class K2636474UiTestResult {
        private final String id;
        private final String name;
        private final String input;
        private final String expected;
        private final String actual;
        private final boolean passed;

        K2636474UiTestResult(String id, String name, String input, String expected, String actual, boolean passed) {
            this.id = id;
            this.name = name;
            this.input = input;
            this.expected = expected;
            this.actual = actual;
            this.passed = passed;
        }

        String id() {
            return id;
        }

        String name() {
            return name;
        }

        String input() {
            return input;
        }

        String expected() {
            return expected;
        }

        String actual() {
            return actual;
        }

        boolean passed() {
            return passed;
        }
    }
}
