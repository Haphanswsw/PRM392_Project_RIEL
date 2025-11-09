package com.example.prn392_project;


import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prn392_project.DAO.ReviewDatabaseDAO;
import com.example.prn392_project.Models.Review;
import com.example.prn392_project.adapter.ReviewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewReviewsActivity extends AppCompatActivity {

    private TextView tvAverageRatingText, tvNoReviews;
    private RatingBar rbAverageRating;
    private RecyclerView rvReviews;

    private ReviewDatabaseDAO reviewDAO;
    private SessionManager sessionManager;
    private ReviewAdapter adapter;
    private List<Review> reviewList;

    private int artistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reviews);

        // 1. Khởi tạo
        sessionManager = new SessionManager(getApplicationContext());
        reviewDAO = new ReviewDatabaseDAO(this);
        sessionManager.checkLogin();
        artistId = sessionManager.getUserId();

        if (artistId == -1) {
            Toast.makeText(this, "Lỗi xác thực nghệ sĩ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Ánh xạ Views
        tvAverageRatingText = findViewById(R.id.tvAverageRatingText);
        rbAverageRating = findViewById(R.id.rbAverageRating);
        tvNoReviews = findViewById(R.id.tvNoReviews);
        rvReviews = findViewById(R.id.rvReviews);

        // 3. Cài đặt RecyclerView
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 4. Tải dữ liệu
        loadAllReviewData();
    }

    private void setupRecyclerView() {
        reviewList = new ArrayList<>();
        adapter = new ReviewAdapter(this, reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(adapter);
    }

    private void loadAllReviewData() {
        // Tải điểm trung bình
        double avgRating = reviewDAO.getAverageRating(artistId);
        tvAverageRatingText.setText(String.format(Locale.US, "%.1f/5", avgRating));
        rbAverageRating.setRating((float) avgRating);

        // Tải danh sách
        List<Review> newList = reviewDAO.getReviewsForArtist(artistId);
        reviewList.clear();
        reviewList.addAll(newList);
        adapter.notifyDataSetChanged();

        // Kiểm tra danh sách rỗng
        if (reviewList.isEmpty()) {
            rvReviews.setVisibility(View.GONE);
            tvNoReviews.setVisibility(View.VISIBLE);
        } else {
            rvReviews.setVisibility(View.VISIBLE);
            tvNoReviews.setVisibility(View.GONE);
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
