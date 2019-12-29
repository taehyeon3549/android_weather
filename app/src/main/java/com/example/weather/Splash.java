package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView iv = (ImageView)findViewById(R.id.imageView);
        Glide.with(this).load(R.drawable.weathersplash_short).into(iv);
        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(), 7000);         //3초 후에 handler 실행
    }

    private class splashHandler implements Runnable{
        @Override
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class));
            Splash.this.finish();
        }
    }

    /** 초반 에 뒤로가기 버튼 못누르게 함 **/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
