package com.fqserver.lang.util;

import io.netty.util.internal.MathUtil;

import java.util.Collection;

/**
 * Utility and fast math functions.
 * <p>
 * Thanks to Riven on JavaGaming.org for the basis of sin/cos/floor/ceil.
 */
public final class MathUtils {
    // ---
    public static final float FLOAT_ROUNDING_ERROR = (float) 1.0E-6; // 32
                                                                     // bits
    public static final double DOUBLE_ROUNDING_ERROR = 1.0E-10; // 64
                                                                // bits

    /**
     * Returns true if the value is zero (using the default tolerance as upper
     * bound)
     */
    static public boolean isZero(float value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    /**
     * Returns true if the value is zero.
     * 
     * @param tolerance
     *            represent an upper bound below which the value is considered
     *            zero.
     */
    static public boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    /**
     * Returns true if a is nearly equal to b. The function uses the default
     * floating error tolerance.
     * 
     * @param a
     *            the first value.
     * @param b
     *            the second value.
     */
    static public boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    /**
     * Returns true if a is nearly equal to b.
     * 
     * @param a
     *            the first value.
     * @param b
     *            the second value.
     * @param tolerance
     *            represent an upper bound below which the two values are
     *            considered equal.
     */
    static public boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    /**
     * Returns true if the value is zero (using the default tolerance as upper
     * bound)
     */
    public static boolean isZero(double value) {
        return Math.abs(value) <= DOUBLE_ROUNDING_ERROR;
    }

    /**
     * Returns true if the value is zero.
     * 
     * @param tolerance
     *            represent an upper bound below which the value is considered
     *            zero.
     */
    public static boolean isZero(double value, double tolerance) {
        return Math.abs(value) <= tolerance;
    }

    /**
     * Returns true if a is nearly equal to b. The function uses the default
     * doubleing error tolerance.
     * 
     * @param a
     *            the first value.
     * @param b
     *            the second value.
     */
    public static boolean isEqual(double a, double b) {
        return Math.abs(a - b) <= DOUBLE_ROUNDING_ERROR;
    }

    /**
     * Returns true if a is nearly equal to b.
     * 
     * @param a
     *            the first value.
     * @param b
     *            the second value.
     * @param tolerance
     *            represent an upper bound below which the two values are
     *            considered equal.
     */
    public static boolean isEqual(double a, double b, double tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    /**
     * 求和
     * 
     * @param values
     * @throws NullPointerException
     *             if (values == null || values.contain(null))
     * @return
     */
    public static int sum(Collection<Integer> values) {
        if (values == null)
            return 0;

        int sum = 0;
        for (Integer integer : values) {
            sum += Objects.toInt(integer);
        }
        return sum;
    }

    /**
     * @see MathUtil#sum(Collection)
     * @param ts
     * @return
     */
    public static int sum(Integer... ts) {
        return sum(Arrays.toList(ts));
    }

    /**
     * 求和
     * 
     * @param values
     * @throws NullPointerException
     *             if (values == null || values.contain(null))
     * @return
     */
    public static float sumF(Collection<Float> values) {
        if (values == null)
            return 0F;

        float sum = 0F;
        for (Float d : values) {
            sum += Objects.toFloat(d);
        }
        return sum;
    }

    /**
     * @see MathUtil#sum(Collection)
     * @param ts
     * @return
     */
    public static float sumF(Float... ts) {
        return sumF(Arrays.toList(ts));
    }

    /**
     * 求和
     * 
     * @param values
     * @throws NullPointerException
     *             if (values == null || values.contain(null))
     * @return
     */
    public static double sumD(Collection<Double> values) {
        if (values == null)
            return 0D;

        double sum = 0D;
        for (Double d : values) {
            sum += Objects.toDouble(d);
        }
        return sum;
    }

    /**
     * @see MathUtil#sum(Collection)
     * @param ts
     * @return
     */
    public static double sumD(Double... ts) {
        return sumD(Arrays.toList(ts));
    }
}