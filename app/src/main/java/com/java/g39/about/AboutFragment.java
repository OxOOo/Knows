package com.java.g39.about;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.java.g39.R;

/**
 * Created by 岳 on 2017/9/12.
 */

public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ((TextView)view.findViewById(R.id.text_url)).setMovementMethod(LinkMovementMethod.getInstance());

        view.findViewById(R.id.button_version).setOnClickListener((View v) -> {
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setTitle("您已经是最新版")
                    .setMessage("当前版本：V1.0.0")
                    .setPositiveButton("确定", null).create();
            dialog.show();
        });
        return view;
    }
}
