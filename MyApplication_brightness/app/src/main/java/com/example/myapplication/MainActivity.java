package com.example.myapplication;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.TextView;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.view.KeyEvent;
import android.util.Log;

import java.util.List;

import static android.provider.Settings.System.getInt;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager manager;

    TextView textView;
    TextView textView2;
    TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // textview
//        TextView textView = findViewById(R.id.textView);
//        TextView textView2 = findViewById(R.id.textView2);
        textView = findViewById(R.id.textView);           // WORKAROUND Comment out
        textView2 = findViewById(R.id.textView2);         // WORKAROUND Comment out
        textView3 = findViewById(R.id.textView3);         // WORKAROUND Comment out

        manager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Windowの明るさ取得.
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        float value = lp.screenBrightness;
        // systemの明るさを取得.
        int sValue = 0;
        try {
            sValue = getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        // 変更前の輝度表示.
        String text1 = "start brightness : " + String.valueOf(value) + "(system : " + String.valueOf(sValue) + ")";
        textView.setText(text1);

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        // Listenerの登録解除
        manager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // Listenerの登録
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_LIGHT);
        if(sensors.size() > 0) {
            Sensor s = sensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
            }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // TODO Auto-genarated method stub
        String str = "";
        float sensorValue = 0.0f;
        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            sensorValue = event.values[0];
            str = "照度:" + event.values[0];
            textView3.setText(str);

            // 照度値よって明るさ値設定.
            float brightness = 0.1f;
            if(sensorValue < 100.0){
                // 町の街灯下：100.
                // これ以下を「暗い状況」とする.
                brightness = 1.0f;
            } else if (sensorValue > 1000.0){
                // 大体部屋の明るさが1000未満？.
                // なのでそれ以上は昼間の戸外などの明るい場所とする.
                brightness = 0.5f;
            } else {
                // 明るい部屋の中.
                brightness = 0.1f;
            }

            // Windowの明るさ設定.
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = brightness;
            getWindow().setAttributes(lp);

            // 変更後の輝度表示.
            // Windowの明るさ取得.
            float value = lp.screenBrightness;
            // systemの明るさを取得.
            int sValue = 0;
            try {
                sValue = getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            // 画面表示
            String text2 = "new brightness : " + String.valueOf(value) + "(system : " + String.valueOf(sValue) + ")";
            textView2.setText(text2);
        }
    }

    // 画面遷移のためのキーイベント処理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v("KeyDown", "KeyCode=" + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP)) {
            Intent intent = new Intent(getApplication(), GotoHomeActivity.class);
            startActivity(intent);
        }
        else if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            Intent intent = new Intent(getApplication(), SOSActivity.class);
            startActivity(intent);
        }
        return super.dispatchKeyEvent(event);
    }

/*  ボタン処理.
    public void onClick(View view) {
        // Windowの明るさ取得.
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        float value = lp.screenBrightness;
        // systemの明るさを取得.
        int sValue = 0;
        try {
            sValue = getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        // 変更前の輝度表示.
        String text1 = "old brightness : " + String.valueOf(value) + "(system : " + String.valueOf(sValue) + ")";
        textView.setText(text1);

        // ボタンによって明るさ値設定.
        float brightness = 0.1f;
        switch(view.getId()) {
            case R.id.button:
                brightness = 0.0f;
                break;
            case R.id.button2:
                brightness = 1.0f;
                break;
        }

        // Windowの明るさ設定.
        lp.screenBrightness = brightness;
        getWindow().setAttributes(lp);

        // 変更後の輝度表示.
        value = lp.screenBrightness;
        try {
            sValue = getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        String text2 = "new brightness : " + String.valueOf(value) + "(system : " + String.valueOf(sValue) + ")";
        textView2.setText(text2);

    }
*/

}
