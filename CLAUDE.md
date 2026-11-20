# SmartStay Hotel Booking System — Build Brief

Kingston University CI6115 Programming III (Patterns and Algorithms), Coursework 1.
Student ID: **K2636474**. Deadline: 24/11/2026.

Read this whole file before writing any code. It is the single source of truth for the design.
The class diagram and the written report are produced from the code you build, so follow the
names, packages and rules here exactly. If something here is impossible or contradictory, stop
and ask instead of improvising.

---

## 1. Goals and marking (why things are the way they are)

Target grade: distinction. The marker awards:

- 30 marks: every functional requirement works, with business rules correct at their exact boundaries
- 10 marks: real use of OOP (abstraction, inheritance, polymorphism, encapsulation, interfaces)
- 5 marks: data structures chosen for a reason
- 5 marks: clear algorithms
- 10 marks: test cases
- 20 marks: programming style (clarity, naming, comments, UI usability)
- Class diagram marks: classes, fields and methods, associations, naming

So: correctness first, clean layered OOP second, polish third. No business logic in UI code.

---

## 2. Tech stack

- Java 17+ (use LocalDate / LocalDateTime, records are NOT to be used for domain classes; use normal classes with getters/setters so encapsulation is visible)
- Maven project, GroupId `com.k2636474`, artifactId `smartstay`
- GUI: Java Swing
- Theme: FlatLaf (`com.formdev:flatlaf`, `flatlaf-extras` for SVG icons,
  `flatlaf-fonts-inter` for the font). These are look-and-feel libraries only; they do not
  generate any application classes. Fall back to Nimbus if FlatLaf fails to load.
- Optional: JUnit 5 for a small test suite (see section 10)
- No other frameworks. No database: in-memory collections only.
- Must build into a runnable jar: `mvn package` → `target/smartstay.jar` (use maven-shade or
  assembly so FlatLaf is bundled). Also runnable from the IDE via the main class.

---

## 3. Naming rules (marks depend on this)

- **Every class, interface, enum and exception name starts with `K2636474`.**
  e.g. `K2636474Room`, `K2636474BookingService`, `K2636474RoomStatus`.
- Classes/interfaces/enums: PascalCase after the prefix. Methods/fields: camelCase.
  Constants: UPPER_SNAKE_CASE.
- Meaningful names only: no `x`, `temp`, `data2`, `btn1`.
- Package root: `com.k2636474.smartstay`

---

## 4. Package layout

```
com.k2636474.smartstay
├── app        K2636474SmartStayApp (main), K2636474DataSeeder
├── model      K2636474User, K2636474Guest, K2636474Admin,
│              K2636474Room, K2636474Booking, K2636474Invoice
├── model.room K2636474Chargeable (interface), K2636474RoomPackage (abstract),
│              K2636474StandardPackage, K2636474DeluxePackage,
│              K2636474SuitePackage, K2636474FamilySuitePackage,
│              K2636474RoomPackageFactory
├── model.enums K2636474RoomType, K2636474BedType, K2636474RoomStatus,
│              K2636474BookingStatus, K2636474IdType, K2636474UserRole
├── pricing    K2636474DiscountPolicy (interface), K2636474LongStayDiscountPolicy,
│              K2636474InvoiceCalculator
├── service    K2636474DataStore (singleton), K2636474AuthService,
│              K2636474RoomService, K2636474BookingService, K2636474InvoiceService
├── exception  K2636474ValidationException, K2636474BookingException,
│              K2636474NotFoundException, K2636474AuthenticationException
├── util       K2636474Validator, K2636474IdGenerator, K2636474Format
└── ui         K2636474MainFrame, K2636474LoginPanel, K2636474RegisterPanel,
               K2636474AdminDashboard, K2636474GuestDashboard,
               K2636474RoomFormDialog, K2636474BookingDialog,
               K2636474InvoiceDialog, K2636474UiTheme (colours, fonts, helpers)
```

UI files may be split further (e.g. separate panels per tab) as long as every class keeps the prefix.

---

## 5. Domain design

