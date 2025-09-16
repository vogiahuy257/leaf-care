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

public class HistoryDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "leafcare_history.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_HISTORY = "history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_RESULT = "result";
    private static final String COLUMN_IMAGE_PATH = "image_path";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private Context context;

    public HistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_HISTORY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RESULT + " TEXT, " +
                COLUMN_IMAGE_PATH + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public void insertHistory(String result, Bitmap image) {
        String fileName = "history_" + System.currentTimeMillis() + ".png";
        File file = new File(context.getFilesDir(), fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String timestamp = String.valueOf(System.currentTimeMillis());

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RESULT, result);
        values.put(COLUMN_IMAGE_PATH, file.getAbsolutePath());
        values.put(COLUMN_TIMESTAMP, timestamp);

        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    // Xóa 1 record trong database theo ID
    public void deleteHistory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

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

                list.add(new HistoryItem(id, result, imagePath, timestamp));
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    public Bitmap loadImage(String path) {
        return BitmapFactory.decodeFile(path);
    }
}
