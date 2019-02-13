package jp.itnav.freehandcropsample.paint.text.type;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

import jp.itnav.freehandcropsample.CropActivity;
import jp.itnav.freehandcropsample.FreeHandCropView;
import jp.itnav.freehandcropsample.MainActivity;
import jp.itnav.freehandcropsample.R;
import jp.itnav.freehandcropsample.paint.text.type.Model.PhotoEntry;
import jp.itnav.freehandcropsample.paint.text.type.Model.PhotoSize;
import jp.itnav.freehandcropsample.paint.text.type.util.FileLoader;
import jp.itnav.freehandcropsample.paint.text.type.util.ImageLoader;

public class TypeTextActivity extends Activity {

    private Bitmap bitmap;
    private int orientation;

    private PhotoPaintView photoPaintView;
    private static LruCache<String, Bitmap> mMemoryCache;
    public static final String CACHE_KEY = "bitmap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(R.style.AppTheme);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };



     /*  // setContentView(R.layout.activity_type_text);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AndroidUtilities.checkDisplaySize(this);

        ImageView img_text = findViewById(R.id.img_text);
        img_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createText();
            }
        });*/

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic2);

        setOrientation(0, true);


        if (photoPaintView == null) {
            photoPaintView = new PhotoPaintView(this, bitmap, orientation);
            // photoPaintView.setLayoutParams(LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
            //  containerView.addView(photoPaintView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
            photoPaintView.getDoneTextView().setOnClickListener(v -> {
                 applyCurrentEditMode();
                //switchToEditMode(0);
            });
            photoPaintView.getCancelTextView().setOnClickListener(v -> photoPaintView.maybeShowDismissalAlert(TypeTextActivity.this));
            // photoPaintView.getColorPicker().setTranslationY(AndroidUtilities.dp(126));
            // photoPaintView.getToolsView().setTranslationY(AndroidUtilities.dp(126));
        }


        // new PhotoPaintView(this,bitmap,orientation);

        // setContentView(new PhotoPaintView(this,bitmap,orientation));


        setContentView(photoPaintView);

        //  setContentView(photoPaintView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


    }


    public void setOrientation(int angle, boolean center) {
        while (angle < 0) {
            angle += 360;
        }
        while (angle > 360) {
            angle -= 360;
        }
        orientation = angle;
    }


    private void applyCurrentEditMode() {
        Bitmap bitmap = null;
        boolean removeSavedState = false;

            bitmap = photoPaintView.getBitmap();
            removeSavedState = true;

        if (bitmap != null) {
            PhotoSize size = null;
            try {
                size = ImageLoader.scaleAndSaveImage(bitmap, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
            } catch (Exception e) {
                e.printStackTrace();
            }
        /*    if (size != null) {
                   PhotoEntry entry = new PhotoEntry();
                    entry.imagePath = FileLoader.getPathToAttach(size, true).toString();
                    size = ImageLoader.scaleAndSaveImage(bitmap, AndroidUtilities.dp(120), AndroidUtilities.dp(120), 70, false, 101, 101);
                    if (size != null) {
                        entry.thumbPath = FileLoader.getPathToAttach(size, true).toString();
                    }

                       // paintItem.setColorFilter(new PorterDuffColorFilter(0xff3dadee, PorterDuff.Mode.MULTIPLY));
                        entry.isPainted = false;
                    }*/

            //centerImage.setImageBitmap(bitmap);

               /* centerImage.setParentView(null);
                centerImage.setOrientation(0, true);
                ignoreDidSetImage = true;
                centerImage.setImageBitmap(bitmap);
                ignoreDidSetImage = false;
                centerImage.setParentView(containerView);
                if (sendPhotoType == SELECT_TYPE_AVATAR) {
                    setCropBitmap();
                }*/


            addBitmapToMemoryCache(size.bitmap);
            //addBitmapToMemoryCache(bitmap);


            Intent intent = new Intent(TypeTextActivity.this, CropActivity.class);
            intent.putExtra(FreeHandCropView.INTENT_KEY_CROP, true);
            startActivity(intent);



            }
        }

    public static Bitmap getBitmapFromMemCache() {
        return mMemoryCache.get(CACHE_KEY);
    }

    public void addBitmapToMemoryCache(Bitmap bitmap) {
        if (getBitmapFromMemCache() == null) {
            mMemoryCache.put(CACHE_KEY, bitmap);
        }
    }
}