### 5.1 Users
- `K2636474User` — **abstract**. Fields (private): `userId`, `fullName`, `contactNumber`,
  `email`, `passwordHash`. Constructor validates via setters. Abstract method
  `K2636474UserRole getRole()`. Method `boolean checkPassword(String raw)`.
  Store passwords as SHA-256 hashes (java.security.MessageDigest), never plain text.
- `K2636474Guest extends K2636474User` — adds `idType` (`K2636474IdType`: NIC, PASSPORT)
  and `idNumber`. `getRole()` returns GUEST.
- `K2636474Admin extends K2636474User` — adds `username`. `getRole()` returns ADMIN.
- Setters validate and throw `K2636474ValidationException` on bad input (encapsulation with
  rules inside, not just plain getters/setters).

### 5.2 Room packages (polymorphism + Factory pattern)
- `K2636474Chargeable` interface: `double getDailyRate()`, `double getTaxRate()`,
  `double getExtraGuestCharge()`.
- `K2636474RoomPackage` — **abstract**, implements `K2636474Chargeable`. Abstract methods for
  the three above plus `boolean includesBreakfast()`, `int getBaseCapacity()`,
  `K2636474RoomType getRoomType()`. Concrete `String describe()` that builds a summary
  using the abstract methods (template-method style).
- Four concrete subclasses override every abstract method with the values below.
- `K2636474RoomPackageFactory.getPackage(K2636474RoomType)` returns a **shared instance** per
  type (cache in an `EnumMap`) so 50 Deluxe rooms share one Deluxe package object.

| Room type | Daily rate (LKR) | Breakfast | Extra guest charge (LKR, per guest per night) | Tax | Base capacity |
|---|---|---|---|---|---|
| Standard | 6,000 | No | 1,000 | 10% | 2 |
| Deluxe | 9,000 | Yes | 1,500 | 12% | 2 |
| Suite | 14,000 | Yes | 2,000 | 15% | 3 |
| Family Suite | 18,000 | Yes | 2,500 | 15% | 4 |

Maximum guests per room = base capacity + `MAX_EXTRA_GUESTS` (constant = 2).

### 5.3 Room
- `K2636474Room`: `roomId` (e.g. `R001`), `K2636474BedType bedType` (SINGLE, DOUBLE, QUEEN, KING),
  `K2636474RoomStatus status` (AVAILABLE, RESERVED, UNDER_MAINTENANCE),
  `K2636474RoomPackage roomPackage`.
- Convenience: `getRoomType()`, `getDailyRate()`, `getMaxOccupancy()` delegate to the package.
- Capacity lives ONLY in the package. Do not add a separate capacity field to Room.
- Enums have a `displayName` field and override `toString()` so combo boxes show
  "Family Suite", "Under Maintenance" etc.

### 5.4 Booking
- `K2636474Booking`: `bookingId` (e.g. `BK0001`), `K2636474Guest guest`, `K2636474Room room`,
  `LocalDate checkInDate`, `LocalDate checkOutDate`, `int numberOfGuests`,
  `double advancePaid`, `K2636474BookingStatus status` (CONFIRMED, CANCELLED, COMPLETED),
  `LocalDateTime createdAt`, `K2636474Invoice invoice`.
- Public static final constants:
  `ADVANCE_AMOUNT = 10_000`, `MIN_ADVANCE_DAYS = 2`, `CHANGE_CUTOFF_HOURS = 24`,
  `CHECK_IN_TIME = LocalTime.of(14, 0)`, `MAX_EXTRA_GUESTS = 2`.
- Methods: `long getNights()`, `boolean overlaps(LocalDate in, LocalDate out)`,
  `boolean isActive()`, `boolean canModifyOrCancel(LocalDateTime now)`,
  `LocalDateTime getCheckInDateTime()`.

### 5.5 Invoice (one per booking)
- `K2636474Invoice`: `invoiceId` (`INV0001`), `booking`, `nights`, `baseCharge`,
  `extraGuests`, `extraGuestCharge`, `discountRate`, `discountAmount`, `taxableAmount`,
  `taxRate`, `taxAmount`, `grossTotal`, `advanceDeducted`, `finalPayable`, `generatedAt`.
