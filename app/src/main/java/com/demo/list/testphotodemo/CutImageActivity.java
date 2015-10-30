package com.demo.list.testphotodemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.demo.list.testphotodemo.tools.T;

/**
 * Created by Administrator on 2015/10/28.
 */
public class CutImageActivity extends AppCompatActivity implements View.OnClickListener {

    private String imagePath = "";
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView bigImg;
    private ImageView smallImg;
    private static final String TAG = "CutPhoto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_image);
        bigImg = (ImageView) findViewById(R.id.image_big);
        smallImg = (ImageView) findViewById(R.id.image_small);
        ((Button) findViewById(R.id.select_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.cut_btn)).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_btn:
                selectPhoto();
                break;
            case R.id.cut_btn:
                cutPhoto();
                break;
        }
    }

    private void selectPhoto() {
        Intent sIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(sIntent, RESULT_LOAD_IMAGE);
    }

    private void cutPhoto() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bigBitmap = BitmapFactory.decodeFile(picturePath);
           /* byte[] bytes = new byte[1024];
            ByteArrayOutputStream mBaos = new ByteArrayOutputStream();
            bigBitmap.compress(Bitmap.CompressFormat.JPEG,100,mBaos);
            bytes = mBaos.toByteArray();
            bytes = getRectBitmap2(bytes,100,500);*/

            bigBitmap = useCanvasCut(bigBitmap, 30, 150);

//            ResizeDrawable mImageDrawable = new ResizeDrawable(bigBitmap);
//            mImageDrawable.setCutSize(30,110);
//            bigBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            bigImg.setImageBitmap(bigBitmap);

        }
    }

    /**
     * 使用遮罩层,
     * 自绘制遮罩矩形
     *
     * @param bitmap
     * @param yStart
     * @param height
     * @return
     */
    private Bitmap getShadeBitmap1(Bitmap bitmap, int yStart, int height) {

        Drawable drawable = new BitmapDrawable(bitmap);
        Bitmap outBmp = Bitmap.createBitmap(bitmap.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBmp);

        RectF rectF = new RectF(0, 0, bitmap.getWidth(), height);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        // 遮罩层Bitmap
        Bitmap first = Bitmap.createBitmap(bitmap.getWidth(),height, Bitmap.Config.ARGB_8888);
        Canvas newCanvas = new Canvas(first);
        Rect newRect = new Rect(0,0,50,80);
        newCanvas.drawColor(Color.BLACK);
//        paint.setARGB(0,0,0,0);

//        Path path = new Path();
//        path.addRect(0, 0, xWidth, yHeight, Path.Direction.CW); //
//        path.close();
//        canvas.drawPath(path, paint);
        canvas.drawRect(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        drawable.setBounds(0, 0, bitmap.getWidth(), height);
        canvas.saveLayer(rectF, paint, Canvas.ALL_SAVE_FLAG);
        drawable.draw(canvas);
//        canvas.save();
        canvas.restore();

        return outBmp;
    }

    /**
     * 使用Bitmap的createBitmap方法进行裁剪
     *
     * @param bitmap
     * @param yStart 起始纵坐标
     * @param height 截取高度
     * @see Bitmap#createBitmap(Bitmap, int, int, int, int)
     * AHA ，简单高效
     */
    private Bitmap getRectBitmap1(Bitmap bitmap, int yStart, int height) {
//        int width = 200;
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte,0,bitmapByte.length);
        // 判断是否高度关系
        if (bitmap.getHeight() > height) {
//            Matrix matrix = new Matrix();
//            matrix.setTranslate(xOffset,yOffset);
            Log.d(TAG, "Bitmap 宽：" + bitmap.getWidth() + ",高：" + bitmap.getHeight());
            Bitmap outbmp = Bitmap.createBitmap(bitmap, 0, yStart, bitmap.getWidth(), height);
           /* ByteArrayOutputStream baos = new ByteArrayOutputStream();
            outbmp.compress(Bitmap.CompressFormat.JPEG,80,baos);*/
            return outbmp;
        } else {
            T.showShort(CutImageActivity.this, "设定的高度太高");
            return bitmap;
        }
    }

    /**
     * 使用canvas的clipRect()方法进行裁剪
     * 说明 clip系列分为：clipRect(),clipPath()和clipRegion(Region region)
     * @see Canvas#clipRect  (参数Op为枚举类型，指定)
     * @param bitmap
     * @param yStart
     * @param height
     * @return
     */
    private Bitmap useCanvasCut(Bitmap bitmap, int yStart, int height) {
//        int width = 200;
        Drawable drawable = new BitmapDrawable(bitmap);
        // 输出用的Bitmap,这里的宽高需要是输出设定的宽和高
        Bitmap outBmp = Bitmap.createBitmap(bitmap.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBmp);
        Rect rect = new Rect(0, yStart, bitmap.getWidth(), height);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);

        // 先调用clip方法，再调用drawBitmap()方法，反之则无效
        canvas.clipRect(rect);
        canvas.save();
        // 这里偏移量指的是left和top的偏移
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.restore();
        drawable.draw(canvas);

        return outBmp;
    }

    /**
     * 绘制遮罩层选取新矩形Bitmap
     * 这里使用.9透明图设定裁剪形状
     * 遮罩层可以两种选择：透明 --SRC_IN  形状 --DST_IN
     *
     * @param bitmap
     * @return
     */
    private Bitmap useShadeBitmap2(Bitmap bitmap,int yStart, int height) {
//        int width = 100;
        Drawable mDrawable = new BitmapDrawable(bitmap);
        // 输出用的Bitmap,
        Bitmap outBmp = Bitmap.createBitmap(bitmap.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect rect = new Rect(0, yStart, bitmap.getWidth(), height);
        RectF rectF = new RectF(rect);
//        paint.setARGB(255,25,74,255);
        paint.setColor(Color.BLUE);
        canvas.drawRoundRect(rectF,20,20,paint);

        // 显示交叉区域
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.saveLayer(rectF, paint, Canvas.ALL_SAVE_FLAG);
        mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
//        canvas.drawBitmap(bitmap, rect, rect, paint);
        mDrawable.draw(canvas);
//        canvas.save();
        canvas.restore();
        return outBmp;
    }

}
