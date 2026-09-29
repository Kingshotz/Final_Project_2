package constants;

import java.util.regex.Pattern;

public class Constants {
    public static final String SITE_URL = "https://www.tbcbank.ge",
    BASE_URL = SITE_URL + "/en",

    MORTGAGE_PATH = "/loans/mortgage",
    CONSUMER_LOAN_PATH = "/loans/consumer-loan/digital",
    BRANCHES_PATH = "/atms&branches",
    CURRENCY_EXCHANGE_PATH = "/valutis-kursi";

    public static final String BROWSER_PROPERTY = "browser",
    HEADLESS_PROPERTY = "headless",
    SLOW_MO_PROPERTY = "slowMo",
    DEFAULT_BROWSER = "chromium",
    FIREFOX = "firefox",
    WEBKIT = "webkit",
    DEFAULT_HEADLESS = "false",
    DEFAULT_SLOW_MO = "0",
    COOKIE_REJECT_BUTTON = "reject all",
    SCREENSHOT_ATTACHMENT = "Screenshot on failure",
    SCREENSHOT_TYPE = "image/png",
    SCREENSHOT_EXTENSION = "png",
    URL_ATTACHMENT = "Page URL";

    public static final int VIEWPORT_WIDTH = 1920,
    VIEWPORT_HEIGHT = 1080;

    public static final double COOKIE_BANNER_TIMEOUT = 15_000,
    CALCULATION_TIMEOUT = 5_000,
    BRANCHES_FIRST_RENDER_TIMEOUT = 60_000;

    public static final String EN_CODE = "en",
    EN_API_LOCALE = "en-US",
    EN_MORTGAGE_TITLE = "Mortgage",
    EN_CALCULATOR_TAB = "Calculator",
    EN_BY_AMOUNT_TAB = "By Amount",
    EN_MORTGAGE_AMOUNT_ERROR = "Income must be lower than 500000",
    EN_LANGUAGE_SWITCH_LABEL = "ქარ",

    KA_CODE = "ka",
    KA_API_LOCALE = "ka-GE",
    KA_MORTGAGE_TITLE = "იპოთეკური სესხი",
    KA_CALCULATOR_TAB = "კალკულატორი",
    KA_BY_AMOUNT_TAB = "თანხის მიხედვით",
    KA_MORTGAGE_AMOUNT_ERROR = "შემოსავალი არ უნდა აჭარბებდეს 500000",
    KA_LANGUAGE_SWITCH_LABEL = "EN";

    public static final String CURRENCY_GEL = "GEL",
    CURRENCY_USD = "USD";

    public static final int EXCHANGE_MIN_AMOUNT = 1,
    EXCHANGE_MAX_AMOUNT = 100_000,
    EXCHANGE_AMOUNT_DECIMALS = 2;

    public static final double EXCHANGE_TOLERANCE = 0.01,
    EXCHANGE_RELATIVE_TOLERANCE = 0.0001;

    public static final String GEL_SYMBOL = "₾",
    USD_SYMBOL = "$",
    EUR_SYMBOL = "€",
    PERIOD_MAX_ATTRIBUTE = "max";

    public static final String CONSUMER_LOAN_TITLE = "Consumer Loan",
    MIN_ATTRIBUTE = "min",
    MAX_ATTRIBUTE = "max";

    public static final int CONSUMER_LOAN_MIN_AMOUNT = 200,
    CONSUMER_LOAN_MAX_AMOUNT = 80_000,
    CONSUMER_LOAN_MIN_MONTHS = 3,
    CONSUMER_LOAN_MAX_MONTHS = 48;

    public static final int MORTGAGE_MIN_AMOUNT = 1000,
    MORTGAGE_MAX_AMOUNT = 500_000,
    MORTGAGE_AMOUNT_ABOVE_MAX = MORTGAGE_MAX_AMOUNT + 1,
    MORTGAGE_MIN_PERIOD = 1,
    GEL_MAX_PERIOD = 20,
    USD_MAX_PERIOD = 10,
    EUR_MAX_PERIOD = 10,
    MORTGAGE_GEL_ONLY_PERIOD = 15,
    MORTGAGE_PERIOD_ABOVE_MAX = 25,
    MONTHS_IN_YEAR = 12;

    public static final double PAYMENT_TOLERANCE = 0.01,
    PERCENT = 100;

    public static final String BRANCHES_TITLE = "ATMs, Branches and CDMs",
    BRANCHES_TAB = "Branches",
    TWENTY_FOUR_SEVEN_CHIP = "24/7",
    TWENTY_FOUR_SEVEN_TEXT = "24/7",
    BRANCH_SEARCH_KEYWORD = "Rustaveli",
    NO_KEYWORD = "",
    BRANCH_FILTER = "BRANCH",
    TWENTY_FOUR_SEVEN_FILTER = "24H",
    BRANCH_TYPE = "Branch",
    BRANCH_ICON = "bank-outlined",
    REGION_TBILISI = "Tbilisi";

