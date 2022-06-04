package com.example.faktoeshop.Dashboard;

public class Product {
    private String Name, Description, Category, MRP, SellingPrice, Brand, Rating, ImageUrl, Id;

    public Product(){

    }

    public Product(String name, String description, String category, String MRP, String sellingPrice, String brand, String rating, String ImageUrl, String Id) {
        this.Name = name;
        this.Description = description;
        this.Category = category;
        this.MRP = MRP;
        this.SellingPrice = sellingPrice;
        this.Brand = brand;
        this.Rating = rating;
        this.ImageUrl = ImageUrl;
        this.Id = Id;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getSellingPrice() {
        return SellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        SellingPrice = sellingPrice;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }
}
