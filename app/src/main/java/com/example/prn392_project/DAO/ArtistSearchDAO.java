package com.example.prn392_project.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.prn392_project.Models.ArtistProfile;
import com.example.prn392_project.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ArtistSearchDAO {

    private DatabaseHelper dbHelper;

    public ArtistSearchDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Tìm kiếm nghệ sĩ dựa trên truy vấn (tên, thể loại, địa điểm)
     */
    public List<ArtistProfile> searchArtists(String query) {
        List<ArtistProfile> artists = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        String tableProfiles = DatabaseHelper.TABLE_ARTIST_PROFILES + " p";
        String tableUsers = DatabaseHelper.TABLE_USERS + " u";

        // Lấy tất cả cột từ profile, và tên từ user
        String sql = "SELECT p.*, u." + DatabaseHelper.COLUMN_USER_FULL_NAME +
                " FROM " + tableProfiles +
                " JOIN " + tableUsers + " ON p." + DatabaseHelper.COLUMN_PROFILE_USER_ID + " = u." + DatabaseHelper.COLUMN_USER_ID +
                " WHERE u." + DatabaseHelper.COLUMN_USER_ROLE + " = 'artist'";

        ArrayList<String> selectionArgs = new ArrayList<>();

        if (!TextUtils.isEmpty(query)) {
            sql += " AND (p." + DatabaseHelper.COLUMN_PROFILE_STAGE_NAME + " LIKE ?" +
                    " OR u." + DatabaseHelper.COLUMN_USER_FULL_NAME + " LIKE ?" +
                    " OR p." + DatabaseHelper.COLUMN_PROFILE_GENRES + " LIKE ?" +
                    " OR p." + DatabaseHelper.COLUMN_PROFILE_LOCATION + " LIKE ?)";
            String likeQuery = "%" + query + "%";
            selectionArgs.add(likeQuery);
            selectionArgs.add(likeQuery);
            selectionArgs.add(likeQuery);
            selectionArgs.add(likeQuery);
        }

        sql += " ORDER BY p." + DatabaseHelper.COLUMN_PROFILE_RATING_AVG + " DESC";

        try {
            cursor = db.rawQuery(sql, selectionArgs.toArray(new String[0]));

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    artists.add(cursorToProfile(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return artists;
    }

    /**
     * Hàm tiện ích để chuyển đổi Cursor sang đối tượng ArtistProfile
     * (Lưu ý: Không lấy full_name của user vì ArtistProfile model không có)
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
