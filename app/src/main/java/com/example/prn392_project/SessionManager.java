package com.example.prn392_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.prn392_project.Models.User;

public class SessionManager {
    private static final String PREF_NAME = "ArtistBookingSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_ROLE = "userRole";
    public static final String KEY_USER_EMAIL = "userEmail";
    public static final String KEY_USER_FULL_NAME = "userFullName";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Tạo session khi đăng nhập thành công
     */
    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_ROLE, user.getRole());
        editor.putString(KEY_USER_FULL_NAME, user.getFullName());
        editor.commit();
    }

    /**
     * Lấy ID của người dùng đã đăng nhập
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1); // -1 là giá trị mặc định nếu không tìm thấy
    }

    /**
     * Lấy thông tin người dùng (ví dụ: tên)
     */
    public String getUserFullName() {
        return pref.getString(KEY_USER_FULL_NAME, "Artist");
    }

    /**
     * Kiểm tra xem người dùng đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Xử lý đăng xuất
     */
    public void logoutUser() {
        // Xóa tất cả dữ liệu session
        editor.clear();
        editor.commit();

        // Chuyển hướng về LoginActivity
        Intent i = new Intent(context, LoginActivity.class);
        // Thêm flags để xóa tất cả Activity cũ
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    /**
     * Kiểm tra đăng nhập, nếu chưa thì chuyển về Login
     */
    public void checkLogin() {
        if (!this.isLoggedIn()) {
            // Người dùng chưa đăng nhập, chuyển về Login
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
