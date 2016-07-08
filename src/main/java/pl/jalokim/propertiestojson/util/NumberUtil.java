package pl.jalokim.propertiestojson.util;

public class NumberUtil {

    public static boolean isNumber(String toParse){
        try {
            Double.valueOf(toParse);
            return true;
        } catch (Exception exc){}
        return false;
    }

    public static Double getNumber(String toParse){
        return Double.valueOf(toParse);
    }
}
