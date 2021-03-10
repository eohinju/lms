package tz.mil.ngome.lms.utils;

import java.text.DecimalFormat;

public class Formatter {

    public final static String toCash(Double money){
        String result = new DecimalFormat("#,###.00").format(money);
        return result.contentEquals(".00")?"0.00":result;
    }
}
