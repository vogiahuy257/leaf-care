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
import java.util.Arrays;

/**
 * Lớp LeafCareAI chịu trách nhiệm:
 * - Tải mô hình PyTorch (.pt)
 * - Xử lý ảnh lá cây (crop, resize, normalize)
 * - Chạy suy luận (inference)
 * - Trả về kết quả chẩn đoán bệnh lá cây
 */
public class LeafCareAI {
    private static final String TAG = "LeafCareAI";
    private static final String MODEL_FILE = "leafcare_mbv4.pt";

    private Module module;
    private Context context;

    // Danh sách các lớp (nhãn) mà mô hình có thể dự đoán
    private static final String[] CLASS_NAMES = {
            "Bình thường",
            "Bệnh phấn trắng",
            "Bệnh đốm nâu"
    };

    // ================== HÀM KHỞI TẠO ==================
    public LeafCareAI(Context context) {
        this.context = context;
        loadModel();    // Gọi hàm tải mô hình khi tạo đối tượng
    }

    // ================== TẢI MÔ HÌNH TỪ ASSETS ==================
    private void loadModel() {
        try {
            // Tạo file tạm để lưu mô hình trong bộ nhớ trong (internal storage)
            File modelFile = new File(context.getFilesDir(), MODEL_FILE);

            // Nếu file chưa tồn tại → copy từ assets vào
            if (!modelFile.exists()) {
                copyAssetToFile(MODEL_FILE, modelFile);
            }

            // Load mô hình PyTorch
            module = Module.load(modelFile.getAbsolutePath());
            Log.d(TAG, "Model loaded successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error loading model: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Hàm hỗ trợ: sao chép file mô hình từ thư mục assets sang thư mục nội bộ
     */
    private void copyAssetToFile(String assetName, File outFile) throws IOException {
        try (InputStream is = context.getAssets().open(assetName);
             OutputStream os = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024];
            int length;
            // Đọc từng phần nhỏ của file rồi ghi vào outFile
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    // ================== PHÂN TÍCH HÌNH ẢNH ==================
    public String analyzeImage(Bitmap bitmap) {
        // Kiểm tra mô hình đã load chưa
        if (module == null) {
            Log.e(TAG, "Model not loaded");
            return "🌱 Bình thường - Lá cây khỏe mạnh!";
        }

        try {
            // === 1. Đảm bảo ảnh là định dạng RGB ===
            Bitmap rgbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            // === 2. Cắt ảnh vuông (crop ở giữa) để tránh méo hình ===
            int width = rgbBitmap.getWidth();
            int height = rgbBitmap.getHeight();
            int newSize = Math.min(width, height); // Lấy cạnh nhỏ hơn
            Bitmap croppedBitmap = Bitmap.createBitmap(rgbBitmap,
                    (width - newSize) / 2,    // Cắt từ giữa
                    (height - newSize) / 2,
                    newSize,
                    newSize
            );

            // === 3. Resize ảnh về kích thước 224x224 (chuẩn của MobileNetV3) ===
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(croppedBitmap, 224, 224, true);

            // === 4. Chuyển ảnh thành tensor để đưa vào mô hình ===
            float[] meanVals = {0.485f, 0.456f, 0.406f}; // Giá trị trung bình chuẩn ImageNet
            float[] stdVals = {0.229f, 0.224f, 0.225f};  // Độ lệch chuẩn chuẩn hóa
            Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, meanVals, stdVals);

            // === 5. Chạy suy luận với mô hình PyTorch ===
            IValue output = module.forward(IValue.from(inputTensor));

            float[] scores;
            // Một số mô hình PyTorch trả về tuple thay vì tensor đơn
            if (output.isTuple()) {
                IValue[] outputTuple = output.toTuple();
                scores = outputTuple[0].toTensor().getDataAsFloatArray();
            } else if (output.isTensor()) {
                scores = output.toTensor().getDataAsFloatArray();
            } else {
                Log.e(TAG, "Unexpected output type: " + output.getClass().getSimpleName());
                return "🌱 Bình thường - Lá cây khỏe mạnh!";
            }

            // === 6. In ra log để kiểm tra điểm dự đoán ===
            Log.d(TAG, "Scores: " + Arrays.toString(scores));

            // === 7. Xác định class có điểm cao nhất ===
            int predictedClass = getMaxIndex(scores);

            // Lấy tên lớp tương ứng
            String className = (predictedClass < CLASS_NAMES.length)
                    ? CLASS_NAMES[predictedClass]
                    : "Không xác định";

            // === 8. Trả về kết quả hiển thị cho người dùng ===
            String result;
            if (predictedClass == 0) {
                // Lớp 0 → lá bình thường
                result = "🌱 " + className + " - Lá cây khỏe mạnh!";
            } else {
                // Các lớp khác → có bệnh
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

    // ================== HÀM PHỤ TRỢ: LẤY CHỈ SỐ MAX ==================
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

    // ================== GIẢI PHÓNG BỘ NHỚ ==================
    public void release() {
        if (module != null) {
            module.destroy(); // Giải phóng tài nguyên của PyTorch Module
            module = null;
        }
    }
}
