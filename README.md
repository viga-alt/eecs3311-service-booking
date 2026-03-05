# Service Booking & Consulting Platform — Phase 1

## Project Structure

**Backend** (`com/booking/`)

- `domain/` — Core entities: User, Client, Consultant, Admin, Booking, Service, TimeSlot, Payment, PaymentMethod
- `domain/state/` — State pattern: 7 concrete booking states (Requested, Confirmed, PendingPayment, Paid, Rejected, Cancelled, Completed)
- `observer/` — Observer pattern: ClientNotificationObserver, ConsultantNotificationObserver, AdminNotificationObserver, NotificationService, BookingObserver
- `strategy/payment/` — Payment strategies: CreditCard, DebitCard, PayPal, BankTransfer
- `strategy/cancellation/` — Cancellation policies: Flexible, Strict, NoCancellation
- `singleton/` — DatabaseManager (in-memory data store)
- `policy/` — SystemPolicy (admin-configurable platform settings)
- `service/` — Business logic: BookingService, ConsultantService, PaymentService, AdminService, PaymentProcessor

**Frontend** (`com/app/ui/`)

- `ConsoleApp.java` — Terminal UI covering all 12 use cases, sample testing data (not persistent)

## Design Patterns

**Singleton — DatabaseManager**

All service classes share one in-memory data store. `DatabaseManager.getInstance()` guarantees a single instance across the application.

**State — Booking**

A booking moves through 7 states and the valid operations differ in each one. `Booking` is the context, `BookingState` is the interface, and each concrete state class only implements the transitions legal from that state. Illegal transitions print an error and do nothing.

```
REQUESTED -> CONFIRMED -> PENDING_PAYMENT -> PAID -> COMPLETED
REQUESTED -> REJECTED
Any active state -> CANCELLED (subject to the active cancellation policy)
```

**Observer — Booking notifications**

`Booking` keeps a list of observers and calls `notifyObservers(event)` on every state transition. Each observer (Client, Consultant, Admin) decides which events are relevant to its role and delivers messages through `NotificationService`.

**Strategy — Payment processing**

Each payment method has different validation rules. `PaymentProcessor` auto-selects the right `PaymentStrategy` based on the method type and delegates validation and simulated processing to it.

**Strategy — Cancellation policies**

The admin can switch between three policies at runtime: `FlexibleCancellationPolicy` (100% refund), `StrictCancellationPolicy` (25% refund), and `NoCancellationPolicy` (no cancellation after confirmation). `BookingService` holds the active policy and enforces it on every cancellation request.

## Use Cases

| UC | Description | Key Classes |
|----|-------------|-------------|
| UC1 | Browse Consulting Services | ConsultantService, Service |
| UC2 | Request a Booking | BookingService, Booking, TimeSlot |
| UC3 | Cancel a Booking | BookingService, CancellationPolicy |
| UC4 | View Booking History | BookingService |
| UC5 | Process Payment | PaymentService, PaymentProcessor, PaymentStrategy |
| UC6 | Manage Payment Methods | Client, PaymentService |
| UC7 | View Payment History | PaymentService |
| UC8 | Manage Availability | ConsultantService, TimeSlot |
| UC9 | Accept or Reject Booking | BookingService |
| UC10 | Complete a Booking | BookingService |
| UC11 | Approve Consultant Registration | AdminService, ConsultantService |
| UC12 | Define System Policies | AdminService, SystemPolicy |

## Payment Validation

All payment processing is simulated with a 2-3 second delay and a generated transaction ID.

| Method | Validation |
|--------|------------|
| Credit Card / Debit Card | 16-digit number, future MM/YY expiry, 3-4 digit CVV |
| PayPal | Valid email address |
| Bank Transfer | 8-17 digit account number, 9-digit routing number |

## How to Run

Requires Java 11 or higher (`java --version` to check).

```bash
# Compile
git clone https://github.com/viga-alt/eecs3311-service-booking.git
cd eecs3311-service-booking
find backend/src frontend/src -name "*.java" > sources.txt
mkdir -p out
javac -d out @sources.txt

# Run
java -cp out com.app.ui.ConsoleApp
```

Sample data loads automatically on startup. To do a full end-to-end test:

1. Login as **Bob** (`bob@email.com` / `pass123`) and request a booking with Dave
2. Login as **Dave** (`dave@consult.com` / `pass789`) and accept the request
3. Back as Bob, process payment using the pre-saved credit card
4. Back as Dave, mark the session as completed
5. Login as **Admin** (`admin@platform.com` / `admin123`) to change policies or approve new consultants

## Team Contributions

| Member | Area |
|--------|------|
| TBD | Booking logic — BookingService, State pattern |
| TBD | Payment system — PaymentService, Strategy pattern |
| TBD | User management — AdminService, ConsultantService, Observer pattern |
| TBD | Frontend UI — ConsoleApp, DatabaseManager Singleton |

## GitHub Repository

https://github.com/viga-alt/eecs3311-service-booking.git