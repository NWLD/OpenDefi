package com.nwld.defi.tools.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class CalcUtils {
    public static final int TYPE_ADD = 0x00; // 加法
    public static final int TYPE_MULTIPLY = 0x01; // 乘法
    public static final int TYPE_DIVIDE = 0x02; // 除法
    public static final int TYPE_SUBTRACT = 0x03; // 减法

    /**
     * 加法
     *
     * @param a
     * @param b
     * @return
     */
    public static String add(Double a, Double b) {
        return calc(a, b, -1, TYPE_ADD, null);
    }

    public static String add(String a, String b) {
        return calc(a, b, -1, TYPE_ADD, null);
    }

    /**
     * 减法
     *
     * @param a
     * @param b
     * @return
     */

    public static String sub(Double a, Double b) {
        return calc(a, b, -1, TYPE_SUBTRACT, null);
    }

    public static String sub(String a, String b) {
        return calc(a, b, -1, TYPE_SUBTRACT, null);
    }

    /**
     * 乘法
     *
     * @param a
     * @param b
     * @return
     */

    public static String multiply(Double a, Double b) {
        return calc(a, b, -1, TYPE_MULTIPLY, null);
    }

    public static String multiply(String a, String b) {
        return calc(a, b, -1, TYPE_MULTIPLY, null);
    }

    public static String pow(int a, int b) {
        try {
            BigDecimal bgA = new BigDecimal(a);
            return bgA.pow(b).toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static String decimals(BigInteger a, int decimals) {
        try {
            BigDecimal bgA = new BigDecimal(a);
            BigDecimal ten = new BigDecimal("10");
            ten = ten.pow(decimals);
            bgA = bgA.divide(ten);
            bgA = bgA.setScale(4, BigDecimal.ROUND_DOWN);
            return bgA.toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }


    /**
     * 除法
     *
     * @param a
     * @param b
     * @return
     */

    public static String divide(Double a, Double b) {
        return calc(a, b, -1, TYPE_DIVIDE, null);
    }

    /**
     * 乘法
     *
     * @param a
     * @param b
     * @param scale 小数点后保留的位数
     * @param mode  保留的模式
     * @return
     */
    public static String multiply(Double a, Double b, int scale, RoundingMode mode) {

        return calc(a, b, scale, TYPE_MULTIPLY, mode);
    }

    /**
     * 除法
     *
     * @param a
     * @param b
     * @param scale 小数点后保留的位数
     * @param mode  保留的模式
     * @return
     */
    public static String divide(String a, String b, int scale, RoundingMode mode) {
        return calc(a, b, scale, TYPE_DIVIDE, mode);
    }

    /**
     * 计算
     *
     * @param a
     * @param b
     * @param scale
     * @param type
     * @param mode
     * @return
     */
    private static String calc(Double a, Double b, int scale, int type, RoundingMode mode) {
        return calc(String.valueOf(a), String.valueOf(b), scale, type, mode);
    }

    private static String calc(String a, String b, int scale, int type, RoundingMode mode) {
        try {
            BigDecimal result = null;
            BigDecimal bgA = new BigDecimal(a);
            BigDecimal bgB = new BigDecimal(b);
            switch (type) {
                case TYPE_ADD:
                    result = bgA.add(bgB);
                    break;
                case TYPE_MULTIPLY:
                    result = bgA.multiply(bgB);
                    break;
                case TYPE_DIVIDE:
                    try {
                        result = bgA.divide(bgB);
                    } catch (ArithmeticException e) {// 防止无限循环而报错  采用四舍五入保留3位有效数字
                        result = bgA.divide(bgB, 3, RoundingMode.HALF_DOWN);
                    }

                    break;
                case TYPE_SUBTRACT:
                    result = bgA.subtract(bgB);
                    break;

            }
            if (mode == null) {
                if (scale != -1) {

                    result = result.setScale(scale);
                }
            } else {
                if (scale != -1) {
                    result = result.setScale(scale, mode);
                }
            }
            return result.toPlainString();
        } catch (Exception e) {
            return "0";
        }
    }
}
