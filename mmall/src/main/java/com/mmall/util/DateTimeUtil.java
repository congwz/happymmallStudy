package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by Administrator on 2017/7/3 0003.
 */
public class DateTimeUtil {

    //使用joda-time这个开源包

    //封装两个重载
    public static  final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dateTimeStr, String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr); //字符串格式
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    //date转String
    public static String dateToStr(String date,String formatStr){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);  //DateTime属于joda-time中的类
        return dateTime.toString(formatStr);
    }



    //String转date
    //DateTimeFormatter、DateTimeFormat都是joda-time中的类
    public static Date strToDate(String dateTimeStr){
        //DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr); //字符串格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT); //字符串格式
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    //date转String
    public static String dateToStr(Date date){
        if(date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);  //DateTime属于joda-time中的类
        //return dateTime.toString(formatStr);
        return dateTime.toString(STANDARD_FORMAT);
    }


    /*
    //测试
    public static void main(String[] args) {
        Date date = new Date();
        String format = "yyyy-MM-dd HH:mm:ss";
        //System.out.println(DateTimeUtil.dateToStr(new Date(),"yyyy-MM-dd HH:mm:ss"));
        System.out.println(DateTimeUtil.dateToStr(date));
        System.out.println(DateTimeUtil.strToDate("2017-07-03 11:11:11",format));
    }
    */

}
