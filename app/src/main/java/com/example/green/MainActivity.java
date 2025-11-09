package com.example.green;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnHome, btnHistory, btnSetting, chatButton, chatContent, bottomContainer;
    private boolean[] isChatOpen = {false};
    private View indicator;
    private LinearLayout containerChatBot;
    private LinearLayout settingPanel;
    private LeafCareAI leafCareAI;
    private FrameLayout loadingOverlay;

    private TextView tvHome, tvHistory, tvSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isEnglish = prefs.getBoolean("english", false);
        boolean isDark = prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        // Set language
        Locale locale = new Locale(isEnglish ? "en" : "vi");
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        // ✅ Test đặt nhắc nhở sau 10 giây (để dễ thấy)
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = SystemClock.elapsedRealtime() + 10 * 1000L; // 10 giây sau

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
        // cái nàyde963e debug
//        scheduleReminder(3); // Nhắc người dùng sau 3 ngày

        // Load AI model 1 lần
        leafCareAI = new LeafCareAI(this);

        // Ánh xạ các view
        btnHome = findViewById(R.id.btnHome);
        btnHistory = findViewById(R.id.btnHistory);
        btnSetting = findViewById(R.id.buttonSetting);
        chatButton = findViewById(R.id.chatButton);
        chatContent = findViewById(R.id.chatContent);
        indicator = findViewById(R.id.indicator);
        containerChatBot = findViewById(R.id.containerChatBot);
        bottomContainer = findViewById(R.id.bottomContainer);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        tvHome = findViewById(R.id.tvHome);
        tvHistory = findViewById(R.id.tvHistory);
        tvSetting = findViewById(R.id.tvSetting);

        // Fix Bottom Navigation tránh navigation bar
        bottomContainer.setOnApplyWindowInsetsListener((v, insets) -> {
            int paddingBottom = insets.getInsets(WindowInsets.Type.systemBars()).bottom;
            bottomContainer.setPadding(
                    bottomContainer.getPaddingLeft(),
                    bottomContainer.getPaddingTop(),
                    bottomContainer.getPaddingRight(),
                    paddingBottom
            );
            return insets;
        });

        // Load HomeFragment mặc định
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            indicator.post(() -> {
                ViewGroup.LayoutParams params = indicator.getLayoutParams();
                params.width = btnHome.getWidth();
                indicator.setLayoutParams(params);
                indicator.setX(btnHome.getX());
            });
            setActiveTab(tvHome);
        }

        // Menu click events
        btnHome.setOnClickListener(v -> {
            setActiveTab(tvHome);          // Đổi màu chữ ngay lập tức
            showLoading();
            moveIndicator(v, () -> {
                replaceFragment(new HomeFragment());
                hideLoading();
            });
        });

        btnHistory.setOnClickListener(v -> {
            setActiveTab(tvHistory);
            showLoading();
            moveIndicator(v, () -> {
                replaceFragment(new HistoryFragment());
                hideLoading();
            });
        });

        btnSetting.setOnClickListener(v -> {
            setActiveTab(tvSetting);
            showLoading();
            moveIndicator(v, () -> {
                replaceFragment(new SettingFragment());
                hideLoading();
            });
        });


        // ChatBot click
        chatButton.setOnClickListener(this::toggleChatBot);

    }

    /** Replace fragment ngay lập tức */
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containerLayout, fragment);
        transaction.commitNow(); // commit ngay để loading chạy song song
    }


    // Hàm đặt nhắc nhở
    private void scheduleReminder(int daysLater) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        long triggerTime = SystemClock.elapsedRealtime() + daysLater * 24 * 60 * 60 * 1000L; // X ngày sau

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
    }

    /** Move indicator dưới nút */
    private void moveIndicator(View target, Runnable onAnimationEnd) {
        float targetX = target.getX();
        int targetWidth = target.getWidth();
        int startWidth = indicator.getWidth();
        if (startWidth <= 0) startWidth = targetWidth;

        indicator.animate().x(targetX).setDuration(300).start();

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
                ViewGroup.LayoutParams params = indicator.getLayoutParams();
                params.width = targetWidth;
                indicator.setLayoutParams(params);
                if (onAnimationEnd != null) onAnimationEnd.run();
            }
        });
        animator.start();
    }

    /** Mở rộng / thu nhỏ ChatBot */
    private void toggleChatBot(View view) {
        showLoading();
        int startWidth = containerChatBot.getWidth();
        int startHeight = containerChatBot.getHeight();
        int endWidth = isChatOpen[0] ? chatButton.getWidth() : 600;
        int endHeight = isChatOpen[0] ? chatButton.getHeight() : 800;

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

        widthAnimator.addUpdateListener(anim -> {
            float fraction = anim.getAnimatedFraction();
            float radius = 35 * (1 - fraction) + 16 * fraction;
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.parseColor("#FFFFFF"));
            drawable.setCornerRadius(radius);
            containerChatBot.setBackground(drawable);
        });

        containerChatBot.setVisibility(View.VISIBLE);
        widthAnimator.setDuration(300);
        heightAnimator.setDuration(300);
        widthAnimator.start();
        heightAnimator.start();

        isChatOpen[0] = !isChatOpen[0];
        chatContent.setVisibility(isChatOpen[0] ? View.VISIBLE : View.GONE);

        containerChatBot.postDelayed(() -> {
            if (!isChatOpen[0]) {
                containerChatBot.setVisibility(View.GONE);
            }
            hideLoading();
        }, 300);
    }

    /** Show loading overlay */
    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    /** Hide loading overlay */
    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
    }

    /** Set active tab ngay lập tức */
    private void setActiveTab(TextView activeTv) {
        boolean isNightMode = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        int colorActive;
        int colorInactive;

        if (isNightMode) {
            // Màu sáng hơn khi ở chế độ tối
            colorActive = Color.parseColor("#FFFFFF");  // trắng
            colorInactive = Color.parseColor("#BBBBBB"); // xám nhạt
        } else {
            // Màu tối hơn khi ở chế độ sáng
            colorActive = Color.parseColor("#000000");  // đen
            colorInactive = Color.parseColor("#888888"); // xám
        }

        TextView[] tvs = {tvHome, tvHistory, tvSetting};
        for (TextView tv : tvs) {
            tv.setTextColor(tv == activeTv ? colorActive : colorInactive);
        }
    }


    /** Getter cho LeafCareAI */
    public LeafCareAI getLeafCareAI() {
        return leafCareAI;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (leafCareAI != null) {
            leafCareAI.release();
        }
    }
}
