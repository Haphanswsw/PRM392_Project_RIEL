package com.example.prn392_project.Models;
public class ArtistProfile {
    private int id;
    private int userId;
    private String stageName;
    private String genres;
    private double pricePerHour;
    private String location;
    private int experienceYears;
    private String socialLinks; // Lưu dưới dạng JSON
    private double ratingAvg; // Thường thì không để nghệ sĩ tự sửa

    // Constructor đầy đủ (dùng khi đọc từ DB)
    public ArtistProfile(int id, int userId, String stageName, String genres, double pricePerHour, String location, int experienceYears, String socialLinks, double ratingAvg) {
        this.id = id;
        this.userId = userId;
        this.stageName = stageName;
        this.genres = genres;
        this.pricePerHour = pricePerHour;
        this.location = location;
        this.experienceYears = experienceYears;
        this.socialLinks = socialLinks;
        this.ratingAvg = ratingAvg;
    }

    // Constructor rỗng (dùng khi tạo mới)
    public ArtistProfile() {
    }

    // --- Getters và Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }

    public String getGenres() { return genres; }
    public void setGenres(String genres) { this.genres = genres; }

    public double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(double pricePerHour) { this.pricePerHour = pricePerHour; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public String getSocialLinks() { return socialLinks; }
    public void setSocialLinks(String socialLinks) { this.socialLinks = socialLinks; }

    public double getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(double ratingAvg) { this.ratingAvg = ratingAvg; }
}