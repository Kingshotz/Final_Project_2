package utils;

import models.LocaleData;

import java.util.regex.Pattern;

import static constants.Constants.SITE_URL;
import static utils.TextParser.escapeRegex;

public class UrlBuilder {
    public static String pageUrl(LocaleData locale, String path) {
        return SITE_URL + "/" + locale.getCode() + path;
    }

    public static Pattern pageUrlPattern(LocaleData locale, String path) {
        return Pattern.compile(".*/" + locale.getCode() + escapeRegex(path) + ".*");
    }
}
