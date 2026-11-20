# SmartStay — Hotel Booking System

CI6115 Programming III (Patterns and Algorithms), Coursework 1 — Student ID **K2636474**.

A desktop hotel booking system in Java Swing. Guests register, search available rooms, book with a
live price breakdown, and modify or cancel up to 24 hours before check-in. Administrators manage rooms
and see every reservation. All data is held in memory and demo data is loaded at start-up.

## Requirements

- Java 17 or newer (JDK to build, JRE to run)
- No Maven install needed: the project includes the Maven wrapper (`mvnw` / `mvnw.cmd`)
- Internet access on the first build only, to download dependencies (FlatLaf and JUnit)

## Build and run

```bash
./mvnw clean package          # Windows: mvnw.cmd clean package
java -jar target/smartstay.jar
```

`mvn package` compiles the code, runs the 64 JUnit tests and builds `target/smartstay.jar` with
FlatLaf bundled inside.

From an IDE, open the folder as a Maven project and run
`com.k2636474.smartstay.app.K2636474SmartStayApp`.

Run only the tests with `./mvnw test`.

## Automated UI test run

`src/test/java/com/k2636474/smartstay/uitest` holds a runner that performs the 47 coursework
test cases through the real Swing UI. It uses the real frame, dialogs and services, fills in
fields and clicks the real buttons, then screenshots each result:

```bash
./mvnw -q test-compile exec:java -Dexec.classpathScope=test \
    -Dexec.mainClass=com.k2636474.smartstay.uitest.K2636474UiTestRunner
```

The run takes about two minutes. It opens the app window on top and moves the mouse for the
tooltip tests, so don't use the mouse or keyboard while it runs. It writes:

- `test-evidence/screenshots/TC01.png … TC47.png`. Extra steps use a suffix: `a` for the
  step before the result (e.g. a confirmation) and `b` for a follow-up (e.g. the invoice).
- `test-evidence/results.csv` and `test-evidence/results.md`, where each actual value is
  read back from the screen and checked against the expected value.
- `test-evidence/TC36_invoice.txt`, the invoice saved through the Save dialog.

The runner is test code only. It is not in `smartstay.jar` and doesn't change how the app behaves.

## Demo logins

| Role | Login | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Guest (old NIC 912345678V) | `nimal@example.com` | `guest123` |
| Guest (new NIC 199512345678) | `kamala@example.com` | `guest123` |
| Guest (passport N1234567) | `emma@example.com` | `guest123` |

Seed data, with dates relative to the day you start the app:

| Booking | Guest | Room | Shows |
|---|---|---|---|
| BK0001 | Nimal | R001 Standard | Checks in today, so Modify/Cancel are blocked by the 24-hour rule |
| BK0002 | Kamala | R004 Deluxe | 6 nights, 4 guests: long-stay discount and extra-guest charges (the worked example) |
| BK0003 | Emma | R010 Family Suite | 6 guests: 2 extra guests |
| BK0004 | Nimal | R007 Suite | Cancelled, advance refunded |
| BK0005 | Emma | R002 Standard | 60 days ahead, can still be modified or cancelled |

There are 12 rooms across all four types. R009 is Under Maintenance and R011 is Reserved, so neither can
be booked.

## What to try

- **Guest:** Find a room → Search → Book → watch the price change as you edit dates or guests → View invoice.
- **Guest:** My bookings → select BK0001 → hover over the disabled Cancel button to see the 24-hour reason.
- **Guest:** try a check-in date only one day ahead. The booking dialog explains the 2-day rule and disables Confirm.
- **Admin:** Rooms → filter, sort, Add / Edit / Remove / Change status. Removing R001 is refused because it has an upcoming booking.
- **Admin:** Reservations → search "BK0002" or "silva", or tick "Check-in on" to filter by date.

## Project structure

```
src/main/java/com/k2636474/smartstay
├── app        entry point (K2636474SmartStayApp) and demo data (K2636474DataSeeder)
├── model      users, room, booking, invoice
├── model.room Chargeable interface, abstract RoomPackage, four packages, RoomPackageFactory
├── model.enums room type, bed type, room status, booking status, ID type, user role
├── pricing    DiscountPolicy strategy, LongStayDiscountPolicy, InvoiceCalculator
├── service    DataStore (singleton) and the auth, room, booking and invoice services
├── exception  validation, booking, not-found and authentication exceptions
├── util       Validator (regex rules), IdGenerator, Format (LKR and dates)
└── ui         Swing screens, dialogs, theme and reusable components
src/main/resources/icons   hand-written SVG icons
src/test/java              JUnit 5 tests (calculator, booking rules, validator, auth)
docs/class-summary.md      every class with its fields, methods and relationships (for the UML diagram)
```

Layers: the UI only calls services. Services enforce every business rule and use the data store.
Money is calculated in exactly one place, `K2636474InvoiceCalculator`, which also drives the booking
dialog's live preview.

## Design patterns and data structures

- **Singleton:** `K2636474DataStore`
- **Factory with shared instances:** `K2636474RoomPackageFactory`, backed by an `EnumMap`
- **Strategy:** `K2636474DiscountPolicy`, injected into `K2636474InvoiceCalculator`
- **Template method:** `K2636474RoomPackage.describe()`, and `K2636474Page.refresh()` called by the dashboard
- `LinkedHashMap` holds rooms (fast lookup by ID, keeps display order).
- `HashMap` holds bookings and logins.
- `HashSet` holds NIC/passport numbers for the duplicate check.
- `TreeMap` indexes bookings by check-in date. It powers sorted listings, the by-date search, `subMap` range
  queries for "this week", and `headMap` to narrow the overlap check.

## Assumptions

1. Room status is an admin-controlled flag. Only AVAILABLE rooms can be booked; date clashes are prevented separately.
2. Base capacities follow the brief's table (Standard 2, Deluxe 2, Suite 3, Family Suite 4). Up to 2 extra guests are allowed.
3. The extra-guest charge is per extra guest per night.
4. The 12% discount (5+ nights) applies to the base room charge only. Tax is charged on the discounted base plus extra-guest charges.
5. Check-in time is 14:00, and the 24-hour cut-off is measured to that time. Exactly 24 hours before is too late.
6. "At least 2 days in advance" means check-in ≥ today + 2.
7. The LKR 10,000 advance is deducted on every confirmed booking's invoice and refunded on a valid cancellation. Payment is recorded, not processed.
8. Guests can only search their own reservations; admins can search all.
9. Data lives in memory and resets when the application closes.
10. A room's type cannot be changed, and the room cannot be removed, while it has upcoming confirmed bookings.
