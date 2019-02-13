package jp.itnav.freehandcropsample.FreeHandCrop;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

import jp.itnav.freehandcropsample.CropActivity;
import jp.itnav.freehandcropsample.R;

import static jp.itnav.freehandcropsample.FreeHandCrop.FreeHandScaleImageView.INTENT_KEY_CROP;

//import freehandcropper.funrary.com.freehandcropper.help.HelpManager;

public class FreeHandCroppingActivity extends AppCompatActivity {
    public static Bitmap tempBitMap = null;
    int SELECT_PHOTO = 20;
    protected int _xDelta;
    protected int _yDelta;
    private int height;
    boolean isMarkedButtonMoved = false;
    //  private AdView mAdView;
    private TextView move_or_scaling;
    String path = "";
    boolean paused = false;
    ProgressDialog pd;
    View preView;
    LinearLayout pressAndDraw;
    FreeHandScaleImageView scaleImageView;
    private TextView start_drawing;
    private int width;
    public static final String APP_DIRECTORY = "StickerLab";
    private Bitmap bitmap;
    private ImageView crop_image;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.freehand_activity_cropping);
        this.width = getResources().getDisplayMetrics().widthPixels;
        this.height = getResources().getDisplayMetrics().heightPixels;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic2);

        init();

        this.scaleImageView.setImageBitmap(bitmap);
        managePressAndDraw();
        manageRadioButtons();
        try {
            // this.path = NavigationController.CropFreeHandPath;
            // this.scaleImageView.setImageURI(Uri.parse(this.path));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.scaleImageView.set_Undo_Redo_Views(findViewById(R.id.undoIMG), findViewById(R.id.undoTXT), findViewById(R.id.RedoIMG), findViewById(R.id.RedoTXT));
    /*    try {
            this.mAdView = (AdView) findViewById(R.id.adView);
            this.mAdView.loadAd(new Builder().build());
        } catch (Exception e) {
        }*/
        //  HelpManager.showSimpleToolTipFreeHand(this, "Draw and Adjust", "When \"Draw\" option is selected.", "When \"Adjust\" option is selected.", "* you can draw on picture by moving hand.\n * A path will drawn on image.", "* you can zoom in/out your picture.\n* you can position your picture.", findViewById(R.id.videoHelp));
    }

    /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandCroppingActivity$1 */
    class C03211 implements OnClickListener {
        C03211() {
        }

        public void onClick(View view) {
            if (FreeHandCroppingActivity.this.preView.getId() != view.getId()) {
                FreeHandCroppingActivity.this.move_or_scaling.setTextColor(Color.parseColor("#000000"));
                FreeHandCroppingActivity.this.move_or_scaling.setBackgroundResource(R.drawable.rightcirclebg_ten);
                FreeHandCroppingActivity.this.start_drawing.setTextColor(-1);
                FreeHandCroppingActivity.this.start_drawing.setBackgroundResource(R.drawable.leftbluecirclebg_eight);
                FreeHandCroppingActivity.this.preView = FreeHandCroppingActivity.this.start_drawing;
                FreeHandCroppingActivity.this.scaleImageView.setdrawing(true);
            }
        }
    }

    /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandCroppingActivity$2 */
    class C03222 implements OnClickListener {
        C03222() {
        }

        @SuppressLint({"DefaultLocale"})
        public void onClick(View view) {
            if (FreeHandCroppingActivity.this.preView.getId() != view.getId()) {
                FreeHandCroppingActivity.this.start_drawing.setTextColor(Color.parseColor("#000000"));
                FreeHandCroppingActivity.this.start_drawing.setBackgroundResource(R.drawable.leftcirclebg_eight);
                FreeHandCroppingActivity.this.move_or_scaling.setTextColor(-1);
                FreeHandCroppingActivity.this.move_or_scaling.setBackgroundResource(R.drawable.rightbluecirclebg_eight);
                FreeHandCroppingActivity.this.preView = FreeHandCroppingActivity.this.move_or_scaling;
                FreeHandCroppingActivity.this.scaleImageView.setdrawing(false);
            }
        }
    }

    /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandCroppingActivity$3 */
    class C03243 implements OnTouchListener {

        /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandCroppingActivity$3$1 */
        class C03231 implements Runnable {
            C03231() {
            }

            public void run() {
                FreeHandCroppingActivity.this.scaleImageView.isMarking = false;
                FreeHandCroppingActivity.this.isMarkedButtonMoved = false;
                FreeHandCroppingActivity.this.pressAndDraw.invalidate();
            }
        }

        C03243() {
        }

        public boolean onTouch(View view, MotionEvent event) {
            int X = (int) event.getRawX();
            int Y = (int) event.getRawY();
            switch (event.getAction() & 255) {
                case 0:
                    FreeHandCroppingActivity.this.scaleImageView.isMarking = true;
                    LayoutParams lParams = (LayoutParams) view.getLayoutParams();
                    FreeHandCroppingActivity.this._xDelta = X - lParams.leftMargin;
                    FreeHandCroppingActivity.this._yDelta = Y - lParams.topMargin;
                    break;
                case 1:
                    FreeHandCroppingActivity.this.scaleImageView.postDelayed(new C03231(), 100);
                    break;
                case 2:
                    LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    int preX = layoutParams.leftMargin;
                    int preY = layoutParams.topMargin;
                    layoutParams.leftMargin = X - FreeHandCroppingActivity.this._xDelta;
                    layoutParams.topMargin = Y - FreeHandCroppingActivity.this._yDelta;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
                    if (((int) Math.sqrt((double) ((layoutParams.leftMargin - preX) * (layoutParams.leftMargin - preX)))) + ((int) Math.sqrt((double) ((layoutParams.topMargin - preY) * (layoutParams.topMargin - preY)))) > 100) {
                        FreeHandCroppingActivity.this.isMarkedButtonMoved = true;
                        break;
                    }
                    break;
            }
            return false;
        }
    }

    /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandCroppingActivity$4 */
    class C03254 implements OnClickListener {
        C03254() {
        }

        public void onClick(View pressAndDraw) {
            if (FreeHandCroppingActivity.this.isMarkedButtonMoved) {
                FreeHandCroppingActivity.this.isMarkedButtonMoved = false;
            }
        }
    }

    public class LoadImagesTask extends AsyncTask<String, Void, String> {
        Uri uri;

        LoadImagesTask(Uri uri) {
            this.uri = uri;
        }

        protected String doInBackground(String... params) {
            return FreeHandCroppingActivity.this.setImageUri(this.uri);
        }

        protected void onPreExecute() {
            super.onPreExecute();
            FreeHandCroppingActivity.this.pd = ProgressDialog.show(FreeHandCroppingActivity.this, "", "");
        }

        protected void onPostExecute(String result) {
            FreeHandCroppingActivity.this.scaleImageView.setImageURI(Uri.parse(result));
            FreeHandCroppingActivity.this.pd.dismiss();
        }
    }

    public class SaveImagesTask extends AsyncTask<String, Void, String> {
        Bitmap bitmp;

        SaveImagesTask(Bitmap bitmp) {
            this.bitmp = bitmp;
        }

        protected String doInBackground(String... params) {
            return FreeHandCroppingActivity.this.onSave(this.bitmp);
        }

        protected void onPreExecute() {
            super.onPreExecute();
            FreeHandCroppingActivity.this.pd = ProgressDialog.show(FreeHandCroppingActivity.this, "", "");
        }

        protected void onPostExecute(String result) {
          /*  FreeHandCroppingActivity.this.pd.dismiss();
            Intent navigateIntent = new Intent(FreeHandCroppingActivity.this, EraserActivity.class);
            navigateIntent.putExtra("SELECT_PHOTO", true);
            FreeHandCroppingActivity.this.startActivity(navigateIntent);
            NavigationController.comingFrom = FreeHandCroppingActivity.class;
            NavigationController.CropEditActivityPath = result;
            FreeHandCroppingActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            FreeHandCroppingActivity.this.finish();*/
        }
    }


    public void onBackPressed() {
        //  startActivity(new Intent(this, Menu.class));
        // overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        finish();
    }

    public void onBackClick(View view) {
       /* startActivity(new Intent(this, Menu.class));
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        finish();*/
    }

    public void init() {
        this.pressAndDraw = (LinearLayout) findViewById(R.id.pressAndDraw);
        this.start_drawing = (TextView) findViewById(R.id.startDraw);
        this.move_or_scaling = (TextView) findViewById(R.id.startMove);
        this.scaleImageView = (FreeHandScaleImageView) findViewById(R.id.image);
        crop_image = (ImageView) findViewById(R.id.crop_image);

    }

    private void manageRadioButtons() {
        this.preView = this.move_or_scaling;
        this.start_drawing.setOnClickListener(new C03211());
        this.move_or_scaling.setOnClickListener(new C03222());
    }

    public void managePressAndDraw() {
        this.pressAndDraw.setOnTouchListener(new C03243());
        this.pressAndDraw.setOnClickListener(new C03254());
    }

    @SuppressLint({"NewApi"})
    private int getDPI(int size) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (metrics.densityDpi * size) / 160;
    }

    public void onRedo(View view) {
        this.scaleImageView.onRedo();
    }

    public void onUndo(View view) {
        this.scaleImageView.Undo();
    }

    public void onCrop(View view) {
        crop_image.setVisibility(View.VISIBLE);


        this.scaleImageView.addBitmapToMemoryCache(this.scaleImageView.doCrop());

        Intent intent = new Intent(FreeHandCroppingActivity.this, CropActivity.class);
        intent.putExtra(INTENT_KEY_CROP, true);
        startActivity(intent);

      //  crop_image.setImageBitmap(this.scaleImageView.doCrop());

       //  new SaveImagesTask(this.scaleImageView.doCrop()).execute(new String[0]);
    }

    public String setImageUri(Uri uri) {
        if (uri == null) {
            return "";
        }
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        double densityAdj = metrics.density > 1.0f ? (double) (1.0f / metrics.density) : 1.0d;
        return onSave(ImageViewUtil.rotateBitmapByExif(this, ImageViewUtil.decodeSampledBitmap(this, uri, (int) (((double) metrics.widthPixels) * densityAdj), (int) (((double) metrics.heightPixels) * densityAdj)).bitmap, uri).bitmap);
    }

    private void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                child.delete();
                DeleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    public String onSave(Bitmap bmp) {
        File myDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + APP_DIRECTORY);
        myDir.mkdirs();
        File temp = new File(myDir + "/Temp");
        temp.mkdirs();
        File file = new File(temp, "test1.png");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    protected void onResume() {
        this.scaleImageView.isMarking = true;
        super.onResume();
      /*  try {
            this.mAdView.resume();
        } catch (Exception e) {
        }*/
    }

    protected void onPostResume() {
        this.scaleImageView.isMarking = false;
        super.onPostResume();
        if (this.paused) {
            this.scaleImageView.onResume();
        }
    }

    protected void onPause() {
        super.onPause();
        this.paused = true;
     /*   try {
            this.mAdView.pause();
        } catch (Exception e) {
        }*/
        this.scaleImageView.onPause();
    }

    public void onVideoHelpClick(View view) {

    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.scaleImageView != null) {
            this.scaleImageView = null;
        }
    }
}
