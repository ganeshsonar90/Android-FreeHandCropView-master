package jp.itnav.freehandcropsample.paint.text.type.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.SparseArray;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.itnav.freehandcropsample.SelectImageActivity;
import jp.itnav.freehandcropsample.paint.text.type.Model.PhotoSize;

public class FileLoader {

    public static final int MEDIA_DIR_IMAGE = 0;
    public static final int MEDIA_DIR_AUDIO = 1;
    public static final int MEDIA_DIR_VIDEO = 2;
    public static final int MEDIA_DIR_DOCUMENT = 3;
    public static final int MEDIA_DIR_CACHE = 4;

    private static SparseArray<File> mediaDirs = null;

    public static File getDirectory(int type) {
        File dir = mediaDirs.get(type);
        if (dir == null && type != FileLoader.MEDIA_DIR_CACHE) {
            dir = mediaDirs.get(FileLoader.MEDIA_DIR_CACHE);
        }
        try {
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            //don't promt
        }
        return dir;
    }

    public static void setMediaDirs(SparseArray<File> dirs) {
        mediaDirs = dirs;
    }

    public static File getPathToAttach(PhotoSize attach, boolean forceCache) {
        return getPathToAttach(attach, null, forceCache);
    }

    public static File getPathToAttach(PhotoSize attach, String ext, boolean forceCache) {
        File dir = null;
        if (forceCache) {
            dir = getDirectory(MEDIA_DIR_CACHE);
        } else {
            if (attach instanceof PhotoSize) {
                PhotoSize photoSize = (PhotoSize) attach;

                dir = getDirectory(MEDIA_DIR_IMAGE);

            }
        }
        if (dir == null) {
            return new File("");
        }
        return new File(dir, getAttachFileName(attach, ext));
    }

    public static String getAttachFileName(PhotoSize attach, String ext) {
        if (attach instanceof PhotoSize) {
            PhotoSize photo = (PhotoSize) attach;
            if (photo.location == null) {
                return "";
            }
            return photo.location.volume_id + "_" + photo.location.local_id + "." + (ext != null ? ext : "jpg");
        }
        return "";
    }


    // String mCurrentPhotoPath;

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String mCurrentPhotoPath = image.getAbsolutePath();


        return image;
    }

    public static  File createImageFileCamera(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //This is the directory in which the file will be created. This is the default location of Camera photos
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        // cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }


    public static void galleryAddPic(Context context, File file) {


        Uri photoURI = null;
        if (file != null) {
            photoURI = FileProvider.getUriForFile(context,
                    context.getPackageName(),
                    file);

        }


        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // File f = new File(mCurrentPhotoPath);
        // Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(photoURI);
        context.sendBroadcast(mediaScanIntent);


        new SingleMediaScanner(context, file);


    }


}
