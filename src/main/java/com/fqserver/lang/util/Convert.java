package com.fqserver.lang.util;

public class Convert {
    /**
     * 根据传入的obj返回 boolean 类型
     * 
     * @param obj
     * @return
     */
    public static boolean toBool(Object obj) {
        try {
            if (obj != null) {
                if (obj instanceof Boolean) {
                    return (boolean) obj;
                } else if (obj instanceof String) {
                    String s = (String) obj;
                    if (s.trim().equals("") || s.equalsIgnoreCase("false")) {
                        return false;
                    }
                } else if (obj instanceof Number) {
                    Number s = (Number) obj;
                    return s.doubleValue() > MathUtils.FLOAT_ROUNDING_ERROR;
                }
                return true;
            }
        }
        catch (Exception e) {}

        return false;
    }

    /**
     * 根据传入的obj返回 byte 类型
     * 
     * @param obj
     * @return
     */
    public static int toByte(Object obj) {
        try {
            return (obj == null ? 0 : Double.valueOf(obj.toString()).byteValue());
        }
        catch (Exception e) {
            return 0;
        }
    }

    /**
     * 根据传入的obj返回 short 类型
     * 
     * @param obj
     * @return
     */
    public static int toShort(Object obj) {
        try {
            return (obj == null ? 0 : Double.valueOf(obj.toString()).shortValue());
        }
        catch (Exception e) {
            return 0;
        }
    }

    /**
     * 根据传入的obj返回 int 类型
     * 
     * @param obj
     * @return
     */
    public static int toInt(Object obj) {
        return toInt(obj, 0);
    }

    /**
     * 根据传入的obj返回 int 类型
     * 
     * @param obj
     * @param defValue
     *            默认值
     * @return
     */
    public static int toInt(Object obj, int defValue) {
        try {
            return (obj == null ? defValue : Double.valueOf(obj.toString()).intValue());
        }
        catch (Exception e) {
            return defValue;
        }
    }

    /**
     * 根据传入的obj返回 long 类型
     * 
     * @param obj
     * @return
     */
    public static long toLong(Object obj) {
        return toLong(obj, 0L);
    }

    /**
     * 根据传入的obj返回 long 类型
     * 
     * @param obj
     * @param defValue
     *            默认值
     * @return
     */
    public static long toLong(Object obj, long defValue) {
        try {
            return (obj == null ? defValue : Double.valueOf(obj.toString()).longValue());
        }
        catch (Exception e) {
            return defValue;
        }
    }

    /**
     * 根据传入的obj返回 float 类型
     * 
     * @param obj
     * @return
     */
    public static float toFloat(Object obj) {
        try {
            return obj == null ? 0.0F : Double.valueOf(obj.toString()).floatValue();
        }
        catch (Exception e) {
            return 0.0F;
        }
    }

    /**
     * 根据传入的obj返回 double 类型
     * 
     * @param obj
     * @return
     */
    public static double toDouble(Object obj) {
        try {
            return obj == null ? 0.0 : Double.valueOf(obj.toString());
        }
        catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 根据传入的obj返回String如果为null，返回""
     * 
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    /**
     * 根据传入的obj返回String如果为null，返回默认值
     * 
     * @param obj
     * @param defValue
     *            默认值
     * @return
     */
    public static String toString(Object obj, String defValue) {
        return obj == null ? defValue : String.valueOf(obj);
    }
}
