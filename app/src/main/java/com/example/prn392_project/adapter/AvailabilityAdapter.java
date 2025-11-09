package com.example.prn392_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prn392_project.Models.ArtistAvailability;
import com.example.prn392_project.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AvailabilityAdapter extends RecyclerView.Adapter<AvailabilityAdapter.ViewHolder> {

    private List<ArtistAvailability> availabilityList;
    private LayoutInflater inflater;
    private OnDeleteClickListener onDeleteClickListener;

    // Định dạng CSDL (Lưu trữ)
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    // Định dạng Hiển thị
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);


    // Interface để xử lý click xóa
    public interface OnDeleteClickListener {
        void onDeleteClick(ArtistAvailability availability, int position);
    }

    public AvailabilityAdapter(Context context, List<ArtistAvailability> availabilityList, OnDeleteClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.availabilityList = availabilityList;
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_availability, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArtistAvailability availability = availabilityList.get(position);

        try {
            // Phân tích cú pháp thời gian từ CSDL
            Date startTime = dbFormat.parse(availability.getStartTime());
            Date endTime = dbFormat.parse(availability.getEndTime());

            // Hiển thị
            holder.tvDate.setText(dateFormat.format(startTime));
            holder.tvTime.setText(String.format("%s - %s", timeFormat.format(startTime), timeFormat.format(endTime)));

        } catch (ParseException e) {
            // Xử lý lỗi nếu định dạng lưu trữ bị sai
            holder.tvDate.setText("Lỗi định dạng");
            holder.tvTime.setText(availability.getStartTime());
            e.printStackTrace();
        }

        // Xử lý trạng thái đã được đặt
        if (availability.isBooked()) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.GONE); // Không cho xóa nếu đã bị đặt
        } else {
            holder.tvStatus.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }

        // Đặt listener cho nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            onDeleteClickListener.onDeleteClick(availability, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return availabilityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime, tvStatus;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvAvailabilityDate);
            tvTime = itemView.findViewById(R.id.tvAvailabilityTime);
            tvStatus = itemView.findViewById(R.id.tvAvailabilityStatus);
            btnDelete = itemView.findViewById(R.id.btnDeleteAvailability);
        }
    }

    // Hàm để cập nhật danh sách (khi thêm/xóa)
    public void updateData(List<ArtistAvailability> newList) {
        availabilityList.clear();
        availabilityList.addAll(newList);
        notifyDataSetChanged();
    }
}
