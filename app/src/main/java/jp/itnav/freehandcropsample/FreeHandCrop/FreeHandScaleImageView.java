package jp.itnav.freehandcropsample.FreeHandCrop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

import jp.itnav.freehandcropsample.R;

public class FreeHandScaleImageView extends ImageView implements OnTouchListener {
    public static CustomActionListner listner;
    public boolean DrawPath;
    private float MAX_SCALE;
    private float Possible_Scale;
    String TAG;
    public Bitmap actualBitmap;
    int bitMapHeightOfSet;
    int bitMapWidthOfSet;
    private ImageView chatHead;
    Point controlPoint;
    private boolean crop;
    int currentAction;
    Point currentPoint;
    Path currentPointsPath;
    private float currentmScale;
    float density;
    private boolean isDrawing;
    boolean isMarking;
    boolean isPositioned;
    private boolean isScaling;
    Point lastPoint;
    ArrayList<DrawPoint> list;
    int[] location;
    private Context mContext;
    private GestureDetector mDetector;
    private int mHeight;
    private float mIntrinsicHeight;
    private float mIntrinsicWidth;
    public Matrix mMatrix;
    private final float[] mMatrixValues;
    private float mMinScale;
    private float mPrevDistance;
    private int mPrevMoveX;
    private int mPrevMoveY;
    public float mScale;
    private int mWidth;
    private Bitmap myBitmap;
    Paint paint;
    Paint paint1;
    LayoutParams params;
    ProgressDialog pd;
    private Bitmap pen_nib;
    RelativeLayout.LayoutParams r_params;
    int screenH;
    int screenW;
    ArrayList<DrawPoint> selectedPoints;
    ArrayList<DrawPoint> selectedPointsBackUp;
    public boolean startDrawing;
    Paint stop;
    Point toDraw;
    Path toDrawPath;
    public Matrix toInvert;
    Matrix toInvert1;
    View v1;
    View v2;
    View v3;
    View v4;
    private WindowManager windowManager;
    private static LruCache<String, Bitmap> mMemoryCache;
    public static final String INTENT_KEY_CROP = "crop";
    public static final String CACHE_KEY = "bitmap";

    public static Bitmap getBitmapFromMemCache() {
        return mMemoryCache.get(CACHE_KEY);
    }

    public void addBitmapToMemoryCache(Bitmap bitmap) {
       // if (getBitmapFromMemCache() == null) {
            mMemoryCache.put(CACHE_KEY, bitmap);
       // }
    }

    public FreeHandScaleImageView(Context context, AttributeSet attr) {
        super(context, attr);
        this.MAX_SCALE = 0.0f;
        this.Possible_Scale = 10.0f;
        this.screenH = 0;
        this.screenW = 0;
        this.density = 1.0f;
        this.mMatrix = null;
        this.mMatrixValues = new float[9];
        this.mScale = -1.0f;
        this.isDrawing = true;
        this.TAG = "ScaleImageView";
        this.toDraw = new Point(0, 0);
        this.toDrawPath = new Path();
        this.list = new ArrayList();
        this.toInvert = new Matrix();
        this.toInvert1 = new Matrix();
        this.selectedPoints = new ArrayList();
        this.selectedPointsBackUp = new ArrayList();
        this.currentPoint = new Point();
        this.lastPoint = new Point();
        this.isMarking = false;
        this.controlPoint = new Point();
        this.startDrawing = false;
        this.location = new int[2];
        this.bitMapHeightOfSet = 0;
        this.bitMapWidthOfSet = 0;
        this.DrawPath = false;
        this.isPositioned = false;
        this.mContext = context;
        initialize();
        postDelayed(new C03261(), 1000);
    }

