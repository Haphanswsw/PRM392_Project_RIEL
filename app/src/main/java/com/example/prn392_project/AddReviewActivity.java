package com.example.prn392_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prn392_project.DAO.ReviewDatabaseDAO;
import com.google.android.material.textfield.TextInputEditText;

public class AddReviewActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "BOOKING_ID";
    public static final String EXTRA_ARTIST_ID = "ARTIST_ID";
    public static final String EXTRA_ARTIST_NAME = "ARTIST_NAME";

    private TextView tvArtistName;
    private RatingBar ratingBar;
    private TextInputEditText etComment;
    private Button btnSubmitReview;

    private ReviewDatabaseDAO reviewDAO;
    private SessionManager sessionManager;

    private int bookingId;
    private int artistId;
    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        // 1. Khởi tạo
        reviewDAO = new ReviewDatabaseDAO(this);
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        // 2. Lấy dữ liệu
        customerId = sessionManager.getUserId();
        bookingId = getIntent().getIntExtra(EXTRA_BOOKING_ID, -1);
        artistId = getIntent().getIntExtra(EXTRA_ARTIST_ID, -1);
        String artistName = getIntent().getStringExtra(EXTRA_ARTIST_NAME);

        if (bookingId == -1 || artistId == -1 || customerId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy booking", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 3. Ánh xạ Views
        tvArtistName = findViewById(R.id.tvReviewArtistName);
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);

        // 4. Cập nhật UI
        tvArtistName.setText("Nghệ sĩ: " + artistName);

        // 5. Cài đặt Listener
        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        int rating = (int) ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn ít nhất 1 sao", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thực hiện thêm vào CSDL (và cập nhật rating_avg)
        boolean success = reviewDAO.addReview(bookingId, customerId, artistId, rating, comment);

        if (success) {
            Toast.makeText(this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_LONG).show();
            finish(); // Đóng Activity và quay lại màn hình "Lịch đặt của tôi"
        } else {
            Toast.makeText(this, "Lỗi! Bạn có thể đã đánh giá booking này rồi.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reviewDAO != null) {
            reviewDAO.close();
        }
    }
}
