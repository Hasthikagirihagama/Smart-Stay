# SmartStay — Fixes + Automated UI Test Run with Screenshots

Read CLAUDE.md first; everything there still applies. Do Part A, then Part B.

---

## Part A — Fixes (do these first, commit, re-run JUnit)

1. **Cancelled bookings.** When a booking is cancelled, regenerate its invoice so it reflects
   the cancellation. A cancelled invoice shows status Cancelled, no advance deducted, nothing
   payable, and the line "Booking cancelled - advance of LKR 10,000.00 refunded."
   Add a JUnit test for this.
2. **Seed data.** BK0001 (Nimal, R001) must check in TODAY (today → today+2), not tomorrow,
   so the 24-hour modify/cancel block is visible whatever time the app is run. Keep it
   bypassing the 2-day rule like the other seeded bookings.
3. **Rooms table.** Widen the Status column so "Under Maintenance" is not clipped.
4. Update `docs/class-summary.md` if anything changed. `mvn clean package` must pass.

---

## Part B — Automated UI test run

### Goal
Run all 47 test cases below **through the real Swing UI** (real frames, panels, dialogs, the
real services), capture a screenshot of each result, and check the actual result against the
expected one. The screenshots go into the coursework report as test evidence, so they must
look exactly like a person using the app.

### Where the code goes
- `src/test/java/com/k2636474/smartstay/uitest/K2636474UiTestRunner.java` (+ small helpers in
  the same package, all prefixed `K2636474`). It has a `main` method.
- It must NOT be part of the shipped jar (test sources only) and must not change app behaviour.
- Add a way to run it, e.g. `mvn -q test-compile exec:java -Dexec.classpathScope=test
  -Dexec.mainClass=com.k2636474.smartstay.uitest.K2636474UiTestRunner`, and document it in
  the README under "Automated UI test run".
- You may add `setName(...)` to UI components to find them reliably (good practice anyway).
  Otherwise find components by walking the component tree (labels, placeholder text, button text).

### How to drive the UI
- Build the app exactly like `K2636474SmartStayApp.main` does (same services, seeder, theme,
  `K2636474MainFrame`), at **1280×800**, centred, on top.
- **"Fresh start"** = clear the data store, re-run the seeder with today's date, dispose the
  old frame and build a new one. No need to relaunch the JVM.
- Interact like a user: set text in fields, choose combo items, set spinner/date values,
  select table rows, then `doClick()` the real buttons. Do all Swing calls on the EDT.
- Modal dialogs (`JOptionPane`, booking dialog, invoice dialog, confirmations) block: trigger
  them with `SwingUtilities.invokeLater`, then poll `Window.getWindows()` for the visible
  dialog, wait for it to render, screenshot, then press its button or dispose it.
- **Screenshots**: use `java.awt.Robot.createScreenCapture` of the frame's on-screen bounds
  (expand the rectangle to include any dialog or tooltip showing), so dialogs, toasts and
  tooltips appear exactly as a user sees them. Wait ~700 ms after each action before
  capturing so painting and animations finish. Make sure nothing else covers the window.
- **Tooltips** (TC38, TC40): set `ToolTipManager` initial delay to 0, move the mouse over the
  disabled button with `Robot.mouseMove`, wait, then capture.
- **TC36 (save invoice)**: when the file chooser opens, set the target to
  `test-evidence/TC36_invoice.txt` and approve it; then show the saved file's contents in a
  simple read-only window and capture that alongside (or use two screenshots: TC36a, TC36b).
- Dates: T = `LocalDate.now()`. T+N = today plus N days.

### Output
Create `test-evidence/` at the project root:
- `test-evidence/screenshots/TC01.png … TC47.png` (extra shots as `TC36b.png` etc. if needed)
- `test-evidence/results.csv` with columns:
  `test_id, test_case, input, expected, actual, result` where `actual` is what the UI really
  showed (message text, amounts, room IDs found) and `result` is PASS or FAIL
- `test-evidence/results.md`: the same as a markdown table plus a one-line summary
  (e.g. "47/47 passed") and the date/time of the run.
- The `actual` value must be read from the UI (label text, dialog message, table rows,
  invoice text), not copied from `expected`. Compare amounts and messages exactly; for
  message checks it's fine to check the message contains the expected sentence.

