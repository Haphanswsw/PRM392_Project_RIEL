package com.example.prn392_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prn392_project.DAO.AvailabilityDatabaseDAO;
import com.example.prn392_project.DAO.ProfileDatabaseDAO;
import com.example.prn392_project.DAO.ReviewDatabaseDAO;
import com.example.prn392_project.DAO.UserDatabaseDAO;
import com.example.prn392_project.Models.ArtistAvailability;
import com.example.prn392_project.Models.ArtistProfile;
import com.example.prn392_project.Models.Review;
import com.example.prn392_project.Models.User;
import com.example.prn392_project.adapter.AvailabilityCustomerAdapter;
import com.example.prn392_project.adapter.ReviewAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArtistDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ARTIST_USER_ID = "ARTIST_USER_ID";
    private int artistId;

    // Views cho thông tin hồ sơ
    private TextView tvStageName, tvFullName, tvGenres, tvLocation, tvExperience, tvPrice;
    private RatingBar rbRating;

    // RecyclerView cho Lịch rảnh
    private RecyclerView rvAvailability;
    private TextView tvNoAvailability;
    private AvailabilityCustomerAdapter availabilityAdapter;
    private List<ArtistAvailability> availabilityList;

    // RecyclerView cho Đánh giá
    private RecyclerView rvReviews;
    private TextView tvNoReviews;
    private ReviewAdapter reviewAdapter; // Tái sử dụng ReviewAdapter
    private List<Review> reviewList;

    // DAOs
    private UserDatabaseDAO userDAO;
    private ProfileDatabaseDAO profileDAO;
    private AvailabilityDatabaseDAO availabilityDAO;
    private ReviewDatabaseDAO reviewDAO;
    private SessionManager sessionManager; // Để biết ai đang đặt

    private ArtistProfile currentProfile;
    private User artistUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        // 1. Lấy ID Nghệ sĩ từ Intent
        artistId = getIntent().getIntExtra(EXTRA_ARTIST_USER_ID, -1);
        if (artistId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy nghệ sĩ", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Khởi tạo DAOs và Session
        userDAO = new UserDatabaseDAO(this);
        profileDAO = new ProfileDatabaseDAO(this);
        availabilityDAO = new AvailabilityDatabaseDAO(this);
        reviewDAO = new ReviewDatabaseDAO(this);
        sessionManager = new SessionManager(getApplicationContext()); // Cần để biết customerId khi đặt

        // 3. Ánh xạ Views
        initViews();

        // 4. Cài đặt 2 RecyclerViews
        setupRecyclerViews();

        // 5. Tải tất cả dữ liệu
        loadArtistAllData();
    }

    private void initViews() {
        tvStageName = findViewById(R.id.tvDetailStageName);
        tvFullName = findViewById(R.id.tvDetailFullName);
        tvGenres = findViewById(R.id.tvDetailGenres);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvExperience = findViewById(R.id.tvDetailExperience);
        tvPrice = findViewById(R.id.tvDetailPrice);
        rbRating = findViewById(R.id.rbDetailRating);

        rvAvailability = findViewById(R.id.rvArtistAvailability);
        tvNoAvailability = findViewById(R.id.tvNoAvailability);
        rvReviews = findViewById(R.id.rvArtistReviews);
        tvNoReviews = findViewById(R.id.tvNoReviews);
    }

    private void setupRecyclerViews() {
        // Cài đặt RecyclerView Lịch rảnh
        availabilityList = new ArrayList<>();
        availabilityAdapter = new AvailabilityCustomerAdapter(this, availabilityList, this::onSlotClicked);
        rvAvailability.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)); // Hiển thị ngang
        rvAvailability.setAdapter(availabilityAdapter);

        // Cài đặt RecyclerView Đánh giá (Tái sử dụng adapter cũ)
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
    }

    private void loadArtistAllData() {
        // Tải Profile
        artistUser = userDAO.getUserById(artistId); // Gán vào biến class
        currentProfile = profileDAO.getProfileByUserId(artistId); // Gán vào biến class

        if (currentProfile == null || artistUser == null) {
            Toast.makeText(this, "Lỗi khi tải hồ sơ nghệ sĩ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cập nhật UI Hồ sơ
        tvStageName.setText(currentProfile.getStageName());
        tvFullName.setText("(" + artistUser.getFullName() + ")");
        tvGenres.setText("Thể loại: " + currentProfile.getGenres());
        tvLocation.setText("Địa điểm: " + currentProfile.getLocation());
        tvExperience.setText("Kinh nghiệm: " + currentProfile.getExperienceYears() + " năm");

        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        tvPrice.setText(currencyFormatter.format(currentProfile.getPricePerHour()) + "/giờ");

        rbRating.setRating((float) currentProfile.getRatingAvg());

        // ... (Phần còn lại của hàm tải lịch rảnh và đánh giá) ...

        List<ArtistAvailability> newSlots = availabilityDAO.getAvailableSlotsByArtist(artistId);
        availabilityList.clear();
        availabilityList.addAll(newSlots);
        availabilityAdapter.notifyDataSetChanged();
        tvNoAvailability.setVisibility(newSlots.isEmpty() ? View.VISIBLE : View.GONE);

        List<Review> newReviews = reviewDAO.getReviewsForArtist(artistId);
        reviewList.clear();
        reviewList.addAll(newReviews);
        reviewAdapter.notifyDataSetChanged();
        tvNoReviews.setVisibility(newReviews.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /**
     * Xử lý khi khách hàng CHỌN một khung giờ rảnh
     */
    private void onSlotClicked(ArtistAvailability availability) {
        if (currentProfile == null) {
            Toast.makeText(this, "Lỗi, không thể lấy giá của nghệ sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        // THAY ĐỔI Ở ĐÂY:
        Intent intent = new Intent(this, CreateBookingActivity.class);
        intent.putExtra(CreateBookingActivity.EXTRA_ARTIST_USER_ID, artistId);
        intent.putExtra(CreateBookingActivity.EXTRA_START_TIME, availability.getStartTime());
        intent.putExtra(CreateBookingActivity.EXTRA_END_TIME, availability.getEndTime());
        intent.putExtra(CreateBookingActivity.EXTRA_PRICE_PER_HOUR, currentProfile.getPricePerHour()); // Gửi giá
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng tất cả DAO
        userDAO.close();
        profileDAO.close();
        availabilityDAO.close();
        reviewDAO.close();
    }
}
