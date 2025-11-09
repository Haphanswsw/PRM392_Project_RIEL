package com.example.prn392_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prn392_project.DAO.UserDatabaseDAO;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etPassword;
    private RadioGroup rgRole;
    private RadioButton rbCustomer, rbArtist;
    private Button btnRegister;
    private TextView tvLoginLink;

    private UserDatabaseDAO userDatabaseDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo DAO
        userDatabaseDAO = new UserDatabaseDAO(this);

        // Ánh xạ View
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        rgRole = findViewById(R.id.rgRole);
        rbCustomer = findViewById(R.id.rbCustomer);
        rbArtist = findViewById(R.id.rbArtist);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Xử lý sự kiện click Đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });

        // Xử lý sự kiện click link Đăng nhập
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang LoginActivity
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void handleRegister() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Xác định vai trò
        int selectedRoleId = rgRole.getCheckedRadioButtonId();
        String role;
        if (selectedRoleId == R.id.rbArtist) {
            role = "artist";
        } else {
            role = "customer";
        }

        // --- Validate đầu vào ---
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        // (Thêm các validate khác như email format nếu cần)

        // --- Thực hiện đăng ký ---
        long newUserId = userDatabaseDAO.registerUser(email, password, fullName, phone, role);

        if (newUserId == -1) {
            // Lỗi (có thể do email trùng)
            Toast.makeText(this, "Đăng ký thất bại. Email có thể đã tồn tại.", Toast.LENGTH_LONG).show();
        } else {
            // Đăng ký thành công
            Toast.makeText(this, "Đăng ký thành công! (User ID: " + newUserId + ")", Toast.LENGTH_SHORT).show();

            // Chuyển thẳng đến màn hình đăng nhập
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.putExtra("REGISTERED_EMAIL", email); // Gửi email qua để điền sẵn
            startActivity(intent);
            finish(); // Đóng RegisterActivity
        }
    }
}