package com.example.green;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
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
        
        createUI();
    }
    
    private void createUI() {
        // Main container
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setBackgroundColor(0xFF2E7D32); // Green background
        mainLayout.setPadding(32, 64, 32, 64);
        
        // Title
        TextView titleText = new TextView(this);
        titleText.setText("🌿 LeafCare AI");
        titleText.setTextSize(32);
        titleText.setTextColor(0xFFFFFFFF);
        titleText.setGravity(Gravity.CENTER);
        titleText.setPadding(0, 0, 0, 32);
        
        // Subtitle
        TextView subtitleText = new TextView(this);
        subtitleText.setText("AI phân tích bệnh lá cây chính xác");
        subtitleText.setTextSize(16);
        subtitleText.setTextColor(0xFFE8F5E8);
        subtitleText.setGravity(Gravity.CENTER);
        subtitleText.setPadding(0, 0, 0, 48);
        
        // Image preview
        imagePreview = new ImageView(this);
        imagePreview.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 
            400
        ));
        imagePreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imagePreview.setBackgroundColor(0xFF1B5E20);
        imagePreview.setVisibility(View.GONE);
        
        // Loading text
        loadingText = new TextView(this);
        loadingText.setText("🤖 AI đang phân tích...");
        loadingText.setTextSize(18);
        loadingText.setTextColor(0xFFFFEB3B);
        loadingText.setGravity(Gravity.CENTER);
        loadingText.setPadding(16, 16, 16, 16);
        loadingText.setVisibility(View.GONE);
        
        // Result layout
        resultLayout = new LinearLayout(this);
        resultLayout.setOrientation(LinearLayout.VERTICAL);
        resultLayout.setGravity(Gravity.CENTER);
        resultLayout.setPadding(16, 16, 16, 16);
        resultLayout.setVisibility(View.GONE);
        
        resultText = new TextView(this);
        resultText.setTextSize(20);
        resultText.setGravity(Gravity.CENTER);
        resultText.setPadding(16, 16, 16, 16);
        
        resultLayout.addView(resultText);
        
        // Buttons container
        LinearLayout buttonContainer = new LinearLayout(this);
        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
        buttonContainer.setGravity(Gravity.CENTER);
        buttonContainer.setPadding(0, 32, 0, 0);
        
        // Camera button
        cameraButton = new Button(this);
        cameraButton.setText("📷 Chụp ảnh");
        cameraButton.setTextSize(16);
        cameraButton.setBackgroundColor(0xFF4CAF50);
        cameraButton.setTextColor(0xFFFFFFFF);
        cameraButton.setPadding(24, 16, 24, 16);
        cameraButton.setOnClickListener(v -> openCamera());
        
        // Gallery button
        galleryButton = new Button(this);
        galleryButton.setText("🖼️ Chọn ảnh");
        galleryButton.setTextSize(16);
        galleryButton.setBackgroundColor(0xFF2196F3);
        galleryButton.setTextColor(0xFFFFFFFF);
        galleryButton.setPadding(24, 16, 24, 16);
        galleryButton.setOnClickListener(v -> openGallery());
        
        // Analyze button
        analyzeButton = new Button(this);
        analyzeButton.setText("🔍 AI Phân tích");
        analyzeButton.setTextSize(16);
        analyzeButton.setBackgroundColor(0xFFFF9800);
        analyzeButton.setTextColor(0xFFFFFFFF);
        analyzeButton.setPadding(24, 16, 24, 16);
        analyzeButton.setVisibility(View.GONE);
        analyzeButton.setOnClickListener(v -> analyzeImage());
        
        // Add buttons to container
        buttonContainer.addView(cameraButton);
        buttonContainer.addView(galleryButton);
        buttonContainer.addView(analyzeButton);
        
        // Add all views to main layout
        mainLayout.addView(titleText);
        mainLayout.addView(subtitleText);
        mainLayout.addView(imagePreview);
        mainLayout.addView(loadingText);
        mainLayout.addView(resultLayout);
        mainLayout.addView(buttonContainer);
        
        setContentView(mainLayout);
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
