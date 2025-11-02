package com.example.green;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;

public class HomeFragment extends Fragment {

    private LinearLayout resultLayout;
    private MaterialButton cameraButton, galleryButton;
    private Bitmap currentImage;
    private LeafCareAI leafCareAI;
    private TextView analysisStatus;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_home, container, false);

        // Ánh xạ
        resultLayout = view.findViewById(R.id.resultLayout);
        cameraButton = view.findViewById(R.id.cameraButton);
        galleryButton = view.findViewById(R.id.galleryButton);
        analysisStatus = view.findViewById(R.id.analysisStatus);

        // Khởi tạo mô hình AI
        leafCareAI = new LeafCareAI(requireContext());

        // Nút Camera
        cameraButton.setOnClickListener(v -> openCamera());

        // Nút Gallery
        galleryButton.setOnClickListener(v -> openGallery());

        return view;
    }

    private void openCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = null;
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                currentImage = bitmap;
                // Hiển thị trạng thái phân tích
                analysisStatus.setVisibility(View.VISIBLE);
                resultLayout.setVisibility(View.GONE);

                // Chạy AI trên background thread
                new AnalyzeTask().execute(bitmap);
            }
        }
    }

    /**
     * AsyncTask để chạy AI mà không block UI
     */
    private class AnalyzeTask extends AsyncTask<Bitmap, Void, String> {
        private Bitmap bitmap;

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            bitmap = bitmaps[0];
            return leafCareAI.analyzeImage(bitmap);
        }

        @Override
        protected void onPostExecute(String aiResult) {
            analysisStatus.setVisibility(View.GONE); // ẩn trạng thái
            resultLayout.setVisibility(View.VISIBLE);

            boolean isHealthy = aiResult.contains("🌱");
            String diseaseType = aiResult
                    .replace("🌱 Bình thường - Lá cây khỏe mạnh!", "")
                    .replace("🔴 Phát hiện bệnh: ", "")
                    .replace("\nCần xử lý ngay!", "")
                    .trim();

            displayResult(bitmap, isHealthy, diseaseType, aiResult);
        }
    }

    /**
     * Hiển thị kết quả dưới dạng card động
     */
    public void displayResult(Bitmap bitmap, boolean isHealthy, String diseaseType, String aiResult) {
        View resultView = getLayoutInflater().inflate(R.layout.result_item, resultLayout, false);

        ImageView resultImage = resultView.findViewById(R.id.resultImage);
        TextView resultTitle = resultView.findViewById(R.id.resultTitle);
        TextView resultMessage = resultView.findViewById(R.id.resultMessage);
        MaterialButton suggestionButton = resultView.findViewById(R.id.suggestionButton);

        resultImage.setImageBitmap(bitmap);

        if (isHealthy) {
            resultTitle.setText("✅ Lá cây khỏe mạnh");
            resultMessage.setText("Chỉ cần chăm sóc bình thường thôi!");
            suggestionButton.setVisibility(View.GONE);
            ((MaterialCardView) resultView).setCardBackgroundColor(0xFFE8F5E9); // xanh nhạt
        } else {
            resultTitle.setText("⚠️ Phát hiện bệnh");
            resultMessage.setText(diseaseType);
            suggestionButton.setVisibility(View.VISIBLE);

            suggestionButton.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), HistoryDetailActivity.class);
                intent.putExtra("diseaseName", diseaseType);
                if (bitmap != null) {
                    HistoryDatabaseHelper dbHelper = new HistoryDatabaseHelper(requireContext());
                    String imagePath = dbHelper.saveTempImage(bitmap);
                    intent.putExtra("imagePath", imagePath);
                }
                intent.putExtra("timestamp", String.valueOf(System.currentTimeMillis()));
                startActivity(intent);
            });

            ((MaterialCardView) resultView).setCardBackgroundColor(0xFFFFEBEE); // đỏ nhạt
        }

        resultLayout.removeAllViews(); // xóa các kết quả cũ
        resultLayout.addView(resultView);

        // ================== Lưu lịch sử ==================
        HistoryDatabaseHelper dbHelper = new HistoryDatabaseHelper(requireContext());
        dbHelper.insertHistory(aiResult, bitmap); // <-- Lưu toàn bộ kết quả, kể cả lá khỏe mạnh
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (leafCareAI != null) {
            leafCareAI.release();
        }
    }
}
