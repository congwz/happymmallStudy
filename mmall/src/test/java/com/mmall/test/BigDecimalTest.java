package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/8/5 0005.
 */
public class BigDecimalTest {

    @Test
    public void test1(){
        System.out.println(0.05+0.01);
        System.out.println(1.0-0.42);
        System.out.println(4.015*100);
        System.out.println(123.3/100);

        //测试输出结果如下：
        /*
        0.060000000000000005
        0.5800000000000001
        401.49999999999994
        1.2329999999999999
        */

        //输出的测试结果表明Java在计算浮点型数据丢失精度的问题
    }

    @Test
    public void test2(){
        BigDecimal b1 = new BigDecimal(0.05);
        BigDecimal b2 = new BigDecimal(0.01);
        System.out.println(b1.add(b2));   //结果是： 0.06000000000000000298372437868010820238851010799407958984375
    }

    @Test
    public void test3(){
        BigDecimal b1 = new BigDecimal("0.05");  //使用String的构造器
        BigDecimal b2 = new BigDecimal("0.01");
        System.out.println(b1.add(b2));  //输出结果： 0.06  所以在使用BigDecimal的时候，一定要用它的String构造器
    }
}
