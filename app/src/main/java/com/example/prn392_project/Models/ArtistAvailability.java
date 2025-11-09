package com.example.prn392_project.Models;

public class ArtistAvailability {
    private int id;
    private int artistId;
    private String startTime; // Sẽ lưu dạng "YYYY-MM-DD HH:MM:SS"
    private String endTime;   // Sẽ lưu dạng "YYYY-MM-DD HH:MM:SS"
    private boolean isBooked;

    // Constructor
    public ArtistAvailability(int id, int artistId, String startTime, String endTime, boolean isBooked) {
        this.id = id;
        this.artistId = artistId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBooked = isBooked;
    }

    // Getters
    public int getId() { return id; }
    public int getArtistId() { return artistId; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public boolean isBooked() { return isBooked; }
}
