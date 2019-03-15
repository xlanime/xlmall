package com.xlmall.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 时间格式转换工具类,利用joda-time实现。
 */
public class DateTimeUtil {

    //设置默认的时间转换格式
    public static final String STANDARD_FORMAT = "yyyy-MM-dd hh:mm:ss";

    public static Date strToDate(String str,String format){
        //生成DateTimeFormatter
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format);
        DateTime dateTime = dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date,String format){
        //这里判断是否为空是因为joda-time的DateTime接收null参数，会出现异常。
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(format);
    }

    public static Date strToDate(String str){
        //生成DateTimeFormatter
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date){
        //这里判断是否为空是因为joda-time的DateTime接收null参数，会出现异常。
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

    public static void main(String[] args) {
        System.out.println(dateToStr(new Date()));
        System.out.println(strToDate("2018-06-11 12:12:12"));
    }

}
