# Verification record - 5 November 2026

## Executed

- Built using JDK 25.0.1 with `javac --release 17`; packaged an executable JAR.
- `scripts/test.ps1`: PASS, 35 behavioral checks, including actual Swing controls.
- Ran the same `restaurant.SystemTest` on JDK 17.0.17: PASS, 35 checks.
- Ran `restaurant.DesktopSmokeTest` on JDK 17.0.17: PASS. `Main` opened a native
  Swing JFrame with RestaurantPanel at 1080 x 760; layout at minimum size succeeded;
  the test disposed the window and exited normally.
- Inspected both offscreen Swing panel renders. No visible clipping or overlapping controls.
- Rendered the PDF to page images and reviewed its layout, diagrams, screenshots, and source
  appendix. The updated report includes all 13 production Java source files, including OrderBuilder.

## UI polish

- Added a Swing-only sage/green palette, white sections, consistent Segoe UI typography,
  flat action buttons, padded order rows, and status text colors.
- Kept both existing tabs and all business behavior. No dependencies were added.
- Adjusted the customer history/receipt proportions so the sample total stays visible.
- Reviewed the normal and smaller-window panel renders after the update.

## Remaining team checks

- Complete the manual interaction and presentation-machine scaling checks in `defense.md`.
- Add real team names and actual contributions to the report generator, then regenerate the PDF.
- Review the revised proposed allocation: Factory Method + Decorator, Builder + Strategy,
  Facade + Observer. State is extra and does not count toward six.

The application intentionally has no database or real payment system. The report's responsibility
split is a proposal; it does not establish that each member individually implemented their patterns.

## Syllabus alignment update

- Added OrderBuilder to the real facade/service submission path. Five additional behavioral
  checks cover default pricing, staged assembly, receipt independence after reuse, empty build
  rejection, and invalid ID rejection.
- The six counted patterns now match dedicated syllabus topics. State remains additional.
- Added explicit SOLID and clean-code evidence/limitations to the PDF and Markdown documentation.
- The syllabus permits AI for reference and clarification only. This generated reference cannot
  establish compliance with the separate independent-student-work requirement.
