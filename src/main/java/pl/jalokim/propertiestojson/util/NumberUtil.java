package pl.jalokim.propertiestojson.util;

public class NumberUtil {

    public static boolean isInteger(String toParse){
        try {
            Integer.valueOf(toParse);
            return true;
        } catch (Exception exc){}
        return false;
    }

    public static Integer getInt(String toParse){
        return Integer.valueOf(toParse);
    }
}
