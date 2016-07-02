package pl.jalokim.propertiestojson.util;

public class NumberUtil {

    protected static boolean isInteger(String toParse){
        try {
            Integer.valueOf(toParse);
            return true;
        } catch (Exception exc){}
        return false;
    }

    protected static Integer getInt(String toParse){
        return Integer.valueOf(toParse);
    }
}
