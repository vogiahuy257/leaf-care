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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<HistoryItem> historyList;
    private HistoryDatabaseHelper dbHelper;

    public HistoryAdapter(Context context, List<HistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
        dbHelper = new HistoryDatabaseHelper(context);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);

        // Hiển thị resultText, luôn có tiêu đề
        String result = item.getResultText();
        if (result == null || result.trim().isEmpty()) {
            result = "Cây khỏe mạnh"; // mặc định nếu không có kết quả bệnh
        }
        holder.resultText.setText(result);

        // Format timestamp
        try {
            long timeMillis = Long.parseLong(item.getTimestamp());
            String formatted = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new Date(timeMillis));
            holder.timestampText.setText(formatted);
        } catch (Exception e) {
            holder.timestampText.setText(item.getTimestamp()); // fallback
        }

        // Load image thumbnail
        Bitmap bitmap = dbHelper.loadImage(item.getImagePath());
        if (bitmap != null) {
            holder.thumbnail.setImageBitmap(bitmap);
        }

        // Click mở chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HistoryDetailActivity.class);
            intent.putExtra("id", item.getId());
            intent.putExtra("result", item.getResultText());
            intent.putExtra("imagePath", item.getImagePath());
            intent.putExtra("timestamp", item.getTimestamp());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView resultText, timestampText;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.historyThumbnail);
            resultText = itemView.findViewById(R.id.historyResultText);
            timestampText = itemView.findViewById(R.id.historyTimestamp);
        }
    }
}
