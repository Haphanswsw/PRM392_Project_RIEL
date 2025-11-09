package com.example.prn392_project.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.prn392_project.Models.User;
import com.example.prn392_project.db.DatabaseHelper;
import com.example.prn392_project.utils.PasswordUtils;

public class UserDatabaseDAO {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public UserDatabaseDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Kiểm tra xem email đã tồn tại hay chưa
     */
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            String selection = DatabaseHelper.COLUMN_USER_EMAIL + " = ?";
            String[] selectionArgs = { email };

            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    new String[]{DatabaseHelper.COLUMN_USER_ID}, // Chỉ cần kiểm tra sự tồn tại
                    selection,
                    selectionArgs,
                    null, null, null
            );

            return cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Không đóng db ở đây nếu bạn dùng chung dbHelper
        }
    }

    /**
     * Đăng ký người dùng mới
     * @return ID của người dùng mới, hoặc -1 nếu lỗi (ví dụ: email tồn tại)
     */
    public long registerUser(String email, String password, String fullName, String phone, String role) {
        if (checkUserExists(email)) {
            return -1; // Email đã tồn tại
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); // Bắt đầu Transaction

        try {
            String hashedPassword = PasswordUtils.hashPassword(password);
            if (hashedPassword == null) {
                return -1; // Lỗi băm mật khẩu
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USER_EMAIL, email);
            values.put(DatabaseHelper.COLUMN_USER_PASSWORD_HASH, hashedPassword);
            values.put(DatabaseHelper.COLUMN_USER_FULL_NAME, fullName);
            values.put(DatabaseHelper.COLUMN_USER_PHONE, phone);
            values.put(DatabaseHelper.COLUMN_USER_ROLE, role); // 'customer' hoặc 'artist'

            // 1. Thêm vào bảng users
            long newUserId = db.insert(DatabaseHelper.TABLE_USERS, null, values);

            // 2. NẾU LÀ NGHỆ SĨ: Tự động tạo một hồ sơ trống cho họ
            if (role.equals("artist") && newUserId != -1) {
                ContentValues profileValues = new ContentValues();
                profileValues.put(DatabaseHelper.COLUMN_PROFILE_USER_ID, newUserId);
                // Bạn có thể điền các giá trị mặc định khác nếu muốn
                // profileValues.put(DatabaseHelper.COLUMN_PROFILE_STAGE_NAME, fullName);

                db.insert(DatabaseHelper.TABLE_ARTIST_PROFILES, null, profileValues);
            }

            db.setTransactionSuccessful(); // Đánh dấu transaction thành công
            return newUserId;

        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Lỗi
        } finally {
            db.endTransaction(); // Kết thúc transaction
        }
    }

    /**
     * Đăng nhập người dùng
     * @return Đối tượng User nếu thành công, null nếu thất bại
     */
    public User loginUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            String hashedPassword = PasswordUtils.hashPassword(password);
            if (hashedPassword == null) {
                return null;
            }

            String selection = DatabaseHelper.COLUMN_USER_EMAIL + " = ? AND " +
                    DatabaseHelper.COLUMN_USER_PASSWORD_HASH + " = ?";
            String[] selectionArgs = { email, hashedPassword };

            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    new String[]{ // Các cột bạn muốn lấy
                            DatabaseHelper.COLUMN_USER_ID,
                            DatabaseHelper.COLUMN_USER_FULL_NAME,
                            DatabaseHelper.COLUMN_USER_ROLE
                    },
                    selection,
                    selectionArgs,
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                // Lấy dữ liệu từ Cursor
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_FULL_NAME));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ROLE));

                // Trả về đối tượng User
                return new User(id, email, fullName, role);
            }

            return null; // Đăng nhập thất bại (không tìm thấy user hoặc sai pass)

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
