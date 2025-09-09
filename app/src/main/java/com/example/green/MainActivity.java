package com.example.green;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnHome, btnHistory;
    private LeafCareAI leafCareAI; // AI model – load 1 lần, dùng chung cho toàn bộ Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load AI model 1 lần khi Activity tạo
        leafCareAI = new LeafCareAI(this);

        // Ánh xạ nút toolbar
        btnHome = findViewById(R.id.btnHome);
        btnHistory = findViewById(R.id.btnHistory);

        // Gán sự kiện click đổi Fragment
        btnHome.setOnClickListener(v -> replaceFragment(new HomeFragment()));
        btnHistory.setOnClickListener(v -> replaceFragment(new HistoryFragment()));

        // Load Home mặc định
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }
    }

    /**
     * Hàm thay Fragment hiển thị trong container
     */
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containerLayout, fragment);
        transaction.commit();
    }

    /**
     * Cho phép các Fragment lấy AI model đang được giữ trong MainActivity
     */
    public LeafCareAI getLeafCareAI() {
        return leafCareAI;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (leafCareAI != null) {
            leafCareAI.release(); // Giải phóng tài nguyên AI khi Activity bị hủy
        }
    }
}
