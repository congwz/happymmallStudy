package com.mmall.util;


import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/8/5 0005.
 */

//BigDecimal的工具类--->Double转String
public class BigDecimalUtil {

    private BigDecimalUtil() {

    }

    //加法
    //public static add(double v1,double v2){
    public static BigDecimal add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);  //不会丢失精度哦，因为把double类型转换成String类型
    }

    //减法
    public static BigDecimal sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }

    //乘法
    public static BigDecimal mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    //除法
    public static BigDecimal div(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));

        //除法的时候注意除不尽的情况，采用策略：四舍五入、保留几位小数等
        //divide这个函数的其中一个重载方法：public BigDecimal divide(BigDecimal divisor, int scale, int roundingMode)
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP);  //保留2位小数，四舍五入

    }
}
