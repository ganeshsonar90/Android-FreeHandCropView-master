package jp.itnav.freehandcropsample.paint.text.type.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

import jp.itnav.freehandcropsample.paint.text.type.ApplicationLoader;
import jp.itnav.freehandcropsample.paint.text.type.Paint.FileLog;

public class SharedConfig {

    public static boolean saveToGallery=true;
    private static boolean configLoaded;
    private static final Object sync = new Object();
    private static SharedPreferences preferences;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        synchronized (sync) {
            if (configLoaded) {
                return;
            }

            preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
            saveToGallery = preferences.getBoolean("save_gallery", false);


            configLoaded = true;
        }
    }

    public static void saveConfig() {
        synchronized (sync) {
            try {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean("save_gallery", saveToGallery);

                editor.commit();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void clearConfig() {
        saveToGallery = false;
        saveConfig();
    }

    public static void checkSaveToGalleryFiles() {
        try {
            File telegramPath = new File(Environment.getExternalStorageDirectory(), "ImageEditing");
            File imagePath = new File(telegramPath, "ImageEditing Images");
            imagePath.mkdir();


            if (saveToGallery) {
                if (imagePath.isDirectory()) {
                    new File(imagePath, ".nomedia").delete();
                }

            } else {
                if (imagePath.isDirectory()) {
                    new File(imagePath, ".nomedia").createNewFile();
                }

            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }
}
