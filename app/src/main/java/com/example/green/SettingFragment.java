package com.example.green;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class SettingFragment extends Fragment {

    private Switch switchLanguage, switchTheme;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_setting, container, false);

        switchLanguage = view.findViewById(R.id.switchLanguage);
        switchTheme = view.findViewById(R.id.switchTheme);
        prefs = requireContext().getSharedPreferences("AppSettings", getContext().MODE_PRIVATE);

        boolean isEnglish = prefs.getBoolean("english", false);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);

        switchLanguage.setChecked(isEnglish);
        switchTheme.setChecked(isDarkMode);

        switchLanguage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("english", isChecked).apply();
            if (isChecked) setLocale("en"); else setLocale("vi");
            requireActivity().recreate(); // reload UI
        });

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        });

        return view;
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}