- Immutable after creation (no setters, all fields final). Modifying a booking creates a
  new invoice that replaces the old one on the booking. Relationship Booking 1 — 1 Invoice.

### 5.6 Discount (Strategy pattern)
- `K2636474DiscountPolicy` interface: `double rateFor(K2636474Booking b)`,
  `String describe()`.
- `K2636474LongStayDiscountPolicy`: 12% when nights ≥ 5, else 0. Constants
  `MIN_NIGHTS = 5`, `RATE = 0.12`.
- `K2636474InvoiceCalculator` takes a `K2636474DiscountPolicy` in its constructor.

---

## 6. Business rules (implement exactly; test the boundaries)

1. **Registration** requires ID type + NIC/passport number, full name, contact number, email,
   password. NIC/passport and email must be unique (case-insensitive email).
2. **Booking needs** room type, number of guests, check-in date, check-out date.
3. **Room must have status AVAILABLE** to be booked. RESERVED and UNDER_MAINTENANCE rooms
   cannot be booked at all.
4. **No date clash**: a room cannot have two active (CONFIRMED) bookings whose date ranges
   overlap. Overlap test: `newIn < existingOut && newOut > existingIn` (check-out day is
   free for the next check-in).
5. **At least 2 days in advance**: `checkIn >= today + 2 days`. Booking exactly 2 days ahead
   is ALLOWED; 1 day ahead is REJECTED.
6. **Check-out must be after check-in** (min 1 night).
7. **Guests**: 1 ≤ numberOfGuests ≤ max occupancy (base capacity + 2).
8. **Advance**: LKR 10,000 recorded as paid when the booking is created.
9. **Modify / cancel cut-off**: allowed only if `now` is MORE than 24 hours before the
   check-in date-time (check-in date at 14:00). Exactly 24h or less → rejected.
   Cancelled/completed bookings cannot be modified or cancelled again.
10. **Cancellation**: status → CANCELLED, room becomes free for those dates, advance is
    marked refundable (show "Advance of LKR 10,000 refunded" in the confirmation).
11. **Modification**: guest can change dates and/or number of guests. Re-run every rule
    (advance days, overlap excluding this booking, occupancy) and regenerate the invoice.

### Bill calculation (K2636474InvoiceCalculator) — in this order
```
nights           = days between checkIn and checkOut
baseCharge       = dailyRate × nights
extraGuests      = max(0, numberOfGuests − baseCapacity)
extraGuestCharge = extraGuests × extraGuestRate × nights
discountAmount   = baseCharge × discountRate      (12% if nights ≥ 5; base charge only)
taxableAmount    = baseCharge − discountAmount + extraGuestCharge
taxAmount        = taxableAmount × taxRate        (by room type)
grossTotal       = taxableAmount + taxAmount
advanceDeducted  = advancePaid (10,000) if booking is CONFIRMED, else 0
finalPayable     = grossTotal − advanceDeducted   (if negative, show as "Refund due")
```
Round money to 2 decimals for display only (use `K2636474Format.lkr(double)` →
"LKR 12,345.00"). Comment each step in the code with the step number.

Worked example for testing: Deluxe, 6 nights, 4 guests →
base 54,000; extra 2×1,500×6 = 18,000; discount 6,480; taxable 65,520; tax 7,862.40;
gross 73,382.40; advance 10,000; final 63,382.40.

---

## 7. Services and data structures

`K2636474DataStore` — **Singleton** (private constructor, `getInstance()`), holds:
- `Map<String, K2636474Room> rooms` → `LinkedHashMap` (O(1) lookup by ID, keeps insertion order for display)
- `Map<String, K2636474Booking> bookings` → `HashMap` (O(1) lookup by booking ID)
- `Map<String, K2636474User> usersByLogin` → `HashMap` (login by email or admin username)
- `Set<String> registeredIdNumbers` → `HashSet` (O(1) duplicate NIC/passport check)
- `NavigableMap<LocalDate, List<K2636474Booking>> bookingsByCheckIn` → `TreeMap`
  (kept sorted; used for "view reservations by check-in date" and date-range queries via `subMap`)
