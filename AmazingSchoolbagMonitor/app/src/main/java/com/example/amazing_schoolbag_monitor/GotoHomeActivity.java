package com.example.amazing_schoolbag_monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class GotoHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gotohome);
    }

    // 画面遷移のためのキーイベント処理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v("KeyDown", "KeyCode=" + keyCode);
//        return super.onKeyDown(keyCode, event);
        return true;
    }

    @Override
    public boolean onKeyUp( int keyCode, KeyEvent event ) {
        Log.v("KeyUp", "KeyCode=" + keyCode);
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
        }
        return true;
    }
/*
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP)) {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
        }
//        return super.dispatchKeyEvent(event);
        return true;
    }
*/
}
