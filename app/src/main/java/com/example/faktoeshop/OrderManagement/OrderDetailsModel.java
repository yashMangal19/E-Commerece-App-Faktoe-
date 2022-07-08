package com.example.faktoeshop.OrderManagement;

public class OrderDetailsModel {
    private String name , shopId;
    private long sellingPrice, count;

    public OrderDetailsModel(){

    }

    public OrderDetailsModel(String name, long count, long sellingPrice, String shopId) {
        this.name = name;
        this.count = count;
        this.sellingPrice = sellingPrice;
        this.shopId = shopId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(long sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}

