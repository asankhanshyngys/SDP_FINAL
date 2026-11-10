# SOLID and clean-code review

This document describes concrete evidence in the current code, not a claim of perfect compliance.
Six counted course patterns: Factory Method, Decorator, Builder, Strategy, Observer, and Facade.
State is additional and does not count toward the required six.

## SOLID

| Principle | Code evidence | Practical effect / boundary |
|---|---|---|
| Single Responsibility | `MealCreator` creates products; `Extra` adds meal features; `OrderBuilder` assembles and validates an order; `Order` owns submitted data and lifecycle; `UiTheme` handles appearance. | Business rules stay outside Swing controls. `RestaurantPanel` still combines layout and event handlers; `OrderService` combines storage and event publication. This small project is not strictly one responsibility per class. |
| Open/Closed | `DiscountStrategy`, `MealCreator`, and `Extra` support additional implementations. | Another discount can be added without changing the builder's pricing algorithm. The UI still needs to register the new choice; extension does not mean zero edits anywhere. |
| Liskov Substitution | Base meals and decorated meals both implement `Meal`; all provided discount strategies accept nonnegative subtotals and return a discount between zero and subtotal. | The builder accepts either a plain meal or any stacked extra through the same contract. Tests exercise these substitutions. Custom implementations must preserve those contracts. |
| Interface Segregation | `Meal` has description/price only; `DiscountStrategy` has name/discount; `OrderObserver` has one callback. | Pricing policies do not implement UI or notification operations. Consumers depend on small relevant contracts. |
| Dependency Inversion | `OrderBuilder` depends on `Meal` and `DiscountStrategy`; the publisher depends on `OrderObserver`; `Order` delegates to `OrderState`. | Policies and subscribers can change independently of their consumers. This is partial application: the facade still depends on concrete `OrderService` and constructs `OrderBuilder`, intentionally avoiding unnecessary repository/factory interfaces. |

No dependency-injection framework is used or needed. Constructor injection is used for the
OrderService supplied to the facade and panel. Accepting an interface as an argument is useful
evidence of abstraction, but does not prove that the entire application perfectly follows DIP.

## Clean-code practices present

1. **Meaningful names:** `placeOrder`, `addMeal`, `withDiscount`, `canAdvance`, and `orderChanged`
   describe operations. Domain classes have restaurant/order responsibilities rather than generic
   manager/helper names. Short local variables in rendering loops are limited in scope.
2. **Focused operations:** the facade separates assembly from submission; the builder separates
   item capture, pricing, and final construction; individual state/strategy methods remain small.
3. **DRY:** both preview and submission use `OrderBuilder.quote()`; the UI does not implement a
   second discount algorithm. `UiTheme` centralizes common visual choices and `money()` formats UI prices.
4. **KISS / YAGNI:** Swing, in-memory storage, integer prices, and JDK-only checks meet the agreed
   scope without a web server, database, accounts, real payments, or dependency-injection framework.
5. **Encapsulation and defensive copying:** Order fields are private; submitted fields are final;
   `List.copyOf` prevents callers from editing submitted items or the service's order list. The builder
   reads each meal price once when added, so later changes do not alter that snapshot. Order status
   remains intentionally mutable through controlled transitions.
6. **Validation at boundaries:** null meals/policies, invalid item data, empty builds, invalid IDs,
   excessive discounts, and completed-order advancement are rejected explicitly. `Math.addExact`
   rejects subtotal overflow instead of silently wrapping a monetary value.
7. **Separation of concerns:** the UI invokes the facade/service, strategies price discounts,
   decorators customize meals, and the theme controls presentation. Business checks run without a window.
8. **Composition over combinatorial inheritance:** extras wrap meals and pricing uses a strategy,
   avoiding a separate product subclass for every extras/discount combination.
9. **Behavioral verification:** tests check totals, immutable receipts, observer updates, state
   boundaries, builder reuse, and the Swing workflow rather than merely checking class names.
10. **Purposeful documentation:** comments identify intent and pattern roles; Markdown explains
    scope, invariants, running instructions, and defense examples. JavaDoc coverage is basic.

## Limits worth acknowledging in the defense

- `RestaurantPanel` is the largest class and could be split into two views if the UI grows.
- Factory products, extras, and strategies are nested classes to keep this project compact; a larger
  project could separate them into files. Prices and discount percentages are fixed sample menu rules.
- Observer handlers run synchronously; an exception in one handler can interrupt later notifications.
  The supplied UI handlers are small, but this publisher is not a hardened messaging system.
- The service is single-threaded and in memory. It provides neither persistence nor concurrency guarantees.
- Source formatting is compact in places; clean-code principles are applied practically, not claimed
  as a complete Clean Code checklist or a guarantee of every SOLID principle everywhere.

## Ownership and course policy

This code/report is an AI-generated reference. The syllabus allows AI for examples and clarification,
not as a substitute for independent work. Each member must independently implement their assigned
patterns and describe their real contribution; a proposed responsibility table alone is insufficient.
