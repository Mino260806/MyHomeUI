package tn.amin.myhomeui;

import android.annotation.SuppressLint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Objects;

public class DeviceInfo {
    public static ROM rom = null;

    static {
        findRom();
    }

    private static void findRom() {
        if (isMIUI()) rom = ROM.MIUI;
        else rom = ROM.OTHER;
    }

    @SuppressLint("PrivateApi")
    private static boolean isMIUI() {
        Class<?> c;
        try {
            c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            String miui = (String) get.invoke(c, "ro.miui.ui.version.code"); // maybe this one or any other
            return !Objects.equals(miui, "");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    public enum ROM {
        MIUI,
        OTHER
    }
}
