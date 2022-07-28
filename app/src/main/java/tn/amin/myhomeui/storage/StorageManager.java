package tn.amin.myhomeui.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.myhomeui.BuildConfig;
import tn.amin.myhomeui.Constants;
import tn.amin.myhomeui.designer.draggable.LockscreenItemContainer;
import tn.amin.myhomeui.util.ViewFilter;
import tn.amin.myhomeui.serializer.factory.ISerializableView;
import tn.amin.myhomeui.util.BitmapUtil;
import tn.amin.myhomeui.util.FileUtil;
import tn.amin.myhomeui.util.JSONUtil;
import tn.amin.myhomeui.util.LogUtil;
import tn.amin.myhomeui.util.ViewUtil;

public class StorageManager {
    // Idk why when using Envirement.getExternalStorageDirectory() won't work on some devices
    public static final File rootDir = new File("/sdcard/Android/media", BuildConfig.APPLICATION_ID);
    public static final File lockscreenItemsDir = new File(rootDir, ".lockscreen_items");
    public static final File userFilesDir = new File(rootDir, ".user_files");
    public static final File cacheDir = new File(rootDir, ".cache");

    public static final File configDir = new File(rootDir, ".config");
    static {
        rootDir.mkdirs();
        userFilesDir.mkdir();
        lockscreenItemsDir.mkdir();
        cacheDir.mkdirs();
        configDir.mkdir();
    }

    public static File getRootDir() {
        return rootDir;
    }

    public static File getCacheDir() { return cacheDir; }

    public static void savePreviewImage(View previewFrame) {
        BitmapUtil.saveBitmapToFile(rootDir, "lockscreen_preview.jpg",
                ViewUtil.screenshot(previewFrame), Bitmap.CompressFormat.JPEG, 95);
    }

    public static Drawable tryLoadPreviewImage() {
        return BitmapUtil.tryLoadDrawable(new File(rootDir, "lockscreen_preview.jpg"));
    }

    public static void saveWallpaper(View root, ClassLoader cl) {
        FileUtil.clearDirectory(lockscreenItemsDir);
        new File(rootDir, "lockscreen.jpg").getAbsoluteFile().delete();

        BitmapUtil.saveBitmapToFile(rootDir, "lockscreen.jpg",
                BitmapUtil.getWallpaper(root.getContext(), cl).getBitmap(),
                Bitmap.CompressFormat.JPEG, 100);
    }

    public static Drawable tryLoadWallpaper() {
        return BitmapUtil.tryLoadDrawable(new File(rootDir, "lockscreen.jpg"));
    }

    public static void saveLockscreenItems(View root, @Nullable ViewFilter filter) {
        lockscreenItemsDir.mkdirs();
        ViewUtil.iterateView(root, (view) -> {
            // Don't include notifications
            if (ViewUtil.getStringId(view).equals("com.android.systemui:id/notification_stack_scroller"))
                return false;
            // Don't include views added by own app
            if (Objects.equals(view.getTag(), Constants.LOCKSCREEN_VIEW_TAG))
                return false;

            Boolean isHiddenByUser = (Boolean) XposedHelpers.getAdditionalInstanceField(view, "hidden_by_user");
            if (!(view instanceof ViewGroup && ((ViewGroup)view).getChildCount() > 0)
                    && ((isHiddenByUser != null && isHiddenByUser) || ViewUtil.isVisible(view))) {
                if (filter != null && filter.filter(view)) return false;

                ViewUtil.doOnPreDraw(view, (v) -> {
                    if (view.getWidth() == 0 || view.getHeight() == 0) return true;
                    if (view.getId() == View.NO_ID) return true;
                    if (view.getId() == View.NO_ID) return true;
                    if (ViewUtil.getStringId(view).equals("com.android.systemui:id/scrim_behind")) return true;
                    if (ViewUtil.getStringId(view).equals("com.android.systemui:id/scrim_in_front")) return true;
                    Bitmap bmp = ViewUtil.screenshot(view);
                    if (bmp == null)
                        return true;
                    if (BitmapUtil.isEmpty(bmp)) {
                        LogUtil.debug("discarding empty view with id " + ViewUtil.getStringId(view));
                        return true;
                    }
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    String detailsJSON;
                    try {
                        detailsJSON = new JSONObject()
                                .put("x", location[0])
                                .put("y", location[1])
                                .put("w", bmp.getWidth())
                                .put("h", bmp.getHeight())
                                .put("id", ViewUtil.getStringId(view))
                                .toString();
                    } catch (JSONException e) {
                        LogUtil.error("Could not save lockscreen item", e);
                        return true;
                    }
                    String fileName = Base64.getEncoder().encodeToString(detailsJSON.getBytes(StandardCharsets.UTF_8));
                    BitmapUtil.saveBitmapToFile(lockscreenItemsDir, fileName + ".png", bmp, Bitmap.CompressFormat.PNG, 100);
                    return true;
                });
            }
            return true;
        });
    }

