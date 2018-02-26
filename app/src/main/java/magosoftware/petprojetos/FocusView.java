package magosoftware.petprojetos;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by root on 23/02/18.
 */

public class FocusView extends View {
    private Paint mTransparentPaint;
    private Paint mSemiBlackPaint;
    private Path mPath = new Path();
    public float radius;
    public Canvas focusCanvas;

    public FocusView(Context context) {
        super(context);
        initPaints();
    }

    public FocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    public FocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
    }

    public void setRadius(float radius) {
        this.radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, getResources().getDisplayMetrics())/2;
        Log.d("DPPX", Float.toString(this.radius));
        invalidate();
    }

    private void initPaints() {
        mTransparentPaint = new Paint();
        mTransparentPaint.setColor(Color.TRANSPARENT);
        mTransparentPaint.setStrokeWidth(10);

        mSemiBlackPaint = new Paint();
        mSemiBlackPaint.setColor(Color.TRANSPARENT);
        mSemiBlackPaint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        focusCanvas = canvas;

        mPath.reset();

        mPath.addCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, Path.Direction.CW);
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);

        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, mTransparentPaint);

        canvas.drawPath(mPath, mSemiBlackPaint);
        canvas.clipPath(mPath);
        canvas.drawColor(Color.parseColor("#A6000000"));
    }
}