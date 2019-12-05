package com.example.amazing_schoolbag_monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.Transport;
import javax.mail.MessagingException;
import java.util.Properties;
import java.util.Calendar;

public class SOSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        // Windowの明るさ設定.
        // SOS中は目立つように輝度をMAXにする.
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);

        // 時刻取得.
        Calendar calendar = Calendar.getInstance();
        // 時間(24時間単位).
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // 分.
        int min = calendar.get(Calendar.MINUTE);

        // メール送信処理
        String subject = "[SOS] ゆうたくん　大ピンチ！！";
        String maintext = hour + "時" + min + "分 ゆうたくんから断続的に激しい衝撃を検知しました。";
        String from_addr = "schoolbag.is.money@gmail.com";
        String from_pw = "money1459toyohashischoolbag6459";
        String to_addr = "schoolbag.is.money@gmail.com";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        sendGmail(subject, maintext, from_addr, from_pw, to_addr);
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
    // メール送信処理
    protected static void sendGmail(String subject,String mainText, String from_addr, String from_pw, String to_addr) {

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com"); // SMTPサーバ名
        props.put("mail.host", "smtp.gmail.com");      // 接続するホスト名
        props.put("mail.smtp.port", "587");       // SMTPサーバポート
        props.put("mail.smtp.auth", "true");    // smtp auth
        props.put("mail.smtp.starttls.enable", "true"); // STTLS

        // セッション
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);

        MimeMessage msg = new MimeMessage(session);

        try {
            msg.setSubject(subject, "UTF-8");
            msg.setFrom(new InternetAddress(from_addr)); //Fromアドレス
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to_addr)); //送信先アドレス（Gmailじゃなくてもよい)
            MimeMultipart mp = new MimeMultipart();

            // text mail 本文の設定
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(mainText, "UTF-8", "plain");
            textBodyPart.setHeader("Content-Transfer-Encoding", "base64");
            mp.addBodyPart(textBodyPart);
            msg.setContent(mp);

            //Gmailアカウント設定
            Transport t = session.getTransport("smtp");
            t.connect(from_addr, from_pw); //Gmailアカウント設定
//            t.sendMessage(msg, msg.getAllRecipients());       // セキュリティの観点から一時的に無効化
            t.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
