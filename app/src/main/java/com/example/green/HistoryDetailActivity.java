package com.example.green;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryDetailActivity extends AppCompatActivity {

    private ImageView detailImage;
    private TextView detailResult, detailTimestamp, detailAdvice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        detailImage = findViewById(R.id.detailImage);
        detailResult = findViewById(R.id.detailResult);
        detailTimestamp = findViewById(R.id.detailTimestamp);
        detailAdvice = findViewById(R.id.detailAdvice);

        // Nhận dữ liệu từ Intent
        String result = getIntent().getStringExtra("result");
        String imagePath = getIntent().getStringExtra("imagePath");
        String timestamp = getIntent().getStringExtra("timestamp");

        detailResult.setText(result);

        long timeMillis = Long.parseLong(timestamp);
        String formatted = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(timeMillis));
        detailTimestamp.setText("Thời gian: " + formatted);

        // Load ảnh
        HistoryDatabaseHelper dbHelper = new HistoryDatabaseHelper(this);
        Bitmap bitmap = dbHelper.loadImage(imagePath);
        if (bitmap != null) {
            detailImage.setImageBitmap(bitmap);
        }

        // Hiển thị hướng dẫn xử lý (demo, bạn có thể thay bằng data từ AI)
        if (result.contains("Bình thường")) {
            detailAdvice.setText("🌱 Cây khỏe mạnh, không cần xử lý.");
        } else if (result.contains("Đốm nâu")) {
            detailAdvice.setText("🔴 Bệnh đốm nâu: Cắt bỏ lá bị bệnh, hạn chế độ ẩm, có thể dùng thuốc gốc đồng.");
        } else if (result.contains("Bạc lá")) {
            detailAdvice.setText("⚠️ Bệnh bạc lá: Tăng cường thoát nước, tránh thừa đạm, có thể phun thuốc kháng khuẩn.");
        } else {
            detailAdvice.setText("ℹ️ Tham khảo chuyên gia nông nghiệp để xử lý.");
        }
    }
}
