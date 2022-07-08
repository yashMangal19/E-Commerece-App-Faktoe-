package com.example.faktoeshop.OrderManagement;

public class OrderModel {
    private String UserName, UserId;

    public OrderModel(){
    }

    public OrderModel(String UserName, String UserId) {
        this.UserName = UserName;
        this.UserId = UserId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}