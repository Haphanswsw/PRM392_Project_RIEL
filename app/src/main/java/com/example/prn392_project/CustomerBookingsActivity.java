package com.example.prn392_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prn392_project.DAO.BookingDatabaseDAO;
import com.example.prn392_project.Models.Booking;
import com.example.prn392_project.adapter.CustomerBookingsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class CustomerBookingsActivity extends AppCompatActivity implements CustomerBookingsAdapter.OnRateButtonClickListener {

    private RecyclerView rvBookings;
    private TextView tvNoBookings;
    private BottomNavigationView bottomNavigation;

    private BookingDatabaseDAO bookingDAO;
    private SessionManager sessionManager;
    private CustomerBookingsAdapter adapter;
    private List<Booking> bookingList;

    private int customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_bookings);

        sessionManager = new SessionManager(getApplicationContext());
        bookingDAO = new BookingDatabaseDAO(this);
        sessionManager.checkLogin();
        customerId = sessionManager.getUserId();

        initViews();
        setupRecyclerView();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigation.setSelectedItemId(R.id.nav_bookings);
        loadCustomerBookings();
    }

    private void initViews() {
        rvBookings = findViewById(R.id.rvCustomerBookings);
        tvNoBookings = findViewById(R.id.tvNoBookings);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        adapter = new CustomerBookingsAdapter(this, bookingList, this);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(adapter);
    }

    private void loadCustomerBookings() {
        List<Booking> newList = bookingDAO.getBookingsByCustomer(customerId);
        bookingList.clear();
        bookingList.addAll(newList);
        adapter.notifyDataSetChanged();

        if (bookingList.isEmpty()) {
            rvBookings.setVisibility(View.GONE);
            tvNoBookings.setVisibility(View.VISIBLE);
        } else {
            rvBookings.setVisibility(View.VISIBLE);
            tvNoBookings.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
        // Đặt mục này là mục được chọn
        bottomNavigation.setSelectedItemId(R.id.nav_bookings);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(CustomerBookingsActivity.this, CustomerHomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_bookings) {
                // Đã ở đây
                return true;
            } else if (itemId == R.id.nav_profile) {
                // TODO: Mở CustomerProfileActivity
                Toast.makeText(this, "Mở Hồ sơ", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    /**
     * Được gọi từ Adapter khi nhấn nút "Đánh giá"
     */
    @Override
    public void onRateClick(Booking booking) {
        Intent intent = new Intent(this, AddReviewActivity.class);
        intent.putExtra(AddReviewActivity.EXTRA_BOOKING_ID, booking.getId());
        intent.putExtra(AddReviewActivity.EXTRA_ARTIST_ID, booking.getArtistId());
        intent.putExtra(AddReviewActivity.EXTRA_ARTIST_NAME, booking.getArtistName());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookingDAO != null) {
            bookingDAO.close();
        }
    }
}
