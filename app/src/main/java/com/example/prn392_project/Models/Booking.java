package com.example.prn392_project.Models;

public class Booking {
    private int id;
    private int customerId;
    private int artistId;
    private String eventTitle;
    private String eventLocation;
    private String startTime; // "YYYY-MM-DD HH:MM:SS"
    private String endTime;   // "YYYY-MM-DD HH:MM:SS"
    private String status;
    private double price;

    private String artistName; // Lấy từ JOIN
    private boolean isReviewed;

    // Trường này được thêm vào từ câu lệnh JOIN
    private String customerName;

    public Booking() {
    }

    // Constructor để tạo đối tượng từ CSDL
    public Booking(int id, int customerId, int artistId, String eventTitle, String eventLocation,
                   String startTime, String endTime, String status, double price,
                   String artistName, boolean isReviewed) { // Thêm 2 tham số mới
        this.id = id;
        this.customerId = customerId;
        this.artistId = artistId;
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.price = price;
        this.artistName = artistName; // Thêm
        this.isReviewed = isReviewed; // Thêm
    }

    public String getArtistName() { return artistName; }
    public boolean isReviewed() { return isReviewed; }
    // --- Getters ---
    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getArtistId() { return artistId; }
    public String getEventTitle() { return eventTitle; }
    public String getEventLocation() { return eventLocation; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getStatus() { return status; }
    public double getPrice() { return price; }
    public String getCustomerName() { return customerName; }

    public void setId(int id) {
        this.id = id;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setReviewed(boolean reviewed) {
        isReviewed = reviewed;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
