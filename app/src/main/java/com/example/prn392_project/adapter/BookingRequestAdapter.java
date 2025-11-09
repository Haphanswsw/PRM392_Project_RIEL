package com.example.prn392_project.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prn392_project.Models.Booking;
import com.example.prn392_project.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingRequestAdapter extends RecyclerView.Adapter<BookingRequestAdapter.ViewHolder> {

    private List<Booking> bookingList;
    private LayoutInflater inflater;
    private OnActionClickListener listener;

    // Định dạng CSDL (Lưu trữ)
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    // Định dạng Hiển thị
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

    // Interfaces cho các nút bấm
    public interface OnActionClickListener {
        void onAcceptClick(Booking booking);
        void onRejectClick(Booking booking);
    }

    public BookingRequestAdapter(Context context, List<Booking> bookingList, OnActionClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_booking_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvEventTitle.setText(booking.getEventTitle());
        holder.tvCustomerName.setText("Khách hàng: " + booking.getCustomerName());
        holder.tvBookingLocation.setText("Địa điểm: " + booking.getEventLocation());

        try {
            Date startTime = dbFormat.parse(booking.getStartTime());
            Date endTime = dbFormat.parse(booking.getEndTime());

            holder.tvBookingDate.setText("Ngày: " + dateFormat.format(startTime));
            holder.tvBookingTime.setText(String.format("Thời gian: %s - %s", timeFormat.format(startTime), timeFormat.format(endTime)));
        } catch (ParseException e) {
            holder.tvBookingDate.setText("Lỗi ngày");
            holder.tvBookingTime.setText("Lỗi giờ");
        }

        // --- Logic hiển thị dựa trên Status ---
        String status = booking.getStatus();
        if (status.equals("pending")) {
            // Đang chờ: Hiển thị nút, ẩn status
            holder.llPendingActions.setVisibility(View.VISIBLE);
            holder.tvBookingStatus.setVisibility(View.GONE);

            holder.btnAccept.setOnClickListener(v -> listener.onAcceptClick(booking));
            holder.btnReject.setOnClickListener(v -> listener.onRejectClick(booking));
        } else {
            // Lịch sử: Ẩn nút, hiển thị status
            holder.llPendingActions.setVisibility(View.GONE);
            holder.tvBookingStatus.setVisibility(View.VISIBLE);
            holder.tvBookingStatus.setText(formatStatus(status));
            holder.tvBookingStatus.setTextColor(getStatusColor(status));
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // Cập nhật dữ liệu
    public void updateData(List<Booking> newList) {
        bookingList.clear();
        bookingList.addAll(newList);
        notifyDataSetChanged();
    }

    // Hàm tiện ích để hiển thị status
    private String formatStatus(String status) {
        switch (status) {
            case "confirmed":
                return "Đã xác nhận";
            case "completed":
                return "Đã hoàn thành";
            case "cancelled":
                return "Đã bị hủy";
            default:
                return status;
        }
    }

    // Hàm tiện ích để tô màu status
    private int getStatusColor(String status) {
        switch (status) {
            case "confirmed":
            case "completed":
                return Color.parseColor("#006400"); // Dark Green
            case "cancelled":
                return Color.parseColor("#8B0000"); // Dark Red
            default:
                return Color.parseColor("#555555"); // Gray
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventTitle, tvCustomerName, tvBookingDate, tvBookingTime, tvBookingLocation, tvBookingStatus;
        LinearLayout llPendingActions;
        Button btnAccept, btnReject;

        ViewHolder(View itemView) {
            super(itemView);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
            tvBookingLocation = itemView.findViewById(R.id.tvBookingLocation);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            llPendingActions = itemView.findViewById(R.id.llPendingActions);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
