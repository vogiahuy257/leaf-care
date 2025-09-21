package com.example.green;

import android.content.Context;
// import android.content.SharedPreferences; // Removed
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
// import androidx.appcompat.widget.SwitchCompat; // Removed

import androidx.annotation.Nullable;
// import androidx.appcompat.app.AppCompatDelegate; // Removed

public class Setting extends LinearLayout {

    // Interface for theme change callback // Removed
    // public interface OnThemeChangedListener { // Removed
    // void onThemeChanged(); // Removed
    // } // Removed

    private ImageView btnClose;
    // private SwitchCompat themeSwitch; // Removed

    // private OnThemeChangedListener themeChangedListener; // Removed

    // private static final String PREFS_NAME = "ThemePrefs"; // Removed
    // private static final String KEY_THEME = "themeMode"; // Removed

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
        // themeSwitch = findViewById(R.id.themeSwitch); // Removed

        btnClose.setOnClickListener(v -> setVisibility(GONE));

        // Load saved theme state // Removed
        // SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); // Removed
        // Default to MODE_NIGHT_NO (Light mode) if nothing is saved or if system follow is too complex for a binary switch // Removed
        // int savedThemeMode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO); // Removed
        // themeSwitch.setChecked(savedThemeMode == AppCompatDelegate.MODE_NIGHT_YES); // Removed

        // themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> { // Removed
        // int selectedThemeMode; // Removed
        // if (isChecked) { // Removed
        // selectedThemeMode = AppCompatDelegate.MODE_NIGHT_YES; // Dark mode // Removed
        // } else { // Removed
        // selectedThemeMode = AppCompatDelegate.MODE_NIGHT_NO; // Light mode // Removed
        // } // Removed
        // AppCompatDelegate.setDefaultNightMode(selectedThemeMode); // Removed
        // Save selected theme // Removed
        // prefs.edit().putInt(KEY_THEME, selectedThemeMode).apply(); // Removed

        // Notify listener // Removed
        // if (themeChangedListener != null) { // Removed
        // themeChangedListener.onThemeChanged(); // Removed
        // } // Removed
        // }); // Removed
    }

    // public void setOnThemeChangedListener(OnThemeChangedListener listener) { // Removed
    // this.themeChangedListener = listener; // Removed
    // } // Removed

    /** Hiển thị panel */
    public void show() {
        // SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); // Removed
        // Default to MODE_NIGHT_NO if nothing is saved. // Removed
        // int savedThemeMode = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO); // Removed
        // themeSwitch.setChecked(savedThemeMode == AppCompatDelegate.MODE_NIGHT_YES); // Removed
        setVisibility(VISIBLE);
    }

    /** Ẩn panel */
    public void hide() {
        setVisibility(GONE);
    }
}
