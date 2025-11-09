package com.example.prn392_project.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class CustomerBookingsAdapter extends RecyclerView.Adapter<CustomerBookingsAdapter.ViewHolder> {

    private List<Booking> bookingList;
    private LayoutInflater inflater;
    private OnRateButtonClickListener listener;

    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

    public interface OnRateButtonClickListener {
        void onRateClick(Booking booking);
    }

    public CustomerBookingsAdapter(Context context, List<Booking> bookingList, OnRateButtonClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_customer_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvArtistName.setText("Nghệ sĩ: " + booking.getArtistName());
        holder.tvEventTitle.setText("Sự kiện: " + booking.getEventTitle());

        try {
            Date startTime = dbFormat.parse(booking.getStartTime());
            Date endTime = dbFormat.parse(booking.getEndTime());
            holder.tvDateTime.setText(String.format("Thời gian: %s - %s, %s",
                    timeFormat.format(startTime), timeFormat.format(endTime), dateFormat.format(startTime)));
        } catch (ParseException e) {
            holder.tvDateTime.setText("Lỗi thời gian");
        }

        // Cập nhật Trạng thái
        String status = booking.getStatus();
        holder.tvStatus.setText(formatStatus(status));
        holder.tvStatus.setTextColor(getStatusColor(status));

        // --- LOGIC HIỂN THỊ NÚT ĐÁNH GIÁ ---
        if (status.equals("completed")) {
            holder.btnRate.setVisibility(View.VISIBLE);
            if (booking.isReviewed()) {
                holder.btnRate.setText("Đã đánh giá");
                holder.btnRate.setEnabled(false);
            } else {
                holder.btnRate.setText("Đánh giá");
                holder.btnRate.setEnabled(true);
                holder.btnRate.setOnClickListener(v -> listener.onRateClick(booking));
            }
        } else {
            holder.btnRate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    private String formatStatus(String status) {
        switch (status) {
            case "pending": return "Đang chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "completed": return "Đã hoàn thành";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "completed":
            case "confirmed":
                return Color.parseColor("#006400"); // Dark Green
            case "cancelled":
                return Color.parseColor("#8B0000"); // Dark Red
            case "pending":
                return Color.parseColor("#FF8C00"); // Dark Orange
            default:
                return Color.parseColor("#555555"); // Gray
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvArtistName, tvEventTitle, tvDateTime, tvStatus;
        Button btnRate;

        ViewHolder(View itemView) {
            super(itemView);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDateTime = itemView.findViewById(R.id.tvBookingDateTime);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
            btnRate = itemView.findViewById(R.id.btnRateArtist);
        }
    }
}
