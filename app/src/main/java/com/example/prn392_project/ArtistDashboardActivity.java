package com.example.prn392_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ArtistDashboardActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private int artistId;

    private TextView tvWelcomeArtist, tvPendingBookingsCount;
    private Button btnManageProfile, btnManageAvailability, btnViewBookingRequests, btnViewReviews, btnLogout;

    // Bạn sẽ cần tạo các DAO khác để lấy dữ liệu
    // private BookingDatabaseDAO bookingDAO;
    // private ProfileDatabaseDAO profileDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_dashboard);

        // 1. Khởi tạo và Kiểm tra Session
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin(); // Kiểm tra, nếu chưa đăng nhập -> đá về Login

        // 2. Lấy thông tin Artist từ Session
        artistId = sessionManager.getUserId();
        String artistName = sessionManager.getUserFullName();

        // 3. Ánh xạ Views
        tvWelcomeArtist = findViewById(R.id.tvWelcomeArtist);
        tvPendingBookingsCount = findViewById(R.id.tvPendingBookingsCount);
        btnManageProfile = findViewById(R.id.btnManageProfile);
        btnManageAvailability = findViewById(R.id.btnManageAvailability);
        btnViewBookingRequests = findViewById(R.id.btnViewBookingRequests);
        btnViewReviews = findViewById(R.id.btnViewReviews);
        btnLogout = findViewById(R.id.btnLogout);

        // 4. Khởi tạo các DAO cần thiết
        // bookingDAO = new BookingDatabaseDAO(this);
        // profileDAO = new ProfileDatabaseDAO(this);

        // 5. Tải dữ liệu lên Dashboard
        loadDashboardData(artistName);

        // 6. Cài đặt Listeners cho các nút
        setupButtonListeners();
    }

    private void loadDashboardData(String artistName) {
        // Cập nhật lời chào
        tvWelcomeArtist.setText("Chào mừng, " + artistName + "!");

        // --- Lấy dữ liệu thống kê (VÍ DỤ) ---
        // (Bạn cần tự viết hàm này trong DAO của mình)
        // int pendingCount = bookingDAO.getPendingBookingsCount(artistId);

        // Dữ liệu giả (placeholder)
        int pendingCount = 3; // <-- Thay thế bằng lệnh gọi DAO

        tvPendingBookingsCount.setText(String.valueOf(pendingCount));

        // (Tương tự, bạn có thể lấy rating trung bình từ profileDAO)
    }

    private void setupButtonListeners() {

        // Nút Quản lý Hồ sơ
        btnManageProfile.setOnClickListener(v -> {
            // THAY ĐỔI Ở ĐÂY:
            Intent intent = new Intent(ArtistDashboardActivity.this, ManageProfileActivity.class);
            startActivity(intent);
            // Toast.makeText(this, "Chuyển đến Quản lý Hồ sơ", Toast.LENGTH_SHORT).show(); // Xóa dòng này
        });

        // Nút Quản lý Lịch rảnh
        btnManageAvailability.setOnClickListener(v -> {
            // THAY ĐỔI Ở ĐÂY:
            Intent intent = new Intent(ArtistDashboardActivity.this, ManageAvailabilityActivity.class);
            startActivity(intent);
            // Toast.makeText(this, "Chuyển đến Quản lý Lịch rảnh", Toast.LENGTH_SHORT).show(); // Xóa dòng này
        });

        // Nút Xem Yêu cầu Booking
        btnViewBookingRequests.setOnClickListener(v -> {
            // THAY ĐỔI Ở ĐÂY:
            Intent intent = new Intent(ArtistDashboardActivity.this, ManageBookingsActivity.class);
            startActivity(intent);
            // Toast.makeText(this, "Chuyển đến Xem Booking", Toast.LENGTH_SHORT).show();
        });

        // Nút Xem Đánh giá
        btnViewReviews.setOnClickListener(v -> {
            // THAY ĐỔI Ở ĐÂY:
            Intent intent = new Intent(ArtistDashboardActivity.this, ViewReviewsActivity.class);
            startActivity(intent);
            // Toast.makeText(this, "Chuyển đến Xem Đánh giá", Toast.LENGTH_SHORT).show();
        });

        // Nút Đăng xuất
        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser();
            finish(); // Đóng Dashboard
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu khi quay lại màn hình này (ví dụ: sau khi chấp nhận 1 booking)
        // loadDashboardData(sessionManager.getUserFullName());
    }
}
