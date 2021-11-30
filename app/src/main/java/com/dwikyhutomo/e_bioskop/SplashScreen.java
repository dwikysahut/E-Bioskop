package com.dwikyhutomo.e_bioskop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ProgressBar;

public class SplashScreen extends Activity {
    private int Value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        progressBar.setProgress(0); //Set Progress Dimulai Dari O
        // Handler untuk Updating data pada latar belakang
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // Menampung semua data yang ingin diproses oleh thread

                if (Value == progressBar.getMax()) {
                    startActivity(new Intent(SplashScreen.this, LandingPageActivity.class));
                    finish();
                }
                Value++;
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i <= progressBar.getMax(); i++) {
                        progressBar.setProgress(i);
                        handler.sendMessage(handler.obtainMessage());
                        Thread.sleep(20); // Waktu Pending 100ms/0.1 detik
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        };
        thread.start();
    }
}