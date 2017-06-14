package com.gpsonguard.listener;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.gpsonguard.screen.R;

public class CallerService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private View view;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onCreate();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                0,
                0,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
                // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.activity_main, null);
        TextView textview = (TextView) view.findViewById(R.id.text1);
        textview.setText("Someone is calling: " + intent.getStringExtra("incomingNumber"));
        textview.setBackgroundColor(Color.YELLOW);

        wm.addView(view, params);

        return START_STICKY;
    }

    public void onDestroy() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.removeView(view);
    }
}
