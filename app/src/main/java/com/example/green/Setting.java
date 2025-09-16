package com.example.green;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class Setting extends LinearLayout {

    private ImageView btnClose;

    public Setting(Context context) {
        super(context);
        init(context);
    }

    public Setting(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Setting(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_setting, this, true);
        btnClose = findViewById(R.id.btnCloseSetting);

        btnClose.setOnClickListener(v -> setVisibility(GONE));
    }

    /** Hiển thị panel */
    public void show() {
        setVisibility(VISIBLE);
    }

    /** Ẩn panel */
    public void hide() {
        setVisibility(GONE);
    }
}
