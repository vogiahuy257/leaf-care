package com.example.green;

/**
 * Lớp HistoryItem đại diện cho một mục lịch sử (history record)
 * lưu lại thông tin kết quả phân tích lá cây từ AI.
 * Mỗi đối tượng HistoryItem tương ứng với một lần người dùng quét ảnh lá.
 */
public class HistoryItem {
    // ====== THUỘC TÍNH ======
    private int id;              // Mã định danh duy nhất của bản ghi lịch sử
    private String resultText;   // Kết quả phân tích (ví dụ: "Phát hiện bệnh phấn trắng")
    private String imagePath;    // Đường dẫn đến ảnh được phân tích (lưu trong bộ nhớ)
    private String timestamp;    // Thời gian thực hiện phân tích (dạng chuỗi, ví dụ: "2025-10-05 14:32")

    // ====== HÀM KHỞI TẠO ======
    public HistoryItem(int id, String resultText, String imagePath, String timestamp) {
        this.id = id;
        this.resultText = resultText;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
    }

    // ====== CÁC HÀM GETTER ======
    // Trả về ID của bản ghi
    public int getId() { return id; }

    // Trả về kết quả chẩn đoán (chuỗi mô tả)
    public String getResultText() { return resultText; }

    // Trả về đường dẫn ảnh đã được quét
    public String getImagePath() { return imagePath; }

    // Trả về thời gian thực hiện quét
    public String getTimestamp() { return timestamp; }
}
