package tn.amin.myhomeui.util;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.myhomeui.DeviceInfo;

public class BitmapUtil {
    public static Bitmap loadBitmap(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    public static BitmapDrawable loadBitmapDrawable(File file) {
        TypedValue noScale = new TypedValue();
        noScale.density = TypedValue.DENSITY_DEFAULT;
        try {
            return (BitmapDrawable) Drawable.createFromResourceStream(
                    null, noScale, new FileInputStream(file), "bitmap");
        } catch (FileNotFoundException e) {
            LogUtil.error("Failed reading Bitmap", e);
            return null;
        }
    }

    public static boolean saveBitmapToFile(File dir, String fileName, Bitmap bm,
                             Bitmap.CompressFormat format, int quality) {
        if (bm == null) return false;
        File imageFile = new File(dir,fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bm.compress(format,quality,fos);
            fos.close();

            return true;
        }
        catch (IOException e) {
            LogUtil.error("Could not save Bitmap to " + imageFile.getAbsolutePath(), e);
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static BitmapDrawable getWallpaper(Context context, ClassLoader cl) {
        BitmapDrawable wallpaper;
        switch (DeviceInfo.rom) {
            case MIUI:
                Class<?> KeyguardWallpaperUtils = XposedHelpers.findClass("com.android.keyguard.wallpaper.KeyguardWallpaperUtils", cl);
                wallpaper = (BitmapDrawable) XposedHelpers.callStaticMethod(KeyguardWallpaperUtils,
                        "getLockWallpaperPreview", context);
                break;
            default:
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                wallpaper = (BitmapDrawable) wallpaperManager.getDrawable();
                break;
        }
        return wallpaper;
    }

    public static Drawable tryLoadDrawable(File file) {
        if (!file.exists()) return null;
        return Drawable.createFromPath(file.getAbsolutePath());
    }

    public static Drawable scaleImage(Drawable image, float scaleFactor) {
        if (!(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(null, bitmapResized);

        return image;
    }

    public static boolean isEmpty(Bitmap bmp) {
        Bitmap emptyBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        return bmp.sameAs(emptyBitmap);
    }
}
