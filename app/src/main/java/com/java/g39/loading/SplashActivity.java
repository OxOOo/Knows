package com.java.g39.loading;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.java.g39.R;
import com.java.g39.data.Manager;
import com.java.g39.main.MainActivity;

import io.reactivex.functions.Consumer;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        long start = System.currentTimeMillis();
        Manager.I.waitForInit()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        System.out.println("waitForInit | " + (System.currentTimeMillis() - start));

                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    }
                });
    }
}
