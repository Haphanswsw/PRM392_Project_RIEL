package com.example.prn392_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prn392_project.DAO.ArtistSearchDAO;
import com.example.prn392_project.Models.ArtistProfile;
import com.example.prn392_project.adapter.ArtistAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class CustomerHomeActivity extends AppCompatActivity implements ArtistAdapter.OnItemClickListener {

    private RecyclerView rvArtists;
    private SearchView searchViewArtist;
    private BottomNavigationView bottomNavigation;
    private TextView tvNoResults;

    private ArtistSearchDAO artistSearchDAO;
    private SessionManager sessionManager;
    private ArtistAdapter adapter;
    private List<ArtistProfile> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        // 1. Khởi tạo
        sessionManager = new SessionManager(getApplicationContext());
        artistSearchDAO = new ArtistSearchDAO(this);
        sessionManager.checkLogin();

        // 2. Ánh xạ Views
        rvArtists = findViewById(R.id.rvArtists);
        searchViewArtist = findViewById(R.id.searchViewArtist);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        tvNoResults = findViewById(R.id.tvNoResults);

        // 3. Cài đặt RecyclerView
        setupRecyclerView();

        // 4. Cài đặt Thanh tìm kiếm
        setupSearchView();

        // 5. Cài đặt Bottom Navigation
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 6. Tải dữ liệu
        // Đảm bảo "Home" được chọn khi quay lại
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        loadArtists(searchViewArtist.getQuery().toString());
    }

    private void setupRecyclerView() {
        artistList = new ArrayList<>();
        adapter = new ArtistAdapter(this, artistList, this);
        rvArtists.setLayoutManager(new LinearLayoutManager(this));
        rvArtists.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchViewArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadArtists(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadArtists(newText);
                return true;
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Đã ở đây
                return true;
            } else if (itemId == R.id.nav_bookings) {
                // TODO: Mở CustomerBookingsActivity
                Intent intent = new Intent(CustomerHomeActivity.this, CustomerBookingsActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Mở Lịch đặt của tôi", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_profile) {
                // TODO: Mở CustomerProfileActivity (để sửa info và logout)
                 Intent intent = new Intent(CustomerHomeActivity.this, CustomerProfileActivity.class);
                 startActivity(intent);
                Toast.makeText(this, "Mở Hồ sơ", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    /**
     * Tải (hoặc tìm kiếm) nghệ sĩ từ DAO và cập nhật RecyclerView
     */
    private void loadArtists(String query) {
        List<ArtistProfile> newList = artistSearchDAO.searchArtists(query);
        adapter.updateData(newList);

        if (newList.isEmpty()) {
            rvArtists.setVisibility(View.GONE);
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            rvArtists.setVisibility(View.VISIBLE);
            tvNoResults.setVisibility(View.GONE);
        }
    }

    /**
     * Xử lý khi nhấn vào một nghệ sĩ trong danh sách
     */
    @Override
    public void onItemClick(ArtistProfile artist) {
        // THAY ĐỔI Ở ĐÂY:
        Intent intent = new Intent(CustomerHomeActivity.this, ArtistDetailActivity.class);
        // Gửi User ID (là artistId trong các bảng khác)
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_USER_ID, artist.getUserId());
        startActivity(intent);

        // Toast.makeText(this, "Mở chi tiết cho: " + artist.getStageName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Không cần đóng DAO ở đây vì nó được tạo và đóng trong mỗi lần gọi
    }
}