- `Map<K2636474RoomType, K2636474RoomPackage>` → `EnumMap` (inside the factory)

Keep the TreeMap index in sync on create / modify / cancel.

Services (constructor-injected with the data store; UI only talks to services):
- `K2636474AuthService`: `registerGuest(...)`, `login(loginId, password)`, `logout()`,
  `getCurrentUser()`.
- `K2636474RoomService`: `addRoom(type, bedType, status)` (auto ID), `updateRoom(...)`,
  `removeRoom(roomId)` (reject if room has active future bookings), `changeStatus(...)`,
  `getAllRooms()`, `findById(...)`,
  `searchAvailable(K2636474RoomType type, LocalDate in, LocalDate out, int guests)` —
  filter by type → status AVAILABLE → capacity → no overlap. Type may be null for "Any".
- `K2636474BookingService`: `createBooking(...)`, `modifyBooking(...)`, `cancelBooking(...)`,
  `findById(...)`, `searchByGuestName(String partial)` (case-insensitive contains, results
  sorted by check-in), `getByCheckInDate(LocalDate)`, `getBookingsInRange(from, to)`,
  `getBookingsForGuest(guest)`, `getAllBookings()`. All rule violations throw
  `K2636474BookingException` with a clear user-facing message. Pass `LocalDateTime now`
  / `LocalDate today` into rule checks (or use an injectable `java.time.Clock`) so the
  boundaries can be tested.
- `K2636474InvoiceService`: `generateInvoice(booking)`, `formatInvoice(invoice)` → nicely
  laid out text for the invoice dialog and for copy/save.

`K2636474IdGenerator`: sequential, zero-padded IDs per prefix (R001, BK0001, INV0001, G0001).
Never reuse an ID after deletion.

---

## 8. Validation (`K2636474Validator`, all static, all regex constants named)

- NIC (Sri Lanka): old format 9 digits + V/X (`^[0-9]{9}[VvXx]$`) or new 12 digits (`^[0-9]{12}$`)
- Passport: 6–9 alphanumeric (`^[A-Za-z0-9]{6,9}$`)
- Phone: Sri Lankan mobile `07XXXXXXXX` or `+947XXXXXXXX`, or international `+` and 8–15 digits
- Email: standard pattern
- Name: letters, spaces, dots, 2–60 chars
- Password: min 6 chars
- Throw `K2636474ValidationException(fieldName, message)` so the UI can highlight the field.

---

## 9. GUI (Swing) — usability earns marks

Overall: one `K2636474MainFrame` (min 1200×760, centred) with `CardLayout` switching
Login ↔ Register ↔ Admin dashboard ↔ Guest dashboard. Never freeze the UI with blocking
dialogs in loops.

### 9.0 Visual design (must NOT look like a default Swing/tabbed app)

Feel: a calm, premium hotel product — warm, spacious, confident. Think hotel booking site,
not a school form.

- **Palette** (all in `K2636474UiTheme` as named constants, nothing hard-coded elsewhere):
  - Primary: deep teal `#0F4C5C`; primary hover `#0B3A47`
  - Accent: warm brass/gold `#C9A227` (used sparingly: active nav marker, price totals, key CTA)
  - Background: warm off-white `#F7F5F0`; surfaces/cards: `#FFFFFF`; borders `#E6E1D8`
  - Text: `#1E2A2F` primary, `#6B7780` secondary
  - Status: Available `#2E7D5B`, Reserved `#C98A1B`, Maintenance `#7A828A`, Cancelled `#B3443A`
- **Typography**: Inter via `com.formdev:flatlaf-fonts-inter`. Clear scale: page title 24 bold,
  section title 16 semibold, body 13, captions 11 secondary colour. Money in tabular figures.
- **FlatLaf tuning** (set via `UIManager.put` in the theme class): `Component.arc` 10,
  `Button.arc` 10, `TextComponent.arc` 10, focus colour = primary, table row height 36,
  no grid lines except subtle horizontal separators, selection colour = light teal tint.
  Use `JTextField.placeholderText` client property for hints.