    public static void gatherLockscreenItems(Context context, Consumer<View> callback, boolean editable) {
        File[] filesList = lockscreenItemsDir.listFiles();
        if (filesList == null) return;
        for (File file: filesList) {
            String fileName = FileUtil.getBaseName(file);
            String jsonString = new String(Base64.getDecoder().decode(fileName), StandardCharsets.UTF_8);
            int x, y, w, h;
            String id;
            JSONObject detailsJSON;
            try {
                detailsJSON = new JSONObject(jsonString);
            } catch (JSONException e) {
                detailsJSON = new JSONObject();
                LogUtil.debug("Could not instantiate details from file name");
            }
            x = JSONUtil.getInt(detailsJSON, "x", 0);
            y = JSONUtil.getInt(detailsJSON, "y", 0);
            w = JSONUtil.getInt(detailsJSON, "w", -1);
            h = JSONUtil.getInt(detailsJSON, "h", -1);
            id = JSONUtil.getString(detailsJSON, "id", ViewUtil.NO_ID);

            View view;
            if (editable) {
                LockscreenItemContainer lockscreenView = new LockscreenItemContainer(context);
                lockscreenView.set("image", file);
                lockscreenView.set("x", x);
                lockscreenView.set("y", y);
                lockscreenView.set("width", w);
                lockscreenView.set("height", h);
                lockscreenView.set("lockscreen_id", id);
                view = lockscreenView;
            } else {
                ImageView imageView = new ImageView(context);
                Glide.with(context).load(file).into(imageView);
                imageView.setX(x);
                imageView.setY(y);
                imageView.setTag(id);
                imageView.setLayoutParams(new FrameLayout.LayoutParams(w, h));
                view = imageView;
            }

            callback.accept(view);
        }
    }

    public static File saveFile(InputStream inputStream, String fileName, String dirname) throws IOException {
        File dir = new File(userFilesDir, dirname);
        dir.mkdirs();
        File newFile = new File(dir, fileName);
        newFile = FileUtil.getUniqueFile(newFile);
        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            FileUtil.copy(inputStream, fos);
        } catch (IOException e) {
            LogUtil.warn(Log.getStackTraceString(e));
        } finally {
            inputStream.close();
        }
        return newFile.exists()? newFile: null;
    }

    public static File getFileFromUserFiles(String fileName, String type) {
        return new File(userFilesDir + File.separator + type, fileName);
    }

    public static boolean loadImageInto(File image, ImageView view) {
        if (image == null) return false;
        if (!image.exists()) return false;
        Glide.with(view.getContext()).load(image).into(view);
        return true;
    }

    public static void onRenderingFinished() {
    }

    public static void disposeOfView(ISerializableView view) {
        // If inserted image was saved and then deleted this is a problem
//        File image = view.get("image");
//        if (image != null) image.getAbsoluteFile().delete();
    }

    public static void removeRedundantFiles(Set<Map<String, Object>> customizationSet) {
        ArrayList<File> neededFiles = new ArrayList<>();
        for (Map<String, Object> attrs: customizationSet) {
            File image = (File) attrs.getOrDefault("image", null);
            if (image != null && image.exists()) neededFiles.add(image);
        }
        FileUtil.listFilesRecursively(userFilesDir, (file) -> {
            if (!file.isFile()) return true;
            // Ignore fonts
            if (file.getParentFile().getName().equals("font")) return true;
            if (!neededFiles.contains(file)) file.getAbsoluteFile().delete();
            return true;
        });
    }

    public static boolean lockscreenPreviewExists() {
        return new File(rootDir, "lockscreen.jpg").exists();
    }
}
