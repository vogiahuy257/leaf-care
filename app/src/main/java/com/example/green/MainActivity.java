package com.example.green;

import android.animation.ValueAnimator;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnHome, btnHistory, chatButton,chatContent;
    private boolean[] isChatOpen = {false}; // dùng array để truy cập trong lambda

    private View indicator;

    private FrameLayout containerChatBot;

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
        chatButton = findViewById(R.id.chatButton);
        indicator = findViewById(R.id.indicator);
        containerChatBot = findViewById(R.id.containerChatBot);
        chatContent = findViewById(R.id.chatContent);

        chatButton.setOnClickListener(v -> {
            openChatBot(v);
        });

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

    private void  openChatBot(View target) {
        int startWidth = containerChatBot.getWidth();
        int startHeight = containerChatBot.getHeight();

        int endWidth, endHeight;

        if (!isChatOpen[0]) {
            // Mở rộng
            endWidth = 600;   // px
            endHeight = 800;  // px
        } else {
            // Thu nhỏ về nút
            endWidth = chatButton.getWidth() + 2;
            endHeight = chatButton.getHeight() + 2;
        }

        ValueAnimator widthAnimator = ValueAnimator.ofInt(startWidth, endWidth);
        widthAnimator.addUpdateListener(anim -> {
            int val = (int) anim.getAnimatedValue();
            ViewGroup.LayoutParams params = containerChatBot.getLayoutParams();
            params.width = val;
            containerChatBot.setLayoutParams(params);
        });

        ValueAnimator heightAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        heightAnimator.addUpdateListener(anim -> {
            int val = (int) anim.getAnimatedValue();
            ViewGroup.LayoutParams params = containerChatBot.getLayoutParams();
            params.height = val;
            containerChatBot.setLayoutParams(params);
        });

        widthAnimator.setDuration(300);
        heightAnimator.setDuration(300);

        containerChatBot.setVisibility(View.VISIBLE);

        // Animation tròn -> chữ nhật (đổi background)
        widthAnimator.addUpdateListener(anim -> {
            float fraction = anim.getAnimatedFraction();
            float radius = 35 * (1 - fraction) + 16 * fraction;
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.white));
            drawable.setCornerRadius(radius);
            containerChatBot.setBackground(drawable);
        });

        widthAnimator.start();
        heightAnimator.start();

        isChatOpen[0] = !isChatOpen[0];

        if (isChatOpen[0]) {
            chatContent.setVisibility(View.VISIBLE);
        } else {
            chatContent.setVisibility(View.GONE);
            containerChatBot.postDelayed(() -> containerChatBot.setVisibility(View.GONE), 300);
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