- **Layout**: no top-level `JTabbedPane`. Each dashboard = **left sidebar** (240px, primary
  colour, white text, logo "SmartStay" with a small gold mark, nav items with SVG icons,
  active item highlighted with a gold left bar) + **content area** (page title, short subtitle,
  then cards). 24px page padding, 16px gaps, 8px grid throughout.
- **Cards**: white rounded panels with 1px border; used for stats, forms, price breakdown.
- **Login / Register**: split screen — left half a teal panel with the brand name, a one-line
  tagline ("Stays made simple across Sri Lanka") and a subtle geometric pattern drawn with
  Java2D; right half the form card, centred.
- **Find a Room results**: show as a grid of **room cards** (not a plain table): coloured top
  strip per room type, room ID, type, bed, capacity, breakfast badge, price per night in large
  text, "Book" button. A "List view" toggle may switch to a table.
- **Status shown as pill badges** (rounded, tinted background, coloured text) in tables and cards
  via a custom `TableCellRenderer`.
- **Price breakdown** (booking dialog and invoice preview): receipt-style card, each line
  label left / amount right, discount line in green, final payable large and bold in primary
  colour with a gold divider above it.
- **Icons**: `com.formdev:flatlaf-extras` `FlatSVGIcon` with simple hand-written SVG icons in
  `src/main/resources/icons` (bed, calendar, search, user, invoice, logout, plus, edit, trash,
  dashboard). Icons tinted to match text colour.
- **Empty states**: friendly message + icon when a table/search has no results
  ("No rooms match those dates — try different dates or another room type").
- **Feedback**: non-blocking toast/status message (bottom of content area, auto-hides after
  3s) for success; dialogs only for errors needing attention and confirmations.
- Hover states on buttons and nav items; hand cursor on clickable items.
- Window title "SmartStay — Hotel Booking"; set an app icon.

After building the UI, take a screenshot-quality look at each screen yourself and fix
alignment, clipping, inconsistent spacing or default-looking components before finishing.

**Login panel**: email/username + password, "Login" (Enter key submits), link to Register.
Show demo credentials in small grey text.

**Register panel**: ID type combo (NIC/Passport) + ID number, full name, contact, email,
password + confirm. Inline red error label under each invalid field (from
ValidationException), not just one popup. Success → back to login with message.

The sections below list what each sidebar page contains (they were originally written
as tabs; implement them as sidebar pages per 9.0).

**Admin dashboard** (sidebar pages):
1. Rooms — `JTable` of all rooms (ID, type, bed, daily rate, capacity, breakfast, status),
   sortable (`TableRowSorter`), filter by type/status, buttons Add / Edit / Remove /
   Change Status. Add/Edit in `K2636474RoomFormDialog`. Remove asks for confirmation.
   Status cells colour-coded (green Available, amber Reserved, grey Maintenance).
2. Reservations — table of all bookings; search box (booking ID or guest name);
   date picker "Check-in date" filter; button to view invoice.
3. Guests — table of registered guests.
4. Overview — simple stat cards: total rooms, available, under maintenance, active bookings,
   check-ins this week.

**Guest dashboard** (sidebar pages):
1. Find a Room — room type combo (incl. "Any"), check-in & check-out date pickers, guests
   spinner, Search. Results table; "Book" button opens `K2636474BookingDialog`.
2. Booking dialog — shows room details and a **live price breakdown** (updates as guests or
   dates change) using the real calculator; Confirm shows the LKR 10,000 advance clearly.
   On success show booking ID and offer "View invoice".
3. My Bookings — table of the guest's bookings with status; buttons Modify, Cancel,
   View Invoice. Disable Modify/Cancel (with tooltip explaining the 24h rule) when not eligible.
4. Search Reservations — by booking ID or guest name, and "by check-in date". For privacy,
   guests only see their own reservations in results; admins see all.

**Dates**: use `JSpinner` with `SpinnerDateModel` (format yyyy-MM-dd) — no external date
picker library. Default check-in = today + 2, check-out = today + 3.

