"""Optional report generator: python -m pip install reportlab, then run this file."""
from pathlib import Path
from html import escape
import textwrap
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, PageBreak, Image, Table, TableStyle,
    Preformatted, KeepTogether,
)
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER
from reportlab.graphics.shapes import Drawing, Rect, String, Line, Polygon

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'output/pdf/restaurant-project-report.pdf'
OUT.parent.mkdir(parents=True, exist_ok=True)
styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name='BodyCustom', fontName='Helvetica', fontSize=10.5, leading=15, spaceAfter=10))
styles.add(ParagraphStyle(name='CodeCustom', fontName='Courier', fontSize=7, leading=9))
styles.add(ParagraphStyle(name='CaptionCustom', fontName='Helvetica-Oblique', fontSize=9, leading=12, spaceAfter=12))
styles['Title'].textColor = colors.HexColor('#194d45')
styles['Heading1'].textColor = colors.HexColor('#194d45')
story = []

def p(text):
    story.append(Paragraph(text, styles['BodyCustom']))

def title(text):
    story.append(Paragraph(text, styles['Heading1']))

def page(text):
    if story:
        story.append(PageBreak())
    title(text)

def table(rows, widths):
    data = [[Paragraph(escape(str(v)), styles['BodyCustom']) for v in row] for row in rows]
    t = Table(data, colWidths=widths, repeatRows=1, hAlign='LEFT')
    t.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#e3efeb')),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), .4, colors.HexColor('#b8c8c4')),
        ('LEFTPADDING', (0,0), (-1,-1), 8), ('RIGHTPADDING', (0,0), (-1,-1), 8),
        ('TOPPADDING', (0,0), (-1,-1), 6), ('BOTTOMPADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t)

def box(d, x, y, w, name, detail='', kind=''):
    h = 58
    d.add(Rect(x, y, w, h, fillColor=colors.HexColor('#f1f6f4'), strokeColor=colors.HexColor('#40665e')))
    d.add(String(x+w/2, y+43, kind, fontSize=8, textAnchor='middle'))
    d.add(String(x+w/2, y+28, name, fontName='Helvetica-Bold', fontSize=10, textAnchor='middle'))
    d.add(Line(x, y+21, x+w, y+21, strokeColor=colors.HexColor('#40665e')))
    d.add(String(x+w/2, y+8, detail, fontSize=8, textAnchor='middle'))

def arrow(d, x1, y1, x2, y2, label='', inherits=False, dashed=False):
    import math
    d.add(Line(x1, y1, x2, y2, strokeColor=colors.HexColor('#344d47'), strokeDashArray=[4,3] if dashed else None))
    a = math.atan2(y2-y1, x2-x1)
    points = [x2,y2, x2-9*math.cos(a)+4*math.sin(a),y2-9*math.sin(a)-4*math.cos(a),
              x2-9*math.cos(a)-4*math.sin(a),y2-9*math.sin(a)+4*math.cos(a)]
    d.add(Polygon(points, fillColor=colors.white if inherits else colors.HexColor('#344d47'), strokeColor=colors.HexColor('#344d47')))
    if label:
        d.add(String((x1+x2)/2+5, (y1+y2)/2+7, label, fontSize=8))

def caption(text):
    story.append(Paragraph(text, styles['CaptionCustom']))

story.append(Spacer(1,70))
story.append(Paragraph('TABLE', styles['Title']))
story.append(Paragraph('Restaurant Ordering System', styles['Title']))
story.append(Paragraph('Final project report | Java desktop application', styles['Heading2']))
p('Prepared: 5 November 2026')
p('Team names: ______________________________<br/>Course / section: __________________________<br/>Instructor: ______________________________')
p('Implementation and verification package. Add team details and actual individual contributions before submission. The suggested three-member split is a plan, not a claim of authorship.')
table([['Technology','Scope'],['Java 17+ / Swing','Two tabs, in-memory orders, six course patterns plus State'],['Storage','No database; orders disappear when the app closes'],['External services','None; no real payments or network calls']], [145,350])

page('1. Introduction and requirements')
p('The application models a small restaurant workflow: a customer configures meals, reviews a cart, selects a discount, and submits an order. A kitchen view advances the order through preparation. Both views display the same order status through event notifications.')
p('The objective is to demonstrate six coordinated course patterns in a cohesive program while keeping the product small enough to explain and defend. The counted patterns are Factory Method, Decorator, Builder, Strategy, Observer, and Facade. State remains additional and is not counted toward the required six. The project uses a local Swing window rather than a website.')
table([['Requirement','Implementation / evidence'],
       ['At least six distinct working patterns','Factory Method, Decorator, Builder, Strategy, Observer, Facade; see sections 3-5 and source appendix. State is additional.'],
       ['At least two patterns per student','Proposed split in section 6; every student must independently implement their assigned patterns and record actual work.'],
       ['One integrated application','Meal customization -> checkout -> order queue -> kitchen states -> customer notifications.'],
       ['Report with code, screenshots and UML','This report includes introductory/body/conclusion/further-work sections, screenshots, diagrams, and full production Java source.'],
       ['PDF and zipped source submission','output/pdf/restaurant-project-report.pdf and dist/restaurant-source.zip.'],
       ['Oral defense','Walkthrough and questions in docs/defense.md.'],
       ['SOLID in final report','Sections 9.1 and 9.2 provide code evidence and limitations.']], [160,335])
p('Sources: Final Project Defense Requirements.pdf and Syllabus_Software Design Patterns.pdf, supplied by the team. The syllabus specifies a team of 2-3, PDF and ZIP submission by the end of Week 9, and defense in Week 10. Check Moodle for the exact deadline.')
p('The syllabus permits AI for reference examples and clarification, not as a replacement for independent work. This generated implementation/report is reference material; it does not establish independent student implementation or authorship.')

page('2. Features, boundaries, and rules')
p('The Place Order tab offers pizza (2,500 KZT), burger (1,800 KZT), and lemonade (600 KZT). Food can be wrapped with cheese (+300 KZT), sauce (+150 KZT), or both. Lemonade disables food extras. Each Add action contributes one serving; repeated items represent multiple servings.')
p('The cart supports item removal and one discount policy: none, student (10%), or promotion (15%). Discounts do not stack. Values use integer tenge; discount amounts round down to a whole tenge. Student eligibility is not verified in this classroom demonstration.')
p('Submitting validates that the order is nonempty, preserves item descriptions/prices and calculated totals, allocates an ID, and notifies both views. It records an order; it does not charge money. The customer can select previous receipts. The kitchen can select any order and advance it once per click.')
p('Lifecycle: Pending -> Preparing -> Ready -> Completed. The terminal action is disabled in the UI and rejected in the model. No backward transition, arbitrary status setter, or skipped step is exposed.')
p('Orders remain in memory during one session. Restarting the application resets orders and IDs. There are no accounts, database, delivery tracking, reservations, external APIs, or payment services. All model operations are performed synchronously on the Swing event dispatch thread; the service is not designed as a concurrent backend.')

page('3. Design patterns: creation and customization')
p('<b>Factory Method.</b> MealCreator defines a common prepare() operation that invokes createMeal(). PizzaCreator, BurgerCreator, and DrinkCreator override this method and supply distinct Meal products. This is polymorphic construction, rather than a switch-based simple factory.')
p('<b>Decorator.</b> Extra implements Meal and holds a wrapped Meal. Cheese and Sauce delegate to the wrapped object and extend description() and price(). A decorator can wrap another decorator, allowing combinations without new product subclasses for each combination.')
d = Drawing(495,350)
box(d,155,275,185,'MealCreator','prepare(); createMeal()', 'abstract')
for x,n in [(0,'PizzaCreator'),(170,'BurgerCreator'),(340,'DrinkCreator')]:
    box(d,x,165,155,n,'createMeal()')
    arrow(d,x+77,223,247,275,inherits=True)
box(d,15,25,150,'Meal','description(); price()', 'interface')
box(d,245,25,150,'Extra','wrapped: Meal', 'abstract')
arrow(d,245,54,165,54,inherits=True,dashed=True)
arrow(d,320,25,90,25,'wraps')
story.append(d)
caption('Figure 1. Factory Method and Decorator class relationships. Open triangles indicate generalization or interface realization (dashed). Cheese and Sauce extend Extra; Pizza, Burger, and Drink implement Meal. The complete editable diagram includes these products.')

page('3.1 Builder: step-by-step order assembly')
p('<b>Builder is the sixth counted course pattern.</b> OrderBuilder captures meals one at a time through addMeal(), selects an optional policy through withDiscount(), and produces a complete Order through build(id). Both configuration methods return the same builder for a fluent API. The default policy is no discount.')
p('The facade creates and configures the builder. The service supplies the next session ID and calls build() before storing or notifying. The builder rejects empty orders and invalid IDs, validates discount bounds, and calculates totals. A failed build does not consume an ID or publish an event.')
p('Each added meal is captured as an immutable item snapshot. Order defensively copies the list on construction, so further builder changes cannot alter an already built receipt. This separates construction from the Order representation and replaces a positional construction call in the service.')
story.append(Preformatted('builder = new OrderBuilder()\n    .addMeal(configuredPizza)\n    .addMeal(lemonade)\n    .withDiscount(new DiscountStrategy.Student());\norder = builder.build(id);', styles['CodeCustom']))
story.append(Spacer(1,18))
d=Drawing(495,240)
box(d,0,155,170,'OrderFacade','assembles builder')
box(d,295,155,195,'OrderService','builds before storing')
box(d,145,35,205,'OrderBuilder','addMeal(); build(id)')
arrow(d,85,155,190,93,'configure')
arrow(d,390,155,310,93,'build(id)')
arrow(d,170,184,295,184,'submit(builder)')
story.append(d)
caption('Figure 1b. Builder integration. OrderBuilder uses Meal and DiscountStrategy abstractions, builds Order, and leaves persistence/notification responsibilities to OrderService. A separate Director class is unnecessary: the facade coordinates construction.')

page('4. Design patterns: checkout and lifecycle')
p('<b>Strategy.</b> DiscountStrategy defines discount(subtotal). None, Student, and Promotion implement separate policies. OrderFacade accepts the chosen policy and uses it without knowing its concrete type. The same cart can be quoted with different policies.')
p('<b>State (additional; not counted toward six).</b> Order owns an OrderState and delegates advancement to next(). Each concrete state returns its sole valid successor. Completed throws an exception and reports canAdvance() as false. The lifecycle rules are therefore in state classes rather than scattered across UI conditions.')
d = Drawing(495,355)
box(d,150,280,195,'DiscountStrategy','discount(subtotal)', 'interface')
for x,n in [(0,'None'),(170,'Student'),(340,'Promotion')]:
    box(d,x,175,155,n,'discount(subtotal)')
    arrow(d,x+77,233,247,280,inherits=True,dashed=True)
box(d,0,65,150,'Order','advance(); status()')
box(d,300,65,185,'OrderState','next(); canAdvance()', 'interface')
arrow(d,150,94,300,94,'current state')
for x,n in [(0,'Pending'),(125,'Preparing'),(250,'Ready'),(375,'Completed')]:
    d.add(String(x+58,20,n,fontSize=9,textAnchor='middle'))
    if x < 375: arrow(d,x+93,23,x+137,23)
story.append(d)
caption('Figure 2. Strategy hierarchy and State context relationship. The four named states implement OrderState. The lifecycle permits only forward advancement to the next state.')

page('5. Design patterns: orchestration and notifications')
p('<b>Facade.</b> OrderFacade provides quote() and placeOrder() as simple client operations. It assembles OrderBuilder, then asks OrderService to build, store, and publish the order. Builder owns snapshots, validation, and pricing through Strategy. The facade hides this coordination from the UI without adding out-of-scope payment behavior.')
p('<b>Observer.</b> OrderService stores OrderObserver subscribers and calls orderChanged(order) after successful submissions and transitions. RestaurantPanel registers separate customer and kitchen method-reference handlers. Each refreshes its own list and selected receipt. Both observe the same publisher even when only one tab is visible.')
d=Drawing(495,340)
box(d,145,275,200,'RestaurantPanel','customer + kitchen handlers')
box(d,0,155,190,'OrderFacade','placeOrder(); quote()')
box(d,300,155,190,'OrderService','subscribe(); advance()')
box(d,145,20,200,'OrderObserver','orderChanged(order)', 'interface')
arrow(d,180,275,95,213,'checkout')
arrow(d,190,184,300,184,'submit')
arrow(d,395,155,345,49,'notifies')
arrow(d,245,275,245,78,dashed=True)
d.add(String(253,115,'two subscribers',fontSize=8))
story.append(d)
caption('Figure 3. Facade and Observer collaboration. UI handlers conform to OrderObserver using Java method references; there are two independent subscriptions. OrderService also stores the session orders.')

page('6. Integrated workflow and contribution plan')
d=Drawing(495,400)
xs=[25,110,205,295,385,465]
names=['UI','Facade','Builder','Strategy','Service','Views']
for x,n in zip(xs,names):
    d.add(String(x,380,n,fontName='Helvetica-Bold',fontSize=10,textAnchor='middle'))
    d.add(Line(x,370,x,15,strokeDashArray=[3,3],strokeColor=colors.grey))
for x1,x2,y,label in [(25,110,345,'placeOrder'),(110,205,310,'configure'),(110,385,275,'submit(builder)'),(385,205,240,'build(id)'),(205,295,205,'discount'),(295,205,170,'amount'),(205,385,135,'order'),(385,465,100,'notify'),(110,25,65,'receipt')]:
    arrow(d,x1,y,x2,y,label)
story.append(d)
caption('Figure 4. Submission sequence (simplified). Configure means add meals and select a discount policy. Views represents both subscribers. Service returns the built order through Facade to UI; the full editable sequence includes return messages and the additional State interaction.')
p('On a kitchen action, UI calls OrderService.advance(id), the service finds Order, Order delegates to its current State, and the service then notifies both views. Invalid IDs and terminal transitions produce no event.')
table([['Proposed member','Counted patterns','Feature area'],['1 - name pending','Factory Method + Decorator','Menu and meal extras'],['2 - name pending','Builder + Strategy','Assembly and pricing'],['3 - name pending','Facade + Observer','Submission and updates']], [120,200,175])
p('This allocation assumes three students. Replace placeholders with real names and document actual implementation work. Every student must individually implement at least two patterns. A different team size needs a reviewed allocation.')

for heading,path,desc in [
    ('7. Application view: Place Order','place-order.png','Figure 5. Real Swing panel rendered offscreen by the integration check. A pizza with both extras has been submitted under the student discount: 2,950 - 295 = 2,655 KZT. The cart is now empty and the Pending receipt is preserved.'),
    ('8. Application view: Kitchen','kitchen.png','Figure 6. The same order after advancing to Preparing. The automated check confirms the customer receipt updates as well. These are offscreen application-panel renders, not native desktop-window captures.'),
]:
    page(heading)
    story.append(Image(str(ROOT/'docs/screenshots'/path), width=495, height=495*700/1080))
    caption(desc)
    p('Use the two tabs in one local window. No browser, server, database, network connection, or special credentials are required.')

page('9. Verification and running the project')
p('Prerequisite: JDK 17 or newer, with java, javac, and jar on PATH. Build and checks were executed with JDK 25.0.1 while producing Java 17 bytecode. No external Java libraries or build-system downloads are required.')
p('<b>Run from PowerShell:</b><br/><font face="Courier" size="9">powershell -ExecutionPolicy Bypass -File .\\scripts\\run.ps1</font>')
p('<b>Verify:</b><br/><font face="Courier" size="9">powershell -ExecutionPolicy Bypass -File .\\scripts\\test.ps1</font>')
p('The verification suite passed 35 behavioral checks. It exercises menu products, stacked extras, all discount policies and rounding, empty-order rejection, immutable snapshots and collections, independent order states, terminal/unknown-order rejection, notification delivery and unsubscribe, invalid discounts, builder defaults/staged construction/reuse/validation, and actual Swing control interactions. It renders both application panels for visual inspection.')
p('The Swing checks submit a configured pizza, verify totals and cart clearing, inspect both view receipts, advance through all states, verify the disabled terminal button, reset extras for drinks, remove an item, and submit a second independent order. Test failures cause a nonzero exit code.')
p('A separate DesktopSmokeTest passed on Java 17: Main created a visible native Swing window at 1080 x 760, laid out its content at the minimum window size, and closed successfully. Both the headless behavioral suite and native startup check ran without external Java libraries.')
p('<b>Verification limitation:</b> automated control interactions and native startup do not establish physical mouse/keyboard usability or behavior at every display scaling setting. Before defense, launch the application on the presentation machine and complete the manual smoke checklist in docs/defense.md. The full manual checklist has not been performed.')
p('Editable UML sources: docs/uml/classes.puml, sequence.puml, and states.puml. The source archive contains Java production code, tests, scripts, documentation, screenshots, and this report. The executable JAR is supplied separately.')

page('9.1 SOLID principles: evidence and limits')
p('<b>Single Responsibility.</b> MealCreator creates meals; Extra customizes them; OrderBuilder assembles and validates orders; UiTheme styles controls. Business rules are outside Swing handlers. This is practical separation: RestaurantPanel still combines layout and interaction, and OrderService combines storage with publication.')
p('<b>Open/Closed.</b> New DiscountStrategy or Extra implementations can extend pricing or customization without changing the builder pricing algorithm. A new concrete creator can supply another meal. The UI must still register a new choice; the project does not claim that adding features requires no edits anywhere.')
p('<b>Liskov Substitution.</b> Plain meals and stacked decorators implement the same Meal contract and can be passed to addMeal(). All supplied discount strategies accept nonnegative subtotals and return an amount between zero and subtotal. Behavioral checks exercise both decorated meals and interchangeable policies; custom implementations must preserve those contracts.')
p('<b>Interface Segregation.</b> Meal exposes only description and price; DiscountStrategy exposes a name and a calculation; OrderObserver exposes one callback. Pricing classes are not forced to implement UI or notification methods.')
p('<b>Dependency Inversion.</b> Builder depends on Meal and DiscountStrategy abstractions; the publisher depends on OrderObserver; Order uses OrderState. The service is constructor-injected into the facade and panel. DIP is applied partially: the facade intentionally depends on concrete OrderService and constructs OrderBuilder. No unnecessary repository interface or injection framework is introduced.')
p('These are code-backed design choices rather than a claim of perfect compliance. If the UI grows, separate customer/kitchen view classes; if storage requirements grow, reconsider a repository abstraction. Neither expansion is needed for the agreed scope.')

page('9.2 Clean-code practices')
p('<b>Meaningful names and focused operations.</b> placeOrder(), addMeal(), withDiscount(), canAdvance(), and orderChanged() state their purpose. The facade coordinates, the builder constructs, and policies calculate discounts. The largest class, RestaurantPanel, remains a possible future refactoring target.')
p('<b>DRY.</b> Quote preview and order construction use the same OrderBuilder.quote() calculation. UI handlers do not repeat discount formulas. UiTheme centralizes visual choices and money() centralizes UI monetary formatting.')
p('<b>KISS and YAGNI.</b> The implementation uses Swing, in-memory orders, integer tenge, and JDK-only checks. It avoids features outside the agreed scope: database, web server, accounts, payments, and external services.')
p('<b>Encapsulation and defensive copying.</b> Order fields are private; submitted data fields are final; List.copyOf() protects item snapshots and returned order lists. Builder reuse cannot alter an existing receipt. Lifecycle state is intentionally mutable through controlled transitions.')
p('<b>Boundary validation and clear failures.</b> Null inputs, invalid items, empty builds, invalid IDs, excessive discounts, and terminal advancement are rejected explicitly. Math.addExact() prevents silent subtotal overflow. Failed submission does not create or publish an order.')
p('<b>Composition and separation of concerns.</b> Extras wrap meals rather than requiring a class for every combination. Strategies supply pricing behavior. The UI, theme, construction, pricing, and notification roles remain distinguishable and the business logic can be checked without a visible window.')
p('<b>Behavioral verification and useful documentation.</b> Checks cover outcomes and boundaries, including pricing, snapshots, builder reuse, observers, lifecycle, and UI controls. Comments explain pattern intent; Markdown and UML explain usage and structure. This is not a claim of comprehensive JavaDoc or complete Clean Code compliance.')
p('Known limits: the service is single-threaded and in memory; synchronous observer exceptions can interrupt subsequent delivery; source formatting is compact in places. More examples and limitations are documented in docs/solid-and-clean-code.md.')

page('10. Conclusion and further work')
p('The implemented system covers the agreed restaurant-ordering scope with a local desktop UI and no persistence layer. Six course patterns work as one workflow: factories create meals, decorators customize them, the facade assembles a builder, strategies calculate discounts, and observers refresh both views. State controls kitchen progression as an additional pattern and is not counted toward the six.')
p('The main learning outcome is separation of responsibilities. Menu creation does not know pricing policies; the UI does not own transition rules; submitted receipts do not depend on a mutable cart; and notification subscribers do not control order storage.')
p('Possible further work, outside the current project scope, includes an explicit quantity editor, cancellation with defined rules, additional meal types, and receipt export. A database or real payment integration is intentionally unnecessary for this defense.')
p('<b>Before submission:</b> complete independent student implementation, fill team details, document actual contributions, complete the manual desktop check, and submit the PDF and source ZIP to Moodle by the stated deadline. This AI-generated reference is not evidence of independent work; follow the syllabus AI and authorship policies.')
p('<b>Defense example:</b> pizza + cheese + sauce -> student discount -> submit -> Pending -> Preparing -> Ready -> Completed. Show matching customer/kitchen status, then demonstrate a second independent order. Each student should explain their two patterns and point to the relevant classes.')

page('Appendix A. Complete production Java code')
p('The following listings reproduce the production Java sources shipped with the project. Tests and PowerShell build scripts are included in the source archive. Long source lines are wrapped for printing only; use the .java files to compile.')
for index, path in enumerate(sorted((ROOT/'src/main/java/restaurant').glob('*.java'))):
    if index:
        story.append(PageBreak())
    story.append(Paragraph(path.name, styles['Heading2']))
    lines=[]
    for number,line in enumerate(path.read_text(encoding='utf-8').splitlines(),1):
        wrapped=textwrap.wrap(line, width=104, expand_tabs=True, replace_whitespace=False,
                              drop_whitespace=False, break_long_words=True, break_on_hyphens=False) or ['']
        lines.append(f'{number:3}  '+wrapped[0])
        lines.extend('     '+part for part in wrapped[1:])
    story.append(Preformatted('\n'.join(lines), styles['CodeCustom']))

def footer(canvas, doc):
    canvas.saveState()
    canvas.setStrokeColor(colors.HexColor('#b8c8c4'))
    canvas.line(50,42,545,42)
    canvas.setFont('Helvetica',8)
    canvas.drawString(50,29,'TABLE | Restaurant Ordering System | Final project report')
    canvas.drawRightString(545,29,str(doc.page))
    canvas.restoreState()

doc=SimpleDocTemplate(str(OUT), pagesize=(595.28,841.89), rightMargin=50,leftMargin=50,
                      topMargin=45,bottomMargin=55, title='TABLE - Restaurant Ordering System',
                      author='Project team (names to be completed)')
doc.build(story,onFirstPage=footer,onLaterPages=footer)
print(OUT)
