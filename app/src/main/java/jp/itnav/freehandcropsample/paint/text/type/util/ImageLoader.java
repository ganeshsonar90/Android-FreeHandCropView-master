package jp.itnav.freehandcropsample.paint.text.type.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.SparseArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import jp.itnav.freehandcropsample.paint.text.type.AndroidUtilities;
import jp.itnav.freehandcropsample.paint.text.type.Model.FileLocation;
import jp.itnav.freehandcropsample.paint.text.type.Model.PhotoSize;
import jp.itnav.freehandcropsample.paint.text.type.Paint.FileLog;

public class ImageLoader {

    private DispatchQueue cacheOutQueue = new DispatchQueue("cacheOutQueue");
    private static volatile ImageLoader Instance = null;
    private File imageEditingPath;

    public static ImageLoader getInstance() {
        ImageLoader localInstance = Instance;
        if (localInstance == null) {
            synchronized (ImageLoader.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ImageLoader();
                }
            }
        }
        return localInstance;
    }


    public ImageLoader() {


        checkMediaPaths();
    }

    public void checkMediaPaths() {
        cacheOutQueue.postRunnable(() -> {
            final SparseArray<File> paths = createMediaPaths();
            AndroidUtilities.runOnUIThread(() -> FileLoader.setMediaDirs(paths));
        });
    }


    public SparseArray<File> createMediaPaths() {
        SparseArray<File> mediaDirs = new SparseArray<>();


        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                imageEditingPath = new File(Environment.getExternalStorageDirectory(), "ImageEditing");
                imageEditingPath.mkdirs();

                if (imageEditingPath.isDirectory()) {
                    try {
                        File imagePath = new File(imageEditingPath, "ImageEditing Images");
                        imagePath.mkdir();
                        if (imagePath.isDirectory() ) {
                            mediaDirs.put(FileLoader.MEDIA_DIR_IMAGE, imagePath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("image path = " + imagePath);
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }


                }
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("this Android can't rename files");
                }
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Exception e) {
            FileLog.e(e);
        }

        return mediaDirs;
    }



    private boolean canMoveFiles(File from, File to, int type) {
        RandomAccessFile file = null;
        try {
            File srcFile = null;
            File dstFile = null;
            if (type == FileLoader.MEDIA_DIR_IMAGE) {
                srcFile = new File(from, "000000000_999999_temp.jpg");
                dstFile = new File(to, "000000000_999999.jpg");
            } else if (type == FileLoader.MEDIA_DIR_DOCUMENT) {
                srcFile = new File(from, "000000000_999999_temp.doc");
                dstFile = new File(to, "000000000_999999.doc");
            } else if (type == FileLoader.MEDIA_DIR_AUDIO) {
                srcFile = new File(from, "000000000_999999_temp.ogg");
                dstFile = new File(to, "000000000_999999.ogg");
            } else if (type == FileLoader.MEDIA_DIR_VIDEO) {
                srcFile = new File(from, "000000000_999999_temp.mp4");
                dstFile = new File(to, "000000000_999999.mp4");
            }
            byte[] buffer = new byte[1024];
            srcFile.createNewFile();
            file = new RandomAccessFile(srcFile, "rws");
            file.write(buffer);
            file.close();
            file = null;
            boolean canRename = srcFile.renameTo(dstFile);
            srcFile.delete();
            dstFile.delete();
            if (canRename) {
                return true;
            }
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return false;
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight) {
        return scaleAndSaveImage(null, bitmap, maxWidth, maxHeight, quality, cache, minWidth, minHeight);
    }



    public static PhotoSize scaleAndSaveImage(PhotoSize photoSize, Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight) {
        if (bitmap == null) {
            return null;
        }
        float photoW = bitmap.getWidth();
        float photoH = bitmap.getHeight();
        if (photoW == 0 || photoH == 0) {
            return null;
        }
        boolean scaleAnyway = false;
        float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);
        if (minWidth != 0 && minHeight != 0 && (photoW < minWidth || photoH < minHeight)) {
            if (photoW < minWidth && photoH > minHeight) {
                scaleFactor = photoW / minWidth;
            } else if (photoW > minWidth && photoH < minHeight) {
                scaleFactor = photoH / minHeight;
            } else {
                scaleFactor = Math.max(photoW / minWidth, photoH / minHeight);
            }
            scaleAnyway = true;
        }
        int w = (int) (photoW / scaleFactor);
        int h = (int) (photoH / scaleFactor);
        if (h == 0 || w == 0) {
            return null;
        }

        try {
            return scaleAndSaveImageInternal(photoSize, bitmap, w, h, photoW, photoH, scaleFactor, quality, cache, scaleAnyway);
        } catch (Throwable e) {
            FileLog.e(e);
           // ImageLoader.getInstance().clearMemory();
            System.gc();
            try {
                return scaleAndSaveImageInternal( photoSize, bitmap, w, h, photoW, photoH, scaleFactor, quality, cache, scaleAnyway);
            } catch (Throwable e2) {
                FileLog.e(e2);
                return null;
            }
        }
    }



    private static PhotoSize scaleAndSaveImageInternal( PhotoSize photoSize, Bitmap bitmap, int w, int h, float photoW, float photoH, float scaleFactor, int quality, boolean cache, boolean scaleAnyway) throws Exception {
        Bitmap scaledBitmap;
        if (scaleFactor > 1 || scaleAnyway) {
            scaledBitmap = Bitmaps.createScaledBitmap(bitmap, w, h, true);
        } else {
            scaledBitmap = bitmap;
        }

        boolean check = photoSize != null;
        FileLocation location;
        if (photoSize == null || !(photoSize.location instanceof FileLocation)) {
            location = new FileLocation();
            location.volume_id = Integer.MIN_VALUE;
            location.dc_id = Integer.MIN_VALUE;
            //location.local_id = SharedConfig.getLastLocalId();
            location.file_reference = new byte[0];

            photoSize = new PhotoSize();
            photoSize.location = location;
            photoSize.w = scaledBitmap.getWidth();
            photoSize.h = scaledBitmap.getHeight();
            if (photoSize.w <= 100 && photoSize.h <= 100) {
                photoSize.type = "s";
            } else if (photoSize.w <= 320 && photoSize.h <= 320) {
                photoSize.type = "m";
            } else if (photoSize.w <= 800 && photoSize.h <= 800) {
                photoSize.type = "x";
            } else if (photoSize.w <= 1280 && photoSize.h <= 1280) {
                photoSize.type = "y";
            } else {
                photoSize.type = "w";
            }
        } else {
            location = (FileLocation) photoSize.location;
        }

     /*   String fileName = location.volume_id + "_" + location.local_id + ".jpg";
        final File cacheFile = new File(FileLoader.getDirectory(FileLoader.MEDIA_DIR_IMAGE), fileName);

        */


        photoSize.bitmap=scaledBitmap;
       // FileOutputStream stream = new FileOutputStream(file);
        //scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);


      //  galleryAddPic()


       /* if (cache) {
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream2);
            photoSize.bytes = stream2.toByteArray();
            photoSize.size = photoSize.bytes.length;
            stream2.close();
        } else {*/
        //    photoSize.size = (int) stream.getChannel().size();
       // }
     //   stream.close();
        if (scaledBitmap != bitmap) {


            if (scaledBitmap != null && !scaledBitmap.isRecycled()) {
               // scaledBitmap.recycle();
                scaledBitmap = null;
            }

            //scaledBitmap.recycle();
        }

        return photoSize;
    }

}
