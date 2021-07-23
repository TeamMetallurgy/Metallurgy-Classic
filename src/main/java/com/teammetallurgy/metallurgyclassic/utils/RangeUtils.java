package com.teammetallurgy.metallurgyclassic.utils;

import org.apache.commons.lang3.Range;

public class RangeUtils {

    public static Range<Integer> rangeFromString(String str, String delimiter) {
        System.out.println(str);
        if(str.contains(delimiter) && !str.equals("-1")) {
            var split = str.split(delimiter);
            System.out.println(split[0] + " " + split[1]);
            return Range.between(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        } else {
            return Range.is(Integer.parseInt(str));
        }
    }
}
