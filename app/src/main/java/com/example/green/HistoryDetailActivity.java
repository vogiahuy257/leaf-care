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

    private ImageView detailImage;
    private TextView detailResult, detailTimestamp, detailAdvice, detailTreatment;
    private FloatingActionButton btnExit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        // Ánh xạ view
        detailImage = findViewById(R.id.detailImage);
        detailResult = findViewById(R.id.detailResult);
        detailTimestamp = findViewById(R.id.detailTimestamp);
        detailAdvice = findViewById(R.id.detailAdvice);
        detailTreatment = findViewById(R.id.detailTreatment);
        btnExit = findViewById(R.id.btnExit);

        // Nút thoát: quay về màn trước
        btnExit.setOnClickListener(v -> onBackPressed());

        // Lấy dữ liệu từ Intent
        String result = getIntent().getStringExtra("result");
        String imagePath = getIntent().getStringExtra("imagePath");
        String timestamp = getIntent().getStringExtra("timestamp");

        // Fallback nếu chỉ có diseaseName
        if (result == null) {
            result = getIntent().getStringExtra("diseaseName");
        }

        // Hiển thị thời gian (nếu có)
        if (timestamp != null) {
            try {
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

        // Hiển thị ảnh (nếu có)
        if (imagePath != null) {
            HistoryDatabaseHelper dbHelper = new HistoryDatabaseHelper(this);
            Bitmap bitmap = dbHelper.loadImage(imagePath);
            if (bitmap != null) {
                detailImage.setImageBitmap(bitmap);
                detailImage.setVisibility(View.VISIBLE);
            } else {
                detailImage.setVisibility(View.GONE);
            }
        } else {
            detailImage.setVisibility(View.GONE);
        }

        // ===== XỬ LÝ KẾT QUẢ VÀ GỢI Ý (CHÍNH) =====
        if (result == null) {
            // Không có dữ liệu
            detailResult.setText("Không có dữ liệu");
            detailAdvice.setVisibility(View.GONE);
            detailTreatment.setVisibility(View.GONE);
            return;
        }

        // Có result: luôn hiển thị nhãn "Gợi ý xử lý"
        detailAdvice.setVisibility(View.VISIBLE);
        detailAdvice.setText("Gợi ý xử lý");

        // Chuẩn hoá chuỗi để so sánh an toàn (không phân biệt hoa thường & bỏ dấu)
        String lower = result.trim().toLowerCase(Locale.getDefault());
        String normalized = Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // loại dấu (diacritics)

        // Kiểm tra các từ khoá cả với chuỗi có dấu và không dấu
        if (lower.contains("bình thường") || normalized.contains("binh thuong")
                || lower.contains("khỏe mạnh") || normalized.contains("khoe manh")
                || normalized.contains("healthy") || lower.contains("healthy")) {

            detailResult.setText("Cây khỏe mạnh 🌱"); // tiêu đề
            detailTreatment.setText("✅ Lá cây bình thường, không cần xử lý.");
            detailTreatment.setVisibility(View.VISIBLE);

        } else if (lower.contains("đốm nâu") || normalized.contains("dom nau")) {

            detailResult.setText("Bệnh đốm nâu");
            detailTreatment.setText("⚠️ Hãy cắt bỏ và tiêu hủy lá bệnh, vệ sinh vườn sạch sẽ, bón phân cân đối, tăng cường Kali và Lân, đồng thời sử dụng thuốc sinh học hoặc hóa học như Difenoconazole, Azoxystrobin, Mancozeb khi cần thiết, đặc biệt vào mùa mưa hoặc khi triệu chứng mới xuất hiện. ");
            detailTreatment.setVisibility(View.VISIBLE);

        } else if (lower.contains("phấn trắng") || normalized.contains("phan trang")) {

            detailResult.setText("Bệnh phấn trắng");
            detailTreatment.setText("⚠️ Sử dụng các biện pháp như tỉa bỏ lá bệnh, phun thuốc diệt nấm hóa học (như Antracol, Score) hoặc các chế phẩm sinh học có chứa nấm đối kháng (Chaetomium, Trichoderma). Các biện pháp tại nhà như phun hỗn hợp baking soda, sữa hoặc dung dịch kali bicarbonate cũng giúp kiểm soát bệnh khi áp dụng đúng cách và định kỳ. ");
            detailTreatment.setVisibility(View.VISIBLE);

        } else {
            // Fallback: hiển thị nguyên result và thông báo chưa có xử lý
            detailResult.setText(result);
            detailTreatment.setText("ℹ️ Tham khảo chuyên gia nông nghiệp để xử lý.");
            detailTreatment.setVisibility(View.VISIBLE);
        }
        // ============================================
    }
}

