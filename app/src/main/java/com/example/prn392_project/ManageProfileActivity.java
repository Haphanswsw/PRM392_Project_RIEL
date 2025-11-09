package com.example.prn392_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prn392_project.DAO.ProfileDatabaseDAO;
import com.example.prn392_project.Models.ArtistProfile;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;

public class ManageProfileActivity extends AppCompatActivity {

    private TextInputEditText etStageName, etGenres, etPricePerHour, etLocation, etExperienceYears, etSocialLinks;
    private Button btnUpdateProfile;

    private SessionManager sessionManager;
    private ProfileDatabaseDAO profileDAO;
    private int currentArtistUserId;
    private ArtistProfile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        // 1. Khởi tạo Session và DAO
        sessionManager = new SessionManager(getApplicationContext());
        profileDAO = new ProfileDatabaseDAO(this);

        // 2. Kiểm tra đăng nhập
        sessionManager.checkLogin();
        currentArtistUserId = sessionManager.getUserId();
        if (currentArtistUserId == -1) {
            // Không thể xảy ra nếu checkLogin() hoạt động đúng
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. Ánh xạ Views
        initViews();

        // 4. Tải dữ liệu hồ sơ hiện có
        loadProfileData();

        // 5. Thiết lập Listener cho nút Lưu
        btnUpdateProfile.setOnClickListener(v -> saveProfileData());
    }

    private void initViews() {
        etStageName = findViewById(R.id.etStageName);
        etGenres = findViewById(R.id.etGenres);
        etPricePerHour = findViewById(R.id.etPricePerHour);
        etLocation = findViewById(R.id.etLocation);
        etExperienceYears = findViewById(R.id.etExperienceYears);
        etSocialLinks = findViewById(R.id.etSocialLinks);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
    }

    /**
     * Tải dữ liệu hồ sơ hiện tại từ DB và điền vào các EditText
     */
    private void loadProfileData() {
        currentProfile = profileDAO.getProfileByUserId(currentArtistUserId);

        if (currentProfile != null) {
            etStageName.setText(currentProfile.getStageName());
            etGenres.setText(currentProfile.getGenres());
            etLocation.setText(currentProfile.getLocation());
            etSocialLinks.setText(currentProfile.getSocialLinks());

            // Xử lý số
            if (currentProfile.getPricePerHour() > 0) {
                etPricePerHour.setText(String.format(Locale.US, "%.0f", currentProfile.getPricePerHour()));
            }
            if (currentProfile.getExperienceYears() > 0) {
                etExperienceYears.setText(String.valueOf(currentProfile.getExperienceYears()));
            }
        } else {
            // Điều này không nên xảy ra vì profile được tạo lúc đăng ký
            Toast.makeText(this, "Không tìm thấy hồ sơ.", Toast.LENGTH_SHORT).show();
            Log.e("ManageProfile", "Không tìm thấy hồ sơ cho user_id: " + currentArtistUserId);
        }
    }

    /**
     * Lấy dữ liệu từ các EditText, validate, và cập nhật vào DB
     */
    private void saveProfileData() {
        if (currentProfile == null) {
            Toast.makeText(this, "Không thể lưu, không có hồ sơ.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy dữ liệu từ text
        String stageName = etStageName.getText().toString().trim();
        String genres = etGenres.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String socialLinks = etSocialLinks.getText().toString().trim();
        double price = 0.0;
        int experience = 0;

        // Validate và chuyển đổi số (cần xử lý lỗi)
        try {
            if (!etPricePerHour.getText().toString().isEmpty()) {
                price = Double.parseDouble(etPricePerHour.getText().toString());
            }
            if (!etExperienceYears.getText().toString().isEmpty()) {
                experience = Integer.parseInt(etExperienceYears.getText().toString());
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá tiền hoặc Số năm kinh nghiệm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (stageName.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Tên sân khấu và Địa điểm là bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật đối tượng currentProfile
        currentProfile.setStageName(stageName);
        currentProfile.setGenres(genres);
        currentProfile.setPricePerHour(price);
        currentProfile.setLocation(location);
        currentProfile.setExperienceYears(experience);
        currentProfile.setSocialLinks(socialLinks);
        // currentProfile.setUserId() đã được đặt khi tải (currentArtistUserId)

        // Thực hiện cập nhật DB
        int rowsAffected = profileDAO.updateProfile(currentProfile);

        if (rowsAffected > 0) {
            Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity và quay lại Dashboard
        } else {
            Toast.makeText(this, "Cập nhật hồ sơ thất bại.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng kết nối DAO khi Activity bị hủy
        if (profileDAO != null) {
            profileDAO.close();
        }
    }
}