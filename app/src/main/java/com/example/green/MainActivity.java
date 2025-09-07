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
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {
    
    private ImageView imagePreview;
    private TextView resultText;
    private LinearLayout resultLayout;
    private Button cameraButton;
    private Button galleryButton;
    private Button analyzeButton;
    private TextView loadingText;
    
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    
    private LeafCareAI leafCareAI;
    private Bitmap currentImage;

    private FrameLayout containerLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gắn layout chính
        setContentView(R.layout.activity_main);

        containerLayout = findViewById(R.id.containerLayout);
        // Initialize AI model
        leafCareAI = new LeafCareAI(this);
        
        // Initialize activity result launchers
        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getExtras() != null) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        if (photo != null) {
                            displayImage(photo);
                        }
                    }
                }
            }
        );
        
        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if (bitmap != null) {
                            displayImage(bitmap);
                        }
                    } catch (IOException e) {
                        Toast.makeText(this, "Lỗi khi mở ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
        toolBar();
        createHome();
    }

    private void toolBar() {
        LinearLayout toolbar = findViewById(R.id.customToolbar);
        LinearLayout btnHome = toolbar.findViewById(R.id.btnHome);
        LinearLayout btnHistory = toolbar.findViewById(R.id.btnHistory);

        btnHome.setOnClickListener(v -> createHome());
        btnHistory.setOnClickListener(v -> createHistory());
    }




    private void createHome() {
        containerLayout.removeAllViews();
        View homeView = getLayoutInflater().inflate(R.layout.layout_home, containerLayout, false);
        containerLayout.addView(homeView);

        // Ánh xạ các view từ homeView thay vì từ activity
        imagePreview = homeView.findViewById(R.id.imagePreview);
        loadingText = homeView.findViewById(R.id.loadingText);
        resultLayout = homeView.findViewById(R.id.resultLayout);
        resultText = homeView.findViewById(R.id.resultText);

        cameraButton = homeView.findViewById(R.id.cameraButton);
        galleryButton = homeView.findViewById(R.id.galleryButton);
        analyzeButton = homeView.findViewById(R.id.analyzeButton);

        // Gán sự kiện
        cameraButton.setOnClickListener(v -> openCamera());
        galleryButton.setOnClickListener(v -> openGallery());
        analyzeButton.setOnClickListener(v -> analyzeImage());
    }


    private void createHistory() {
        containerLayout.removeAllViews();
        View historyView = getLayoutInflater().inflate(R.layout.layout_history, containerLayout, false);
        containerLayout.addView(historyView);

        // Nếu trong layout_history.xml có các view khác, bạn ánh xạ từ historyView
        // Ví dụ:
        // TextView historyText = historyView.findViewById(R.id.historyText);
        // RecyclerView historyList = historyView.findViewById(R.id.historyList);
    }

    
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }
    
    private void openGallery() {
        galleryLauncher.launch("image/*");
    }
    
    private void displayImage(Bitmap bitmap) {
        currentImage = bitmap;
        imagePreview.setImageBitmap(bitmap);
        imagePreview.setVisibility(View.VISIBLE);
        analyzeButton.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
    }
    
    private void analyzeImage() {
        if (currentImage == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading
        loadingText.setVisibility(View.VISIBLE);
        analyzeButton.setVisibility(View.GONE);
        resultLayout.setVisibility(View.GONE);
        
        // Run AI analysis in background thread
        new Thread(() -> {
            final String result = leafCareAI.analyzeImage(currentImage);
            
            // Update UI on main thread
            runOnUiThread(() -> {
                loadingText.setVisibility(View.GONE);
                displayResult(result);
            });
        }).start();
    }
    
    private void displayResult(String result) {
        resultText.setText(result);
        
        if (result.contains("Bình thường")) {
            resultLayout.setBackgroundColor(0xFF4CAF50); // Green for healthy
            resultText.setTextColor(0xFFFFFFFF);
        } else {
            resultLayout.setBackgroundColor(0xFFF44336); // Red for disease
            resultText.setTextColor(0xFFFFFFFF);
        }
        
        resultLayout.setVisibility(View.VISIBLE);
        analyzeButton.setVisibility(View.VISIBLE);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (leafCareAI != null) {
            leafCareAI.release();
        }
    }
}
