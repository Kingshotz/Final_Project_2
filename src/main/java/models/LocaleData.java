package models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static constants.Constants.EN_API_LOCALE;
import static constants.Constants.EN_BY_AMOUNT_TAB;
import static constants.Constants.EN_CALCULATOR_TAB;
import static constants.Constants.EN_CODE;
import static constants.Constants.EN_LANGUAGE_SWITCH_LABEL;
import static constants.Constants.EN_MORTGAGE_AMOUNT_ERROR;
import static constants.Constants.EN_MORTGAGE_TITLE;
import static constants.Constants.KA_API_LOCALE;
import static constants.Constants.KA_BY_AMOUNT_TAB;
import static constants.Constants.KA_CALCULATOR_TAB;
import static constants.Constants.KA_CODE;
import static constants.Constants.KA_LANGUAGE_SWITCH_LABEL;
import static constants.Constants.KA_MORTGAGE_AMOUNT_ERROR;
import static constants.Constants.KA_MORTGAGE_TITLE;

@Getter
@AllArgsConstructor
public enum LocaleData {
    EN(EN_CODE, EN_API_LOCALE,
            EN_MORTGAGE_TITLE, EN_CALCULATOR_TAB, EN_BY_AMOUNT_TAB,
            EN_MORTGAGE_AMOUNT_ERROR,
            EN_LANGUAGE_SWITCH_LABEL),
    KA(KA_CODE, KA_API_LOCALE,
            KA_MORTGAGE_TITLE, KA_CALCULATOR_TAB, KA_BY_AMOUNT_TAB,
            KA_MORTGAGE_AMOUNT_ERROR,
            KA_LANGUAGE_SWITCH_LABEL);

    private final String code;
    private final String apiLocale;
    private final String mortgageTitle;
    private final String calculatorTab;
    private final String byAmountTab;
    private final String mortgageAmountError;
    private final String languageSwitchLabel;

    public LocaleData other() {
        return this == EN ? KA : EN;
    }
}
