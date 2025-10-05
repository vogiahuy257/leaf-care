package com.example.green;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter dùng để hiển thị danh sách các HistoryItem trong RecyclerView.
 * Mỗi item thể hiện: hình ảnh, kết quả nhận diện và thời gian quét.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    // Ngữ cảnh (dùng để inflate layout hoặc start activity)
    private Context context;

    // Danh sách các bản ghi lịch sử lấy từ database
    private List<HistoryItem> historyList;

    // Trợ lý truy cập database (để load hình ảnh từ đường dẫn)
    private HistoryDatabaseHelper dbHelper;

    // Constructor khởi tạo adapter
    public HistoryAdapter(Context context, List<HistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
        dbHelper = new HistoryDatabaseHelper(context);
    }

    // Tạo view cho từng item trong danh sách (gọi khi RecyclerView cần tạo ViewHolder mới)
    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout XML thành View thực tế (item_history.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    // Gắn dữ liệu thực tế vào từng item trong danh sách
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        // Lấy ra đối tượng HistoryItem tương ứng vị trí hiện tại
        HistoryItem item = historyList.get(position);

        // Hiển thị nội dung kết quả nhận diện (nếu rỗng thì ghi "Cây khỏe mạnh")
        String result = item.getResultText();
        if (result == null || result.trim().isEmpty()) {
            result = "Cây khỏe mạnh"; // Mặc định khi không có kết quả bệnh
        }
        holder.resultText.setText(result);

        // Định dạng lại timestamp (đổi từ mili-giây → ngày/giờ dễ đọc)
        try {
            long timeMillis = Long.parseLong(item.getTimestamp());
            String formatted = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new Date(timeMillis));
            holder.timestampText.setText(formatted);
        } catch (Exception e) {
            // Nếu lỗi, hiển thị nguyên chuỗi timestamp ban đầu
            holder.timestampText.setText(item.getTimestamp());
        }

        // Load ảnh thumbnail từ đường dẫn (path) trong database
        Bitmap bitmap = dbHelper.loadImage(item.getImagePath());
        if (bitmap != null) {
            holder.thumbnail.setImageBitmap(bitmap);
        }

        // Khi người dùng bấm vào item → mở trang chi tiết (HistoryDetailActivity)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HistoryDetailActivity.class);
            intent.putExtra("id", item.getId());
            intent.putExtra("result", item.getResultText());
            intent.putExtra("imagePath", item.getImagePath());
            intent.putExtra("timestamp", item.getTimestamp());
            context.startActivity(intent);
        });
    }

    // Trả về tổng số item trong danh sách
    @Override
    public int getItemCount() {
        return historyList.size();
    }

    /**
     * ViewHolder: lớp con đại diện cho từng item trong danh sách RecyclerView.
     * Giữ tham chiếu đến các View (ImageView, TextView) để tránh tìm lại nhiều lần.
     */
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;        // Hình ảnh thumbnail của lá cây
        TextView resultText;        // Kết quả nhận diện (bệnh hoặc khỏe mạnh)
        TextView timestampText;     // Thời gian quét

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.historyThumbnail);
            resultText = itemView.findViewById(R.id.historyResultText);
            timestampText = itemView.findViewById(R.id.historyTimestamp);
        }
    }
}
