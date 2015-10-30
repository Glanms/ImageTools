package com.demo.list.testphotodemo.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/10/29.
 * ToastUtils 用来提示
 */
public class T {

    /**
     *
     * @param context
     * @param msg
     */
    public static void showShort(Context context,String msg){
        Toast toast =  Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        toast.show();
    }
}
