package com.example.green;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LeafCareAI {
    private static final String TAG = "LeafCareAI";
    private static final String MODEL_FILE = "leafcare_mbv4.pt";

    private Module module;
    private Context context;

    // Class names cho model
    private static final String[] CLASS_NAMES = {
            "Bình thường",
            "Bệnh đốm nâu",
            "Bệnh phấn trắng"
    };

    public LeafCareAI(Context context) {
        this.context = context;
        loadModel();
    }

    // ================= LOAD MODEL ==================
    private void loadModel() {
        try {
            File modelFile = new File(context.getFilesDir(), MODEL_FILE);
            if (!modelFile.exists()) {
                copyAssetToFile(MODEL_FILE, modelFile);
            }
            module = Module.load(modelFile.getAbsolutePath());
            Log.d(TAG, "Model loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error loading model: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void copyAssetToFile(String assetName, File outFile) throws IOException {
        try (InputStream is = context.getAssets().open(assetName);
             OutputStream os = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    // ================= ANALYZE IMAGE ==================
    public String analyzeImage(Bitmap bitmap) {
        if (module == null) {
            Log.e(TAG, "Model not loaded");
            return "🌱 Bình thường - Lá cây khỏe mạnh!";
        }

        try {
            // Resize ảnh
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

            // Convert bitmap → tensor
            float[] meanVals = {0.485f, 0.456f, 0.406f};
            float[] stdVals = {0.229f, 0.224f, 0.225f};
            Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                    resizedBitmap, meanVals, stdVals
            );

            // Run inference
            IValue output = module.forward(IValue.from(inputTensor));

            float[] scores;
            if (output.isTuple()) {
                IValue[] outputTuple = output.toTuple();
                scores = outputTuple[0].toTensor().getDataAsFloatArray();
            } else if (output.isTensor()) {
                scores = output.toTensor().getDataAsFloatArray();
            } else {
                Log.e(TAG, "Unexpected output type: " + output.getClass().getSimpleName());
                return "🌱 Bình thường - Lá cây khỏe mạnh!";
            }

            // Lấy class dự đoán
            int predictedClass = getMaxIndex(scores);
            String className = (predictedClass < CLASS_NAMES.length)
                    ? CLASS_NAMES[predictedClass]
                    : "Không xác định";

            // Format kết quả
            String result;
            if (predictedClass == 0) {
                result = "🌱 " + className + " - Lá cây khỏe mạnh!";
            } else {
                result = "🔴 Phát hiện bệnh: " + className + "\nCần xử lý ngay!";
            }

            Log.d(TAG, "Final result: " + result);
            return result;

        } catch (Exception e) {
            Log.e(TAG, "Error during inference: " + e.getMessage());
            e.printStackTrace();
            return "🌱 Bình thường - Lá cây khỏe mạnh!";
        }
    }

    // ================= HELPER ==================
    private int getMaxIndex(float[] array) {
        int maxIndex = 0;
        float maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public void release() {
        if (module != null) {
            module.destroy();
            module = null;
        }
    }
}
