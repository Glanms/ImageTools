package com.demo.list.testphotodemo.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2015/10/29.
 * 裁剪图片之 -- 通过Drawable转换操作
 */
public class ResizeDrawable extends Drawable {

    private Paint mPaint;
    private RectF rectF;
    private Bitmap bitmap;
    private int dTop = 0;
    private int dBottom = 0;
    private boolean isCut = false;

    public ResizeDrawable(Bitmap bitmap) {
        this.bitmap = bitmap;
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if (isCut) {
            rectF = new RectF(left, dTop, right, dBottom);
        } else
            rectF = new RectF(left, top, right, bottom);
    }

    /**
     * 自定义裁剪框大小
     *
     * @param cTop  图片的起始纵坐标（相对左上角）
     * @param cHeight 裁剪的图片高度
     * @return
     */
    public void setCutSize(int cTop, int cHeight) {
        isCut = true;
        this.dTop = cTop;
        this.dBottom = dTop + cHeight;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(rectF, mPaint);//可以设置圆角矩形
    }

    @Override
    public int getIntrinsicWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return bitmap.getHeight();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
