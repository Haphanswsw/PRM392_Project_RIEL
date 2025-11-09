package com.example.prn392_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prn392_project.DAO.UserDatabaseDAO;
import com.example.prn392_project.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class CustomerProfileActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etFullName, etPhone, etBio, etAvatarUrl;
    private Button btnUpdateProfile, btnLogout;
    private BottomNavigationView bottomNavigation;

    private UserDatabaseDAO userDAO;
    private SessionManager sessionManager;

    private int customerId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        // 1. Khởi tạo
        sessionManager = new SessionManager(getApplicationContext());
        userDAO = new UserDatabaseDAO(this);
        sessionManager.checkLogin();
        customerId = sessionManager.getUserId();

        // 2. Ánh xạ Views
        initViews();

        // 3. Cài đặt Nút bấm
        setupButtonListeners();

        // 4. Cài đặt Bottom Navigation
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đặt mục "Hồ sơ" là được chọn
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
        // 5. Tải dữ liệu
        loadProfileData();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etProfileEmail);
        etFullName = findViewById(R.id.etProfileFullName);
        etPhone = findViewById(R.id.etProfilePhone);
        etBio = findViewById(R.id.etProfileBio);
        etAvatarUrl = findViewById(R.id.etProfileAvatarUrl);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    /**
     * Tải dữ liệu người dùng hiện tại lên các EditText
     */
    private void loadProfileData() {
        currentUser = userDAO.getUserById(customerId);
        if (currentUser != null) {
            etEmail.setText(currentUser.getEmail());
            etFullName.setText(currentUser.getFullName());
            etPhone.setText(currentUser.getPhone());
            etBio.setText(currentUser.getBio());
            etAvatarUrl.setText(currentUser.getAvatarUrl());
        } else {
            Toast.makeText(this, "Không thể tải hồ sơ, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            sessionManager.logoutUser();
        }
    }

    /**
     * Cài đặt các nút "Lưu" và "Đăng xuất"
     */
    private void setupButtonListeners() {
        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            finish(); // Đóng Activity này
        });

        btnUpdateProfile.setOnClickListener(v -> saveProfileData());
    }

    /**
     * Lấy dữ liệu từ UI và lưu vào CSDL
     */
    private void saveProfileData() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        String avatarUrl = etAvatarUrl.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Tên không được để trống");
            return;
        }

        boolean success = userDAO.updateCustomerProfile(customerId, fullName, phone, bio, avatarUrl);

        if (success) {
            Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
            // Cập nhật tên trong session
            sessionManager.updateUserFullName(fullName);
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Cài đặt điều hướng cho thanh Bottom Nav
     */
    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(CustomerProfileActivity.this, CustomerHomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_bookings) {
                startActivity(new Intent(CustomerProfileActivity.this, CustomerBookingsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Đã ở đây
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDAO != null) {
            userDAO.close();
        }
    }
}
