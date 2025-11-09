package com.example.prn392_project.Models;

public class Review {
    private int id;
    private int rating;
    private String comment;
    private String createdAt;
    private String customerName; // Lấy từ JOIN

    public Review(int id, int rating, String comment, String createdAt, String customerName) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.customerName = customerName;
    }

    // --- Getters ---
    public int getId() { return id; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
    public String getCustomerName() { return customerName; }
}
