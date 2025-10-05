package com.example.green;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryDetailActivity extends AppCompatActivity {

    // 🔹 Khai báo các thành phần giao diện (UI)
    private ImageView detailImage;
    private TextView detailResult, detailTimestamp, detailAdvice, detailTreatment;
    private FloatingActionButton btnExit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail); // Gắn layout giao diện cho Activity

        // 🔹 Ánh xạ (kết nối) các view từ layout XML vào code Java
        detailImage = findViewById(R.id.detailImage);
        detailResult = findViewById(R.id.detailResult);
        detailTimestamp = findViewById(R.id.detailTimestamp);
        detailAdvice = findViewById(R.id.detailAdvice);
        detailTreatment = findViewById(R.id.detailTreatment);
        btnExit = findViewById(R.id.btnExit);

        // 🔹 Xử lý nút thoát → quay lại màn hình trước
        btnExit.setOnClickListener(v -> onBackPressed());

        // 🔹 Lấy dữ liệu được truyền sang từ HistoryAdapter (qua Intent)
        String result = getIntent().getStringExtra("result");
        String imagePath = getIntent().getStringExtra("imagePath");
        String timestamp = getIntent().getStringExtra("timestamp");

        // 🔹 Nếu không có "result", thử lấy "diseaseName" (trường hợp fallback)
        if (result == null) {
            result = getIntent().getStringExtra("diseaseName");
        }

        // 🔹 Hiển thị thời gian (nếu có)
        if (timestamp != null) {
            try {
                // Chuyển timestamp (milliseconds) sang dạng ngày giờ dd/MM/yyyy HH:mm
                long timeMillis = Long.parseLong(timestamp);
                String formatted = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(new Date(timeMillis));
                detailTimestamp.setText("⏰ " + formatted);
                detailTimestamp.setVisibility(View.VISIBLE);
            } catch (NumberFormatException e) {
                detailTimestamp.setText("⏰ Không xác định");
                detailTimestamp.setVisibility(View.VISIBLE);
            }
        } else {
            detailTimestamp.setVisibility(View.GONE);
        }

        // 🔹 Hiển thị hình ảnh của kết quả (nếu có)
        if (imagePath != null) {
            HistoryDatabaseHelper dbHelper = new HistoryDatabaseHelper(this);
            Bitmap bitmap = dbHelper.loadImage(imagePath); // Tải ảnh từ đường dẫn lưu trong database
            if (bitmap != null) {
                detailImage.setImageBitmap(bitmap);
                detailImage.setVisibility(View.VISIBLE);
            } else {
                detailImage.setVisibility(View.GONE);
            }
        } else {
            detailImage.setVisibility(View.GONE);
        }

        // 🔹 Nếu không có dữ liệu kết quả → ẩn các phần gợi ý
        if (result == null) {
            detailResult.setText("Không có dữ liệu");
            detailAdvice.setVisibility(View.GONE);
            detailTreatment.setVisibility(View.GONE);
            return;
        }

        // 🔹 Nếu có kết quả → hiển thị phần “Gợi ý xử lý”
        detailAdvice.setVisibility(View.VISIBLE);
        detailAdvice.setText("Gợi ý xử lý");

        // 🔹 Chuẩn hóa chuỗi (bỏ dấu và chuyển thường) để dễ so sánh
        String lower = result.trim().toLowerCase(Locale.getDefault());
        String normalized = Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // loại bỏ dấu tiếng Việt

        // 🔹 Phân tích kết quả và đưa ra gợi ý xử lý tương ứng
        if (lower.contains("bình thường") || normalized.contains("binh thuong")
                || lower.contains("khỏe mạnh") || normalized.contains("khoe manh")
                || normalized.contains("healthy") || lower.contains("healthy")) {

            // ➤ Trường hợp cây khỏe mạnh
            detailResult.setText("Cây khỏe mạnh 🌱");
            detailTreatment.setText("✅ Lá cây bình thường, không cần xử lý.");
            detailTreatment.setVisibility(View.VISIBLE);

        } else if (lower.contains("đốm nâu") || normalized.contains("dom nau")) {

            // ➤ Trường hợp bệnh đốm nâu
            detailResult.setText("Bệnh đốm nâu");
            detailTreatment.setText("⚠️ Hãy cắt bỏ và tiêu hủy lá bệnh, vệ sinh vườn sạch sẽ, "
                    + "bón phân cân đối, tăng cường Kali và Lân, đồng thời sử dụng thuốc sinh học "
                    + "hoặc hóa học như Difenoconazole, Azoxystrobin, Mancozeb khi cần thiết, "
                    + "đặc biệt vào mùa mưa hoặc khi triệu chứng mới xuất hiện.");
            detailTreatment.setVisibility(View.VISIBLE);

        } else if (lower.contains("phấn trắng") || normalized.contains("phan trang")) {

            // ➤ Trường hợp bệnh phấn trắng
            detailResult.setText("Bệnh phấn trắng");
            detailTreatment.setText("⚠️ Sử dụng các biện pháp như tỉa bỏ lá bệnh, phun thuốc "
                    + "diệt nấm hóa học (như Antracol, Score) hoặc các chế phẩm sinh học có chứa "
                    + "nấm đối kháng (Chaetomium, Trichoderma). Ngoài ra, phun dung dịch baking soda "
                    + "hoặc sữa cũng giúp kiểm soát bệnh nếu làm đúng cách.");
            detailTreatment.setVisibility(View.VISIBLE);

        } else {
            // ➤ Các bệnh khác chưa có gợi ý cụ thể
            detailResult.setText(result);
            detailTreatment.setText("ℹ️ Tham khảo chuyên gia nông nghiệp để xử lý.");
            detailTreatment.setVisibility(View.VISIBLE);
        }
    }
}
