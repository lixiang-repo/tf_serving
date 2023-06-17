package utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringHelper {
    public static int parseInt(String s, int defaultValue) {
        int ret = defaultValue;
        if (!StringUtils.isNumeric(s)) return ret;

        try {
            ret = Integer.parseInt(s);
        } catch (Exception e) {

        }

        return ret;
    }

    public static long parseLong(String s, long defaultValue) {
        long ret = defaultValue;
        if (!StringUtils.isNumeric(s)) return ret;

        try {
            ret = Long.parseLong(s);
        } catch (Exception e) {

        }

        return ret;
    }

    public static float parseFlat(String s, float defaultValue) {
        float ret = defaultValue;
        if (!StringUtils.isNumeric(s)) return ret;

        try {
            ret = Float.parseFloat(s);
        } catch (Exception e) {

        }

        return ret;
    }

    public static boolean isAllNumeric(final CharSequence... css) {
        for (final CharSequence cs : css) {
            if (!StringUtils.isNumeric(cs)) {
                return false;
            }
        }

        return true;
    }

    public static List<String> getTfList(String str, String sep, int len, String dv) {
        String[] rapper = str.split(sep);
        if (rapper.length == 1 && rapper[0].length() == 0) {
            rapper[0] = dv;
        }
        ArrayList<String> result = new ArrayList<>(rapper.length);
        Collections.addAll(result, rapper);
        int diff = len - result.size();
        for (int i = 0; i < diff; i++) {
            result.add(dv);
        }
        return result.subList(0, len);
    }

    ;
}
