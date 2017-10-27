package com.mikhaellopez.circularimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class CircularImageView extends ImageView {
    private static final float DEFAULT_BORDER_WIDTH = 4.0f;
    private static final float DEFAULT_SHADOW_RADIUS = 8.0f;
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private float borderWidth;
    private int canvasSize;
    private Drawable drawable;
    private Bitmap image;
    private Paint paint;
    private Paint paintBorder;
    private int shadowColor;
    private float shadowRadius;

    public CircularImageView(Context context) {
        this(context, null);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.shadowColor = ViewCompat.MEASURED_STATE_MASK;
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paintBorder = new Paint();
        this.paintBorder.setAntiAlias(true);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView, defStyleAttr, 0);
        if (attributes.getBoolean(R.styleable.CircularImageView_civ_border, true)) {
            setBorderWidth(attributes.getDimension(R.styleable.CircularImageView_civ_border_width, DEFAULT_BORDER_WIDTH * getContext().getResources().getDisplayMetrics().density));
            setBorderColor(attributes.getColor(R.styleable.CircularImageView_civ_border_color, -1));
        }
        if (attributes.getBoolean(R.styleable.CircularImageView_civ_shadow, false)) {
            this.shadowRadius = DEFAULT_SHADOW_RADIUS;
            drawShadow(attributes.getFloat(R.styleable.CircularImageView_civ_shadow_radius, this.shadowRadius), attributes.getColor(R.styleable.CircularImageView_civ_shadow_color, this.shadowColor));
        }
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        requestLayout();
        invalidate();
    }

    public void setBorderColor(int borderColor) {
        if (this.paintBorder != null) {
            this.paintBorder.setColor(borderColor);
        }
        invalidate();
    }

    public void addShadow() {
        if (this.shadowRadius == 0.0f) {
            this.shadowRadius = DEFAULT_SHADOW_RADIUS;
        }
        drawShadow(this.shadowRadius, this.shadowColor);
        invalidate();
    }

    public void setShadowRadius(float shadowRadius) {
        drawShadow(shadowRadius, this.shadowColor);
        invalidate();
    }

    public void setShadowColor(int shadowColor) {
        drawShadow(this.shadowRadius, shadowColor);
        invalidate();
    }

    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported. ScaleType.CENTER_CROP is used by default. So you don't need to use ScaleType.", new Object[]{scaleType}));
        }
    }

    public void onDraw(Canvas canvas) {
        loadBitmap();
        if (this.image != null) {
            if (!isInEditMode()) {
                this.canvasSize = canvas.getWidth();
                if (canvas.getHeight() < this.canvasSize) {
                    this.canvasSize = canvas.getHeight();
                }
            }
            int circleCenter = ((int) (((float) this.canvasSize) - (this.borderWidth * 2.0f))) / 2;
            canvas.drawCircle(((float) circleCenter) + this.borderWidth, ((float) circleCenter) + this.borderWidth, (((float) circleCenter) + this.borderWidth) - (this.shadowRadius + (this.shadowRadius / 2.0f)), this.paintBorder);
            canvas.drawCircle(((float) circleCenter) + this.borderWidth, ((float) circleCenter) + this.borderWidth, ((float) circleCenter) - (this.shadowRadius + (this.shadowRadius / 2.0f)), this.paint);
        }
    }

    private void loadBitmap() {
        if (this.drawable != getDrawable()) {
            this.drawable = getDrawable();
            this.image = drawableToBitmap(this.drawable);
            updateShader();
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.canvasSize = w;
        if (h < this.canvasSize) {
            this.canvasSize = h;
        }
        if (this.image != null) {
            updateShader();
        }
    }

    private void drawShadow(float shadowRadius, int shadowColor) {
        this.shadowRadius = shadowRadius;
        this.shadowColor = shadowColor;
        if (VERSION.SDK_INT >= 11) {
            setLayerType(1, this.paintBorder);
        }
        this.paintBorder.setShadowLayer(shadowRadius, 0.0f, shadowRadius / 2.0f, shadowColor);
    }

    private void updateShader() {
        if (this.image != null) {
            this.image = cropBitmap(this.image);
            BitmapShader shader = new BitmapShader(this.image, TileMode.CLAMP, TileMode.CLAMP);
            Matrix matrix = new Matrix();
            matrix.setScale(((float) this.canvasSize) / ((float) this.image.getWidth()), ((float) this.canvasSize) / ((float) this.image.getHeight()));
            shader.setLocalMatrix(matrix);
            this.paint.setShader(shader);
        }
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            return Bitmap.createBitmap(bitmap, (bitmap.getWidth() / 2) - (bitmap.getHeight() / 2), 0, bitmap.getHeight(), bitmap.getHeight());
        }
        return Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() / 2) - (bitmap.getWidth() / 2), bitmap.getWidth(), bitmap.getWidth());
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            return null;
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            Log.e(getClass().toString(), "Encountered OutOfMemoryError while generating bitmap!");
            return null;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == 1073741824) {
            return specSize;
        }
        if (specMode == Integer.MIN_VALUE) {
            return specSize;
        }
        return this.canvasSize;
    }

    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);
        if (specMode == 1073741824) {
            result = specSize;
        } else if (specMode == Integer.MIN_VALUE) {
            result = specSize;
        } else {
            result = this.canvasSize;
        }
        return result + 2;
    }
}
