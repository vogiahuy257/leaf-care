package com.example.green;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;

public class HomeFragment extends Fragment {

    private ImageView imagePreview;
    private TextView resultText, loadingText;
    private LinearLayout resultLayout;
    private Button cameraButton, galleryButton, analyzeButton;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    private LeafCareAI leafCareAI;   // Không tạo mới, mượn từ MainActivity
    private Bitmap currentImage;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            leafCareAI = ((MainActivity) context).getLeafCareAI();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_home, container, false);

        // Ánh xạ view
        imagePreview = view.findViewById(R.id.imagePreview);
        resultText = view.findViewById(R.id.resultText);
        loadingText = view.findViewById(R.id.loadingText);
        resultLayout = view.findViewById(R.id.resultLayout);
        cameraButton = view.findViewById(R.id.cameraButton);
        galleryButton = view.findViewById(R.id.galleryButton);
        analyzeButton = view.findViewById(R.id.analyzeButton);

        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getExtras() != null) {
                            Bitmap photo = (Bitmap) data.getExtras().get("data");
                            if (photo != null) displayImage(photo);
                        }
                    }
                }
        );

        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri)) {
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            if (bitmap != null) displayImage(bitmap);
                        } catch (IOException e) {
                            Toast.makeText(requireContext(), "Lỗi khi mở ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Sự kiện nút
        cameraButton.setOnClickListener(v -> openCamera());
        galleryButton.setOnClickListener(v -> openGallery());
        analyzeButton.setOnClickListener(v -> analyzeImage());

        return view;
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
            Toast.makeText(requireContext(), "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingText.setVisibility(View.VISIBLE);
        analyzeButton.setVisibility(View.GONE);
        resultLayout.setVisibility(View.GONE);

        new Thread(() -> {
            final String result = leafCareAI.analyzeImage(currentImage);
            requireActivity().runOnUiThread(() -> {
                loadingText.setVisibility(View.GONE);
                displayResult(result);
            });
        }).start();
    }

    private void displayResult(String result) {
        resultText.setText(result);
        if (result.contains("Bình thường")) {
            resultLayout.setBackgroundColor(0xFF4CAF50); // xanh lá
            resultText.setTextColor(0xFFFFFFFF);
        } else {
            resultLayout.setBackgroundColor(0xFFF44336); // đỏ
            resultText.setTextColor(0xFFFFFFFF);
        }
        resultLayout.setVisibility(View.VISIBLE);
        analyzeButton.setVisibility(View.VISIBLE);
    }
}
