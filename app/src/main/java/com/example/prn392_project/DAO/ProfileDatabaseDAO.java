package com.example.prn392_project.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.prn392_project.Models.ArtistProfile;
import com.example.prn392_project.db.DatabaseHelper;

public class ProfileDatabaseDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public ProfileDatabaseDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Lấy thông tin hồ sơ của nghệ sĩ bằng USER ID (không phải profile ID)
     */
    public ArtistProfile getProfileByUserId(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        ArtistProfile profile = null;

        try {
            String selection = DatabaseHelper.COLUMN_PROFILE_USER_ID + " = ?";
            String[] selectionArgs = { String.valueOf(userId) };

            cursor = db.query(
                    DatabaseHelper.TABLE_ARTIST_PROFILES,
                    null, // Lấy tất cả các cột
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                profile = cursorToProfile(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return profile; // Trả về null nếu không tìm thấy
    }

    /**
     * Cập nhật hồ sơ nghệ sĩ
     * @return số hàng bị ảnh hưởng (1 nếu thành công, 0 nếu thất bại)
     */
    public int updateProfile(ArtistProfile profile) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PROFILE_STAGE_NAME, profile.getStageName());
        values.put(DatabaseHelper.COLUMN_PROFILE_GENRES, profile.getGenres());
        values.put(DatabaseHelper.COLUMN_PROFILE_PRICE_PER_HOUR, profile.getPricePerHour());
        values.put(DatabaseHelper.COLUMN_PROFILE_LOCATION, profile.getLocation());
        values.put(DatabaseHelper.COLUMN_PROFILE_EXPERIENCE_YEARS, profile.getExperienceYears());
        values.put(DatabaseHelper.COLUMN_PROFILE_SOCIAL_LINKS, profile.getSocialLinks());

        // Cập nhật dựa trên user_id
        String selection = DatabaseHelper.COLUMN_PROFILE_USER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(profile.getUserId()) };

        int rowsAffected = 0;
        try {
            rowsAffected = db.update(
                    DatabaseHelper.TABLE_ARTIST_PROFILES,
                    values,
                    selection,
                    selectionArgs
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    /**
     * Hàm tiện ích để chuyển đổi Cursor sang đối tượng ArtistProfile
     */
    private ArtistProfile cursorToProfile(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_ID));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_USER_ID));
        String stageName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_STAGE_NAME));
        String genres = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_GENRES));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_PRICE_PER_HOUR));
        String location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_LOCATION));
        int exp = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_EXPERIENCE_YEARS));
        String social = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_SOCIAL_LINKS));
        double rating = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFILE_RATING_AVG));

        return new ArtistProfile(id, userId, stageName, genres, price, location, exp, social, rating);
    }
}
