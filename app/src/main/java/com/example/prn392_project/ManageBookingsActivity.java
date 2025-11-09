package com.example.prn392_project;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prn392_project.DAO.BookingDatabaseDAO;
import com.example.prn392_project.Models.Booking;
import com.example.prn392_project.adapter.BookingRequestAdapter;

import java.util.ArrayList;
import java.util.List;

public class ManageBookingsActivity extends AppCompatActivity implements BookingRequestAdapter.OnActionClickListener {

    private RecyclerView rvBookings;
    private TextView tvNoBookings;
    private RadioGroup rgBookingFilter;

    private BookingDatabaseDAO bookingDAO;
    private SessionManager sessionManager;
    private BookingRequestAdapter adapter;
    private List<Booking> bookingList;
    private int artistId;

    private String currentFilter = "pending"; // Mặc định xem các yêu cầu đang chờ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        // 1. Khởi tạo
        sessionManager = new SessionManager(getApplicationContext());
        bookingDAO = new BookingDatabaseDAO(this);
        sessionManager.checkLogin();
        artistId = sessionManager.getUserId();

        if (artistId == -1) {
            Toast.makeText(this, "Lỗi xác thực nghệ sĩ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Ánh xạ Views
        rvBookings = findViewById(R.id.rvBookings);
        tvNoBookings = findViewById(R.id.tvNoBookings);
        rgBookingFilter = findViewById(R.id.rgBookingFilter);

        // 3. Cài đặt RecyclerView
        setupRecyclerView();

        // 4. Cài đặt Listener cho Filter
        rgBookingFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPending) {
                currentFilter = "pending";
            } else if (checkedId == R.id.rbCompleted) {
                currentFilter = "completed";
            }
            loadBookings();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 5. Tải dữ liệu khi Activity mở hoặc quay lại
        loadBookings();
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        adapter = new BookingRequestAdapter(this, bookingList, this);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(adapter);
    }

    /**
     * Tải danh sách booking dựa trên filter hiện tại (pending/completed)
     */
    private void loadBookings() {
        List<Booking> newList = bookingDAO.getBookingsByArtist(artistId, currentFilter);

        bookingList.clear();
        bookingList.addAll(newList);
        adapter.notifyDataSetChanged();

        // Hiển thị thông báo nếu danh sách rỗng
        if (bookingList.isEmpty()) {
            rvBookings.setVisibility(View.GONE);
            tvNoBookings.setVisibility(View.VISIBLE);
            tvNoBookings.setText(currentFilter.equals("pending") ? "Không có yêu cầu nào đang chờ." : "Không có lịch sử show.");
        } else {
            rvBookings.setVisibility(View.VISIBLE);
            tvNoBookings.setVisibility(View.GONE);
        }
    }

    /**
     * Được gọi từ Adapter khi nhấn "Chấp nhận"
     */
    @Override
    public void onAcceptClick(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận Đặt lịch")
                .setMessage("Bạn có chắc muốn chấp nhận yêu cầu từ " + booking.getCustomerName() + "?\n\nCác yêu cầu khác trùng lịch này (nếu có) sẽ tự động bị hủy.")
                .setPositiveButton("Chấp nhận", (dialog, which) -> {
                    boolean success = bookingDAO.acceptBookingRequest(
                            booking.getId(),
                            booking.getArtistId(),
                            booking.getStartTime(),
                            booking.getEndTime()
                    );

                    if (success) {
                        Toast.makeText(this, "Đã chấp nhận!", Toast.LENGTH_SHORT).show();
                        loadBookings(); // Tải lại danh sách "pending"
                    } else {
                        Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Được gọi từ Adapter khi nhấn "Từ chối"
     */
    @Override
    public void onRejectClick(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Từ chối Yêu cầu")
                .setMessage("Bạn có chắc muốn từ chối yêu cầu này?")
                .setPositiveButton("Từ chối", (dialog, which) -> {
                    // Chúng ta đổi status thành 'cancelled'
                    boolean success = bookingDAO.updateBookingStatus(booking.getId(), "cancelled");

                    if (success) {
                        Toast.makeText(this, "Đã từ chối", Toast.LENGTH_SHORT).show();
                        loadBookings(); // Tải lại danh sách "pending"
                    } else {
                        Toast.makeText(this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookingDAO != null) {
            bookingDAO.close();
        }
    }
}