    public static final double GEORGIA_MIN_LATITUDE = 40,
    GEORGIA_MAX_LATITUDE = 44,
    GEORGIA_MIN_LONGITUDE = 39,
    GEORGIA_MAX_LONGITUDE = 47,
    NO_LOCATION_LATITUDE = 0,
    NO_LOCATION_LONGITUDE = 0;

    public static final String API_BASE_URL = "https://apigw.tbcbank.ge/api/v1",
    BRANCHES_LIST_ENDPOINT = "/atmsAndBranches/list",
    BRANCHES_LIST_URL = API_BASE_URL + BRANCHES_LIST_ENDPOINT,
    BRANCHES_REGIONS_ENDPOINT = "/atmsAndBranches/regions",
    CONTENT_PAGE_ENDPOINT = "/sites/pages/{pageId}",
    COMMERCIAL_RATES_ENDPOINT = "/exchangeRates/commercialList",
    EXCHANGE_RATE_ENDPOINT = "/exchangeRates/getExchangeRate",
    ISO1_PARAM = "Iso1",
    ISO2_PARAM = "Iso2",
    PAGE_ID_PARAM = "pageId",
    LOCALE_PARAM = "locale",
    POST = "POST",

    ACCEPT_HEADER_VALUE = "application/json, application/problem+json",
    ORIGIN_HEADER = "Origin",
    REFERER_HEADER = "Referer",
    USER_AGENT_HEADER = "User-Agent",
    USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0 Safari/537.36",
    PROBLEM_JSON = "application/problem+json",

    INVALID_FILTER = "INVALID",
    FILTER_ERROR_FIELD = "Filter[0]",
    FILTER_ERROR_MESSAGE = "Available values:ATM;BRANCH;24H;CDM;OPEN",
    EMPTY_JSON_BODY = "{}",
    LOCALE_ERROR_FIELD = "Locale",
    LOCALE_ERROR_MESSAGE = "Incorrect format",
    UNKNOWN_KEYWORD = "zzzqqqxxx",
    UNKNOWN_PAGE_ID = "doesNotExist123",
    NOT_FOUND_TITLE = "Object not found";

    public static final int STATUS_OK = 200,
    STATUS_BAD_REQUEST = 400,
    STATUS_NOT_FOUND = 404;

    public static final String MYBATIS_CONFIG = "mybatis-config.xml";

    public static final Pattern
    CURRENCY_EXCHANGE_URL = Pattern.compile(".*" + CURRENCY_EXCHANGE_PATH + ".*"),
    MORTGAGE_URL = Pattern.compile(".*" + MORTGAGE_PATH + ".*"),
    BRANCHES_URL = Pattern.compile(".*" + BRANCHES_PATH + ".*"),

    ACTIVE_CLASS = Pattern.compile("\\bactive\\b"),
    ERRORED_CLASS = Pattern.compile("\\berrored\\b"),
    OPEN_PANEL_CLASS = Pattern.compile("tbcx-pw-sticky-actions--open"),
    ACTIVE_CURRENCY_CLASS = Pattern.compile("tbcx-pw-calculator-form__currency--active"),

    COOKIE_REJECT_TEXT = Pattern.compile(COOKIE_REJECT_BUTTON, Pattern.CASE_INSENSITIVE),
    CURRENCY_CODE = Pattern.compile("^\\s*[\\u00A0]*[A-Z]{3}\\s*[\\u00A0]*$"),
    RATE_NUMBER = Pattern.compile("\\d+(\\.\\d+)?"),
    BASE_AMOUNT = Pattern.compile("^[\\s\\u00A0]*\\d+[\\s\\u00A0]"),
    EXCHANGE_AMOUNT = Pattern.compile("^[\\d,]+(\\.\\d+)?$"),
    SUMMARY_AMOUNT = Pattern.compile("^[\\s\\u00A0]*\\d+[\\s\\u00A0]+\\S+[\\s\\u00A0]*$"),
    PERCENT_VALUE = Pattern.compile("^[\\s\\u00A0]*\\d+(\\.\\d+)?[\\s\\u00A0]*%[\\s\\u00A0]*$"),
    RATE_VALUE = Pattern.compile("\\d+(\\.\\d+)?[\\s\\u00A0]*%"),
    TIME_HH_MM = Pattern.compile("^\\d{2}:\\d{2}$"),

    REGEX_SPECIAL_CHARACTERS = Pattern.compile("[\\\\^$.|?*+()\\[\\]{}/]");
}
