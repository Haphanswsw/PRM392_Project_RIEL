package com.example.prn392_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prn392_project.Models.Review;
import com.example.prn392_project.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> reviewList;
    private LayoutInflater inflater;

    // Định dạng CSDL
    private SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    // Định dạng Hiển thị
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.inflater = LayoutInflater.from(context);
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.tvCustomerName.setText(review.getCustomerName());
        holder.rbRating.setRating(review.getRating());

        if (review.getComment() != null && !review.getComment().isEmpty()) {
            holder.tvComment.setText(review.getComment());
            holder.tvComment.setVisibility(View.VISIBLE);
        } else {
            holder.tvComment.setText("Khách hàng không để lại bình luận.");
            holder.tvComment.setVisibility(View.VISIBLE); // Hoặc GONE
        }

        try {
            Date date = dbFormat.parse(review.getCreatedAt());
            holder.tvDate.setText(dateFormat.format(date));
        } catch (ParseException e) {
            holder.tvDate.setText("Ngày không rõ");
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void updateData(List<Review> newList) {
        reviewList.clear();
        reviewList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvComment, tvDate;
        RatingBar rbRating;

        ViewHolder(View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvReviewCustomerName);
            rbRating = itemView.findViewById(R.id.rbReviewRating);
            tvComment = itemView.findViewById(R.id.tvReviewComment);
            tvDate = itemView.findViewById(R.id.tvReviewDate);
        }
    }
}
