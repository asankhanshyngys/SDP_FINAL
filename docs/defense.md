# Defense and manual smoke check

## Demonstration (approximately five minutes)

1. Launch using `scripts/run.ps1`. Explain that this is Java Swing with in-memory data.
2. Select pizza, cheese, and sauce. Add it. Show 2,950 KZT before discount.
3. Select Student (10%). Total becomes 2,655 KZT. Switch to Promotion to show strategy replacement,
   then back to Student. Explain the whole-tenge rounding rule.
4. Submit. Show receipt #1 and Pending in the customer view. Explain Builder assembling item
   snapshots and the policy, Facade coordinating submission, and Observer updating both views.
5. In Kitchen, advance to Preparing. Return to Place Order and show the matching update.
6. Advance through Ready and Completed. Explain State as an additional pattern, not one of the six counted.
7. Create another order and show that its status is independent. Explain the immutable receipt.
8. Show class and sequence diagrams and point to each pattern's code.

## Manual checks on the target machine

- Build and launch on JDK 17+. Confirm the window opens without console errors.
- Check both tabs at the initial and minimum window size; resize larger and confirm readability.
- Add two food items with different extras; remove one; inspect totals.
- Switch to lemonade; food extras should reset and become unavailable.
- Switch discounts before submission; changing the cart afterward must not alter an earlier receipt.
- Create two orders; select each in Kitchen and verify the correct receipt advances.
- Complete one order; verify it cannot advance again.
- Close and relaunch; no previous orders should remain.

Automated tests render real Swing panels and exercise controls headlessly. They do not establish
native window-manager behavior, display scaling, or physical mouse/keyboard usability.

## Questions to prepare for

- Why Factory Method rather than a single switch-based constructor?
- How do stacked decorators preserve the Meal interface?
- Why should discount logic live outside the UI?
- How does the State pattern prevent invalid lifecycle transitions?
- How do both views learn that an order changed?
- Which responsibilities does the facade hide, and which remain in OrderService?
- How does Builder differ from Factory Method? Show staged assembly and the final build step.
- Which SOLID and clean-code principles are supported, and where are the limitations?
- Why does this project intentionally omit persistence and payments?

Before Moodle submission: add names and actual contributions to the report, confirm course
pattern coverage, run the manual checks, and submit the PDF plus source ZIP.
