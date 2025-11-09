package com.example.prn392_project.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.prn392_project.Models.Review;
import com.example.prn392_project.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ReviewDatabaseDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public ReviewDatabaseDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Lấy điểm đánh giá trung bình của nghệ sĩ từ hồ sơ
     */
    public double getAverageRating(int artistUserId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        double avgRating = 0.0;

        try {
            String selection = DatabaseHelper.COLUMN_PROFILE_USER_ID + " = ?";
            String[] selectionArgs = { String.valueOf(artistUserId) };
            String[] columns = { DatabaseHelper.COLUMN_PROFILE_RATING_AVG };

            cursor = db.query(
                    DatabaseHelper.TABLE_ARTIST_PROFILES,
                    columns,
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                avgRating = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_RATING_AVG));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return avgRating;
    }

    /**
     * Lấy danh sách tất cả các đánh giá cho một nghệ sĩ
     * (JOIN với bảng user để lấy tên customer)
     */
    public List<Review> getReviewsForArtist(int artistId) {
        List<Review> reviews = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        // Tên bảng
        String tableReviews = DatabaseHelper.TABLE_REVIEWS + " r";
        String tableUsers = DatabaseHelper.TABLE_USERS + " u";

        // Cột cần lấy
        String customerNameCol = "customer_name"; // Tên cột tạm thời

        String query = "SELECT r.*, u." + DatabaseHelper.COLUMN_USER_FULL_NAME + " AS " + customerNameCol +
                " FROM " + tableReviews +
                " LEFT JOIN " + tableUsers + " ON r." + DatabaseHelper.COLUMN_REVIEW_CUSTOMER_ID + " = u." + DatabaseHelper.COLUMN_USER_ID +
                " WHERE r." + DatabaseHelper.COLUMN_REVIEW_ARTIST_ID + " = ?" +
                " ORDER BY r." + DatabaseHelper.COLUMN_REVIEW_CREATED_AT + " DESC";

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(artistId)});

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    reviews.add(cursorToReview(cursor, customerNameCol));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return reviews;
    }

    private Review cursorToReview(Cursor cursor, String customerNameCol) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_ID));
        int rating = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_RATING));
        String comment = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_COMMENT));
        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REVIEW_CREATED_AT));

        String customerName = "Khách hàng ẩn danh";
        int nameIndex = cursor.getColumnIndex(customerNameCol);
        if (nameIndex != -1 && !cursor.isNull(nameIndex)) {
            customerName = cursor.getString(nameIndex);
        }

        return new Review(id, rating, comment, createdAt, customerName);
    }
}
