package com.example.prn392_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prn392_project.DAO.BookingDatabaseDAO;
import com.example.prn392_project.DAO.UserDatabaseDAO;
import com.example.prn392_project.Models.User;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CreateBookingActivity extends AppCompatActivity {

    // Keys để nhận Intent
    public static final String EXTRA_ARTIST_USER_ID = "ARTIST_USER_ID";
    public static final String EXTRA_START_TIME = "START_TIME";
    public static final String EXTRA_END_TIME = "END_TIME";
    public static final String EXTRA_PRICE_PER_HOUR = "PRICE_PER_HOUR";

    private TextView tvArtistName, tvBookingDateTime, tvTotalPrice;
    private TextInputEditText etEventTitle, etEventLocation;
    private Button btnSendBookingRequest;

    private UserDatabaseDAO userDAO;
    private BookingDatabaseDAO bookingDAO;
    private SessionManager sessionManager;

    // Dữ liệu nhận được
    private int customerId;
    private int artistId;
    private String startTimeStr;
    private String endTimeStr;
    private double pricePerHour;
    private double calculatedTotalPrice;

    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private Locale localeVN = new Locale("vi", "VN");
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_booking);

        // 1. Khởi tạo DAO và Session
        userDAO = new UserDatabaseDAO(this);
        bookingDAO = new BookingDatabaseDAO(this);
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        // 2. Lấy dữ liệu
        customerId = sessionManager.getUserId();
        artistId = getIntent().getIntExtra(EXTRA_ARTIST_USER_ID, -1);
        startTimeStr = getIntent().getStringExtra(EXTRA_START_TIME);
        endTimeStr = getIntent().getStringExtra(EXTRA_END_TIME);
        pricePerHour = getIntent().getDoubleExtra(EXTRA_PRICE_PER_HOUR, 0.0);

        if (artistId == -1 || customerId == -1 || startTimeStr == null || endTimeStr == null) {
            Toast.makeText(this, "Lỗi: Thông tin đặt lịch không hợp lệ", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 3. Ánh xạ Views
        initViews();

        // 4. Điền thông tin chi tiết
        populateBookingDetails();

        // 5. Cài đặt Listener
        btnSendBookingRequest.setOnClickListener(v -> sendBookingRequest());
    }

    private void initViews() {
        tvArtistName = findViewById(R.id.tvBookingArtistName);
        tvBookingDateTime = findViewById(R.id.tvBookingDateTime);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        etEventTitle = findViewById(R.id.etEventTitle);
        etEventLocation = findViewById(R.id.etEventLocation);
        btnSendBookingRequest = findViewById(R.id.btnSendBookingRequest);
    }

    private void populateBookingDetails() {
        // Lấy tên nghệ sĩ
        User artist = userDAO.getUserById(artistId);
        if (artist != null) {
            tvArtistName.setText("Nghệ sĩ: " + artist.getFullName());
        } else {
            tvArtistName.setText("Nghệ sĩ: Không rõ");
        }

        try {
            // Định dạng ngày giờ
            Date startTime = dbFormat.parse(startTimeStr);
            Date endTime = dbFormat.parse(endTimeStr);

            String date = dateFormat.format(startTime);
            String time = String.format("%s - %s", timeFormat.format(startTime), timeFormat.format(endTime));
            tvBookingDateTime.setText(String.format("Thời gian: %s, %s", time, date));

            // Tính toán chi phí
            long durationMillis = endTime.getTime() - startTime.getTime();
            double durationHours = (double) durationMillis / (1000 * 60 * 60);

            calculatedTotalPrice = durationHours * pricePerHour;
            tvTotalPrice.setText(currencyFormatter.format(calculatedTotalPrice));

        } catch (ParseException e) {
            e.printStackTrace();
            tvBookingDateTime.setText("Lỗi định dạng thời gian");
            tvTotalPrice.setText("Lỗi tính giá");
            btnSendBookingRequest.setEnabled(false);
        }
    }

    private void sendBookingRequest() {
        String eventTitle = etEventTitle.getText().toString().trim();
        String eventLocation = etEventLocation.getText().toString().trim();

        if (TextUtils.isEmpty(eventTitle) || TextUtils.isEmpty(eventLocation)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ Tên và Địa điểm sự kiện", Toast.LENGTH_SHORT).show();
            return;
        }

        // Thực hiện tạo booking
        boolean success = bookingDAO.createBookingRequest(
                customerId,
                artistId,
                eventTitle,
                eventLocation,
                startTimeStr,
                endTimeStr,
                calculatedTotalPrice
        );

        if (success) {
            Toast.makeText(this, "Đã gửi yêu cầu đặt lịch! Vui lòng chờ nghệ sĩ xác nhận.", Toast.LENGTH_LONG).show();
            // TODO: Bạn có thể muốn gửi thông báo (Notification) cho nghệ sĩ ở đây

            // Đóng Activity và quay về trang chi tiết nghệ sĩ
            finish();
        } else {
            Toast.makeText(this, "Gửi yêu cầu thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng DAO
        userDAO.close();
        bookingDAO.close();
    }
}