    public FreeHandScaleImageView(Context context) {
        super(context);
        this.MAX_SCALE = 0.0f;
        this.Possible_Scale = 10.0f;
        this.screenH = 0;
        this.screenW = 0;
        this.density = 1.0f;
        this.mMatrix = null;
        this.mMatrixValues = new float[9];
        this.mScale = -1.0f;
        this.isDrawing = true;
        this.TAG = "ScaleImageView";
        this.toDraw = new Point(0, 0);
        this.toDrawPath = new Path();
        this.list = new ArrayList();
        this.toInvert = new Matrix();
        this.toInvert1 = new Matrix();
        this.selectedPoints = new ArrayList();
        this.selectedPointsBackUp = new ArrayList();
        this.currentPoint = new Point();
        this.lastPoint = new Point();
        this.isMarking = false;
        this.controlPoint = new Point();
        this.startDrawing = false;
        this.location = new int[2];
        this.bitMapHeightOfSet = 0;
        this.bitMapWidthOfSet = 0;
        this.DrawPath = false;
        this.isPositioned = false;
        this.mContext = context;
        initialize();
        postDelayed(new C03272(), 1000);
    }

    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initialize();
    }

    public void setImageResource(int resId) {
        super.setImageResource(resId);
        initialize();
    }

    public void setonUri() {
        initialize();
    }

    private void initialize() {
        // this.pd = ProgressDialog.show(this.mContext, "", "");



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


        this.pd = new ProgressDialog(mContext);
        setScaleType(ScaleType.MATRIX);
        this.mMatrix = new Matrix();
        Drawable d = getDrawable();
        if (d != null) {
            this.mIntrinsicWidth = (float) d.getIntrinsicWidth();
            this.mIntrinsicHeight = (float) d.getIntrinsicHeight();
            setOnTouchListener(this);
        }
        this.mDetector = new GestureDetector(this.mContext, new C03283());
        this.paint = new Paint();
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setColor(-1);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeWidth(2.0f);
        this.paint1 = new Paint();
        this.paint1.setStrokeCap(Cap.ROUND);
        this.paint1.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.paint1.setStyle(Style.STROKE);
        this.paint1.setStrokeWidth(2.0f);
        this.stop = new Paint();
        this.stop.setStrokeCap(Cap.ROUND);
        this.stop.setColor(SupportMenu.CATEGORY_MASK);
        this.stop.setStyle(Style.STROKE);
        this.stop.setStrokeWidth(10.0f);
        getBitMap();
        this.bitMapHeightOfSet = getDPI(81);
        this.bitMapWidthOfSet = -getDPI(-12);
        postDelayed(new C03294(), 1000);
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) this.mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.density = metrics.density;
        try {
            postDelayed(new C03305(), 2000);
        } catch (Exception e) {
        }
    }



    /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandScaleImageView$1 */
    class C03261 implements Runnable {
        C03261() {
        }

        public void run() {
            FreeHandScaleImageView.this.manageChatHeadCustom();
        }
    }

    /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandScaleImageView$2 */
    class C03272 implements Runnable {
        C03272() {
        }

        public void run() {
            FreeHandScaleImageView.this.manageChatHeadCustom();
        }
    }

    /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandScaleImageView$3 */
    class C03283 extends SimpleOnGestureListener {
        C03283() {
        }

        public boolean onDoubleTap(MotionEvent e) {
            FreeHandScaleImageView.this.maxZoomTo((int) e.getX(), (int) e.getY());
            FreeHandScaleImageView.this.cutting();
            return super.onDoubleTap(e);
        }
    }

    /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandScaleImageView$4 */
    class C03294 implements Runnable {
        C03294() {
        }

        public void run() {
            int[] position = new int[2];
            FreeHandScaleImageView.this.getLocationOnScreen(position);
            FreeHandScaleImageView.this.bitMapHeightOfSet = position[1];
            FreeHandScaleImageView.this.bitMapWidthOfSet = position[0];
            FreeHandScaleImageView.this.bitMapWidthOfSet -= FreeHandScaleImageView.this.getDPI(20);
        }
    }

    /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandScaleImageView$5 */
    class C03305 implements Runnable {
        C03305() {
        }

        public void run() {
            if (FreeHandScaleImageView.this.getScale() > FreeHandScaleImageView.this.mMinScale) {
                FreeHandScaleImageView.this.maxZoomTo(FreeHandScaleImageView.this.mWidth / 2, FreeHandScaleImageView.this.mHeight / 2);
                FreeHandScaleImageView.this.cutting();
            }
            if (FreeHandScaleImageView.this.MAX_SCALE <= FreeHandScaleImageView.this.getScale()) {
                FreeHandScaleImageView.this.maxZoomTo(FreeHandScaleImageView.this.mWidth / 2, FreeHandScaleImageView.this.mHeight / 2);
                FreeHandScaleImageView.this.cutting();
            }
            FreeHandScaleImageView.this.pd.dismiss();
        }
    }

    /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandScaleImageView$6 */
    class C03316 implements OnTouchListener {
        private float initialTouchX;
        private float initialTouchY;
        private int initialX = (FreeHandScaleImageView.this.screenW / 2);
        private int initialY = (FreeHandScaleImageView.this.screenH / 2);

        C03316() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                    this.initialX = FreeHandScaleImageView.this.params.x;
                    this.initialY = FreeHandScaleImageView.this.params.y;
                    this.initialTouchX = event.getRawX();
                    this.initialTouchY = event.getRawY();
                    return true;
                case 1:
                    FreeHandScaleImageView.this.isPositioned = true;
                    FreeHandScaleImageView.this.toInvert = new Matrix();
                    FreeHandScaleImageView.this.mMatrix.invert(FreeHandScaleImageView.this.toInvert);
                    FreeHandScaleImageView.this.selectPoint(new DrawPoint(FreeHandScaleImageView.this.mMatrix, FreeHandScaleImageView.this.toInvert, FreeHandScaleImageView.this.params.x - FreeHandScaleImageView.this.bitMapWidthOfSet, FreeHandScaleImageView.this.params.y - FreeHandScaleImageView.this.bitMapHeightOfSet));
                    FreeHandScaleImageView.this.manageUndo_Redo();
                    return true;
                case 2:
                    if (!FreeHandScaleImageView.this.isDrawing) {
                        return true;
                    }
                    FreeHandScaleImageView.this.params.x = this.initialX + ((int) (event.getRawX() - this.initialTouchX));
                    FreeHandScaleImageView.this.params.y = this.initialY + ((int) (event.getRawY() - this.initialTouchY));
                    FreeHandScaleImageView.this.windowManager.updateViewLayout(FreeHandScaleImageView.this.chatHead, FreeHandScaleImageView.this.params);
                    if (!FreeHandScaleImageView.this.isPositioned) {
                        return true;
                    }
                    FreeHandScaleImageView.this.toInvert = new Matrix();
                    FreeHandScaleImageView.this.mMatrix.invert(FreeHandScaleImageView.this.toInvert);
                    FreeHandScaleImageView.this.selectPoint(new DrawPoint(FreeHandScaleImageView.this.mMatrix, FreeHandScaleImageView.this.toInvert, FreeHandScaleImageView.this.params.x - FreeHandScaleImageView.this.bitMapWidthOfSet, FreeHandScaleImageView.this.params.y - FreeHandScaleImageView.this.bitMapHeightOfSet));
                    return true;
                default:
                    return false;
            }
        }
    }

    /* renamed from: freehandcropper.funrary.com.freehandcropper.FreeHandScaleImageView$7 */
    class C03327 implements OnTouchListener {
        private float initialTouchX;
        private float initialTouchY;
        private int initialX = (FreeHandScaleImageView.this.screenW / 2);
        private int initialY = (FreeHandScaleImageView.this.screenH / 2);

        C03327() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case 0:
                    this.initialX = FreeHandScaleImageView.this.r_params.leftMargin;
                    this.initialY = FreeHandScaleImageView.this.r_params.topMargin;
                    this.initialTouchX = event.getRawX();
                    this.initialTouchY = event.getRawY();
                    return true;
                case 1:
                    FreeHandScaleImageView.this.isPositioned = true;
                    FreeHandScaleImageView.this.toInvert = new Matrix();
                    FreeHandScaleImageView.this.mMatrix.invert(FreeHandScaleImageView.this.toInvert);
                    FreeHandScaleImageView.this.selectPoint(new DrawPoint(FreeHandScaleImageView.this.mMatrix, FreeHandScaleImageView.this.toInvert, FreeHandScaleImageView.this.r_params.leftMargin - FreeHandScaleImageView.this.bitMapWidthOfSet, FreeHandScaleImageView.this.r_params.topMargin - FreeHandScaleImageView.this.bitMapHeightOfSet));
                    FreeHandScaleImageView.this.manageUndo_Redo();
                    return true;
                case 2:
                    if (!FreeHandScaleImageView.this.isDrawing) {
                        return true;
                    }
                    FreeHandScaleImageView.this.r_params.leftMargin = this.initialX + ((int) (event.getRawX() - this.initialTouchX));
                    FreeHandScaleImageView.this.r_params.topMargin = this.initialY + ((int) (event.getRawY() - this.initialTouchY));
                    FreeHandScaleImageView.this.chatHead.setLayoutParams(FreeHandScaleImageView.this.r_params);
                    if (!FreeHandScaleImageView.this.isPositioned) {
                        return true;
                    }
                    FreeHandScaleImageView.this.toInvert = new Matrix();
                    FreeHandScaleImageView.this.mMatrix.invert(FreeHandScaleImageView.this.toInvert);
                    FreeHandScaleImageView.this.selectPoint(new DrawPoint(FreeHandScaleImageView.this.mMatrix, FreeHandScaleImageView.this.toInvert, FreeHandScaleImageView.this.r_params.leftMargin - FreeHandScaleImageView.this.bitMapWidthOfSet, FreeHandScaleImageView.this.r_params.topMargin - FreeHandScaleImageView.this.bitMapHeightOfSet));
                    return true;
                default:
                    return false;
            }
        }
    }



    protected boolean setFrame(int l, int t, int r, int b) {
        if (this.isMarking) {
            return super.setFrame(l, t, r, b);
        }
        int paddingWidth;
        int paddingHeight;
        Drawable d = getDrawable();
        if (d != null) {
            this.mIntrinsicWidth = (float) d.getIntrinsicWidth();
            this.mIntrinsicHeight = (float) d.getIntrinsicHeight();
            this.actualBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }
        this.mWidth = r - l;
        this.mHeight = b - t;
        this.mMatrix.reset();
        int r_norm = r - l;
        if (this.mScale == -1.0f) {
            this.mScale = ((float) r_norm) / this.mIntrinsicWidth;
        }
        if (this.mScale * this.mIntrinsicHeight > ((float) this.mHeight)) {
            this.mScale = ((float) this.mHeight) / this.mIntrinsicHeight;
            this.mMatrix.postScale(this.mScale, this.mScale);
            paddingWidth = (r - this.mWidth) / 2;
            paddingHeight = 0;
        } else {
            this.mMatrix.postScale(this.mScale, this.mScale);
            paddingHeight = (b - this.mHeight) / 2;
            paddingWidth = 0;
        }
        this.mMatrix.postTranslate((float) paddingWidth, (float) paddingHeight);
        setImageMatrix(this.mMatrix);
        this.mMinScale = this.mScale;
        this.MAX_SCALE = this.mMinScale + this.Possible_Scale;
        zoomTo(this.mScale, this.mWidth / 2, this.mHeight / 2);
        cutting();
        return super.setFrame(l, t, r, b);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(this.mMatrixValues);
        return this.mMatrixValues[whichValue];
    }

    protected float getScale() {
        return getValue(this.mMatrix, 0);
    }

    public float getTranslateX() {
        return getValue(this.mMatrix, 2);
    }

    protected float getTranslateY() {
        return getValue(this.mMatrix, 5);
    }

    protected void maxZoomTo(int x, int y) {
        if (this.mMinScale == getScale() || getScale() - this.mMinScale <= 0.1f) {
            zoomTo(this.MAX_SCALE / getScale(), x, y);
        } else {
            zoomTo(this.mMinScale / getScale(), x, y);
        }
    }

    public void zoomTo(float scale, int x, int y) {
        if (getScale() * scale >= this.mMinScale) {
            if (scale < 1.0f || getScale() * scale <= this.MAX_SCALE) {
                this.mMatrix.postScale(scale, scale);
                this.currentmScale = scale;
                this.mMatrix.postTranslate((-((((float) this.mWidth) * scale) - ((float) this.mWidth))) / 2.0f, (-((((float) this.mHeight) * scale) - ((float) this.mHeight))) / 2.0f);
                this.mMatrix.postTranslate(((float) (-(x - (this.mWidth / 2)))) * scale, 0.0f);
                this.mMatrix.postTranslate(0.0f, ((float) (-(y - (this.mHeight / 2)))) * scale);
                setImageMatrix(this.mMatrix);
            }
        }
    }

    public void _zoomTo(float scale, int x, int y) {
        this.mMatrix.postScale(scale, scale);
        this.mMatrix.postTranslate((-((((float) this.mWidth) * scale) - ((float) this.mWidth))) / 2.0f, (-((((float) this.mHeight) * scale) - ((float) this.mHeight))) / 2.0f);
        this.mMatrix.postTranslate(((float) (-(x - (this.mWidth / 2)))) * scale, 0.0f);
        this.mMatrix.postTranslate(0.0f, ((float) (-(y - (this.mHeight / 2)))) * scale);
        setImageMatrix(this.mMatrix);
    }

    public void cutting() {
        int visible_width = (int) (this.mIntrinsicWidth * getScale());
        int visible_height = (int) (this.mIntrinsicHeight * getScale());
        if (getTranslateX() < ((float) (-(visible_width - this.mWidth)))) {
            this.mMatrix.postTranslate(-((getTranslateX() + ((float) visible_width)) - ((float) this.mWidth)), 0.0f);
        }
        if (getTranslateX() > 0.0f) {
            this.mMatrix.postTranslate(-getTranslateX(), 0.0f);
        }
        if (getTranslateY() < ((float) (-(visible_height - this.mHeight)))) {
            this.mMatrix.postTranslate(0.0f, -((getTranslateY() + ((float) visible_height)) - ((float) this.mHeight)));
        }
        if (getTranslateY() > 0.0f) {
            this.mMatrix.postTranslate(0.0f, -getTranslateY());
        }
        if (visible_width < this.mWidth) {
            this.mMatrix.postTranslate((float) ((this.mWidth - visible_width) / 2), 0.0f);
        }
        if (visible_height < this.mHeight) {
            this.mMatrix.postTranslate(0.0f, (float) ((this.mHeight - visible_height) / 2));
        }
        setImageMatrix(this.mMatrix);
    }

    private float distance(float x0, float x1, float y0, float y1) {
        float x = x0 - x1;
        float y = y0 - y1;
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    private float dispDistance() {
        return (float) Math.sqrt((double) ((this.mWidth * this.mWidth) + (this.mHeight * this.mHeight)));
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!(this.isDrawing || this.mDetector.onTouchEvent(event))) {
            int touchCount = event.getPointerCount();
            switch (event.getAction()) {
                case 0:
                case 5:
                case 261:
                    if (touchCount < 2) {
                        this.mPrevMoveX = (int) event.getX();
                        this.mPrevMoveY = (int) event.getY();
                        break;
                    }
                    this.mPrevDistance = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                    this.isScaling = true;
                    break;
                case 1:
                case 6:
                case 262:
                    if (event.getPointerCount() <= 1) {
                        this.isScaling = false;
                        break;
                    }
                    break;
                case 2:
                    break;
            }
            if (touchCount < 2 || !this.isScaling) {
                if (!this.isScaling) {
                    int distanceX = this.mPrevMoveX - ((int) event.getX());
                    int distanceY = this.mPrevMoveY - ((int) event.getY());
                    this.mPrevMoveX = (int) event.getX();
                    this.mPrevMoveY = (int) event.getY();
                    this.mMatrix.postTranslate((float) (-distanceX), (float) (-distanceY));
                    cutting();
                }
                invalidate();
            } else {
                float dist = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                float scale = (dist - this.mPrevDistance) / dispDistance();
                this.mPrevDistance = dist;
                scale += 1.0f;
                scale *= scale;
                zoomTo(scale, this.mWidth / 2, this.mHeight / 2);
                this.currentmScale = scale;
                cutting();
                invalidate();
            }
        }
        return true;
    }

    public boolean onTouch(View v, MotionEvent event) {
        return super.onTouchEvent(event);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path pathToDraw = new Path();
        Path pathToDraw1 = new Path();
        for (int i = 0; i < this.selectedPoints.size(); i++) {
            DrawPoint tmp = (DrawPoint) this.selectedPoints.get(i);
            float[] pts = new float[]{(float) tmp.f9x, (float) tmp.f10y};
            tmp.getIMatrix().mapPoints(pts);
            tmp.getMatrix().mapPoints(pts);
            if (i == 0) {
                pathToDraw.moveTo(pts[0], pts[1]);
                pathToDraw1.moveTo(pts[0] + 2.0f, pts[1] + 2.0f);
            } else {
                pathToDraw.lineTo(pts[0], pts[1]);
                pathToDraw1.lineTo(pts[0] + 2.0f, pts[1] + 2.0f);
            }
        }
        canvas.drawPath(pathToDraw, this.paint);
        canvas.drawPath(pathToDraw1, this.paint1);
        if (this.selectedPoints.size() > 0) {
            DrawPoint tmp = (DrawPoint) this.selectedPoints.get(this.selectedPoints.size() - 1);
            float[] pts = new float[]{(float) tmp.f9x, (float) tmp.f10y};
            tmp.getIMatrix().mapPoints(pts);
            tmp.getMatrix().mapPoints(pts);
            DrawPoint tmp1 = (DrawPoint) this.selectedPoints.get(0);
            float[] pts1 = new float[]{(float) tmp1.f9x, (float) tmp1.f10y};
            tmp1.getIMatrix().mapPoints(pts1);
            tmp1.getMatrix().mapPoints(pts1);
            canvas.drawPoint(pts1[0] + 2.0f, pts1[1] + 1.0f, this.stop);
            if (this.selectedPoints.size() > 1) {
                canvas.drawPoint(pts[0], pts[1], this.stop);
            }
            this.r_params.leftMargin = ((int) pts[0]) + this.bitMapWidthOfSet;
            this.r_params.topMargin = ((int) pts[1]) + this.bitMapHeightOfSet;
            this.chatHead.setLayoutParams(this.r_params);
        }
    }

    public void setdrawing(boolean isDrawing) {
        this.isDrawing = isDrawing;
        if (isDrawing) {
            this.chatHead.setVisibility(View.VISIBLE);
        } else {
            this.chatHead.setVisibility(View.INVISIBLE);
        }
    }

    public boolean getdrawing() {
        return this.isDrawing;
    }

    public void onMyDraw(Point pointer, MotionEvent event) {
        this.toDraw = pointer;
        this.currentAction = event.getAction();
        if (this.currentAction == 0) {
            touch_start((float) this.toDraw.x, (float) this.toDraw.y);
        } else if (this.currentAction == 2) {
            touch_move((float) this.toDraw.x, (float) this.toDraw.y);
        }
        invalidate();
    }

    public void setlistner(CustomActionListner listner) {
        listner = listner;
    }

    private void touch_start(float _x, float _y) {
        this.toInvert = new Matrix(this.mMatrix);
        this.mMatrix.invert(this.toInvert);
        this.list.add(new DrawPoint(this.mMatrix, this.toInvert, (int) _x, ((int) _y) - this.pen_nib.getHeight()));
    }

    private void touch_move(float _x, float _y) {
        invalidate();
        this.list.add(new DrawPoint(this.mMatrix, this.toInvert, (int) _x, ((int) _y) - this.pen_nib.getHeight()));
        this.lastPoint.x = (int) _x;
        this.lastPoint.y = ((int) _y) - this.pen_nib.getHeight();
    }

    public Path pointsToPath(ArrayList<Point> points) {
        Path mpath = new Path();
        try {
            Point point = (Point) points.get(0);
            mpath.moveTo((float) point.x, (float) point.y);
            for (int i = 1; i < points.size(); i++) {
                Point point1 = (Point) points.get(i);
                mpath.lineTo((float) point1.x, (float) point1.y);
            }
        } catch (Exception e) {
        }
        return mpath;
    }

    public Path pointsToPath1(ArrayList<DrawPoint> points) {
        Path mpath = new Path();
        try {
            DrawPoint point = (DrawPoint) points.get(0);
            mpath.moveTo((float) (point.f9x - 5), (float) (point.f10y - 5));
            mpath.lineTo((float) (point.f9x + 5), (float) (point.f10y + 5));
            for (int i = 1; i < points.size(); i++) {
                DrawPoint point1 = (DrawPoint) points.get(i);
                mpath.lineTo((float) point1.f9x, (float) point1.f10y);
            }
        } catch (Exception e) {
        }
        return mpath;
    }

    public void getBitMap() {
        this.pen_nib = scaleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_touch_app), getDPI(60), getDPI(60));
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale(((float) wantedWidth) / ((float) bitmap.getWidth()), ((float) wantedHeight) / ((float) bitmap.getHeight()));
        canvas.drawBitmap(bitmap, m, new Paint());
        return output;
    }

    @SuppressLint({"NewApi"})
    private int getDPI(int size) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) this.mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int result = (metrics.densityDpi * size) / 160;
        this.screenH = metrics.heightPixels;
        this.screenW = metrics.widthPixels;
        return result;
    }

    public void onRedo() {
        try {
            if (this.selectedPointsBackUp != null && this.selectedPointsBackUp.size() > 0 && this.selectedPointsBackUp.size() > this.selectedPoints.size()) {
                if (this.selectedPointsBackUp.size() + 10 >= this.selectedPoints.size()) {
                    this.selectedPoints.addAll(this.selectedPointsBackUp.subList(this.selectedPoints.size(), this.selectedPoints.size() + 10));
                } else {
                    this.selectedPoints.addAll(this.selectedPointsBackUp.subList(this.selectedPoints.size(), this.selectedPointsBackUp.size()));
                }
            }
            invalidate();
            manageUndo_Redo();
        } catch (Exception e) {
        }
    }

    public Path drawBazier(Point startPoint, Point controlPoint, Point endPoint, Canvas canvas) {
        Path temp = new Path();
        temp.moveTo((float) startPoint.x, (float) startPoint.y);
        for (double t = 0.01d; t < 1.0d; t += 0.01d) {
            temp.lineTo((float) ((int) (((((1.0d - t) * (1.0d - t)) * ((double) startPoint.x)) + (((2.0d * (1.0d - t)) * t) * ((double) controlPoint.x))) + ((t * t) * ((double) endPoint.x)))), (float) ((int) (((((1.0d - t) * (1.0d - t)) * ((double) startPoint.y)) + (((2.0d * (1.0d - t)) * t) * ((double) controlPoint.y))) + ((t * t) * ((double) endPoint.y)))));
        }
        return temp;
    }

    public void adjustBezierCurve(int X, int Y) {
        this.controlPoint.x = X;
        this.controlPoint.y = Y;
        invalidate();
    }

    public void Undo() {
        if (this.selectedPoints.size() > 0) {
            int size = this.selectedPoints.size();
            int i;
            if (this.selectedPoints.size() - 10 >= 0) {
                for (i = size - 1; i >= size - 10; i--) {
                    this.selectedPoints.remove(i);
                }
            } else {
                for (i = size - 1; i >= 0; i--) {
                    this.selectedPoints.remove(i);
                }
            }
            if (this.selectedPoints.size() == 0) {
                this.isPositioned = false;
            }
        }
        invalidate();
        manageUndo_Redo();
    }

    public Bitmap doCrop() {
        _zoomTo(this.mMinScale / getScale(), this.mWidth / 2, this.mWidth / 2);
        cutting();
        return get_CroppedCustomImage();
    }

    public static Bitmap bitMapOverlay(Bitmap one, Bitmap two) {
        Bitmap overLay = Bitmap.createBitmap(one.getWidth(), one.getHeight(), one.getConfig());
        Canvas canvas = new Canvas(overLay);
        canvas.drawBitmap(one, new Matrix(), null);
        canvas.drawBitmap(two, 0.0f, 0.0f, null);
        return overLay;
    }

    public Bitmap get_CroppedCustomImage() {
        this.myBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        if (this.selectedPoints.size() == 0) {
            return this.myBitmap;
        }
        Bitmap tempBitmap = Bitmap.createBitmap(this.myBitmap.getWidth(), this.myBitmap.getHeight(), this.myBitmap.getConfig());
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawARGB(0, 0, 0, 0);
        Path path = getCropedPath();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(-154);
        Paint paintt = new Paint();
        paintt.setAntiAlias(true);
        paintt.setColor(SupportMenu.CATEGORY_MASK);
        tempCanvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        tempCanvas.drawBitmap(this.myBitmap, 0.0f, 0.0f, paint);
        try {
            return cropBitmapToBoundingBox(tempBitmap, Color.parseColor("#00000000"));
        } catch (Exception e) {
            return tempBitmap;
        }
    }

    private Path getCropedPath() {
        Path pathToDraw = new Path();
        int left = (int) ((((float) this.mWidth) - (this.mIntrinsicWidth * getScale())) / 2.0f);
        int top = (int) ((((float) this.mHeight) - (this.mIntrinsicHeight * getScale())) / 2.0f);
        float toDivide = getScale() / this.density;
        if (toDivide < 1.0f) {
            toDivide = getScale();
        }
        for (int i = 0; i < this.selectedPoints.size(); i++) {
            DrawPoint tmp = (DrawPoint) this.selectedPoints.get(i);
            float[] pts = new float[]{(float) tmp.f9x, (float) tmp.f10y};
            tmp.getIMatrix().mapPoints(pts);
            tmp.getMatrix().mapPoints(pts);
            if (i == 0) {
                pathToDraw.moveTo((pts[0] - ((float) left)) / toDivide, (pts[1] - ((float) top)) / toDivide);
            } else {
                pathToDraw.lineTo((pts[0] - ((float) left)) / toDivide, (pts[1] - ((float) top)) / toDivide);
            }
        }
        return pathToDraw;
    }

    public Rect getCustomRect() {
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;
        ArrayList<DrawPoint> pathPoints = this.selectedPoints;
        if (pathPoints.size() > 0) {
            DrawPoint point = (DrawPoint) pathPoints.get(0);
            float[] pts = new float[]{(float) point.f9x, (float) point.f10y};
            point.getIMatrix().mapPoints(pts);
            point.getMatrix().mapPoints(pts);
            right = (int) pts[0];
            left = right;
            bottom = (int) pts[1];
            top = bottom;
        }
        float[] mapPoints = new float[2];
        for (int i = 1; i < pathPoints.size(); i++) {
            DrawPoint point = (DrawPoint) pathPoints.get(i);
            float[] pts = new float[]{(float) point.f9x, (float) point.f10y};
            point.getIMatrix().mapPoints(pts);
            point.getMatrix().mapPoints(pts);
            int tempX = (int) pts[0];
            int tempY = (int) pts[1];
            if (tempX < left) {
                left = tempX;
            }
            if (tempX > right) {
                right = tempX;
            }
            if (tempY < top) {
                top = tempY;
            }
            if (tempY > bottom) {
                bottom = tempY;
            }
        }
        int Marginleft = (int) ((((float) this.mWidth) - (this.mIntrinsicWidth * getScale())) / this.density);
        int Margintop = (int) ((((float) this.mHeight) - (this.mIntrinsicHeight * getScale())) / this.density);
        return new Rect((int) (((float) (left - Marginleft)) / (getScale() / this.density)), (int) (((float) (top - Margintop)) / (getScale() / this.density)), (int) (((float) (right - Marginleft)) / (getScale() / this.density)), (int) (((float) (bottom - Margintop)) / (getScale() / this.density)));
    }

    public void manageChatHead() {
        this.windowManager = (WindowManager) getContext().getSystemService("window");
        this.chatHead = new ImageView(getContext());
        this.chatHead.setAlpha(0.6f);
        this.chatHead.setImageBitmap(this.pen_nib);
        if (VERSION.SDK_INT >= 26) {
            this.params = new LayoutParams(-2, -2, 2038, 8, -3);
        } else {
            this.params = new LayoutParams(-2, -2, 2002, 8, -3);
        }
        this.params.gravity = 51;
        this.params.x = this.screenW / 2;
        this.params.y = this.screenH / 2;
        this.chatHead.setOnTouchListener(new C03316());
        try {
            this.windowManager.addView(this.chatHead, this.params);
            this.chatHead.setVisibility(4);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void manageChatHeadCustom() {
        this.chatHead = (ImageView) ((Activity) this.mContext).findViewById(R.id.handDraw);
        this.chatHead.setImageBitmap(this.pen_nib);
        this.chatHead.setAlpha(0.6f);
        this.r_params = (RelativeLayout.LayoutParams) this.chatHead.getLayoutParams();
        this.r_params.leftMargin = this.screenW / 2;
        this.r_params.topMargin = this.screenH / 2;
        this.chatHead.setOnTouchListener(new C03327());
        try {
            this.chatHead.setLayoutParams(this.r_params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void selectPoint(DrawPoint point) {
        if (this.isDrawing) {
            this.selectedPoints.add(point);
            this.selectedPointsBackUp.add(point);
            invalidate();
        }
    }

    public void manageUndo_Redo() {
        try {
            if (this.selectedPoints.size() > 0 && this.selectedPointsBackUp.size() == this.selectedPoints.size()) {
                ((ImageView) this.v1).setImageResource(R.drawable.undo);
                ((TextView) this.v2).setTextColor(Color.parseColor("#000000"));
                ((ImageView) this.v3).setImageResource(R.drawable.d_redo);
                ((TextView) this.v4).setTextColor(Color.parseColor("#c2c2c2"));
            } else if (this.selectedPoints.size() > 0 && this.selectedPointsBackUp.size() > this.selectedPoints.size()) {
                ((ImageView) this.v1).setImageResource(R.drawable.undo);
                ((TextView) this.v2).setTextColor(Color.parseColor("#000000"));
                ((ImageView) this.v3).setImageResource(R.drawable.redo);
                ((TextView) this.v4).setTextColor(Color.parseColor("#000000"));
            } else if (this.selectedPoints.size() != 0 || this.selectedPointsBackUp.size() <= 0) {
                ((ImageView) this.v1).setImageResource(R.drawable.d_undo);
                ((TextView) this.v2).setTextColor(Color.parseColor("#c2c2c2"));
                ((ImageView) this.v3).setImageResource(R.drawable.d_redo);
                ((TextView) this.v4).setTextColor(Color.parseColor("#c2c2c2"));
            } else {
                ((ImageView) this.v1).setImageResource(R.drawable.d_undo);
                ((TextView) this.v2).setTextColor(Color.parseColor("#c2c2c2"));
                ((ImageView) this.v3).setImageResource(R.drawable.redo);
                ((TextView) this.v4).setTextColor(Color.parseColor("#000000"));
            }
        } catch (NullPointerException ex) {
            ex.toString();
        }
    }

    public void set_Undo_Redo_Views(View v1, View v2, View v3, View v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
    }

    public void onPause() {
        if (this.chatHead != null && this.isDrawing) {
            this.chatHead.setVisibility(INVISIBLE);
        }
    }

    public void onResume() {
        if (this.chatHead != null && this.isDrawing) {
            this.chatHead.setVisibility(View.VISIBLE);
        }
    }

    public Bitmap cropBitmapToBoundingBox(Bitmap picToCrop, int unusedSpaceColor) {
        int i;
        int[] pixels = new int[(picToCrop.getHeight() * picToCrop.getWidth())];
        int marginTop = 0;
        int marginBottom = 0;
        int marginLeft = 0;
        int marginRight = 0;
        picToCrop.getPixels(pixels, 0, picToCrop.getWidth(), 0, 0, picToCrop.getWidth(), picToCrop.getHeight());
        for (i = 0; i < pixels.length; i++) {
            if (pixels[i] != unusedSpaceColor) {
                marginTop = i / picToCrop.getWidth();
                break;
            }
        }
        loop1:
        for (i = 0; i < picToCrop.getWidth(); i++) {
            int j = i;
            while (j < pixels.length) {
                if (pixels[j] != unusedSpaceColor) {
                    marginLeft = j % picToCrop.getWidth();
                    break loop1;
                }
                j += picToCrop.getWidth();
            }
        }
        for (i = pixels.length - 1; i >= 0; i--) {
            if (pixels[i] != unusedSpaceColor) {
                marginBottom = (pixels.length - i) / picToCrop.getWidth();
                break;
            }
        }
        loop4:
        for (i = pixels.length - 1; i >= 0; i--) {
            int j = i;
            while (j >= 0) {
                if (pixels[j] != unusedSpaceColor) {
                    marginRight = picToCrop.getWidth() - (j % picToCrop.getWidth());
                    break loop4;
                }
                j -= picToCrop.getWidth();
            }
        }
        return Bitmap.createBitmap(picToCrop, marginLeft, marginTop, (picToCrop.getWidth() - marginLeft) - marginRight, (picToCrop.getHeight() - marginTop) - marginBottom);
    }

    public void resetView() {
      /*  points.clear();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        flgPathDraw = true;*/
        invalidate();
    }

}
