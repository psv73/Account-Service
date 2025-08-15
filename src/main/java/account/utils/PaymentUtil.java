package account.utils;

import java.time.Month;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentUtil {

    /**
     * Convert period from "01-2021" to "January-2021"
     *
     * @param period period of salary - month and year
     * @return converted period
     */
    public static String convertPeriod(String period) {

        StringBuilder str = new StringBuilder();

        Pattern pattern = Pattern.compile("^[0-9]{2}");
        Matcher matcher = pattern.matcher(period);

        if (matcher.find()) {
            String monthName = Month.of(Integer.parseInt(matcher.group())).name();

            str.append(Pattern.compile("^.")
                    .matcher(monthName.toLowerCase())
                    .replaceFirst(m -> m.group().toUpperCase()))
                    .append("-");
        }

        str.append(period.replaceAll("[0-9]{2}-", ""));

        return str.toString();
    }

    /**
     * Convert salary from long 123456 to string "1234 dollar(s) 56 cent(s)"
     *
     * @param salary long value
     * @return converted salary to string
     */
    public static String convertSalary(long salary) {

        return salary / 100 + " dollar(s) " +
                (salary % 100 > 10 ? salary % 100 : "0" + salary % 100) + " cent(s)";
    }
}
