# Ticket to taste - Restaurant Ordering System

A small Java Swing desktop project demonstrating six course patterns plus State as an additional pattern.
No website, database, network connection, accounts, or payment integration is required.
Orders exist only during the current application session. All prices are in KZT.

## Run

Install a **JDK 17 or newer** and make `java`, `javac`, and `jar` available on PATH.
From the project folder in PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\run.ps1
```

Or build once and run the resulting executable JAR:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\build.ps1
java -jar .\dist\restaurant-ordering.jar
```

No Maven, Gradle, or third-party Java libraries are needed. The JAR requires Java;
it does not bundle a runtime. Java 17 bytecode is produced even with newer JDKs.

## Use

1. In **Place Order**, select pizza, burger, or lemonade.
2. Add optional cheese/sauce to food and click **Add item to order**. Repeat for more items.
3. Select a discount. Remove a selected cart item if needed.
4. Click **Submit order**. The cart clears; the submitted receipt is preserved.
5. Open **Kitchen**, select an order, and click **Advance status**.
6. Move through Pending, Preparing, Ready, and Completed. Return to Place Order
   to see the same status on the customer's receipt.

Submitting records an order only; there is no payment step. Each click adds one item;
repeated items represent multiple servings. Completed orders remain visible for the session.
Student and promotion discounts are demonstration choices without eligibility verification.
Percentage discounts round down to whole tenge; the total equals subtotal minus discount.

## Design patterns

| Pattern | Implementation | Role |
|---|---|---|
| Factory Method | `MealCreator` and three creator subclasses | Shared `prepare()` invokes an overridden product creation method |
| Decorator | `Extra`, `Cheese`, `Sauce` | Wraps `Meal`, extending description and price |
| Strategy | `DiscountStrategy` and three policies | Interchangeable pricing behavior |
| Builder | `OrderBuilder` | Assembles item snapshots and a discount policy before building an order |
| Observer | `OrderObserver`, `OrderService`, two UI subscribers | Publishes submissions and status updates to both views |
| Facade | `OrderFacade` | Coordinates validation, immutable item snapshots, pricing, and submission |
| State (additional; not counted) | `OrderState` and four states | Each state defines the next legal state |

Application flow: creator -> decorated meal -> facade -> builder + discount strategy ->
order service -> observer views. Kitchen action -> order -> current state -> observer views.
The UI and service operate on Swing's event dispatch thread. This is a single-user demo,
not a thread-safe backend. See `docs/architecture.md` and the PlantUML sources in `docs/uml/`.
See `docs/solid-and-clean-code.md` for concrete SOLID and clean-code examples and limitations.

## Verification

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\test.ps1
```

Dependency-free behavioral checks cover calculations, snapshots, invalid operations,
observer delivery, independent states, and the actual Swing controls. Tests also render
the two panels to `docs/screenshots/`. These are offscreen renders of real Swing components,
not screen captures of an operating-system window. A native startup check is also available
after running the tests: `java -cp 'build/classes;build/test-classes' restaurant.DesktopSmokeTest`.
It briefly opens and closes the application's window. Results and remaining manual checks
are recorded in `docs/verification.md` and `docs/defense.md`.

## Submission

- `output/pdf/restaurant-project-report.pdf`: generated report including diagrams, screenshots, and Java source appendix.
- `dist/restaurant-source.zip`: source, scripts, documentation, and report.
- `dist/restaurant-ordering.jar`: executable application.

The report includes a **proposed** three-person responsibility split. Add real team names
and record actual contributions before submission. The six counted patterns are covered in
the supplied syllabus; State is additional. The syllabus permits AI only for reference and
clarification, so this generated implementation must not replace independent student work.
No individual authorship claims are made by this repository.
