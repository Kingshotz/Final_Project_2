# TBC Digital – Advanced Test Automation

UI, API and data-driven tests for [tbcbank.ge](https://www.tbcbank.ge), built with **Java 21, Playwright, TestNG, Rest Assured, MyBatis (H2) and Allure**. Test cases are documented in Zephyr Scale (project `SCRUM`).

## How to run

Requirements: JDK 21 or newer and Maven 3.9+. Playwright downloads its browsers on the first run.

```bash
mvn clean test                                    # whole suite: headless, 3 classes in parallel
mvn test -Dheadless=false -DslowMo=500            # watch the browser
mvn test -Dbrowser=firefox -Dthreads=4            # other browser, more threads
mvn test -Dtest=BranchesScenario -Dsurefire.suiteXmlFiles=   # one class
mvn allure:serve                                  # open the Allure report
```

| Setting | Default with `mvn` | Default in the IDE | Values |
|---|---|---|---|
| `browser` | chromium | chromium | `chromium`, `firefox`, `webkit` |
| `headless` | true | false | |
| `slowMo` (ms) | 0 | 0 | delay between browser actions, only for watching a run |
| `threads` | 3 | 3 | TestNG thread count |

The Maven defaults are in the `<properties>` of `pom.xml` and are passed to the tests by Surefire; the IDE defaults are in `constants/Constants.java`.

`testng.xml` lists every test class in two blocks, **UI Tests** and **API Tests**, with `parallel="classes"`: each class runs on its own thread with its own browser (`BaseTest`), and the tests inside a class run in `priority` order. `group-by-instances="true"` keeps the steps of one database case together (see 9.3).

## Scenarios and Zephyr traceability

| Zephyr | Class | What it proves | Brief section |
|---|---|---|---|
| SCRUM-T11 | `tests.ui.CurrecyExchangeScenario` | The currency converter agrees with the rates table in both directions (foreign → GEL, swap, GEL → foreign) | 1 |
| SCRUM-T12 | `tests.ui.MortgageCalculatorHappyPathScenario` | The mortgage monthly payment matches the annuity formula for the default loan, a random GEL loan and after switching to USD and EUR | 1 (advanced) |
| SCRUM-T13 | `tests.ui.BranchesScenario` | Branch finder tab, search and 24/7 filter send the right request and the page shows exactly what the API returned | 1, 7 |
| SCRUM-T14 | `tests.ui.LocalizationScenario` | The mortgage journey works in Georgian and English, including switching language mid-journey | 3 |
| SCRUM-T15 | `tests.ui.MortgageCalculatorNegativeScenario` | Mortgage periods and amounts outside the limits are marked invalid and the last valid calculation is kept | 1 |
| SCRUM-T16 | `tests.ui.CurrencyConverterApiUiScenario` | The converter calculates a random cross-currency pair with the rate the exchange rates API returns, in both directions | 6 |
| SCRUM-T17 | `tests.api.BranchesApiHappyPathScenario` | ATMs & Branches API: branches by type, keyword and 24/7 filter, and regions return complete, valid data | 5 |
| SCRUM-T18 | `tests.api.BranchesApiNegativeScenario` | ATMs & Branches API: 400 validation errors, 404 for an unknown page, empty result for an unknown keyword | 5 |
| SCRUM-T19 | `tests.ui.ConsumerLoanDataDrivenScenario` | The consumer loan calculator accepts or rejects every loan case stored in the SQL database as expected and calculates the payment correctly | 4 |

Every test class carries its Zephyr key as `@TmsLink` (e.g. `@TmsLink("SCRUM-T16")`), so every test in the Allure report shows the Zephyr case it automates. The test methods follow the numbered steps of their Zephyr case in the same order. Allure labels: `@Epic` is the system, `@Feature` the area under test, `@Story` names what the single test validates and `@Description` says what it does and checks.

## 9.1 Framework architecture

```
src/main/java
├── components/   HeaderComponent, QuickActionsComponent, CalculatorSummaryComponent, CookieConsentComponent
├── pages/        CurrencyExchangePage, MortgagePage, ConsumerLoanPage, BranchesPage
├── steps/
│   ├── ui/       CurrencyExchangeSteps, MortgageSteps, ConsumerLoanSteps, BranchesSteps
│   └── api/      BaseApiSteps, BranchesApiSteps, ExchangeRatesApiSteps, ContentApiSteps
├── api/models/   Jackson POJOs: BranchLocation, WorkHour, Region, RegionBounds, GeoPoint, BranchesRequest,
│                 ExchangeRate, CommercialRates, CommercialRate, ProblemDetails
├── database/     DatabaseManager, mappers/ConsumerLoanCaseMapper, models/ConsumerLoanCase
├── models/       UI data: CurrencyRate, MortgageLoan, ConsumerLoan, ConsumerLoanValidation, LoanCurrency, LocaleData, CurrencyActions
├── constants/    Constants: URLs, paths, endpoints, test data, expected texts (EN/KA), limits, timeouts, regex patterns
└── utils/        RandomGenerator, TextParser, LoanCalculator, UrlBuilder
src/main/resources   mybatis-config.xml, db/schema.sql, db/data.sql
src/test/java/tests
├── ui/           BaseTest and the browser scenarios
└── api/          Rest Assured scenarios (no browser)
src/test/resources   allure.properties
```

| Layer | Responsibility |
|---|---|
| **pages/** | Locators only, public and built in the constructor. A page includes the components it uses. No clicks, no assertions. |
| **components/** | Locators and behaviour of UI parts that exist on several pages, e.g. `QuickActionsComponent.open()` clicks the toggle only if the panel is closed and waits for its `--open` class. |
| **steps/ui** | Business actions on one page: chainable `@Step` methods that navigate, click, fill, wait and assert with web-first assertions. Navigation lives in the steps of the page it leads to (`CurrencyExchangeSteps.gotoCurrencyExchangePage()`, `MortgageSteps.gotoMortgagePage()`, `MortgageSteps.openMortgagePage(locale)` via `utils/UrlBuilder`). |
| **steps/api** | The API clients: `BaseApiSteps` holds the shared Rest Assured `RequestSpecification` (base URI, JSON, headers, Allure filter) and common checks; one class per API sends the requests and validates the POJOs. |
| **api/models** | Jackson POJOs for requests and responses. The same `BranchLocation` is used by the Rest Assured tests **and** by the browser network check in `BranchesSteps`. |
| **database/** | Turns SQL rows into Java objects: `DatabaseManager` builds the MyBatis session factory once, `ConsumerLoanCaseMapper` holds the query, `ConsumerLoanCase` is the row. |
| **constants/** | The only place with fixed values; pages keep only their locators. Changing a test value, an expected text or a timeout means editing one line. |
| **tests/** | Only the scenario flow, TestNG annotations and Allure labels. UI classes extend `BaseTest` (browser per class, cookie banner rejected once, screenshot and URL attached on failure); API classes don't start a browser. |

**Why components are separate from page objects:** the quick-actions widget, the calculator result block and the header are the same Angular components on several pages. If every page object declared their selectors, one change on the site would mean fixing every page; now each selector exists once, and every page object includes only the components it actually uses.

| Component | Used by | For |
|---|---|---|
| `QuickActionsComponent` | `CurrencyExchangePage`, `BranchesPage` | opening the Currency Exchange page (SCRUM-T11, T16) and the branch finder (SCRUM-T13) from the floating widget |
| `CalculatorSummaryComponent` | `MortgagePage`, `ConsumerLoanPage` | both calculators render the same result block (monthly payment, amount, period, interest and effective rate); the component owns those locators and reads them as numbers (`readMonthlyPayment()`, `readInterestRate()`, ...), so mortgage and consumer loan payments are checked the same way (SCRUM-T12, T14, T15, T19) |
| `HeaderComponent` | `MortgagePage` | main menu → Mortgage link (SCRUM-T12, T15) and the language switch (SCRUM-T14) |
| `CookieConsentComponent` | `BaseTest` | rejects non-essential cookies once per browser before a scenario starts |

Concrete reuse example: `MortgageSteps.checkMonthlyPayment()` and `ConsumerLoanSteps.checkMonthlyPayment()` both read `summary.readMonthlyPayment()` and `summary.readInterestRate()` from `CalculatorSummaryComponent` and compare with the same `LoanCalculator.monthlyPayment()`.

## 9.2 Localization strategy

- The journey is written once in `LocalizationScenario` (SCRUM-T14) and runs for every row of the `locales` DataProvider: `LocaleData.KA` and `LocaleData.EN`.
- All locale-specific expectations live in the `LocaleData` enum, with the texts in `Constants`: URL prefix (`ka`/`en`), API locale (`ka-GE`/`en-US`), page title (`იპოთეკური სესხი` / `Mortgage`), calculator tab and mode labels, the amount limit error and the label of the language switcher.
- Locators never depend on text: tabs are found by position (`button.tbcx-pw-tab-menu__item` first), inputs by component and type. Only the **expected** values change per locale.
- Test 1 opens the calculator in the locale, checks the localized title, tabs and error message and that a random loan is calculated correctly. Test 2 opens the page, switches language through the header, checks it is the same page in the other language (URL, title, tabs, switcher label) and that the calculator still calculates correctly.

**Adding a language** (e.g. Russian): add the texts to `Constants`, one `LocaleData` constant, and one row in the DataProvider. No new test methods.

## 9.3 SQL & test data strategy

Flow: **`db/schema.sql` + `db/data.sql` → H2 → MyBatis `ConsumerLoanCaseMapper` → `ConsumerLoanCase` → `@DataProvider consumerLoanCases` → `@Factory` → `ConsumerLoanDataDrivenScenario`** (SCRUM-T19, Digital Consumer Loan calculator on `/en/loans/consumer-loan/digital`).

- H2 runs embedded (`jdbc:h2:mem:tbc`), so the local SQL database needs no installation; the scripts run when the first connection opens (`INIT=RUNSCRIPT ...` in `mybatis-config.xml`).
- Every row of `consumer_loan_cases` is one case: amount, number of months and the **expected result** (`VALID`, `AMOUNT_TOO_LOW`, `AMOUNT_TOO_HIGH`, `PERIOD_TOO_SHORT`, `PERIOD_TOO_LONG`). MyBatis maps the columns to `ConsumerLoanCase`, including `expected_result` to the `ConsumerLoanValidation` enum, so the database decides both the input and the expected outcome. The data covers the minimum and maximum values, a typical loan and one row per limit.
- The DataProvider feeds a `@Factory`: every row becomes its own instance of the scenario with its own browser and runs the same five steps – open calculator → check the default loan → enter the case → check validation → check summary and payment. `group-by-instances="true"` makes each case finish before the next starts, and every step shows its case as an Allure parameter.
- Valid rows: no field is invalid, the summary shows the case and the payment matches the annuity formula. Invalid rows: only the field outside its limit is marked invalid, the summary takes the valid field's new value and keeps the previous value of the invalid one, and the payment matches the loan that is shown.
- The query returns only rows with `active = TRUE`, so a case can be switched off without deleting it.
- **Why SQL/MyBatis rather than a hard-coded array:** the data is reviewable and changeable without touching Java, the query decides which cases run, and MyBatis keeps SQL out of the test code.

**Adding a variation:** add a row to `db/data.sql`, e.g. `(9, 'Two year loan', 25000, 24, 'VALID', TRUE)`. The next run has one more case; no code changes.

## 9.4 API → UI strategy

`CurrencyConverterApiUiScenario` (SCRUM-T16) checks that the converter on `/en/valutis-kursi` uses the rate returned by the exchange rates API – the API is the source of truth.

- **API:** `GET https://apigw.tbcbank.ge/api/v1/exchangeRates/commercialList?locale=en-US` (deserialized into `CommercialRates` → `CommercialRate`) gives the currencies the bank exchanges; two different ones are picked at random, so the pair is a cross rate without GEL (e.g. USD → EUR). `GET /exchangeRates/getExchangeRate?Iso1=USD&Iso2=EUR` gives the rate for that pair, deserialized into `ExchangeRate`. This is the same request the page sends when the pair changes.
- **Values used:** the currency pair (`iso1`, `iso2`), `buyRate`, `sellRate` (checked: positive, buy < sell) and `currencyWeight`.
- **UI check:** the pair is selected in the converter and a random amount is entered; the amount to receive must equal amount × `buyRate` ÷ `currencyWeight` (±0.01 or ±0.01 %). Then the currencies are swapped, the reversed pair's rate is read from the API and the check is repeated.
- **Why these values:** the converter result is what a customer acts on, and cross pairs are calculated by the bank's system, so they can't be derived from the rates table the other converter test (SCRUM-T11) uses.
- **What it would catch:** a stale or cached rate in the converter, the wrong side of the spread (sell instead of buy rate), a wrong direction after swapping, a wrong currency weight, or rounding errors in cross-currency conversion.

## 9.5 Network validation

`BranchesScenario` (SCRUM-T13) on `/en/atms&branches`:

| UI action | Request validated | Response validated | UI validated |
|---|---|---|---|
| Click the **Branches** tab | `POST https://apigw.tbcbank.ge/api/v1/atmsAndBranches/list`, body `filter=["BRANCH"]`, `keyword=""`, `locale=en-US` | status 200, JSON list deserialized to `BranchLocation`, every `type == "Branch"`, working hours valid | tab active, number of results == response size, every returned address is shown, every icon is `bank-outlined` |
| Type **Rustaveli** in the search box | same endpoint, `keyword="Rustaveli"` | only branches | the page shows exactly the returned locations, each contains "Rustaveli" |
| Turn on **24/7** | `filter=["BRANCH","24H"]`, keyword kept | every item `worksallday == true` | checkbox checked, every result shows "24/7" |

**Synchronization:** every action runs inside `page.waitForResponse(predicate, action)`. The predicate matches the endpoint, the method and the expected filter and keyword in the request body, so the test continues exactly when that response arrives – no fixed waits, and an earlier response (e.g. from the page load) can't be mistaken for it.

## 9.6 Test stability – loan calculators (SCRUM-T12, T14, T15, T19)

| Source of instability | How the implementation handles it |
|---|---|
| **Animated result.** The payment is drawn digit by digit, and the page keeps the previous value (`__number--old`) next to the new one (`__number--new`); reading the whole block returns `720.25₾1728.6₾`. | `CalculatorSummaryComponent` targets only `__number--new`. |
| **No network event to wait for.** Both calculators calculate in the browser, so there is no response to synchronize on, and the value changes a moment after input. | `page.waitForCondition(...)` polls until the shown payment equals the expected one and returns as soon as it does (max 5 s); then a normal assertion reports expected vs actual. |
| **Recalculation on blur.** Filling an input doesn't recalculate until focus leaves it. | `fill()` is followed by `blur()`, then `checkSummary()` waits (web-first `hasText`) until the summary shows the entered values – proof the calculator used them. |
| **Limits depend on the currency.** Mortgages in GEL can last 20 years, in USD/EUR only 10; a random 11–20 year period made the USD/EUR tests fail intermittently. | The maximum is part of `LoanCurrency`, `chooseCurrency()` asserts the input's `max` attribute, and random periods come only from the range valid in every currency. |
| **Different layouts per screen size.** At 1920×1080 the consumer loan calculator shows slider inputs that only get a red border when invalid; the narrow layout shows text messages instead. | The viewport is fixed in `BaseTest`; the consumer loan test checks the `errored` class and the inputs' `min`/`max` limits, which exist in the desktop layout. |
| **Rates change.** | The expected payment always uses the interest rate read from the page in the same step, never a hard-coded rate. |
| **Text formatting.** Values are padded with (non-breaking) spaces: `" 50000 ₾ "`, `"From 9.9%"`. | `TextParser.clean/parseNumber` and whitespace-tolerant regexes in `Constants`. |
| **Server-rendered page, clicks before scripts attach.** | Every click is preceded by a visibility assertion and followed by an assertion of the resulting state (active class, URL, summary), so a lost click fails with a clear message. |

No `Thread.sleep()`, no fixed waits and no retries are used.

## Findings on the site

- Mortgage calculator, "By Amount": the limit error says **"Income must be lower than 500000"** although the field is the loan amount.
- Mortgage: above the currency's maximum period the field only gets a red border, without a message. While that error is shown, switching currency updates the interest rate but keeps the old currency symbol and payment (e.g. "5805.42 €" with the GEL rate 12.1 %).
- Consumer loan, desktop layout: invalid values get no message at all, only a red border. The amount and the period update the summary independently, so with one invalid field the summary mixes the new valid value with the old invalid one.
