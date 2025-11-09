package com.example.prn392_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AvailabilityCustomerAdapter extends RecyclerView.Adapter<AvailabilityCustomerAdapter.ViewHolder> {

    private List<ArtistAvailability> availabilityList;
    private LayoutInflater inflater;
    private OnSlotClickListener listener;

    // Định dạng
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

    public interface OnSlotClickListener {
        void onSlotClick(ArtistAvailability availability);
    }

    public AvailabilityCustomerAdapter(Context context, List<ArtistAvailability> availabilityList, OnSlotClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.availabilityList = availabilityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_availability_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArtistAvailability availability = availabilityList.get(position);

        try {
            Date startTime = dbFormat.parse(availability.getStartTime());
            Date endTime = dbFormat.parse(availability.getEndTime());

            holder.tvDate.setText("Ngày: " + dateFormat.format(startTime));
            holder.tvTime.setText(String.format("Thời gian: %s - %s", timeFormat.format(startTime), timeFormat.format(endTime)));
        } catch (ParseException e) {
            holder.tvDate.setText("Lỗi ngày");
            holder.tvTime.setText("Lỗi giờ");
        }

        // Bắt sự kiện click trên toàn bộ item
        holder.itemView.setOnClickListener(v -> listener.onSlotClick(availability));
    }

    @Override
    public int getItemCount() {
        return availabilityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvAvailDate);
            tvTime = itemView.findViewById(R.id.tvAvailTime);
        }
    }
}
