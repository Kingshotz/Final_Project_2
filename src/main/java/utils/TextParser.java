package utils;

import java.util.regex.Pattern;
import static constants.Constants.REGEX_SPECIAL_CHARACTERS;

public class TextParser {
    public static String escapeRegex(String text) {
        return REGEX_SPECIAL_CHARACTERS.matcher(text).replaceAll("\\\\$0");
    }

    public static Pattern exactText(String text) {
        return Pattern.compile("^\\s*" + escapeRegex(text) + "\\s*$");
    }

    public static String clean(String text) {
        return text.replace(' ', ' ').replaceAll("\\s+", " ").trim();
    }

    public static double parseNumber(String text) {
        String digits = clean(text).replace(",", "").replaceAll("[^0-9.]", "");
        return digits.isEmpty() ? Double.NaN : Double.parseDouble(digits);
    }
}
