package theawesomebox.com.app.awesomebox.common.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class StringUtils {
    /**
     * Extracts and returns the string between beginDelimiter and endDelimiter. For example:
     * string        : "64 bytes from 66.102.7.99: icmp_seq=0 ttl=56 time=27.252 ms"
     * beginDelimiter:          "from "
     * endDelimiter:                          ":"
     * return:               "66.102.7.99"
     */
    public static String parseString(final String text, final String beginDelimiter, final String endDelimiter) {
        // look for the beginning of the string to extract
        final int beginDelimiterIndex = text.indexOf(beginDelimiter);
        if (beginDelimiterIndex < 0)
            return null;
        // look for the end of the string to extract
        final int fromIndex = beginDelimiterIndex + beginDelimiter.length();
        final int endDelimiterIndex = text.indexOf(endDelimiter, fromIndex);
        if (endDelimiterIndex < 0)
            return null;
        return text.substring(fromIndex, endDelimiterIndex);
    }

    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    private static boolean isInteger(String s, int radix) {
        if (s.isEmpty())
            return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1)
                    return false;
                else
                    continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0)
                return false;
        }
        return true;
    }

    public static String formatNumber(String number) {
        if (number != null && !number.isEmpty()) {
            number = number.replaceAll(",", "");
            return NumberFormat.getNumberInstance(Locale.US).format(
                    Double.parseDouble(number));
        } else
            return number;
    }

    public static boolean isStringAcceptable(String str, int start, int end) {
        return str != null && str.length() >= start && str.length() <= end;
    }

    public static boolean haveSpecialCharacter(String str, char ch, int position) {
        if (str.length() == 0)
            return false;
        else if (position != -1)
            return str.charAt(position) == ch;
        else
            for (int i = 0; i < str.length(); i++)
                if (str.charAt(i) == ch)
                    return true;
        return false;
    }
}
