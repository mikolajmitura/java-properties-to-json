package pl.jalokim.propertiestojson.util;

public class NumberUtil {

    public static final String DOT = ".";

    public static boolean isDoubleNumber(String toParse) {
        try {
            Double.valueOf(toParse);
            return toParse.contains(DOT);
        } catch (Exception exc) {
        }
        return false;
    }

    public static boolean isIntegerNumber(String toParse) {
        try {
            Integer.valueOf(toParse);
            return true;
        } catch (Exception exc) {
        }
        return false;
    }

    public static boolean isBoolean(String toParse) {
        try {
            return "true".equalsIgnoreCase(toParse) || "false".equalsIgnoreCase(toParse);
        } catch (Exception exc) {
        }
        return false;
    }

    public static Double getDoubleNumber(String toParse) {
        return Double.valueOf(toParse);
    }

    public static Integer getIntegerNumber(String toParse) {
        return Integer.valueOf(toParse);
    }

    public static Boolean getBoolean(String toParse) {
        return Boolean.valueOf(toParse);
    }
}
