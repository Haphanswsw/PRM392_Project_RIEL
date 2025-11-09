package com.example.prn392_project.Models;

public class User {
    private int id;
    private String email;
    private String fullName;
    private String role;
    // Thêm các trường khác nếu bạn cần sau khi đăng nhập

    // Constructor
    public User(int id, String email, String fullName, String role) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
}