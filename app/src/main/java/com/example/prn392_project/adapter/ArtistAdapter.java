package com.example.prn392_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prn392_project.Models.ArtistProfile;
import com.example.prn392_project.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private List<ArtistProfile> artistList;
    private LayoutInflater inflater;
    private OnItemClickListener listener;
    private Locale localeVN = new Locale("vi", "VN");
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);

    // Interface để xử lý click
    public interface OnItemClickListener {
        void onItemClick(ArtistProfile artist);
    }

    public ArtistAdapter(Context context, List<ArtistProfile> artistList, OnItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.artistList = artistList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_artist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArtistProfile artist = artistList.get(position);

        holder.tvStageName.setText(artist.getStageName());
        holder.tvGenres.setText(artist.getGenres());
        holder.tvLocation.setText(artist.getLocation());
        holder.rbRating.setRating((float) artist.getRatingAvg());

        // Định dạng tiền tệ
        holder.tvPrice.setText(String.format("%s/giờ", currencyFormatter.format(artist.getPricePerHour())));

        // Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> listener.onItemClick(artist));
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    // Cập nhật dữ liệu khi tìm kiếm
    public void updateData(List<ArtistProfile> newList) {
        artistList.clear();
        artistList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStageName, tvGenres, tvLocation, tvPrice;
        RatingBar rbRating;

        ViewHolder(View itemView) {
            super(itemView);
            tvStageName = itemView.findViewById(R.id.tvArtistStageName);
            tvGenres = itemView.findViewById(R.id.tvArtistGenres);
            tvLocation = itemView.findViewById(R.id.tvArtistLocation);
            tvPrice = itemView.findViewById(R.id.tvArtistPrice);
            rbRating = itemView.findViewById(R.id.rbArtistRating);
        }
    }
}
