package com.example.green;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView; // Thêm import này
import android.widget.ArrayAdapter; // Thêm import này
import android.widget.Spinner;      // Thêm import này
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class SettingFragment extends Fragment {

    // Đổi switchLanguage thành spinnerLanguage
    private Spinner spinnerLanguage;
    private Switch switchTheme;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_setting, container, false);

        // Ánh xạ Spinner và Switch
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage);
        switchTheme = view.findViewById(R.id.switchTheme);
        prefs = requireContext().getSharedPreferences("AppSettings", getContext().MODE_PRIVATE);

        // Xử lý Chế độ tối (Theme) - Giữ nguyên logic cũ
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        switchTheme.setChecked(isDarkMode);
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        });

        // --- LOGIC MỚI CHO SPINNER NGÔN NGỮ ---

        // 1. Tạo Adapter để kết nối danh sách ngôn ngữ (từ strings.xml) với Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.languages_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        // 2. Đặt giá trị ban đầu cho Spinner dựa trên lựa chọn đã lưu
        boolean isEnglish = prefs.getBoolean("english", false);
        if (isEnglish) {
            spinnerLanguage.setSelection(1); // Vị trí 1 là "English"
        } else {
            spinnerLanguage.setSelection(0); // Vị trí 0 là "Tiếng Việt"
        }

        // 3. Đặt Listener để xử lý khi người dùng chọn ngôn ngữ mới
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy trạng thái ngôn ngữ đang được lưu
                boolean currentIsEnglish = prefs.getBoolean("english", false);
                boolean newIsEnglish = (position == 1); // 0 = Tiếng Việt, 1 = English

                // Chỉ thực hiện đổi ngôn ngữ VÀ TẢI LẠI (recreate)
                // nếu người dùng chọn một ngôn ngữ MỚI (khác với cái đã lưu)
                if (newIsEnglish != currentIsEnglish) {
                    prefs.edit().putBoolean("english", newIsEnglish).apply();
                    if (newIsEnglish) {
                        setLocale("en");
                    } else {
                        setLocale("vi");
                    }
                    requireActivity().recreate(); // Tải lại UI để áp dụng ngôn ngữ
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần làm gì
            }
        });
        
        return view;
    }

    // Hàm setLocale giữ nguyên
    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }
}