package com.example.vovch.ordis;

public class OrderInfo {
    public OrderInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String location;
    public String name;
    public String description;

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
