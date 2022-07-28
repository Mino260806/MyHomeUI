package tn.amin.myhomeui.util;

public class PrimitiveUtil {
    public static float unboxFloat(Object o) {
        if (o instanceof Integer) return (int) o;
        if (o instanceof Double) return (float) ((double) o);
        if (o instanceof Float) return (float) o;
        if (o instanceof Long) return (long) o;
        throw new ClassCastException("Unable to unbox object " + o);
    }

    public static int unboxInt(Object o) {
        if (o instanceof Integer) return (int) o;
        if (o instanceof Long) return (int) ((long) o);
        if (o instanceof Float) return (int) ((float) o);
        if (o instanceof Double) return (int) ((double) o);
        throw new ClassCastException("Unable to unbox object " + o);
    }

    public static long unboxLong(Object o) {
        if (o instanceof Integer) return (long) o;
        if (o instanceof Long) return (long) o;
        if (o instanceof Float) return (long) ((float) o);
        if (o instanceof Double) return (long) ((double) o);
        throw new ClassCastException("Unable to unbox object " + o);
    }
}