**Invoice dialog**: monospaced, clearly aligned invoice with all required fields: room
details (ID, type, bed, daily rate), guest details (name, ID, contact, email), check-in,
check-out, nights, guests, extra guest charge, discount applied (rate and amount, or
"None"), tax (rate and amount), advance deducted, final payable. Buttons: Copy, Save as
.txt, Close.

**Feedback**: every action gives clear success/error feedback (`JOptionPane` with proper
icons, or a status bar). Never show a stack trace to the user. Confirm destructive actions.
Keyboard: Enter submits forms, Esc closes dialogs. Tooltips on buttons.

---

## 10. Seed data (`K2636474DataSeeder`)

Loaded on startup so the marker can try everything immediately:
- Admin: username `admin`, password `admin123`
- 3 guests (one NIC old format, one NIC new format, one passport), password `guest123`
  e.g. `nimal@example.com`
- 12 rooms across all 4 types and bed types; 1 UNDER_MAINTENANCE, 1 RESERVED
- 5 bookings with dates **relative to today**: one starting tomorrow (to demonstrate the
  24h block on cancel), one 5+ nights (discount), one with extra guests, one cancelled,
  one far future.

Seeded bookings may bypass the 2-day rule (document this with a comment) since they
represent existing data.

---

## 11. Code quality requirements

- Javadoc on every class and every public method (purpose, @param, @return, @throws).
- Inline comments on algorithm steps (bill calculation, overlap check, availability search,
  cut-off check, ID generation) — explain *why*, not just *what*.
- Fields private; expose only what is needed. Use `final` where appropriate.
- Custom exceptions extend `RuntimeException` or `Exception` consistently; catch them in the UI
  and show friendly messages.
- No duplicated logic: the UI's live price preview must call `K2636474InvoiceCalculator`.
- Methods short and single-purpose; no God classes. UI never touches the DataStore directly.
- Consistent 4-space indentation, no unused imports, no commented-out code.
- `@Override` on every overridden method.

### Optional but recommended: JUnit 5 tests (`src/test/java`)
- `K2636474InvoiceCalculatorTest`: worked example above; 4 nights (no discount) vs 5 nights
  (discount); no extra guests; each room type's tax.
- `K2636474BookingServiceTest`: book 1 day ahead (reject), exactly 2 days (accept);
  overlap reject; check-out on another's check-in day (accept); cancel at 25h (accept),
  exactly 24h (reject); maintenance room (reject); too many guests (reject).

---

## 12. Workflow

1. `git init`, commit after each milestone with clear messages.
2. Build in this order: enums → room packages + factory → users → room/booking/invoice →
   validator/IDs/format → data store → calculator + policy → services → seeder →
   JUnit tests → UI.
3. After services are done, run the JUnit tests and fix until green before starting the UI.
4. After the UI, run the app and click through every feature in section 9 yourself.
5. Final: `mvn clean package` must succeed; run the jar once to confirm it launches.
6. Write `README.md`: requirements, how to build/run, demo logins, project structure.
7. Produce `docs/class-summary.md`: for every class its package, fields (type + visibility),
   methods (signature + visibility), and relationships (inheritance, implements,
   association with multiplicity, composition/aggregation, dependency). This is used to
   draw the UML diagram and write the report, so it must match the code exactly.

## 13. Assumptions (implemented; will be stated in the report)

1. Room status is the admin-controlled operational flag. Only AVAILABLE rooms can be booked;
   date clashes between bookings are prevented separately by the overlap check.
2. Base capacities as in the table in 5.2; up to 2 extra guests allowed per room.
3. Extra guest charge is per extra guest per night.
4. The 12% discount applies only to the base room charge; tax is charged on
   (discounted base + extra guest charges).
5. Check-in time is 14:00; the 24-hour cut-off is measured to that time.
6. "At least 2 days in advance" means check-in date ≥ today + 2.
7. The advance is deducted from every confirmed booking's invoice and refunded on a valid
   cancellation. Payment is recorded, not processed (no payment gateway).
8. Guests can search only their own reservations; admins can search all.
9. Data is held in memory and resets when the application closes.
