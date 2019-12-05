package com.example.amazing_schoolbag_monitor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.Intent;
import android.view.KeyEvent;
import android.util.Log;

import java.util.List;

import static android.provider.Settings.System.getInt;
import static java.lang.Math.abs;

public class MainActivity extends Activity implements SensorEventListener {

    private float[] currentOrientationValues = { 0.0f, 0.0f, 0.0f };
    private float[] currentAccelerationValues = { 0.0f, 0.0f, 0.0f };
    private float[][] recentlyAccelerationValues = new float[100][3];
    private int recentlyCount = 0;
    private float avarageAccelerationValuesX = 0;
    private float avarageAccelerationValuesY = 0;
    private float avarageAccelerationValuesZ = 0;
    private boolean initialFlg = false;

    private SensorManager manager;

    private static final boolean DEBUG = false;
    TextView textView;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // オブジェクト取得。
        ImageView _randomImage = findViewById(R.id.imageView);

        // 画像リスト取得。
        TypedArray typedArray = getResources().obtainTypedArray(R.array.list_random_image);

        // Arrayの要素数のランダムな値生成。
        int rand = (int) Math.floor(Math.random() * (typedArray.length()));

        // ランダムな画像を選択。
        Drawable drawable = typedArray.getDrawable(rand);

        // 選択した画像を表示。
        _randomImage.setImageDrawable(drawable);

        if (DEBUG) {
            // textview
            textView = findViewById(R.id.textView);           // WORKAROUND Comment out
            textView2 = findViewById(R.id.textView2);         // WORKAROUND Comment out
        }
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
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
        List<Sensor> sensors2 = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensors2.size() > 0) {
            Sensor s = sensors2.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onSensorChanged(SensorEvent event) {

        // TODO Auto-genarated method stub
        switch (event.sensor.getType()) {
            // 照度センサー
            case Sensor.TYPE_LIGHT:
                float sensorValue;
                sensorValue = event.values[0];
                if (DEBUG) {
                    // 照度値を表示.
                    String str;
                    str = "照度:" + event.values[0];
                    textView2.setText(str);
                }

                // 照度値よって明るさ値設定.
                float brightness;
                if (sensorValue < 100.0) {
                    // 町の街灯下：100.
                    // これ以下を「暗い状況」とする.
                    brightness = 1.0f;
                } else if (sensorValue > 1000.0) {
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

                if (DEBUG) {
                    // Windowの明るさ取得.
                    float value = lp.screenBrightness;
                    // systemの明るさを取得.
                    int sValue = 0;
                    try {
                        sValue = getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }

                    // 変更後の輝度表示.
                    String text2 = "new brightness : " + value + "(system : " + sValue + ")";
                    textView.setText(text2);
                }

            // 加速度センサー
            case Sensor.TYPE_ACCELEROMETER:
                currentAccelerationValues[0] = event.values[0];
                currentAccelerationValues[1] = event.values[1];
                currentAccelerationValues[2] = event.values[2];

                // 30 msecごとに収集している加速度データ直近100回分(3 sec)を保持
                recentlyAccelerationValues[recentlyCount][0] = currentAccelerationValues[0];
                recentlyAccelerationValues[recentlyCount][1] = currentAccelerationValues[1];
                recentlyAccelerationValues[recentlyCount][2] = currentAccelerationValues[2];
                if (recentlyCount > 98) {
                    recentlyCount = 0;
                    initialFlg = true;
                } else {
                    recentlyCount++;
                }

                // 直近100回（3,000 [msec]）の平均値を算出
                avarageAccelerationValuesX = 0;
                avarageAccelerationValuesY = 0;
                avarageAccelerationValuesZ = 0;
                for(int i = 0; i < 100; i++) {
                    avarageAccelerationValuesX += recentlyAccelerationValues[i][0];
                    avarageAccelerationValuesY += recentlyAccelerationValues[i][1];
                    avarageAccelerationValuesZ += recentlyAccelerationValues[i][2];
                }
                avarageAccelerationValuesX /= 100;
                avarageAccelerationValuesY /= 100;
                avarageAccelerationValuesZ /= 100;
                // 加速度情報ログ出力
                Log.v("currentAccelerationValues[0]","currentAccelerationValues[0]=" + currentAccelerationValues[0]);
                Log.v("currentAccelerationValues[1]","currentAccelerationValues[1]=" + currentAccelerationValues[1]);
                Log.v("currentAccelerationValues[2]","currentAccelerationValues[2]=" + currentAccelerationValues[2]);
                Log.v("avarageAccelerationValuesX","avarageAccelerationValuesX=" + avarageAccelerationValuesX);
                Log.v("avarageAccelerationValuesY","avarageAccelerationValuesY=" + avarageAccelerationValuesY);
                Log.v("avarageAccelerationValuesZ","avarageAccelerationValuesZ=" + avarageAccelerationValuesZ);

                // 衝撃判定
                // タブレットが水平の状態で一定時間経過したきに状態遷移
                if (initialFlg == true
                && abs(avarageAccelerationValuesX) > 9
                && abs(avarageAccelerationValuesY) < 3) {
                    // 状態遷移した瞬間の加速度情報ログ出力
                    Log.v("avarageAccelerationValuesX","avarageAccelerationValuesX=" + avarageAccelerationValuesX);
                    Log.v("avarageAccelerationValuesY","avarageAccelerationValuesY=" + avarageAccelerationValuesY);
                    Log.v("avarageAccelerationValuesZ","avarageAccelerationValuesZ=" + avarageAccelerationValuesZ);
                    Intent intent = new Intent(getApplication(), SOSActivity.class);
                    startActivity(intent);
                }

            default:
        }
    }

    private boolean flag = false;
    // 画面遷移のためのキーイベント処理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v("KeyDown", "KeyCode=" + keyCode);
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            flag = true;
        }
       // return super.onKeyDown(keyCode, event);
        return true;
    }

    @Override
    public boolean onKeyUp( int keyCode, KeyEvent event ) {
        Log.v("KeyUp", "KeyCode=" + keyCode);
        if ((!flag) && (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP)) {
            Intent intent = new Intent(getApplication(), GotoHomeActivity.class);
            startActivity(intent);
        }
        if ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) ||
                (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)){
            flag = false;
            Intent intent = new Intent(getApplication(), SOSActivity.class);
            startActivity(intent);
        }
        return true;
    }
/*
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
//        return super.dispatchKeyEvent(event);
        return true;
    }
*/

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
