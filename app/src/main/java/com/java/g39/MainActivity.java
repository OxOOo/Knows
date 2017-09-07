package com.java.g39;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.*;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Flowable.just("Hello RxJava").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("DEBUG", s);
            }
        });
    }
}