Run it once fully. If any test FAILS, investigate: if it's a real bug in the app, fix the app
and re-run; if the expected value in this file is wrong, tell me in your summary instead of
changing it. Finish with a short summary: passed/failed counts and anything you changed.

---

## The 47 test cases

T = today. "Fresh start" = reset data and rebuild the frame (see above).
Demo logins: admin / admin123; nimal@example.com, kamala@example.com, emma@example.com / guest123.

| ID | Test case | Steps & input | Expected result |
|---|---|---|---|
| TC01 | Register a local guest with an old-format NIC | Fresh start. Create an account: NIC 851234567V, Sunil Fernando, 0712223344, sunil@example.com, password sunil123 (confirm sunil123) | Account created; back on login with "Account created. Welcome, Sunil Fernando — please log in." |
| TC02 | Register an international guest with a passport | Passport P9876543, John Smith, +14155550123, john@example.com, john1234 / john1234 | Account created and returned to login with the welcome message |
| TC03 | Reject an invalid NIC | NIC 12345, all other fields valid | Error under NIC: "NIC must be 9 digits + V/X (e.g. 912345678V) or 12 digits." No account created |
| TC04 | Reject an invalid passport number | ID type Passport, number AB1, other fields valid | "Passport number must be 6-9 letters or digits." |
| TC05 | Reject an invalid email | Email test.example, other fields valid | "Enter a valid email such as name@example.com." |
| TC06 | Reject an invalid phone number | Contact number 12345, other fields valid | "Enter a mobile like 0771234567, +94771234567 or an international number starting with +." |
| TC07 | Reject a duplicate email | Email nimal@example.com, new NIC 861234567V | "An account with this email already exists." |
| TC08 | Reject a duplicate NIC | NIC 912345678V (Nimal's), new email t3@example.com | "This NIC number is already registered." |
| TC09 | Reject mismatched passwords | Password test123, confirm test999 | "Passwords do not match." |
| TC10 | Reject an empty form | Press Create account with every field empty | An error under each field: NIC number / Full name / Contact number / Email is required; password "Use at least 6 characters." |
| TC11 | Guest login | nimal@example.com / guest123 | Guest dashboard opens on Find a room; sidebar shows Nimal Perera · Guest |
| TC12 | Admin login | admin / admin123 | Admin dashboard opens on Overview with the stat cards |
| TC13 | Reject a wrong password | nimal@example.com / wrong | "Incorrect email/username or password." Stays on login |
| TC14 | Log out | From any dashboard press Log out | Back on login with "You have been logged out." |
| TC15 | View all room records | Fresh start, admin → Rooms | 12 rooms R001–R012 with type, bed, daily rate, capacity, breakfast and status; R009 Under Maintenance, R011 Reserved |
| TC16 | Add a room | Add room: Deluxe, King, Available | R013 added: Deluxe, King, LKR 9,000.00, 2 (max 4), breakfast Included, Available |
| TC17 | Update a room | Edit R013 → type Suite, bed Queen | R013 now Suite, Queen, LKR 14,000.00, 3 (max 5) |
| TC18 | Change availability status | Change status of R005 → Under Maintenance | R005 shows a grey Under Maintenance badge |
| TC19 | Remove a room | Remove R013, confirm (capture the confirmation, then the result) | Confirmation asked; R013 removed; 12 rooms remain |
| TC20 | Block removing a room with bookings | Remove R004 (booked by BK0002) | "Room R004 has upcoming confirmed bookings and cannot be removed. Cancel or move them first." |
| TC21 | Search by room type and dates | Fresh start, Nimal → Find a room: Deluxe, T+2 → T+4, 2 guests | R004, R005, R006 shown as room cards |
| TC22 | Hide rooms under maintenance | Suite, T+2 → T+3, 2 guests | R007, R008 shown; R009 not shown |
| TC23 | Hide rooms marked Reserved | Family Suite, T+2 → T+3, 2 guests | R010, R012 shown; R011 not shown |
| TC24 | Hide rooms booked for those dates | Deluxe, T+8 → T+10, 2 guests | R005, R006 shown; R004 not shown (BK0002 holds it T+7 → T+13) |
| TC25 | Respect room capacity | Standard, T+2 → T+3, 5 guests | No rooms; empty-state message shown |
| TC26 | Reject check-in less than 2 days ahead | Any type, T+1 → T+2, 2 guests | "Bookings must be made at least 2 days in advance. The earliest check-in is …" (shows T+2) |
| TC27 | Reject check-out not after check-in | Any type, T+5 → T+5 | "Check-out must be at least one night after check-in." |
| TC28 | Book exactly 2 days ahead (boundary) | Nimal: Standard, T+2 → T+4, 2 guests → Book R003 → Confirm (capture the booking dialog with its breakdown, then the confirmation) | Booking BK0006 confirmed; LKR 10,000 advance shown; total 13,200.00; final payable 3,200.00 |
| TC29 | Prevent double booking | After TC28: Standard, T+3 → T+5, 2 guests | R003 not listed |
| TC30 | Allow back-to-back stays | After TC28: Standard, T+4 → T+5, 2 guests | R003 listed |
| TC31 | Extra guest charge | Booking dialog for R002, T+20 → T+22, 3 guests (capture the live breakdown, then cancel the dialog) | Base 12,000; extra guests 2,000 (1 × 1,000 × 2); tax 10% 1,400; gross 15,400; final 5,400 |
| TC32 | No discount at 4 nights | R002, T+20 → T+24, 2 guests | Base 24,000; discount none; tax 2,400; gross 26,400; final 16,400 |
| TC33 | 12% discount at 5 nights (boundary) | R002, T+20 → T+25, 2 guests | Base 30,000; discount 12% −3,600; tax 2,640; gross 29,040; final 19,040 |
| TC34 | Tax rate by room type | 1 night (T+20 → T+21) at base capacity: R002 Standard (2), R005 Deluxe (2), R008 Suite (3), R012 Family Suite (4). One screenshot each: TC34a–TC34d | Gross: Standard 6,600 (10%), Deluxe 10,080 (12%), Suite 16,100 (15%), Family Suite 20,700 (15%) |
| TC35 | Detailed invoice with every required field | Admin → Reservations → BK0002 → View invoice | Room R004 Deluxe, Double, 9,000; guest Kamala Silva; 6 nights; 4 guests; room 54,000; extra 18,000; discount −6,480; tax 12% 7,862.40; gross 73,382.40; advance −10,000; final 63,382.40 |
| TC36 | Save the invoice | In the invoice dialog press Save as .txt (see TC36 notes above) | A .txt file with the same invoice is saved |
| TC37 | Modify an eligible booking | Fresh start, Emma → My bookings → BK0005 → Modify: check-out T+65 (5 nights); then view the invoice (TC37b) | Booking updated; invoice regenerated with 12% discount: final payable 19,040 |
| TC38 | Block changes within 24h of check-in | Nimal → My bookings → select BK0001 (checks in today); hover Cancel | Modify and Cancel disabled; tooltip "Changes are only allowed more than 24 hours before check-in (…)" |
| TC39 | Cancel an eligible booking | Emma → BK0005 → Cancel booking → Yes (capture confirm, result, then the invoice as TC39b) | Status Cancelled; "Advance of LKR 10,000.00 refunded." Invoice marked cancelled with nothing payable |
| TC40 | Block changing a cancelled booking | Nimal → select BK0004 (cancelled); hover Cancel | Modify and Cancel disabled; tooltip "This booking is cancelled and can no longer be changed." |
| TC41 | Search by booking ID | Admin → Reservations, search BK0002 | One result: BK0002, Kamala Silva, R004 |
| TC42 | Search by guest name | Admin, search emma | BK0003 and BK0005 (Emma Clarke) |
| TC43 | View reservations by check-in date | Admin, check-in date filter T+4 | BK0003 (Emma Clarke, R010) |
| TC44 | Guests only see their own reservations | Nimal → Search reservations, search Emma | No results |
| TC45 | Unknown booking ID | Admin, search BK9999 | Empty-state message, no results |
| TC46 | View registered guests | Admin → Guests | Nimal Perera, Kamala Silva, Emma Clarke with ID type/number, contact and email |
| TC47 | Overview statistics | Fresh start, admin → Overview | 12 rooms, 10 available, 1 under maintenance, 4 active bookings, arrivals in the next 7 days listed |

When done, zip the whole project (including `test-evidence/`) so it can be sent for the report.
