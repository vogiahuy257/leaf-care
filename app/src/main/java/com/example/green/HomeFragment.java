package com.example.green;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryFragment extends Fragment {

    // Thành phần giao diện hiển thị danh sách lịch sử
    private RecyclerView recyclerView;

    // Adapter hiển thị từng HistoryItem trong RecyclerView
    private HistoryAdapter adapter;

    // Trợ lý truy cập CSDL SQLite
    private HistoryDatabaseHelper dbHelper;

    // Danh sách các bản ghi lịch sử lấy từ database
    private List<HistoryItem> historyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Ánh xạ layout XML thành View thực tế (layout_history.xml)
        View view = inflater.inflate(R.layout.layout_history, container, false);

        // Gắn RecyclerView từ layout
        recyclerView = view.findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Khởi tạo database helper để truy xuất dữ liệu
        dbHelper = new HistoryDatabaseHelper(requireContext());

        // Lấy toàn bộ danh sách lịch sử từ database
        historyList = dbHelper.getAllHistory();

        // Khởi tạo adapter và gán vào RecyclerView
        adapter = new HistoryAdapter(requireContext(), historyList);
        recyclerView.setAdapter(adapter);

        // Gắn tính năng vuốt để xóa từng item
        attachSwipeToDelete();

        return view;
    }

    /**
     * Thiết lập hành vi vuốt sang trái để xóa một mục trong danh sách.
     */
    private void attachSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                0, // Không hỗ trợ kéo lên/xuống
                ItemTouchHelper.LEFT // Chỉ hỗ trợ vuốt sang trái để xóa
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                // Không cho phép kéo để sắp xếp lại
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Lấy vị trí item vừa bị vuốt
                int position = viewHolder.getAdapterPosition();

                // Lấy ID của item đó trong database
                int id = historyList.get(position).getId();

                // Xóa bản ghi trong database
                dbHelper.deleteHistory(id);

                // Xóa item khỏi danh sách hiện tại
                historyList.remove(position);

                // Cập nhật lại RecyclerView để ẩn item bị xóa
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                // Lấy view tương ứng với item
                View itemView = viewHolder.itemView;
                Paint paint = new Paint();

                if (dX < 0) { // Nếu vuốt sang trái
                    // Vẽ nền đỏ báo hiệu xóa
                    paint.setColor(Color.RED);
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), paint);

                    // Vẽ biểu tượng thùng rác
                    Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete);
                    if (icon != null) {
                        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        int iconTop = itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                        int iconRight = itemView.getRight() - iconMargin;

                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }
                }

                // Gọi phương thức gốc để tiếp tục xử lý hiệu ứng vuốt
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // Gắn ItemTouchHelper vào RecyclerView để kích hoạt hành vi vuốt
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }
}
