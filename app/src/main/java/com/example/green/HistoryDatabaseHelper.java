package com.example.green;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp HistoryDatabaseHelper chịu trách nhiệm quản lý
 * cơ sở dữ liệu SQLite lưu lại lịch sử phân tích của người dùng.
 *
 * Nó thực hiện các thao tác:
 *  - Tạo bảng lưu dữ liệu
 *  - Thêm bản ghi mới (insert)
 *  - Xóa bản ghi hoặc toàn bộ lịch sử
 *  - Truy xuất danh sách các lịch sử (getAllHistory)
 */
public class HistoryDatabaseHelper extends SQLiteOpenHelper {

    // ====== THÔNG TIN CƠ SỞ DỮ LIỆU ======
    private static final String DATABASE_NAME = "leafcare_history.db"; // Tên file DB
    private static final int DATABASE_VERSION = 1;                     // Phiên bản DB

    // ====== TÊN BẢNG & CỘT ======
    private static final String TABLE_HISTORY = "history";             // Tên bảng lưu lịch sử
    private static final String COLUMN_ID = "id";                      // ID (khóa chính)
    private static final String COLUMN_RESULT = "result";              // Chuỗi kết quả hiển thị
    private static final String COLUMN_IMAGE_PATH = "image_path";      // Đường dẫn ảnh
    private static final String COLUMN_TIMESTAMP = "timestamp";        // Thời gian lưu

    private final Context context;

    // ====== HÀM KHỞI TẠO ======
    public HistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // ====== TẠO BẢNG LẦN ĐẦU ======
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_HISTORY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // ID tự tăng
                COLUMN_RESULT + " TEXT, " +
                COLUMN_IMAGE_PATH + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT)";
        db.execSQL(sql);
    }

    // ====== XỬ LÝ KHI NÂNG CẤP PHIÊN BẢN DB ======
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    // ================== THÊM LỊCH SỬ MỚI ==================
    // Hàm thêm lịch sử có ảnh và kết quả — thời gian tự động lấy hệ thống
    public void insertHistory(String result, Bitmap image) {
        insertHistory(result, image, String.valueOf(System.currentTimeMillis()));
    }

    // Hàm thêm lịch sử có ảnh, kết quả và thời gian tùy chỉnh
    public void insertHistory(String result, Bitmap image, String timestamp) {
        // Tạo file lưu ảnh trong bộ nhớ trong
        String fileName = "history_" + System.currentTimeMillis() + ".png";
        File file = new File(context.getFilesDir(), fileName);

        // Ghi ảnh Bitmap ra file PNG
        try (FileOutputStream fos = new FileOutputStream(file)) {
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lưu thông tin vào cơ sở dữ liệu SQLite
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RESULT, result);
        values.put(COLUMN_IMAGE_PATH, file.getAbsolutePath());
        values.put(COLUMN_TIMESTAMP, timestamp);

        db.insert(TABLE_HISTORY, null, values);
    }

    // ================== LƯU ẢNH TẠM (KHÔNG LƯU DB) ==================
    public String saveTempImage(Bitmap image) {
        String fileName = "temp_" + System.currentTimeMillis() + ".png";
        File file = new File(context.getFilesDir(), fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath(); // Trả về đường dẫn ảnh
    }

    // ================== XÓA LỊCH SỬ ==================
    // Xóa một bản ghi cụ thể theo ID
    public void deleteHistory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Xóa toàn bộ lịch sử
    public void clearAllHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, null, null);
    }

    // ================== LẤY DANH SÁCH LỊCH SỬ ==================
    public List<HistoryItem> getAllHistory() {
        List<HistoryItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, null, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String result = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESULT));
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));

                // Mỗi dòng dữ liệu trong DB → tạo 1 đối tượng HistoryItem
                list.add(new HistoryItem(id, result, imagePath, timestamp));
            }
            cursor.close();
        }
        return list;
    }

    // ================== LẤY MỘT LỊCH SỬ THEO ID ==================
    public HistoryItem getHistoryById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        HistoryItem item = null;
        if (cursor != null && cursor.moveToFirst()) {
            String result = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESULT));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));

            // Tạo đối tượng HistoryItem từ dữ liệu trong DB
            item = new HistoryItem(id, result, imagePath, timestamp);
            cursor.close();
        }
        return item;
    }

    // ================== TẢI ẢNH TỪ ĐƯỜNG DẪN ==================
    public Bitmap loadImage(String path) {
        return BitmapFactory.decodeFile(path); // Chuyển file ảnh → Bitmap
    }
}
