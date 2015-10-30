package com.demo.list.testphotodemo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_CAPTURE_IMAGE = 1;// 照相的requestCode
    private static final int REQUEST_CODE_TAKE_VIDEO = 2;// 摄像的照相的requestCode
    private static final int RESULT_CAPTURE_RECORDER_SOUND = 3;// 录音的requestCode

    private String strImgPath = "";// 照片文件绝对路径
    private String strVideoPath = "";// 视频文件的绝对路径
    private String strRecorderPath = "";// 录音文件的绝对路径

    private GridView gView = null;
    private SimpleAdapter sAdapter;
    private List<Map<String,Object>> aList;
    private String[] demoStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        demoStr = new String[]{"拍照","录像","录音","图片裁剪"};
        aList = new ArrayList<>();
        getData();
        gView = (GridView)findViewById(R.id.demo_grid);
        String[] from = {"itemName"};
        int[] to = {R.id.item_text_grid};
        sAdapter =  new SimpleAdapter(this,aList,R.layout.grid_item_main,from,to);
        gView.setAdapter(sAdapter);
        gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        cameraMethod();
                        break;
                    case 1:
                        videoMethod();
                        break;
                    case 2:
                        soundRecorderMethod();
                        break;
                    case 3:
                        photoCutMethod();
                        break;
                }
            }
        });
    }

    private List<Map<String,Object>> getData(){
        for(int i=0;i<demoStr.length;i++ ){
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("itemName",demoStr[i]);
            aList.add(dataMap);
        }
        return aList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CAPTURE_IMAGE://拍照
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, strImgPath, Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_TAKE_VIDEO://拍摄视频
                if (resultCode == RESULT_OK) {
                    Uri uriVideo = data.getData();
                    Cursor cursor=this.getContentResolver().query(uriVideo, null, null, null, null);
                    if (cursor.moveToNext()) {
                        /** _data：文件的绝对路径 ，_display_name：文件名 */
                        strVideoPath = cursor.getString(cursor.getColumnIndex("_data"));
                        Toast.makeText(this, strVideoPath, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case RESULT_CAPTURE_RECORDER_SOUND://录音
                if (resultCode == RESULT_OK) {
                    Uri uriRecorder = data.getData();
                    Cursor cursor=this.getContentResolver().query(uriRecorder, null, null, null, null);
                    if (cursor.moveToNext()) {
                        /** _data：文件的绝对路径 ，_display_name：文件名 */
                        strRecorderPath = cursor.getString(cursor.getColumnIndex("_data"));
                        Toast.makeText(this, strRecorderPath, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }



    /**
     * 照相功能
     */
    private void cameraMethod() {
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        strImgPath = Environment.getExternalStorageDirectory().toString() + "/CONSDCGMPIC/";//存放照片的文件夹
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";//照片命名
        File out = new File(strImgPath);
        if (!out.exists()) {
            out.mkdirs();
        }
        out = new File(strImgPath, fileName);
        strImgPath = strImgPath + fileName;//该照片的绝对路径
        Uri uri = Uri.fromFile(out);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(imageCaptureIntent, RESULT_CAPTURE_IMAGE);

    }

    /**
     * 拍摄视频
     */
    private void videoMethod() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO);
    }

    /**
     * 录音功能
     */
    private void soundRecorderMethod() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/amr");
        startActivityForResult(intent, RESULT_CAPTURE_RECORDER_SOUND);
    }


    private void photoCutMethod(){
        Intent intent = new Intent(MainActivity.this,CutImageActivity.class);
        startActivity(intent);
    }

    /**
     * 提示信息
     * @param text
     * @param duration
     */
    private void showToast(String text, int duration) {
        Toast.makeText(MainActivity.this, text, duration).show();
    }

}
