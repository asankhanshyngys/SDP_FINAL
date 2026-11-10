# Architecture and responsibility plan

## Scope

One Java Swing window with Place Order and Kitchen tabs. Three hard-coded menu items,
two stackable extras, three discount strategies, a cart, order receipts, and four lifecycle
states. Data is intentionally in memory. No database, payment, authentication, or external API.

## Pattern decisions

The six counted course patterns are Factory Method, Decorator, Builder, Strategy, Observer,
and Facade. State remains an additional implementation detail and is not counted toward six.

**Builder:** `OrderBuilder.addMeal()` snapshots items one at a time; `withDiscount()` selects
a policy and returns the builder for fluent assembly. `build(id)` validates a complete order,
calculates its totals, and creates an independent Order. The default is no discount. The
facade assembles the builder; the service assigns an ID and invokes build before storing or
publishing. The same builder can create independent snapshots as its configuration changes.

**Factory Method:** `MealCreator.prepare()` is a common operation; concrete creators override
`createMeal()` to supply distinct products implementing `Meal`. Choosing a creator in the UI
does not replace this polymorphic construction with a conditional factory.

**Decorator:** `Extra` implements `Meal` and contains another `Meal`. Cheese and Sauce delegate
and extend both methods. Ordering extras changes neither the underlying base meal nor its creator.

**Strategy:** the facade accepts a `DiscountStrategy`. None, Student (10%), and Promotion (15%)
can be exchanged without changing checkout. One policy applies per order; they do not stack.

**State (additional, not counted):** `Order` delegates advancement to its `OrderState`. Pending returns Preparing,
Preparing returns Ready, Ready returns Completed. Completed rejects advancement. There is no
public arbitrary status setter, so backward transitions and skipped states are unavailable.

**Observer:** `OrderService` owns subscriptions and publishes after successful changes. Customer
and kitchen handlers subscribe independently, rebuild their lists, and update selected receipts.
Events do not require networking or simultaneous windows. Observers run synchronously on the EDT.

**Facade:** `OrderFacade.placeOrder()` assembles an OrderBuilder and asks the service to build,
store, and publish an order. The builder owns snapshots and pricing/validation; the facade
coordinates the workflow. `quote()` uses the same builder pricing path without storing anything.
It does not simulate a payment service because payment is outside scope.

## Invariants

- Monetary values are integer KZT. Discount amounts round down.
- A submitted order retains immutable item/price snapshots, policy name, and calculated totals.
- Only status changes after submission. Returned collections cannot be edited.
- IDs are unique and increasing during one session, starting at 1 on a fresh launch.
- Invalid submissions/transitions do not create orders or publish events.
- Each order has its own state. All created orders remain available until exit.

## Proposed three-person split (assign names before submission)

| Member | Patterns | Feature responsibility |
|---|---|---|
| 1 | Factory Method + Decorator | Menu and extras |
| 2 | Builder + Strategy | Order assembly and pricing |
| 3 | Facade + Observer | Submission and view updates; State as an extra |

This is a planning allocation, not a statement of actual authorship. Every member should
independently implement, understand, and explain their assigned patterns. This generated
reference implementation does not establish student authorship. The supplied syllabus restricts
AI to reference and clarification. See `solid-and-clean-code.md` for the design review.
