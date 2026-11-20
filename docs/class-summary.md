# SmartStay — Class Summary

Generated from the compiled classes (`javap -p`) so names, types and visibility match the code exactly. 
Relationships for the UML diagram are listed at the end.

Visibility: `+` public, `#` protected, `-` private, `~` package-private. Static members are marked *static*; final fields *final*.


## Package `com.k2636474.smartstay.app`

### K2636474DataSeeder  *(class)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| + public | `GUEST_PASSWORD` | `String` | *static* *final* |
| + public | `ADMIN_USERNAME` | `String` | *static* *final* |
| + public | `ADMIN_PASSWORD` | `String` | *static* *final* |
| - private | `authService` | `K2636474AuthService` | *final* |
| - private | `roomService` | `K2636474RoomService` | *final* |
| - private | `bookingService` | `K2636474BookingService` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474DataSeeder(K2636474AuthService, K2636474RoomService, K2636474BookingService)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `seed(LocalDate)` | `void` | |
| - private | `seedRooms()` | `void` | |

### K2636474SmartStayApp  *(class, final)*

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474SmartStayApp()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `main(String[])` | `void` | *static* |


## Package `com.k2636474.smartstay.model`

### K2636474Admin  *(class)*
- extends `K2636474User`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `username` | `String` | |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474Admin(String, String, String, String, String, String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getRole()` | `K2636474UserRole` | |
| + public | `getUsername()` | `String` | |
| + public | `setUsername(String)` | `void` | |

### K2636474Booking  *(class)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| + public | `ADVANCE_AMOUNT` | `double` | *static* *final* |
| + public | `MIN_ADVANCE_DAYS` | `int` | *static* *final* |
| + public | `CHANGE_CUTOFF_HOURS` | `int` | *static* *final* |
| + public | `CHECK_IN_TIME` | `LocalTime` | *static* *final* |
| + public | `MAX_EXTRA_GUESTS` | `int` | *static* *final* |
| - private | `bookingId` | `String` | *final* |
| - private | `guest` | `K2636474Guest` | *final* |
| - private | `room` | `K2636474Room` | *final* |
| - private | `createdAt` | `LocalDateTime` | *final* |
| - private | `advancePaid` | `double` | *final* |
| - private | `checkInDate` | `LocalDate` | |
| - private | `checkOutDate` | `LocalDate` | |
| - private | `numberOfGuests` | `int` | |
| - private | `status` | `K2636474BookingStatus` | |
| - private | `advanceRefunded` | `boolean` | |
| - private | `invoice` | `K2636474Invoice` | |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474Booking(String, K2636474Guest, K2636474Room, LocalDate, LocalDate, int, LocalDateTime)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getNights()` | `long` | |
| + public | `overlaps(LocalDate, LocalDate)` | `boolean` | |
| + public | `isActive()` | `boolean` | |
| + public | `canModifyOrCancel(LocalDateTime)` | `boolean` | |
| + public | `getCheckInDateTime()` | `LocalDateTime` | |
| + public | `getRefundedAmount()` | `double` | |
| + public | `setStayDates(LocalDate, LocalDate)` | `void` | |
| + public | `setNumberOfGuests(int)` | `void` | |
| + public | `cancel()` | `void` | |
| + public | `getBookingId()` | `String` | |
| + public | `getGuest()` | `K2636474Guest` | |
| + public | `getRoom()` | `K2636474Room` | |
| + public | `getCheckInDate()` | `LocalDate` | |
| + public | `getCheckOutDate()` | `LocalDate` | |
| + public | `getNumberOfGuests()` | `int` | |
| + public | `getAdvancePaid()` | `double` | |
| + public | `isAdvanceRefunded()` | `boolean` | |
| + public | `getStatus()` | `K2636474BookingStatus` | |
| + public | `getCreatedAt()` | `LocalDateTime` | |
| + public | `getInvoice()` | `K2636474Invoice` | |
| + public | `setInvoice(K2636474Invoice)` | `void` | |
| + public | `toString()` | `String` | |

### K2636474Guest  *(class)*
- extends `K2636474User`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `idType` | `K2636474IdType` | |
| - private | `idNumber` | `String` | |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474Guest(String, K2636474IdType, String, String, String, String, String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getRole()` | `K2636474UserRole` | |
| + public | `setIdentification(K2636474IdType, String)` | `void` | |
| + public | `getIdType()` | `K2636474IdType` | |
| + public | `getIdNumber()` | `String` | |

### K2636474Invoice  *(class, final)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `invoiceId` | `String` | *final* |
| - private | `booking` | `K2636474Booking` | *final* |
| - private | `nights` | `long` | *final* |
| - private | `baseCharge` | `double` | *final* |
| - private | `extraGuests` | `int` | *final* |
| - private | `extraGuestCharge` | `double` | *final* |
| - private | `discountRate` | `double` | *final* |
| - private | `discountAmount` | `double` | *final* |
| - private | `taxableAmount` | `double` | *final* |
| - private | `taxRate` | `double` | *final* |
| - private | `taxAmount` | `double` | *final* |
| - private | `grossTotal` | `double` | *final* |
| - private | `advanceDeducted` | `double` | *final* |
| - private | `finalPayable` | `double` | *final* |
| - private | `generatedAt` | `LocalDateTime` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474Invoice(String, K2636474Booking, long, double, int, double, double, double, double, double, double, double, double, double, LocalDateTime)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `isRefundDue()` | `boolean` | |
| + public | `hasDiscount()` | `boolean` | |
| + public | `getInvoiceId()` | `String` | |
| + public | `getBooking()` | `K2636474Booking` | |
| + public | `getNights()` | `long` | |
| + public | `getBaseCharge()` | `double` | |
| + public | `getExtraGuests()` | `int` | |
| + public | `getExtraGuestCharge()` | `double` | |
| + public | `getDiscountRate()` | `double` | |
| + public | `getDiscountAmount()` | `double` | |
| + public | `getTaxableAmount()` | `double` | |
| + public | `getTaxRate()` | `double` | |
| + public | `getTaxAmount()` | `double` | |
| + public | `getGrossTotal()` | `double` | |
| + public | `getAdvanceDeducted()` | `double` | |
| + public | `getFinalPayable()` | `double` | |
| + public | `getGeneratedAt()` | `LocalDateTime` | |

### K2636474Room  *(class)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `roomId` | `String` | *final* |
| - private | `bedType` | `K2636474BedType` | |
| - private | `status` | `K2636474RoomStatus` | |
| - private | `roomPackage` | `K2636474RoomPackage` | |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474Room(String, K2636474RoomPackage, K2636474BedType, K2636474RoomStatus)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getRoomType()` | `K2636474RoomType` | |
| + public | `getDailyRate()` | `double` | |
| + public | `getMaxOccupancy()` | `int` | |
| + public | `isBookable()` | `boolean` | |
| + public | `getRoomId()` | `String` | |
| + public | `getBedType()` | `K2636474BedType` | |
| + public | `setBedType(K2636474BedType)` | `void` | |
| + public | `getStatus()` | `K2636474RoomStatus` | |
| + public | `setStatus(K2636474RoomStatus)` | `void` | |
| + public | `getRoomPackage()` | `K2636474RoomPackage` | |
| + public | `setRoomPackage(K2636474RoomPackage)` | `void` | |
| + public | `toString()` | `String` | |

### K2636474User  *(abstract class)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `HASH_ALGORITHM` | `String` | *static* *final* |
| - private | `userId` | `String` | *final* |
| - private | `fullName` | `String` | |
| - private | `contactNumber` | `String` | |
| - private | `email` | `String` | |
| - private | `passwordHash` | `String` | |

| Vis | Constructor | |
|---|---|---|
| # protected | `K2636474User(String, String, String, String, String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getRole()` | `K2636474UserRole` | *abstract* |
| + public | `checkPassword(String)` | `boolean` | |
| + public | `setPassword(String)` | `void` | |
| - private | `hash(String)` | `String` | *static* |
| + public | `getUserId()` | `String` | |
| + public | `getFullName()` | `String` | |
| + public | `setFullName(String)` | `void` | |
| + public | `getContactNumber()` | `String` | |
| + public | `setContactNumber(String)` | `void` | |
| + public | `getEmail()` | `String` | |
| + public | `setEmail(String)` | `void` | |
| + public | `getPasswordHash()` | `String` | |
| + public | `toString()` | `String` | |


## Package `com.k2636474.smartstay.model.room`

### K2636474Chargeable  *(interface)*

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDailyRate()` | `double` | *abstract* |
| + public | `getTaxRate()` | `double` | *abstract* |
| + public | `getExtraGuestCharge()` | `double` | *abstract* |

### K2636474DeluxePackage  *(class)*
- extends `K2636474RoomPackage`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `DAILY_RATE` | `double` | *static* *final* |
| - private | `EXTRA_GUEST_CHARGE` | `double` | *static* *final* |
| - private | `TAX_RATE` | `double` | *static* *final* |
| - private | `BASE_CAPACITY` | `int` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474DeluxePackage()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDailyRate()` | `double` | |
| + public | `getTaxRate()` | `double` | |
| + public | `getExtraGuestCharge()` | `double` | |
| + public | `includesBreakfast()` | `boolean` | |
| + public | `getBaseCapacity()` | `int` | |
| + public | `getRoomType()` | `K2636474RoomType` | |

### K2636474FamilySuitePackage  *(class)*
- extends `K2636474RoomPackage`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `DAILY_RATE` | `double` | *static* *final* |
| - private | `EXTRA_GUEST_CHARGE` | `double` | *static* *final* |
| - private | `TAX_RATE` | `double` | *static* *final* |
| - private | `BASE_CAPACITY` | `int` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474FamilySuitePackage()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDailyRate()` | `double` | |
| + public | `getTaxRate()` | `double` | |
| + public | `getExtraGuestCharge()` | `double` | |
| + public | `includesBreakfast()` | `boolean` | |
| + public | `getBaseCapacity()` | `int` | |
| + public | `getRoomType()` | `K2636474RoomType` | |

### K2636474RoomPackage  *(abstract class)*
- implements `K2636474Chargeable`

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474RoomPackage()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `includesBreakfast()` | `boolean` | *abstract* |
| + public | `getBaseCapacity()` | `int` | *abstract* |
| + public | `getRoomType()` | `K2636474RoomType` | *abstract* |
| + public | `getMaxOccupancy()` | `int` | |
| + public | `describe()` | `String` | |
| + public | `toString()` | `String` | |

### K2636474RoomPackageFactory  *(class, final)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `PACKAGES` | `Map<K2636474RoomType, K2636474RoomPackage>` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474RoomPackageFactory()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getPackage(K2636474RoomType)` | `K2636474RoomPackage` | *static* |
| - private | `createPackage(K2636474RoomType)` | `K2636474RoomPackage` | *static* |

### K2636474StandardPackage  *(class)*
- extends `K2636474RoomPackage`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `DAILY_RATE` | `double` | *static* *final* |
| - private | `EXTRA_GUEST_CHARGE` | `double` | *static* *final* |
| - private | `TAX_RATE` | `double` | *static* *final* |
| - private | `BASE_CAPACITY` | `int` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474StandardPackage()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDailyRate()` | `double` | |
| + public | `getTaxRate()` | `double` | |
| + public | `getExtraGuestCharge()` | `double` | |
| + public | `includesBreakfast()` | `boolean` | |
| + public | `getBaseCapacity()` | `int` | |
| + public | `getRoomType()` | `K2636474RoomType` | |

### K2636474SuitePackage  *(class)*
- extends `K2636474RoomPackage`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `DAILY_RATE` | `double` | *static* *final* |
| - private | `EXTRA_GUEST_CHARGE` | `double` | *static* *final* |
| - private | `TAX_RATE` | `double` | *static* *final* |
| - private | `BASE_CAPACITY` | `int` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474SuitePackage()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDailyRate()` | `double` | |
| + public | `getTaxRate()` | `double` | |
| + public | `getExtraGuestCharge()` | `double` | |
| + public | `includesBreakfast()` | `boolean` | |
| + public | `getBaseCapacity()` | `int` | |
| + public | `getRoomType()` | `K2636474RoomType` | |


## Package `com.k2636474.smartstay.model.enums`

### K2636474BedType  *(enum)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| constant | `SINGLE` | `K2636474BedType` | |
| constant | `DOUBLE` | `K2636474BedType` | |
| constant | `QUEEN` | `K2636474BedType` | |
| constant | `KING` | `K2636474BedType` | |
| - private | `displayName` | `String` | *final* |

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474BedType(String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDisplayName()` | `String` | |
| + public | `toString()` | `String` | |

### K2636474BookingStatus  *(enum)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| constant | `CONFIRMED` | `K2636474BookingStatus` | |
| constant | `CANCELLED` | `K2636474BookingStatus` | |
| constant | `COMPLETED` | `K2636474BookingStatus` | |
| - private | `displayName` | `String` | *final* |

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474BookingStatus(String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDisplayName()` | `String` | |
| + public | `toString()` | `String` | |

### K2636474IdType  *(enum)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| constant | `NIC` | `K2636474IdType` | |
| constant | `PASSPORT` | `K2636474IdType` | |
| - private | `displayName` | `String` | *final* |

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474IdType(String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDisplayName()` | `String` | |
| + public | `toString()` | `String` | |

### K2636474RoomStatus  *(enum)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| constant | `AVAILABLE` | `K2636474RoomStatus` | |
| constant | `RESERVED` | `K2636474RoomStatus` | |
| constant | `UNDER_MAINTENANCE` | `K2636474RoomStatus` | |
| - private | `displayName` | `String` | *final* |

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474RoomStatus(String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDisplayName()` | `String` | |
| + public | `toString()` | `String` | |

### K2636474RoomType  *(enum)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| constant | `STANDARD` | `K2636474RoomType` | |
| constant | `DELUXE` | `K2636474RoomType` | |
| constant | `SUITE` | `K2636474RoomType` | |
| constant | `FAMILY_SUITE` | `K2636474RoomType` | |
| - private | `displayName` | `String` | *final* |

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474RoomType(String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDisplayName()` | `String` | |
| + public | `toString()` | `String` | |

### K2636474UserRole  *(enum)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| constant | `GUEST` | `K2636474UserRole` | |
| constant | `ADMIN` | `K2636474UserRole` | |
| - private | `displayName` | `String` | *final* |

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474UserRole(String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDisplayName()` | `String` | |
| + public | `toString()` | `String` | |


## Package `com.k2636474.smartstay.pricing`

### K2636474DiscountPolicy  *(interface)*

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `rateFor(K2636474Booking)` | `double` | *abstract* |
| + public | `describe()` | `String` | *abstract* |

### K2636474InvoiceCalculator  *(class)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `discountPolicy` | `K2636474DiscountPolicy` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474InvoiceCalculator(K2636474DiscountPolicy)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `calculate(K2636474Booking, String, LocalDateTime)` | `K2636474Invoice` | |
| + public | `getDiscountPolicy()` | `K2636474DiscountPolicy` | |

### K2636474LongStayDiscountPolicy  *(class)*
- implements `K2636474DiscountPolicy`

| Vis | Field | Type | Notes |
|---|---|---|---|
| + public | `MIN_NIGHTS` | `int` | *static* *final* |
| + public | `RATE` | `double` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474LongStayDiscountPolicy()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `rateFor(K2636474Booking)` | `double` | |
| + public | `describe()` | `String` | |


## Package `com.k2636474.smartstay.service`

### K2636474AuthService  *(class)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `dataStore` | `K2636474DataStore` | *final* |
| - private | `currentUser` | `K2636474User` | |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474AuthService(K2636474DataStore)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `validateRegistration(K2636474IdType, String, String, String, String, String, String)` | `Map<String, String>` | |
| - private | `collect(Map<String, String>, Runnable)` | `void` | *static* |
| + public | `registerGuest(K2636474IdType, String, String, String, String, String, String)` | `K2636474Guest` | |
| + public | `registerAdmin(String, String, String, String, String)` | `K2636474Admin` | |
| + public | `login(String, String)` | `K2636474User` | |
| + public | `logout()` | `void` | |
| + public | `getCurrentUser()` | `K2636474User` | |
| + public | `getAllGuests()` | `List<K2636474Guest>` | |

### K2636474BookingService  *(class)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| + public | `DRAFT_BOOKING_ID` | `String` | *static* *final* |
| + public | `UPCOMING_DAYS` | `int` | *static* *final* |
| - private | `dataStore` | `K2636474DataStore` | *final* |
| - private | `roomService` | `K2636474RoomService` | *final* |
| - private | `invoiceService` | `K2636474InvoiceService` | *final* |
| - private | `clock` | `Clock` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474BookingService(K2636474DataStore, K2636474RoomService, K2636474InvoiceService, Clock)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `findAvailableRooms(K2636474RoomType, LocalDate, LocalDate, int)` | `List<K2636474Room>` | |
| + public | `createBooking(K2636474Guest, String, LocalDate, LocalDate, int)` | `K2636474Booking` | |
| + public | `recordExistingBooking(K2636474Guest, String, LocalDate, LocalDate, int)` | `K2636474Booking` | |
| - private | `store(K2636474Guest, K2636474Room, LocalDate, LocalDate, int)` | `K2636474Booking` | |
| + public | `modifyBooking(String, K2636474User, LocalDate, LocalDate, int)` | `K2636474Booking` | |
| + public | `cancelBooking(String, K2636474User)` | `K2636474Booking` | |
| + public | `previewInvoice(K2636474Guest, String, LocalDate, LocalDate, int)` | `K2636474Invoice` | |
| + public | `whyCannotChange(K2636474Booking)` | `String` | |
| - private | `requireChangeAllowed(K2636474Booking, K2636474User)` | `void` | |
| + public | `validateStayDates(LocalDate, LocalDate)` | `void` | |
| + public | `earliestCheckIn()` | `LocalDate` | |
| - private | `validateRoomFor(K2636474Room, LocalDate, LocalDate, int, String)` | `void` | |
| + public | `findById(String)` | `K2636474Booking` | |
| + public | `searchByGuestName(String)` | `List<K2636474Booking>` | |
| + public | `searchReservations(K2636474User, String)` | `List<K2636474Booking>` | |
| + public | `searchReservations(K2636474User, String, LocalDate)` | `List<K2636474Booking>` | |
| + public | `getByCheckInDate(LocalDate)` | `List<K2636474Booking>` | |
| + public | `getByCheckInDate(K2636474User, LocalDate)` | `List<K2636474Booking>` | |
| + public | `getBookingsInRange(LocalDate, LocalDate)` | `List<K2636474Booking>` | |
| + public | `getBookingsForGuest(K2636474Guest)` | `List<K2636474Booking>` | |
| + public | `getAllBookings()` | `List<K2636474Booking>` | |
| + public | `countActiveBookings()` | `long` | |
| + public | `getUpcomingCheckIns()` | `List<K2636474Booking>` | |
| - private | `matchesQuery(K2636474Booking, String)` | `boolean` | *static* |
| - private | `isVisibleTo(K2636474Booking, K2636474User)` | `boolean` | *static* |

### K2636474DataStore  *(class, final)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `INSTANCE` | `K2636474DataStore` | *static* *final* |
| - private | `rooms` | `Map<String, K2636474Room>` | *final* |
| - private | `bookings` | `Map<String, K2636474Booking>` | *final* |
| - private | `usersByLogin` | `Map<String, K2636474User>` | *final* |
| - private | `registeredIdNumbers` | `Set<String>` | *final* |
| - private | `bookingsByCheckIn` | `NavigableMap<LocalDate, List<K2636474Booking>>` | *final* |
| - private | `idGenerator` | `K2636474IdGenerator` | *final* |

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474DataStore()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getInstance()` | `K2636474DataStore` | *static* |
| + public | `clear()` | `void` | |
| + public | `getIdGenerator()` | `K2636474IdGenerator` | |
| + public | `saveRoom(K2636474Room)` | `void` | |
| + public | `deleteRoom(String)` | `void` | |
| + public | `findRoom(String)` | `Optional<K2636474Room>` | |
| + public | `getRooms()` | `List<K2636474Room>` | |
| + public | `saveUser(String, K2636474User)` | `void` | |
| + public | `findUserByLogin(String)` | `Optional<K2636474User>` | |
| + public | `getUsers()` | `Collection<K2636474User>` | |
| + public | `registerIdNumber(String)` | `void` | |
| + public | `isIdNumberRegistered(String)` | `boolean` | |
| - private | `normaliseLogin(String)` | `String` | *static* |
| + public | `saveBooking(K2636474Booking)` | `void` | |
| + public | `reindexBooking(K2636474Booking, LocalDate)` | `void` | |
| + public | `findBooking(String)` | `Optional<K2636474Booking>` | |
| + public | `getBookingsSortedByCheckIn()` | `List<K2636474Booking>` | |
| + public | `getBookingsByCheckIn(LocalDate)` | `List<K2636474Booking>` | |
| + public | `getBookingsByCheckInRange(LocalDate, LocalDate)` | `List<K2636474Booking>` | |
| + public | `getBookingsCheckingInBefore(LocalDate)` | `List<K2636474Booking>` | |

### K2636474InvoiceService  *(class)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| + public | `PREVIEW_ID` | `String` | *static* *final* |
| - private | `LINE_WIDTH` | `int` | *static* *final* |
| - private | `LABEL_WIDTH` | `int` | *static* *final* |
| - private | `dataStore` | `K2636474DataStore` | *final* |
| - private | `calculator` | `K2636474InvoiceCalculator` | *final* |
| - private | `clock` | `Clock` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474InvoiceService(K2636474DataStore, K2636474InvoiceCalculator, Clock)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `generateInvoice(K2636474Booking)` | `K2636474Invoice` | |
| + public | `previewInvoice(K2636474Booking)` | `K2636474Invoice` | |
| + public | `describeDiscountPolicy()` | `String` | |
| + public | `formatInvoice(K2636474Invoice)` | `String` | |
| - private | `centre(String)` | `String` | *static* |
| - private | `field(String, String)` | `String` | *static* |
| - private | `amountLine(String, double)` | `String` | *static* |
| - private | `textLine(String, String)` | `String` | *static* |

### K2636474RoomService  *(class)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `dataStore` | `K2636474DataStore` | *final* |
| - private | `clock` | `Clock` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474RoomService(K2636474DataStore, Clock)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `addRoom(K2636474RoomType, K2636474BedType, K2636474RoomStatus)` | `K2636474Room` | |
| + public | `updateRoom(String, K2636474RoomType, K2636474BedType, K2636474RoomStatus)` | `K2636474Room` | |
| + public | `removeRoom(String)` | `void` | |
| + public | `changeStatus(String, K2636474RoomStatus)` | `K2636474Room` | |
| + public | `getAllRooms()` | `List<K2636474Room>` | |
| + public | `findById(String)` | `K2636474Room` | |
| + public | `countByStatus(K2636474RoomStatus)` | `long` | |
| + public | `searchAvailable(K2636474RoomType, LocalDate, LocalDate, int)` | `List<K2636474Room>` | |
| + public | `isRoomFreeFor(K2636474Room, LocalDate, LocalDate, String)` | `boolean` | |
| - private | `hasActiveFutureBookings(K2636474Room)` | `boolean` | |


## Package `com.k2636474.smartstay.exception`

### K2636474AuthenticationException  *(class)*
- extends `RuntimeException`

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474AuthenticationException(String)` | |

### K2636474BookingException  *(class)*
- extends `RuntimeException`

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474BookingException(String)` | |

### K2636474NotFoundException  *(class)*
- extends `RuntimeException`

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474NotFoundException(String)` | |

### K2636474ValidationException  *(class)*
- extends `RuntimeException`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `fieldName` | `String` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474ValidationException(String, String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getFieldName()` | `String` | |


## Package `com.k2636474.smartstay.util`

### K2636474Format  *(class, final)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| + public | `DATE_FORMAT` | `DateTimeFormatter` | *static* *final* |
| + public | `DATE_TIME_FORMAT` | `DateTimeFormatter` | *static* *final* |
| + public | `FRIENDLY_DATE_FORMAT` | `DateTimeFormatter` | *static* *final* |
| - private | `MONEY_PATTERN` | `String` | *static* *final* |
| - private | `PERCENT_PATTERN` | `String` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474Format()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `lkr(double)` | `String` | *static* |
| + public | `amount(double)` | `String` | *static* |
| + public | `percent(double)` | `String` | *static* |
| + public | `date(LocalDate)` | `String` | *static* |
| + public | `friendlyDate(LocalDate)` | `String` | *static* |
| + public | `dateTime(LocalDateTime)` | `String` | *static* |

### K2636474IdGenerator  *(class)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| + public | `ROOM_PREFIX` | `String` | *static* *final* |
| + public | `BOOKING_PREFIX` | `String` | *static* *final* |
| + public | `INVOICE_PREFIX` | `String` | *static* *final* |
| + public | `GUEST_PREFIX` | `String` | *static* *final* |
| + public | `ADMIN_PREFIX` | `String` | *static* *final* |
| - private | `ROOM_DIGITS` | `int` | *static* *final* |
| - private | `DEFAULT_DIGITS` | `int` | *static* *final* |
| - private | `lastIssued` | `Map<String, Integer>` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474IdGenerator()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `next(String, int)` | `String` | |
| + public | `nextRoomId()` | `String` | |
| + public | `nextBookingId()` | `String` | |
| + public | `nextInvoiceId()` | `String` | |
| + public | `nextGuestId()` | `String` | |
| + public | `nextAdminId()` | `String` | |
| + public | `reset()` | `void` | |

### K2636474Validator  *(class, final)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| + public | `NIC_OLD_PATTERN` | `Pattern` | *static* *final* |
| + public | `NIC_NEW_PATTERN` | `Pattern` | *static* *final* |
| + public | `PASSPORT_PATTERN` | `Pattern` | *static* *final* |
| + public | `PHONE_PATTERN` | `Pattern` | *static* *final* |
| + public | `EMAIL_PATTERN` | `Pattern` | *static* *final* |
| + public | `NAME_PATTERN` | `Pattern` | *static* *final* |
| + public | `USERNAME_PATTERN` | `Pattern` | *static* *final* |
| + public | `MIN_PASSWORD_LENGTH` | `int` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474Validator()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `requireName(String, String)` | `String` | *static* |
| + public | `requirePhone(String, String)` | `String` | *static* |
| + public | `requireEmail(String, String)` | `String` | *static* |
| + public | `requireIdNumber(String, K2636474IdType, String)` | `String` | *static* |
| + public | `requireUsername(String, String)` | `String` | *static* |
| + public | `requirePassword(String, String)` | `String` | *static* |
| + public | `requireNotNull(String, T, String)` | `<T> T` | *static* |
| - private | `requireText(String, String, String)` | `String` | *static* |


## Package `com.k2636474.smartstay.ui`

### K2636474AdminDashboard  *(class)*
- extends `K2636474DashboardShell`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `OVERVIEW` | `String` | *static* *final* |
| - private | `ROOMS` | `String` | *static* *final* |
| - private | `RESERVATIONS` | `String` | *static* *final* |
| - private | `GUESTS` | `String` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474AdminDashboard(K2636474UiContext, Runnable)` | |

### K2636474BookingDialog  *(class)*
- extends `JDialog`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `DIALOG_WIDTH` | `int` | *static* *final* |
| - private | `DIALOG_HEIGHT` | `int` | *static* *final* |
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `room` | `K2636474Room` | *final* |
| - private | `existingBooking` | `K2636474Booking` | *final* |
| - private | `guest` | `K2636474Guest` | *final* |
| - private | `onSaved` | `Consumer<K2636474Booking>` | *final* |
| - private | `checkInField` | `K2636474DateField` | *final* |
| - private | `checkOutField` | `K2636474DateField` | *final* |
| - private | `guestsSpinner` | `JSpinner` | *final* |
| - private | `nightsLabel` | `JLabel` | *final* |
| - private | `breakdown` | `K2636474PriceBreakdownPanel` | *final* |
| - private | `confirmButton` | `JButton` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474BookingDialog(K2636474UiContext, K2636474Room, K2636474Booking, LocalDate, LocalDate, int, Consumer<K2636474Booking>)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| - private | `buildHeader()` | `JComponent` | |
| - private | `buildForm()` | `JComponent` | |
| - private | `labelled(String, JComponent)` | `JComponent` | *static* |
| - private | `buildNote()` | `JComponent` | *static* |
| - private | `buildButtons()` | `JComponent` | |
| - private | `updatePreview()` | `void` | |
| - private | `confirm()` | `void` | |
| - private | `showConfirmation(K2636474Booking)` | `void` | |

### K2636474BookingTableModel  *(class)*
- extends `AbstractTableModel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `COLUMNS` | `String[]` | *static* *final* |
| - private | `TYPES` | `Class<?>[]` | *static* *final* |
| + public | `TOTAL_COLUMN` | `int` | *static* *final* |
| + public | `STATUS_COLUMN` | `int` | *static* *final* |
| - private | `WIDTHS` | `int[]` | *static* *final* |
| - private | `rows` | `List<K2636474Booking>` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474BookingTableModel()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `configure(JTable)` | `void` | *static* |
| + public | `setBookings(List<K2636474Booking>)` | `void` | |
| + public | `getBookingAt(int)` | `K2636474Booking` | |
| + public | `getRowCount()` | `int` | |
| + public | `getColumnCount()` | `int` | |
| + public | `getColumnName(int)` | `String` | |
| + public | `getColumnClass(int)` | `Class<?>` | |
| + public | `getValueAt(int, int)` | `Object` | |

### K2636474BrandPanel  *(class)*
- extends `JPanel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `PATTERN_STEP` | `int` | *static* *final* |
| - private | `PATTERN_LINE` | `Color` | *static* *final* |
| - private | `RING` | `Color` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474BrandPanel()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| # protected | `paintComponent(Graphics)` | `void` | |
| - private | `paintDiamondLattice(Graphics2D)` | `void` | |
| - private | `paintRings(Graphics2D)` | `void` | |

### K2636474CardPanel  *(class)*
- extends `JPanel`

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474CardPanel(LayoutManager)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| # protected | `paintComponent(Graphics)` | `void` | |

### K2636474DashboardShell  *(abstract class)*
- extends `JPanel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `pageCards` | `CardLayout` | *final* |
| - private | `pageArea` | `JPanel` | *final* |
| - private | `navList` | `JPanel` | *final* |
| - private | `pages` | `Map<String, K2636474Page>` | *final* |
| - private | `navItems` | `Map<String, K2636474NavItem>` | *final* |

| Vis | Constructor | |
|---|---|---|
| # protected | `K2636474DashboardShell(K2636474UiContext, Runnable)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| - private | `buildSidebar(K2636474User, Runnable)` | `JPanel` | |
| - private | `buildLogo()` | `JComponent` | *static* |
| - private | `buildUserBox(K2636474User)` | `JComponent` | *static* |
| # protected | `addPage(String, String, String, K2636474Page)` | `void` | |
| + public | `showPage(String)` | `void` | |
| # protected | `getContext()` | `K2636474UiContext` | |

### K2636474DateField  *(class)*
- extends `JSpinner`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `PATTERN` | `String` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474DateField(LocalDate)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getDate()` | `LocalDate` | |
| + public | `setDate(LocalDate)` | `void` | |
| - private | `toDate(LocalDate)` | `Date` | *static* |

### K2636474FindRoomPage  *(class)*
- extends `K2636474Page`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `ANY_TYPE` | `String` | *static* *final* |
| - private | `GRID_COLUMNS` | `int` | *static* *final* |
| - private | `MAX_GUESTS_SEARCH` | `int` | *static* *final* |
| - private | `PROMPT_VIEW` | `String` | *static* *final* |
| - private | `GRID_VIEW` | `String` | *static* *final* |
| - private | `LIST_VIEW` | `String` | *static* *final* |
| - private | `EMPTY_VIEW` | `String` | *static* *final* |
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `typeBox` | `JComboBox<Object>` | *final* |
| - private | `checkInField` | `K2636474DateField` | *final* |
| - private | `checkOutField` | `K2636474DateField` | *final* |
| - private | `guestsSpinner` | `JSpinner` | *final* |
| - private | `resultsTitle` | `JLabel` | *final* |
| - private | `resultViews` | `CardLayout` | *final* |
| - private | `resultsArea` | `JPanel` | *final* |
| - private | `cardGrid` | `JPanel` | *final* |
| - private | `listModel` | `K2636474RoomTableModel` | *final* |
| - private | `listTable` | `JTable` | *final* |
| - private | `gridToggle` | `JToggleButton` | *final* |
| - private | `listToggle` | `JToggleButton` | *final* |
| - private | `bookSelectedButton` | `JButton` | *final* |
| - private | `lastResults` | `List<K2636474Room>` | |
| - private | `hasSearched` | `boolean` | |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474FindRoomPage(K2636474UiContext)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| - private | `buildSearchCard()` | `JComponent` | |
| - private | `addSearchField(JPanel, int, String, JComponent, double)` | `void` | *static* |
| - private | `buildResults()` | `JComponent` | |
| - private | `wrapInCard(JComponent)` | `JComponent` | *static* |
| - private | `search()` | `void` | |
| - private | `showResults()` | `void` | |
| - private | `bookSelectedRow()` | `void` | |
| - private | `openBooking(K2636474Room)` | `void` | |
| + public | `refresh()` | `void` | |

### K2636474FormPanel  *(class)*
- extends `JPanel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `errorLabels` | `Map<String, JLabel>` | *final* |
| - private | `fields` | `Map<String, JComponent>` | *final* |
| - private | `nextRow` | `int` | |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474FormPanel()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `addRow(JComponent, int)` | `void` | |
| + public | `addField(String, String, JComponent)` | `void` | |
| + public | `showError(String, String)` | `void` | |
| + public | `clearErrors()` | `void` | |

### K2636474GuestDashboard  *(class)*
- extends `K2636474DashboardShell`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `FIND_ROOM` | `String` | *static* *final* |
| - private | `MY_BOOKINGS` | `String` | *static* *final* |
| - private | `SEARCH` | `String` | *static* *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474GuestDashboard(K2636474UiContext, Runnable)` | |

### K2636474GuestsPage  *(class)*
- extends `K2636474Page`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `COLUMNS` | `String[]` | *static* *final* |
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `model` | `DefaultTableModel` | *final* |
| - private | `tableCard` | `K2636474TableCard` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474GuestsPage(K2636474UiContext)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `refresh()` | `void` | |

### K2636474InvoiceDialog  *(class)*
- extends `JDialog`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `COPIED_FEEDBACK_MILLIS` | `int` | *static* *final* |
| - private | `invoiceText` | `String` | *final* |
| - private | `invoice` | `K2636474Invoice` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474InvoiceDialog(K2636474UiContext, K2636474Invoice)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| - private | `copyToClipboard(JButton)` | `void` | |
| - private | `saveToFile()` | `void` | |

### K2636474LoginPanel  *(class)*
- extends `JPanel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `CARD_WIDTH` | `int` | *static* *final* |
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `onLoggedIn` | `Consumer<K2636474User>` | *final* |
| - private | `loginField` | `JTextField` | *final* |
| - private | `passwordField` | `JPasswordField` | *final* |
| - private | `messageLabel` | `JLabel` | *final* |
| - private | `loginButton` | `JButton` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474LoginPanel(K2636474UiContext, Consumer<K2636474User>, Runnable)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `reset(String, String)` | `void` | |
| - private | `attemptLogin()` | `void` | |
| - private | `showMessage(String, boolean)` | `void` | |

### K2636474MainFrame  *(class)*
- extends `JFrame`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `LOGIN_CARD` | `String` | *static* *final* |
| - private | `REGISTER_CARD` | `String` | *static* *final* |
| - private | `DASHBOARD_CARD` | `String` | *static* *final* |
| - private | `MINIMUM_SIZE` | `Dimension` | *static* *final* |
| - private | `cards` | `CardLayout` | *final* |
| - private | `root` | `JPanel` | *final* |
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `loginPanel` | `K2636474LoginPanel` | *final* |
| - private | `registerPanel` | `K2636474RegisterPanel` | *final* |
| - private | `dashboard` | `K2636474DashboardShell` | |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474MainFrame(K2636474AuthService, K2636474RoomService, K2636474BookingService, K2636474InvoiceService)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `showLogin(String, String)` | `void` | |
| + public | `showRegister()` | `void` | |
| - private | `onRegistered(K2636474Guest)` | `void` | |
| - private | `showDashboard(K2636474User)` | `void` | |
| - private | `logout()` | `void` | |

### K2636474MyBookingsPage  *(class)*
- extends `K2636474Page`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `model` | `K2636474BookingTableModel` | *final* |
| - private | `table` | `JTable` | *final* |
| - private | `modifyButton` | `JButton` | *final* |
| - private | `cancelButton` | `JButton` | *final* |
| - private | `invoiceButton` | `JButton` | *final* |
| - private | `tableCard` | `K2636474TableCard` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474MyBookingsPage(K2636474UiContext)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| - private | `selectedBooking()` | `K2636474Booking` | |
| - private | `updateButtons()` | `void` | |
| - private | `modify()` | `void` | |
| - private | `cancel()` | `void` | |
| - private | `viewInvoice()` | `void` | |
| + public | `refresh()` | `void` | |

### K2636474NavItem  *(class)*
- extends `JPanel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `HEIGHT` | `int` | *static* *final* |
| - private | `ACTIVE_BAR_WIDTH` | `int` | *static* *final* |
| - private | `HOVER` | `Color` | *static* *final* |
| - private | `ACTIVE` | `Color` | *static* *final* |
| - private | `label` | `JLabel` | *final* |
| - private | `iconName` | `String` | *final* |
| - private | `active` | `boolean` | |
| - private | `hovered` | `boolean` | |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474NavItem(String, String, Runnable)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `setActive(boolean)` | `void` | |
| # protected | `paintComponent(Graphics)` | `void` | |

### K2636474OverviewPage  *(class)*
- extends `K2636474Page`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `totalRoomsValue` | `JLabel` | *final* |
| - private | `availableValue` | `JLabel` | *final* |
| - private | `maintenanceValue` | `JLabel` | *final* |
| - private | `activeBookingsValue` | `JLabel` | *final* |
| - private | `checkInsValue` | `JLabel` | *final* |
| - private | `upcomingModel` | `K2636474BookingTableModel` | *final* |
| - private | `upcomingCard` | `K2636474TableCard` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474OverviewPage(K2636474UiContext)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| - private | `statValueLabel()` | `JLabel` | *static* |
| - private | `statCard(String, String, JLabel, Color)` | `JPanel` | *static* |
| + public | `refresh()` | `void` | |

### K2636474Page  *(abstract class)*
- extends `JPanel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `header` | `JPanel` | *final* |
| - private | `body` | `JPanel` | *final* |

| Vis | Constructor | |
|---|---|---|
| # protected | `K2636474Page(String, String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| # protected | `getBody()` | `JPanel` | |
| + public | `refresh()` | `void` | *abstract* |

### K2636474PillLabel  *(class)*
- extends `JLabel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `TINT_ALPHA` | `int` | *static* *final* |
| - private | `pillColor` | `Color` | |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474PillLabel()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `setPill(String, Color)` | `void` | |
| # protected | `paintComponent(Graphics)` | `void` | |

### K2636474PriceBreakdownPanel  *(class)*
- extends `K2636474CardPanel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `lines` | `JPanel` | *final* |
| - private | `discountPolicyDescription` | `String` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474PriceBreakdownPanel(String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `showInvoice(K2636474Invoice)` | `void` | |
| + public | `showMessage(String)` | `void` | |
| - private | `addLine(String, String, String, Color)` | `void` | |
| - private | `addFinalLine(K2636474Invoice)` | `void` | |
| - private | `addDivider(Color, int)` | `void` | |
| - private | `addRow(JComponent)` | `void` | |
| - private | `refreshLayout()` | `void` | |

### K2636474RegisterPanel  *(class)*
- extends `JPanel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `CARD_WIDTH` | `int` | *static* *final* |
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `onRegistered` | `Consumer<K2636474Guest>` | *final* |
| - private | `form` | `K2636474FormPanel` | *final* |
| - private | `columns` | `Map<String, K2636474FormPanel>` | *final* |
| - private | `idTypeBox` | `JComboBox<K2636474IdType>` | *final* |
| - private | `idNumberField` | `JTextField` | *final* |
| - private | `fullNameField` | `JTextField` | *final* |
| - private | `contactField` | `JTextField` | *final* |
| - private | `emailField` | `JTextField` | *final* |
| - private | `passwordField` | `JPasswordField` | *final* |
| - private | `confirmField` | `JPasswordField` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474RegisterPanel(K2636474UiContext, Consumer<K2636474Guest>, Runnable)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| - private | `fieldColumn(String, String, JTextField)` | `K2636474FormPanel` | |
| - private | `twoColumns(JPanel, JPanel)` | `JPanel` | *static* |
| - private | `configurePlaceholders()` | `void` | |
| - private | `updateIdPlaceholder()` | `void` | |
| + public | `reset()` | `void` | |
| - private | `attemptRegister()` | `void` | |
| - private | `showError(String, String)` | `void` | |
| - private | `clearErrors()` | `void` | |

### K2636474ReservationsPage  *(class)*
- extends `K2636474Page`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `model` | `K2636474BookingTableModel` | *final* |
| - private | `table` | `JTable` | *final* |
| - private | `searchField` | `JTextField` | *final* |
| - private | `dateFilterBox` | `JCheckBox` | *final* |
| - private | `dateField` | `K2636474DateField` | *final* |
| - private | `invoiceButton` | `JButton` | *final* |
| - private | `resultCount` | `JLabel` | *final* |
| - private | `tableCard` | `K2636474TableCard` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474ReservationsPage(K2636474UiContext, String, String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| - private | `buildToolbar()` | `JPanel` | |
| - private | `selectedBooking()` | `K2636474Booking` | |
| - private | `viewInvoice()` | `void` | |
| + public | `refresh()` | `void` | |

### K2636474RoomCard  *(class)*
- extends `K2636474CardPanel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `STRIP_HEIGHT` | `int` | *static* *final* |
| - private | `room` | `K2636474Room` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474RoomCard(K2636474Room, Consumer<K2636474Room>)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| - private | `buildFooter(Consumer<K2636474Room>)` | `JComponent` | |
| - private | `detailLine(String, String)` | `JLabel` | *static* |
| # protected | `paintComponent(Graphics)` | `void` | |

### K2636474RoomFormDialog  *(class)*
- extends `JDialog`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `room` | `K2636474Room` | *final* |
| - private | `onSaved` | `Consumer<K2636474Room>` | *final* |
| - private | `typeBox` | `JComboBox<K2636474RoomType>` | *final* |
| - private | `bedBox` | `JComboBox<K2636474BedType>` | *final* |
| - private | `statusBox` | `JComboBox<K2636474RoomStatus>` | *final* |
| - private | `packageLabel` | `JLabel` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474RoomFormDialog(K2636474UiContext, K2636474Room, Consumer<K2636474Room>)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| - private | `updatePackageInfo()` | `void` | |
| - private | `save()` | `void` | |

### K2636474RoomTableModel  *(class)*
- extends `AbstractTableModel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `COLUMNS` | `String[]` | *static* *final* |
| - private | `TYPES` | `Class<?>[]` | *static* *final* |
| + public | `TYPE_COLUMN` | `int` | *static* *final* |
| + public | `RATE_COLUMN` | `int` | *static* *final* |
| + public | `STATUS_COLUMN` | `int` | *static* *final* |
| - private | `rows` | `List<K2636474Room>` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474RoomTableModel()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `setRooms(List<K2636474Room>)` | `void` | |
| + public | `getRoomAt(int)` | `K2636474Room` | |
| + public | `getRowCount()` | `int` | |
| + public | `getColumnCount()` | `int` | |
| + public | `getColumnName(int)` | `String` | |
| + public | `getColumnClass(int)` | `Class<?>` | |
| + public | `getValueAt(int, int)` | `Object` | |

### K2636474RoomsPage  *(class)*
- extends `K2636474Page`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `ALL` | `String` | *static* *final* |
| - private | `context` | `K2636474UiContext` | *final* |
| - private | `model` | `K2636474RoomTableModel` | *final* |
| - private | `table` | `JTable` | *final* |
| - private | `sorter` | `TableRowSorter<K2636474RoomTableModel>` | *final* |
| - private | `typeFilter` | `JComboBox<Object>` | *final* |
| - private | `statusFilter` | `JComboBox<Object>` | *final* |
| - private | `editButton` | `JButton` | *final* |
| - private | `removeButton` | `JButton` | *final* |
| - private | `statusButton` | `JButton` | *final* |
| - private | `tableCard` | `K2636474TableCard` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474RoomsPage(K2636474UiContext)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| - private | `buildToolbar()` | `JPanel` | |
| - private | `applyFilters()` | `void` | |
| - private | `selectedRoom()` | `K2636474Room` | |
| - private | `updateButtons()` | `void` | |
| - private | `addRoom()` | `void` | |
| - private | `editRoom()` | `void` | |
| - private | `removeRoom()` | `void` | |
| - private | `showStatusMenu()` | `void` | |
| + public | `refresh()` | `void` | |

### K2636474StatusPillRenderer  *(class)*
- extends `JPanel`; implements `TableCellRenderer`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `pill` | `K2636474PillLabel` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474StatusPillRenderer()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)` | `Component` | |

### K2636474TableCard  *(class)*
- extends `K2636474CardPanel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `TABLE_VIEW` | `String` | *static* *final* |
| - private | `EMPTY_VIEW` | `String` | *static* *final* |
| - private | `views` | `CardLayout` | *final* |
| - private | `table` | `JTable` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474TableCard(JTable, String, String, String)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `updateView()` | `void` | |

### K2636474Toast  *(class)*
- extends `JPanel`

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `messageLabel` | `JLabel` | *final* |
| - private | `hideTimer` | `Timer` | *final* |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474Toast()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `showMessage(String)` | `void` | |

### K2636474UiContext  *(class)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| - private | `authService` | `K2636474AuthService` | *final* |
| - private | `roomService` | `K2636474RoomService` | *final* |
| - private | `bookingService` | `K2636474BookingService` | *final* |
| - private | `invoiceService` | `K2636474InvoiceService` | *final* |
| - private | `window` | `Window` | *final* |
| - private | `toast` | `K2636474Toast` | |

| Vis | Constructor | |
|---|---|---|
| + public | `K2636474UiContext(K2636474AuthService, K2636474RoomService, K2636474BookingService, K2636474InvoiceService, Window)` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `toast(String)` | `void` | |
| + public | `setToast(K2636474Toast)` | `void` | |
| + public | `getCurrentUser()` | `K2636474User` | |
| + public | `getCurrentGuest()` | `K2636474Guest` | |
| + public | `getAuthService()` | `K2636474AuthService` | |
| + public | `getRoomService()` | `K2636474RoomService` | |
| + public | `getBookingService()` | `K2636474BookingService` | |
| + public | `getInvoiceService()` | `K2636474InvoiceService` | |
| + public | `getWindow()` | `Window` | |

### K2636474UiTheme  *(class, final)*

| Vis | Field | Type | Notes |
|---|---|---|---|
| + public | `PRIMARY` | `Color` | *static* *final* |
| + public | `PRIMARY_HOVER` | `Color` | *static* *final* |
| + public | `PRIMARY_TINT` | `Color` | *static* *final* |
| + public | `ACCENT` | `Color` | *static* *final* |
| + public | `BACKGROUND` | `Color` | *static* *final* |
| + public | `SURFACE` | `Color` | *static* *final* |
| + public | `BORDER` | `Color` | *static* *final* |
| + public | `TEXT` | `Color` | *static* *final* |
| + public | `TEXT_SECONDARY` | `Color` | *static* *final* |
| + public | `SIDEBAR_TEXT_MUTED` | `Color` | *static* *final* |
| + public | `STATUS_AVAILABLE` | `Color` | *static* *final* |
| + public | `STATUS_RESERVED` | `Color` | *static* *final* |
| + public | `STATUS_MAINTENANCE` | `Color` | *static* *final* |
| + public | `STATUS_CANCELLED` | `Color` | *static* *final* |
| + public | `SUCCESS` | `Color` | *static* *final* |
| + public | `ERROR` | `Color` | *static* *final* |
| + public | `STANDARD_STRIP` | `Color` | *static* *final* |
| + public | `DELUXE_STRIP` | `Color` | *static* *final* |
| + public | `SUITE_STRIP` | `Color` | *static* *final* |
| + public | `FAMILY_SUITE_STRIP` | `Color` | *static* *final* |
| + public | `GRID` | `int` | *static* *final* |
| + public | `GAP` | `int` | *static* *final* |
| + public | `PAGE_PADDING` | `int` | *static* *final* |
| + public | `SIDEBAR_WIDTH` | `int` | *static* *final* |
| + public | `CARD_ARC` | `int` | *static* *final* |
| + public | `CONTROL_ARC` | `int` | *static* *final* |
| + public | `TITLE_SIZE` | `int` | *static* *final* |
| + public | `SECTION_SIZE` | `int` | *static* *final* |
| + public | `BODY_SIZE` | `int` | *static* *final* |
| + public | `CAPTION_SIZE` | `int` | *static* *final* |
| + public | `MONEY_LARGE_SIZE` | `int` | *static* *final* |
| + public | `TOAST_MILLIS` | `int` | *static* *final* |
| - private | `ICON_FOLDER` | `String` | *static* *final* |
| - private | `interAvailable` | `boolean` | *static* |

| Vis | Constructor | |
|---|---|---|
| - private | `K2636474UiTheme()` | |

| Vis | Method | Returns | Notes |
|---|---|---|---|
| + public | `install()` | `void` | *static* |
| - private | `applyFlatLafTuning()` | `void` | *static* |
| - private | `installNimbusFallback()` | `void` | *static* |
| + public | `font(int, int)` | `Font` | *static* |
| + public | `semibold(int)` | `Font` | *static* |
| + public | `monospaced(int)` | `Font` | *static* |
| + public | `pageTitle(String)` | `JLabel` | *static* |
| + public | `sectionTitle(String)` | `JLabel` | *static* |
| + public | `subtitle(String)` | `JLabel` | *static* |
| + public | `caption(String)` | `JLabel` | *static* |
| + public | `fieldLabel(String)` | `JLabel` | *static* |
| + public | `label(String, Font, Color)` | `JLabel` | *static* |
| + public | `icon(String, int, Color)` | `Icon` | *static* |
| + public | `appIcons()` | `List<Image>` | *static* |
| + public | `paintBrandMark(Graphics2D, float, float, float)` | `void` | *static* |
| + public | `primaryButton(String, String)` | `JButton` | *static* |
| + public | `secondaryButton(String, String)` | `JButton` | *static* |
| + public | `dangerButton(String, String)` | `JButton` | *static* |
| + public | `linkButton(String)` | `JButton` | *static* |
| - private | `styledButton(String, String, Color, String)` | `JButton` | *static* |
| + public | `card()` | `K2636474CardPanel` | *static* |
| + public | `styleTable(JTable)` | `void` | *static* |
| + public | `scroll(Component)` | `JScrollPane` | *static* |
| + public | `moneyRenderer()` | `TableCellRenderer` | *static* |
| + public | `emptyState(String, String, String)` | `JPanel` | *static* |
| + public | `statusColor(Object)` | `Color` | *static* |
| + public | `roomTypeColor(K2636474RoomType)` | `Color` | *static* |
| + public | `hex(Color)` | `String` | *static* |
| + public | `installDialogKeys(JDialog, JButton)` | `void` | *static* |
| + public | `showError(Component, RuntimeException)` | `void` | *static* |
| + public | `showSuccess(Component, String, String)` | `void` | *static* |


---

## Relationships (for the UML class diagram)

### Inheritance (generalisation)
| Subclass | Superclass |
|---|---|
| K2636474Guest | K2636474User *(abstract)* |
| K2636474Admin | K2636474User *(abstract)* |
| K2636474StandardPackage, K2636474DeluxePackage, K2636474SuitePackage, K2636474FamilySuitePackage | K2636474RoomPackage *(abstract)* |
| K2636474ValidationException, K2636474BookingException, K2636474NotFoundException, K2636474AuthenticationException | RuntimeException |
| K2636474AdminDashboard, K2636474GuestDashboard | K2636474DashboardShell *(abstract)* |
| K2636474OverviewPage, K2636474RoomsPage, K2636474ReservationsPage, K2636474GuestsPage, K2636474FindRoomPage, K2636474MyBookingsPage | K2636474Page *(abstract)* |
| K2636474CardPanel | JPanel |
| K2636474RoomCard, K2636474PriceBreakdownPanel, K2636474TableCard | K2636474CardPanel |
| K2636474BookingTableModel, K2636474RoomTableModel | AbstractTableModel |
| K2636474MainFrame | JFrame |
| K2636474BookingDialog, K2636474InvoiceDialog, K2636474RoomFormDialog | JDialog |
| K2636474DateField | JSpinner |
| K2636474PillLabel | JLabel |
| K2636474LoginPanel, K2636474RegisterPanel, K2636474BrandPanel, K2636474FormPanel, K2636474NavItem, K2636474Toast | JPanel |

### Interface realisation
| Class | Interface |
|---|---|
| K2636474RoomPackage | K2636474Chargeable |
| K2636474LongStayDiscountPolicy | K2636474DiscountPolicy |
| K2636474StatusPillRenderer | javax.swing.table.TableCellRenderer |

### Associations (domain)
| From | To | Multiplicity | Kind / notes |
|---|---|---|---|
| K2636474Booking | K2636474Guest | many → 1 (`guest`) | association; a guest has 0..* bookings |
| K2636474Booking | K2636474Room | many → 1 (`room`) | association; a room has 0..* bookings |
| K2636474Booking | K2636474Invoice | 1 → 0..1 (`invoice`) | composition: the invoice only exists for its booking; a new invoice replaces the old one on modification |
| K2636474Invoice | K2636474Booking | 1 → 1 (`booking`) | back-reference (bidirectional 1–1) |
| K2636474Room | K2636474RoomPackage | many → 1 (`roomPackage`) | shared aggregation: one package instance shared by all rooms of a type (Flyweight) |
| K2636474Guest | K2636474IdType | 1 → 1 | attribute of enum type |
| K2636474Room | K2636474BedType, K2636474RoomStatus | 1 → 1 each | attributes of enum type |
| K2636474Booking | K2636474BookingStatus | 1 → 1 | attribute of enum type |
| K2636474RoomPackage | K2636474RoomType | 1 → 1 (returned by `getRoomType()`) | each subclass maps to one type |

### Associations (services and storage)
| From | To | Multiplicity | Kind / notes |
|---|---|---|---|
| K2636474DataStore | K2636474Room | 1 → 0..* (`rooms`: LinkedHashMap) | composition (store owns the collection) |
| K2636474DataStore | K2636474Booking | 1 → 0..* (`bookings`: HashMap; `bookingsByCheckIn`: TreeMap) | composition |
| K2636474DataStore | K2636474User | 1 → 0..* (`usersByLogin`: HashMap) | composition |
| K2636474DataStore | K2636474IdGenerator | 1 → 1 (`idGenerator`) | composition |
| K2636474DataStore | K2636474DataStore | 1 → 1 (`INSTANCE`) | Singleton self-association |
| K2636474RoomPackageFactory | K2636474RoomPackage | 1 → 0..4 (`PACKAGES`: EnumMap) | composition / cache (Factory) |
| K2636474AuthService | K2636474DataStore | 1 → 1 | association (constructor-injected) |
| K2636474AuthService | K2636474User | 1 → 0..1 (`currentUser`) | association |
| K2636474RoomService | K2636474DataStore | 1 → 1 | association (constructor-injected) |
| K2636474BookingService | K2636474DataStore, K2636474RoomService, K2636474InvoiceService | 1 → 1 each | association (constructor-injected) |
| K2636474InvoiceService | K2636474DataStore, K2636474InvoiceCalculator | 1 → 1 each | association (constructor-injected) |
| K2636474InvoiceCalculator | K2636474DiscountPolicy | 1 → 1 (`discountPolicy`) | association (Strategy) |
| K2636474DataSeeder | K2636474AuthService, K2636474RoomService, K2636474BookingService | 1 → 1 each | association |

### Associations (UI)
| From | To | Multiplicity | Kind / notes |
|---|---|---|---|
| K2636474MainFrame | K2636474UiContext | 1 → 1 | composition |
| K2636474MainFrame | K2636474LoginPanel, K2636474RegisterPanel | 1 → 1 each | composition |
| K2636474MainFrame | K2636474DashboardShell | 1 → 0..1 (`dashboard`) | composition (rebuilt on each login) |
| K2636474UiContext | K2636474AuthService, K2636474RoomService, K2636474BookingService, K2636474InvoiceService | 1 → 1 each | association |
| K2636474UiContext | K2636474Toast | 1 → 0..1 | association |
| K2636474DashboardShell | K2636474Page | 1 → 1..* (`pages`) | composition |
| K2636474DashboardShell | K2636474NavItem | 1 → 1..* (`navItems`) | composition |
| Every page and dialog | K2636474UiContext | many → 1 (`context`) | association |
| K2636474BookingDialog | K2636474PriceBreakdownPanel, K2636474DateField | 1 → 1 / 1 → 2 | composition |
| K2636474BookingTableModel | K2636474Booking | 1 → 0..* (`rows`) | aggregation |
| K2636474RoomTableModel | K2636474Room | 1 → 0..* (`rows`) | aggregation |
| K2636474RoomCard | K2636474Room | 1 → 1 (`room`) | association |

### Dependencies (uses, no stored reference)
- K2636474User, K2636474Guest, K2636474Admin, K2636474Room, K2636474Booking → K2636474Validator (setter validation); all throw K2636474ValidationException.
- K2636474RoomPackage → K2636474Booking (`MAX_EXTRA_GUESTS`), K2636474Format.
- K2636474RoomService → K2636474RoomPackageFactory (creating and editing rooms).
- K2636474InvoiceCalculator → K2636474Booking, K2636474RoomPackage; creates K2636474Invoice.
- K2636474LongStayDiscountPolicy → K2636474Booking.
- K2636474BookingService throws K2636474BookingException, K2636474NotFoundException, K2636474AuthenticationException.
- K2636474SmartStayApp → creates all services, K2636474DataSeeder, K2636474MainFrame, calls K2636474UiTheme.install().
- All UI classes → K2636474UiTheme (colours, fonts, factory helpers); K2636474UiTheme → K2636474CardPanel.

### Design patterns
| Pattern | Where |
|---|---|
| Singleton | K2636474DataStore (`getInstance()`, private constructor) |
| Factory (+ Flyweight cache) | K2636474RoomPackageFactory.getPackage(type), EnumMap of shared packages |
| Strategy | K2636474DiscountPolicy injected into K2636474InvoiceCalculator |
| Template method | K2636474RoomPackage.describe() / getMaxOccupancy() built on abstract methods; K2636474Page.refresh() called by K2636474DashboardShell.showPage() |
| Layered architecture | ui → service → model / pricing / util; the UI never touches K2636474DataStore |
