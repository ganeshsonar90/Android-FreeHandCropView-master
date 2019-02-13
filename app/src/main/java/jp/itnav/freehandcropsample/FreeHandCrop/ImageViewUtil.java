package jp.itnav.freehandcropsample.FreeHandCrop;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView.ScaleType;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageViewUtil {

    /* renamed from: freehandcropper.funrary.com.freehandcropper.ImageViewUtil$1 */
    static /* synthetic */ class C03371 {
        static final /* synthetic */ int[] $SwitchMap$android$widget$ImageView$ScaleType = new int[ScaleType.values().length];

        static {
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.CENTER_INSIDE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$widget$ImageView$ScaleType[ScaleType.FIT_CENTER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public static final class DecodeBitmapResult {
        public final Bitmap bitmap;
        public final int sampleSize;

        DecodeBitmapResult(Bitmap bitmap, int sampleSize) {
            this.sampleSize = sampleSize;
            this.bitmap = bitmap;
        }
    }

    public static final class RotateBitmapResult {
        public final Bitmap bitmap;
        public final int degrees;

        RotateBitmapResult(Bitmap bitmap, int degrees) {
            this.bitmap = bitmap;
            this.degrees = degrees;
        }
    }

    public static Rect getBitmapRect(Bitmap bitmap, View view, ScaleType scaleType) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        switch (C03371.$SwitchMap$android$widget$ImageView$ScaleType[scaleType.ordinal()]) {
            case 2:
                return getBitmapRectFitCenterHelper(bitmapWidth, bitmapHeight, viewWidth, viewHeight);
            default:
                return getBitmapRectCenterInsideHelper(bitmapWidth, bitmapHeight, viewWidth, viewHeight);
        }
    }

    public static Rect getBitmapRect(int bitmapWidth, int bitmapHeight, int viewWidth, int viewHeight, ScaleType scaleType) {
        switch (C03371.$SwitchMap$android$widget$ImageView$ScaleType[scaleType.ordinal()]) {
            case 2:
                return getBitmapRectFitCenterHelper(bitmapWidth, bitmapHeight, viewWidth, viewHeight);
            default:
                return getBitmapRectCenterInsideHelper(bitmapWidth, bitmapHeight, viewWidth, viewHeight);
        }
    }

    public static RotateBitmapResult rotateBitmapByExif(Context context, Bitmap bitmap, Uri uri) {
        try {
            File file = getFileFromUri(context, uri);
            if (file.exists()) {
                return rotateBitmapByExif(bitmap, new ExifInterface(file.getAbsolutePath()));
            }
        } catch (Exception e) {
        }
        return new RotateBitmapResult(bitmap, 0);
    }

    public static RotateBitmapResult rotateBitmapByExif(Bitmap bitmap, ExifInterface exif) {
        int degrees = 0;
        switch (exif.getAttributeInt("Orientation", 1)) {
            case 3:
                degrees = 180;
                break;
            case 6:
                degrees = 90;
                break;
            case 8:
                degrees = 270;
                break;
        }
        if (degrees > 0) {
            bitmap = rotateBitmap(bitmap, degrees);
        }
        return new RotateBitmapResult(bitmap, degrees);
    }

    public static DecodeBitmapResult decodeSampledBitmap(Context context, Uri uri, int reqWidth, int reqHeight) {
        InputStream stream = null;
        DecodeBitmapResult decodeBitmapResult = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            stream = resolver.openInputStream(uri);
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream, new Rect(0, 0, 0, 0), options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
            closeSafe(stream);
            stream = resolver.openInputStream(uri);
             decodeBitmapResult = new DecodeBitmapResult(BitmapFactory.decodeStream(stream, new Rect(0, 0, 0, 0), options), options.inSampleSize);
            closeSafe(stream);
            return decodeBitmapResult;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sampled bitmap", e);
        } catch (Throwable th) {
            closeSafe(stream);
        }
        return decodeBitmapResult;
    }

    public static DecodeBitmapResult decodeSampledBitmapRegion(Context context, Uri uri, Rect rect, int reqWidth, int reqHeight) {
        InputStream stream = null;
        DecodeBitmapResult decodeBitmapResult=null;
        try {
            stream = context.getContentResolver().openInputStream(uri);
            Options options = new Options();
            options.inSampleSize = calculateInSampleSize(rect.width(), rect.height(), reqWidth, reqHeight);
             decodeBitmapResult = new DecodeBitmapResult(BitmapRegionDecoder.newInstance(stream, false).decodeRegion(rect, options), options.inSampleSize);
            closeSafe(stream);
            return decodeBitmapResult;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load sampled bitmap", e);
        } catch (Throwable th) {
            closeSafe(stream);
        }
        return decodeBitmapResult;
    }

    public static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static File getFileFromUri(Context context, Uri uri) {
        File file = new File(uri.getPath());
        if (file.exists()) {
            return file;
        }
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow("_data");
            cursor.moveToFirst();
            File file2 = new File(cursor.getString(column_index));
            if (cursor != null) {
                cursor.close();
                file = file2;
            } else {
                file = file2;
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return file;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate((float) degrees);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        bitmap.recycle();
        return newBitmap;
    }

    public static void closeSafe(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    private static Rect getBitmapRectCenterInsideHelper(int bitmapWidth, int bitmapHeight, int viewWidth, int viewHeight) {
        double resultHeight;
        double resultWidth;
        int resultX;
        int resultY;
        double viewToBitmapWidthRatio = Double.POSITIVE_INFINITY;
        double viewToBitmapHeightRatio = Double.POSITIVE_INFINITY;
        if (viewWidth < bitmapWidth) {
            viewToBitmapWidthRatio = ((double) viewWidth) / ((double) bitmapWidth);
        }
        if (viewHeight < bitmapHeight) {
            viewToBitmapHeightRatio = ((double) viewHeight) / ((double) bitmapHeight);
        }
        if (viewToBitmapWidthRatio == Double.POSITIVE_INFINITY && viewToBitmapHeightRatio == Double.POSITIVE_INFINITY) {
            resultHeight = (double) bitmapHeight;
            resultWidth = (double) bitmapWidth;
        } else if (viewToBitmapWidthRatio <= viewToBitmapHeightRatio) {
            resultWidth = (double) viewWidth;
            resultHeight = (((double) bitmapHeight) * resultWidth) / ((double) bitmapWidth);
        } else {
            resultHeight = (double) viewHeight;
            resultWidth = (((double) bitmapWidth) * resultHeight) / ((double) bitmapHeight);
        }
        if (resultWidth == ((double) viewWidth)) {
            resultX = 0;
            resultY = (int) Math.round((((double) viewHeight) - resultHeight) / 2.0d);
        } else if (resultHeight == ((double) viewHeight)) {
            resultX = (int) Math.round((((double) viewWidth) - resultWidth) / 2.0d);
            resultY = 0;
        } else {
            resultX = (int) Math.round((((double) viewWidth) - resultWidth) / 2.0d);
            resultY = (int) Math.round((((double) viewHeight) - resultHeight) / 2.0d);
        }
        return new Rect(resultX, resultY, ((int) Math.ceil(resultWidth)) + resultX, ((int) Math.ceil(resultHeight)) + resultY);
    }

    private static Rect getBitmapRectFitCenterHelper(int bitmapWidth, int bitmapHeight, int viewWidth, int viewHeight) {
        double resultWidth;
        double resultHeight;
        int resultX;
        int resultY;
        if (((double) viewWidth) / ((double) bitmapWidth) <= ((double) viewHeight) / ((double) bitmapHeight)) {
            resultWidth = (double) viewWidth;
            resultHeight = (((double) bitmapHeight) * resultWidth) / ((double) bitmapWidth);
        } else {
            resultHeight = (double) viewHeight;
            resultWidth = (((double) bitmapWidth) * resultHeight) / ((double) bitmapHeight);
        }
        if (resultWidth == ((double) viewWidth)) {
            resultX = 0;
            resultY = (int) Math.round((((double) viewHeight) - resultHeight) / 2.0d);
        } else if (resultHeight == ((double) viewHeight)) {
            resultX = (int) Math.round((((double) viewWidth) - resultWidth) / 2.0d);
            resultY = 0;
        } else {
            resultX = (int) Math.round((((double) viewWidth) - resultWidth) / 2.0d);
            resultY = (int) Math.round((((double) viewHeight) - resultHeight) / 2.0d);
        }
        return new Rect(resultX, resultY, ((int) Math.ceil(resultWidth)) + resultX, ((int) Math.ceil(resultHeight)) + resultY);
    }
}
