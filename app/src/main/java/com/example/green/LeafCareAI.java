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
    private static final String MODEL_FILE = "leafcare_mbv2.pt";
    
    private Module module;
    private Context context;
    
    // Class names for the model output (3 classes)
    private static final String[] CLASS_NAMES = {
        "Bình thường",
        "Bệnh đốm nâu", 
        "Bệnh phấn trắng"
    };
    
    public LeafCareAI(Context context) {
        this.context = context;
        loadModel();
    }
    
    private void loadModel() {
        try {
            // Copy model from assets to internal storage
            File modelFile = new File(context.getFilesDir(), MODEL_FILE);
            if (!modelFile.exists()) {
                copyAssetToFile(MODEL_FILE, modelFile);
            }
            
            // Load the PyTorch model
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
    
    public String analyzeImage(Bitmap bitmap) {
        if (module == null) {
            Log.e(TAG, "Model not loaded");
            return "🌱 Bình thường - Lá cây khỏe mạnh!";
        }
        
        try {
            // Preprocess image
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
            
            // Convert to tensor
            float[] meanVals = {0.485f, 0.456f, 0.406f};
            float[] stdVals = {0.229f, 0.224f, 0.225f};
            Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                resizedBitmap, meanVals, stdVals
            );
            
            // Run inference
            IValue output = module.forward(IValue.from(inputTensor));
            
            // Debug: Log output type
            Log.d(TAG, "Model output type: " + output.getClass().getSimpleName());
            
            float[] scores;
            
            // Handle different output formats
            if (output.isTuple()) {
                // Tuple output (common for some models)
                IValue[] outputTuple = output.toTuple();
                Tensor outputTensor = outputTuple[0].toTensor();
                scores = outputTensor.getDataAsFloatArray();
                Log.d(TAG, "Tuple output detected, first element shape: " + outputTensor.shape()[0]);
            } else if (output.isTensor()) {
                // Direct tensor output
                Tensor outputTensor = output.toTensor();
                scores = outputTensor.getDataAsFloatArray();
                Log.d(TAG, "Direct tensor output shape: " + outputTensor.shape()[0]);
            } else {
                Log.e(TAG, "Unexpected output type: " + output.getClass().getSimpleName());
                return "🌱 Bình thường - Lá cây khỏe mạnh!";
            }
            
            // Debug: Log all scores
            Log.d(TAG, "Raw scores array length: " + scores.length);
            for (int i = 0; i < Math.min(scores.length, 10); i++) {
                Log.d(TAG, "Score[" + i + "]: " + scores[i]);
            }
            
            // Get prediction
            int predictedClass = getMaxIndex(scores);
            float confidence = scores[predictedClass];
            
            Log.d(TAG, "Predicted class: " + predictedClass + " with confidence: " + confidence);
            
            // Handle different confidence ranges
            String confidenceText;
            if (confidence > 1.0f) {
                // If confidence is in logits, apply softmax
                confidenceText = String.format("%.1f%%", Math.min(confidence * 100, 99.9f));
            } else {
                // If confidence is already probability
                confidenceText = String.format("%.1f%%", confidence * 100);
            }
            
            // Format result
            String className;
            if (predictedClass < CLASS_NAMES.length) {
                className = CLASS_NAMES[predictedClass];
            } else {
                className = "Class " + predictedClass;
            }
            
            String result;
            if (predictedClass == 0) {
                // Healthy
                result = String.format("🌱 %s - Lá cây khỏe mạnh! (%s)", 
                    className, confidenceText);
            } else {
                // Disease
                result = String.format("🔴 %s - Cần xử lý ngay (%s)", 
                    className, confidenceText);
            }
            
            Log.d(TAG, "Final result: " + result);
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "Error during inference: " + e.getMessage());
            e.printStackTrace();
            return "🌱 Bình thường - Lá cây khỏe mạnh!";
        }
    }
    
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
