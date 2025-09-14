package com.example.green;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
        View indicator = findViewById(R.id.indicator);

        btnHome.setOnClickListener(v -> {
            replaceFragment(new HomeFragment());
            moveIndicator(v);
        });

        btnHistory.setOnClickListener(v -> {
            replaceFragment(new HistoryFragment());
            moveIndicator(v);
        });

        // Load Home mặc định
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());

            indicator.post(() -> {
                // khởi tạo width bằng width target Home
                ViewGroup.LayoutParams params = indicator.getLayoutParams();
                params.width = btnHome.getWidth();
                indicator.setLayoutParams(params);

                // di chuyển ngay dưới Home
                indicator.setX(btnHome.getX());
            });
        }
    }

    private void moveIndicator(View target) {
        View indicator = findViewById(R.id.indicator);

        // Vị trí và width target
        float targetX = target.getX();
        int targetWidth = target.getWidth();

        // Lấy width bắt đầu (fallback)
        int startWidth = indicator.getWidth();
        if (startWidth <= 0) {
            startWidth = targetWidth;
        }

        // Animate X
        indicator.animate()
                .x(targetX)
                .setDuration(300)
                .start();

        // Animate width
        ValueAnimator animator = ValueAnimator.ofInt(startWidth, targetWidth);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            int newWidth = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = indicator.getLayoutParams();
            params.width = newWidth;
            indicator.setLayoutParams(params);
        });
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // Lưu width cuối cùng
                ViewGroup.LayoutParams params = indicator.getLayoutParams();
                params.width = targetWidth;
                indicator.setLayoutParams(params);
            }
        });
        animator.start();
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
