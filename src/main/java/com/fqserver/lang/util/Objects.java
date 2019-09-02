package com.fqserver.lang.util;

public class Objects {

    public static boolean toBool(Object obj) {
        return obj == null ? false : (boolean) obj;
    }

    public static char toChar(Object obj) {
        return obj == null ? ' ' : (char) obj;
    }

    public static byte toByte(Object obj) {
        return obj == null ? 0 : (byte) obj;
    }

    public static short toShort(Object obj) {
        return obj == null ? 0 : (short) obj;
    }

    public static int toInt(Object obj) {
        return obj == null ? 0 : (int) obj;
    }

    public static long toLong(Object obj) {
        return obj == null ? 0L : (long) obj;
    }

    public static String toString(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    public static String toString(Object obj, String def) {
        return obj == null ? def : String.valueOf(obj);
    }

    public static float toFloat(Object obj) {
        return obj == null ? 0.0F : (float) obj;
    }

    public static double toDouble(Object obj) {
        return obj == null ? 0.0 : (double) obj;
    }
}
