package com.example.prn392_project.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.prn392_project.Models.ArtistAvailability;
import com.example.prn392_project.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityDatabaseDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public AvailabilityDatabaseDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Thêm một khung giờ rảnh mới
     */
    public boolean addAvailability(int artistId, String startTime, String endTime) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_AVAIL_ARTIST_ID, artistId);
        values.put(DatabaseHelper.COLUMN_AVAIL_START_TIME, startTime);
        values.put(DatabaseHelper.COLUMN_AVAIL_END_TIME, endTime);
        values.put(DatabaseHelper.COLUMN_AVAIL_IS_BOOKED, 0); // Mới tạo, chưa bị đặt

        long result = -1;
        try {
            result = db.insert(DatabaseHelper.TABLE_ARTIST_AVAILABILITIES, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result != -1;
    }

    /**
     * Lấy tất cả các lịch rảnh (chưa bị đặt) của một nghệ sĩ
     */
    public List<ArtistAvailability> getAvailabilitiesByArtist(int artistId) {
        List<ArtistAvailability> availabilities = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Sắp xếp theo thời gian bắt đầu để hiển thị
            String selection = DatabaseHelper.COLUMN_AVAIL_ARTIST_ID + " = ?";
            String[] selectionArgs = { String.valueOf(artistId) };
            String orderBy = DatabaseHelper.COLUMN_AVAIL_START_TIME + " ASC";

            cursor = db.query(
                    DatabaseHelper.TABLE_ARTIST_AVAILABILITIES,
                    null, // Lấy tất cả các cột
                    selection,
                    selectionArgs,
                    null, null, orderBy
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    availabilities.add(cursorToAvailability(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return availabilities;
    }

    /**
     * Xóa một lịch rảnh (chỉ khi nó chưa bị đặt)
     */
    public boolean deleteAvailability(int availabilityId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Chỉ xóa nếu is_booked = 0 (an toàn)
        String selection = DatabaseHelper.COLUMN_AVAIL_ID + " = ? AND " +
                DatabaseHelper.COLUMN_AVAIL_IS_BOOKED + " = 0";
        String[] selectionArgs = { String.valueOf(availabilityId) };

        int rowsAffected = 0;
        try {
            rowsAffected = db.delete(
                    DatabaseHelper.TABLE_ARTIST_AVAILABILITIES,
                    selection,
                    selectionArgs
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected > 0;
    }

    private ArtistAvailability cursorToAvailability(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AVAIL_ID));
        int artistId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AVAIL_ARTIST_ID));
        String startTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AVAIL_START_TIME));
        String endTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AVAIL_END_TIME));
        boolean isBooked = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AVAIL_IS_BOOKED)) == 1;

        return new ArtistAvailability(id, artistId, startTime, endTime, isBooked);
    }
}
