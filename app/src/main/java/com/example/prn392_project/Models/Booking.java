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

    // Trường này được thêm vào từ câu lệnh JOIN
    private String customerName;

    // Constructor để tạo đối tượng từ CSDL
    public Booking(int id, int customerId, int artistId, String eventTitle, String eventLocation,
                   String startTime, String endTime, String status, double price, String customerName) {
        this.id = id;
        this.customerId = customerId;
        this.artistId = artistId;
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.price = price;
        this.customerName = customerName;
    }

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
}
