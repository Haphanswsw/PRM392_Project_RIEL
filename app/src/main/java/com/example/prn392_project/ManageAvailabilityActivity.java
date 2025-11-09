package com.example.prn392_project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prn392_project.DAO.AvailabilityDatabaseDAO;
import com.example.prn392_project.Models.ArtistAvailability;
import com.example.prn392_project.adapter.AvailabilityAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageAvailabilityActivity extends AppCompatActivity implements AvailabilityAdapter.OnDeleteClickListener {

    private Button btnSelectDate, btnSelectStartTime, btnSelectEndTime, btnAddAvailability;
    private TextView tvSelectedDateTime;
    private RecyclerView rvAvailabilities;

    private AvailabilityDatabaseDAO availabilityDAO;
    private SessionManager sessionManager;
    private AvailabilityAdapter adapter;
    private List<ArtistAvailability> availabilityList;

    private int artistId;

    // Biến tạm để lưu lựa chọn
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar selectedStartTime = Calendar.getInstance();
    private Calendar selectedEndTime = Calendar.getInstance();
    private boolean dateSelected = false;
    private boolean startTimeSelected = false;
    private boolean endTimeSelected = false;

    // Định dạng
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_availability);

        // 1. Khởi tạo Session và DAO
        sessionManager = new SessionManager(getApplicationContext());
        availabilityDAO = new AvailabilityDatabaseDAO(this);
        sessionManager.checkLogin();
        artistId = sessionManager.getUserId();

        if (artistId == -1) {
            Toast.makeText(this, "Lỗi xác thực nghệ sĩ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Ánh xạ Views
        initViews();

        // 3. Cài đặt RecyclerView
        setupRecyclerView();

        // 4. Tải danh sách lịch rảnh
        loadAvailabilities();

        // 5. Cài đặt Listeners cho các nút chọn
        setupPickers();
    }

    private void initViews() {
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime);
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime);
        btnAddAvailability = findViewById(R.id.btnAddAvailability);
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime);
        rvAvailabilities = findViewById(R.id.rvAvailabilities);
    }

    private void setupRecyclerView() {
        availabilityList = new ArrayList<>();
        adapter = new AvailabilityAdapter(this, availabilityList, this);
        rvAvailabilities.setLayoutManager(new LinearLayoutManager(this));
        rvAvailabilities.setAdapter(adapter);
    }

    private void loadAvailabilities() {
        List<ArtistAvailability> newList = availabilityDAO.getAvailabilitiesByArtist(artistId);
        availabilityList.clear();
        availabilityList.addAll(newList);
        adapter.notifyDataSetChanged();
    }

    private void setupPickers() {
        Calendar now = Calendar.getInstance();

        // Nút Chọn Ngày
        btnSelectDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                dateSelected = true;
                updateSelectedDateTimeText();
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Nút Chọn Giờ Bắt Đầu
        btnSelectStartTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                selectedStartTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedStartTime.set(Calendar.MINUTE, minute);
                startTimeSelected = true;
                updateSelectedDateTimeText();
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
        });

        // Nút Chọn Giờ Kết Thúc
        btnSelectEndTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                selectedEndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedEndTime.set(Calendar.MINUTE, minute);
                endTimeSelected = true;
                updateSelectedDateTimeText();
            }, now.get(Calendar.HOUR_OF_DAY) + 1, now.get(Calendar.MINUTE), true).show(); // Gợi ý +1 giờ
        });

        // Nút Thêm
        btnAddAvailability.setOnClickListener(v -> addAvailabilitySlot());
    }

    /**
     * Cập nhật TextView hiển thị ngày giờ đã chọn
     */
    private void updateSelectedDateTimeText() {
        if (!dateSelected && !startTimeSelected && !endTimeSelected) {
            tvSelectedDateTime.setText("Chưa chọn ngày giờ");
            btnAddAvailability.setEnabled(false);
            return;
        }

        String dateStr = dateSelected ? dateFormat.format(selectedDate.getTime()) : "Chưa chọn ngày";
        String startStr = startTimeSelected ? timeFormat.format(selectedStartTime.getTime()) : "??:??";
        String endStr = endTimeSelected ? timeFormat.format(selectedEndTime.getTime()) : "??:??";

        tvSelectedDateTime.setText(String.format("Ngày: %s | Từ: %s Đến: %s", dateStr, startStr, endStr));

        // Chỉ bật nút Thêm khi đã chọn đủ
        btnAddAvailability.setEnabled(dateSelected && startTimeSelected && endTimeSelected);
    }

    /**
     * Xử lý thêm lịch rảnh
     */
    private void addAvailabilitySlot() {
        // Kết hợp Ngày và Giờ
        Calendar startDateTime = (Calendar) selectedDate.clone();
        startDateTime.set(Calendar.HOUR_OF_DAY, selectedStartTime.get(Calendar.HOUR_OF_DAY));
        startDateTime.set(Calendar.MINUTE, selectedStartTime.get(Calendar.MINUTE));
        startDateTime.set(Calendar.SECOND, 0);

        Calendar endDateTime = (Calendar) selectedDate.clone();
        endDateTime.set(Calendar.HOUR_OF_DAY, selectedEndTime.get(Calendar.HOUR_OF_DAY));
        endDateTime.set(Calendar.MINUTE, selectedEndTime.get(Calendar.MINUTE));
        endDateTime.set(Calendar.SECOND, 0);

        // --- Validate ---
        if (endDateTime.before(startDateTime) || endDateTime.equals(startDateTime)) {
            Toast.makeText(this, "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDateTime.before(Calendar.getInstance())) {
            Toast.makeText(this, "Không thể thêm lịch rảnh trong quá khứ", Toast.LENGTH_SHORT).show();
            return;
        }

        // (Bạn nên thêm logic kiểm tra trùng lặp ở đây nếu cần)

        // Chuyển sang định dạng chuỗi của DB
        String startTimeStr = dbFormat.format(startDateTime.getTime());
        String endTimeStr = dbFormat.format(endDateTime.getTime());

        // Thêm vào CSDL
        boolean success = availabilityDAO.addAvailability(artistId, startTimeStr, endTimeStr);

        if (success) {
            Toast.makeText(this, "Đã thêm lịch rảnh", Toast.LENGTH_SHORT).show();
            loadAvailabilities(); // Tải lại danh sách
            resetPickers(); // Xóa lựa chọn hiện tại
        } else {
            Toast.makeText(this, "Thêm lịch thất bại (Có thể bị trùng?)", Toast.LENGTH_LONG).show();
        }
    }

    private void resetPickers() {
        dateSelected = false;
        startTimeSelected = false;
        endTimeSelected = false;
        updateSelectedDateTimeText();
    }

    /**
     * Được gọi từ Adapter khi nhấn nút Xóa
     */
    @Override
    public void onDeleteClick(ArtistAvailability availability, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận Xóa")
                .setMessage("Bạn có chắc muốn xóa khung giờ rảnh này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean success = availabilityDAO.deleteAvailability(availability.getId());
                    if (success) {
                        availabilityList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Xóa thất bại (Lịch đã bị đặt?)", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (availabilityDAO != null) {
            availabilityDAO.close();
        }
    }
}
