package com.example.green;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    // Các view trong giao diện
    private ImageView imagePreview;   // Hiển thị ảnh chọn/chụp
    private TextView resultText;      // Hiển thị kết quả phân tích
    private LinearLayout resultLayout;// Layout chứa kết quả
    private Button cameraButton;      // Nút mở camera
    private Button galleryButton;     // Nút mở thư viện
    private Button analyzeButton;     // Nút phân tích ảnh
    private TextView loadingText;     // Text hiển thị "đang phân tích..."

    // Launcher cho camera và thư viện
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    // AI phân tích lá
    private LeafCareAI leafCareAI;
    private Bitmap currentImage; // Ảnh hiện tại người dùng chọn

    // Container để load layout động (home, history)
    private FrameLayout containerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gắn layout chính từ activity_main.xml
        setContentView(R.layout.activity_main);

        // Lấy FrameLayout chính để load view con
        containerLayout = findViewById(R.id.containerLayout);

        // Khởi tạo AI
        leafCareAI = new LeafCareAI(this);

        // Launcher mở camera
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            if (photo != null) {
                                displayImage(photo); // Hiển thị ảnh vừa chụp
                            }
                        }
                    }
                }
        );

        // Launcher mở thư viện
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            if (bitmap != null) {
                                displayImage(bitmap); // Hiển thị ảnh chọn từ thư viện
                            }
                        } catch (IOException e) {
                            Toast.makeText(this, "Lỗi khi mở ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Khởi tạo thanh menu
        toolBar();

        // Load trang Home mặc định
        createHome();
    }

    // Thiết lập toolbar và gán sự kiện cho Home + History
    private void toolBar() {
        LinearLayout toolbar = findViewById(R.id.customToolbar);
        LinearLayout btnHome = toolbar.findViewById(R.id.btnHome);
        LinearLayout btnHistory = toolbar.findViewById(R.id.btnHistory);

        // Khi click Home -> load trang Home
        btnHome.setOnClickListener(v -> createHome());

        // Khi click History -> load trang History
        btnHistory.setOnClickListener(v -> createHistory());
    }

    // Khởi tạo trang Home
    private void createHome() {
        containerLayout.removeAllViews(); // Xóa view cũ
        View homeView = getLayoutInflater().inflate(R.layout.layout_home, containerLayout, false);
        containerLayout.addView(homeView); // Thêm view mới

        // Ánh xạ các view trong layout_home
        imagePreview = homeView.findViewById(R.id.imagePreview);
        loadingText = homeView.findViewById(R.id.loadingText);
        resultLayout = homeView.findViewById(R.id.resultLayout);
        resultText = homeView.findViewById(R.id.resultText);

        cameraButton = homeView.findViewById(R.id.cameraButton);
        galleryButton = homeView.findViewById(R.id.galleryButton);
        analyzeButton = homeView.findViewById(R.id.analyzeButton);

        // Gán sự kiện cho các nút
        cameraButton.setOnClickListener(v -> openCamera());
        galleryButton.setOnClickListener(v -> openGallery());
        analyzeButton.setOnClickListener(v -> analyzeImage());
    }

    // Khởi tạo trang History
    private void createHistory() {
        containerLayout.removeAllViews(); // Xóa view cũ
        View historyView = getLayoutInflater().inflate(R.layout.layout_history, containerLayout, false);
        containerLayout.addView(historyView); // Load layout_history

        // TODO: ánh xạ các view trong layout_history nếu có
    }

    // Hàm mở camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    // Hàm mở thư viện ảnh
    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    // Hiển thị ảnh sau khi chọn/chụp
    private void displayImage(Bitmap bitmap) {
        currentImage = bitmap;
        imagePreview.setImageBitmap(bitmap);
        imagePreview.setVisibility(View.VISIBLE);
        analyzeButton.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
    }

    // Phân tích ảnh bằng AI
    private void analyzeImage() {
        if (currentImage == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiện loading, ẩn kết quả cũ
        loadingText.setVisibility(View.VISIBLE);
        analyzeButton.setVisibility(View.GONE);
        resultLayout.setVisibility(View.GONE);

        // Chạy AI trong luồng nền (không làm treo giao diện)
        new Thread(() -> {
            final String result = leafCareAI.analyzeImage(currentImage);

            // Sau khi xong -> cập nhật giao diện trong UI thread
            runOnUiThread(() -> {
                loadingText.setVisibility(View.GONE);
                displayResult(result);
            });
        }).start();
    }

    // Hiển thị kết quả phân tích
    private void displayResult(String result) {
        resultText.setText(result);

        if (result.contains("Bình thường")) {
            resultLayout.setBackgroundColor(0xFF4CAF50); // Xanh lá = bình thường
            resultText.setTextColor(0xFFFFFFFF);
        } else {
            resultLayout.setBackgroundColor(0xFFF44336); // Đỏ = có bệnh
            resultText.setTextColor(0xFFFFFFFF);
        }

        resultLayout.setVisibility(View.VISIBLE);
        analyzeButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (leafCareAI != null) {
            leafCareAI.release(); // Giải phóng tài nguyên AI khi thoát
        }
    }
}
