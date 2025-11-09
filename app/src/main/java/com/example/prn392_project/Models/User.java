package com.example.prn392_project.Models;

public class User {
    private int id;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String bio;
    private String avatarUrl;

    // Constructor rỗng (Cần cho DAO)
    public User() {}

    public User(int id, String email, String fullName, String role) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    // Constructor đầy đủ (Dùng khi đọc từ DB)
    public User(int id, String email, String fullName, String phone, String role, String bio, String avatarUrl) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getBio() { return bio; }
    public String getAvatarUrl() { return avatarUrl; }

    // --- Setters (Quan trọng cho việc cập nhật) ---
    public void setId(int id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) { this.role = role; }
    public void setBio(String bio) { this.bio = bio; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}