package tz.mil.ngome.lms.utils;

import java.text.DecimalFormat;

public class Formatter {

    public final static String toCash(Double money){
        return new DecimalFormat("#,###.00").format(money);
    }
}
