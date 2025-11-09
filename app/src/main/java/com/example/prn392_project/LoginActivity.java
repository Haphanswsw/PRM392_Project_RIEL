package com.example.prn392_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prn392_project.DAO.UserDatabaseDAO;
import com.example.prn392_project.Models.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailLogin, etPasswordLogin;
    private Button btnLogin;
    private TextView tvRegisterLink;

    private UserDatabaseDAO userDatabaseDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo DAO
        userDatabaseDAO = new UserDatabaseDAO(this);

        // Ánh xạ View
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // Nhận email nếu vừa đăng ký xong
        String registeredEmail = getIntent().getStringExtra("REGISTERED_EMAIL");
        if (registeredEmail != null) {
            etEmailLogin.setText(registeredEmail);
        }

        // Xử lý sự kiện click Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        // Xử lý sự kiện click link Đăng ký
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang RegisterActivity
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void handleLogin() {
        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Thực hiện đăng nhập ---
        User user = userDatabaseDAO.loginUser(email, password);

        if (user != null) {
            // Đăng nhập thành công
            Toast.makeText(this, "Đăng nhập thành công! Welcome " + user.getFullName(), Toast.LENGTH_SHORT).show();

            // --- QUAN TRỌNG: Lưu phiên đăng nhập ---
            // (Bạn nên tạo một lớp SessionManager dùng SharedPreferences ở đây)
            // Ví dụ: SessionManager.getInstance(this).createLoginSession(user.getId(), user.getEmail(), user.getRole());

            // --- Điều hướng dựa trên vai trò ---
            if (user.getRole().equals("artist")) {
                // Tới màn hình Artist Dashboard
                // Intent intent = new Intent(LoginActivity.this, ArtistDashboardActivity.class);
                // startActivity(intent);
            } else {
                // Tới màn hình Customer Home (Màn hình chính)
                // Intent intent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
                // startActivity(intent);
            }

            // finish(); // Đóng LoginActivity để người dùng không back lại được

        } else {
            // Đăng nhập thất bại
            Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_LONG).show();
        }
    }
}