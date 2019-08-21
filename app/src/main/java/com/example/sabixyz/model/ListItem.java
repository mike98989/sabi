package com.example.sabixyz.model;

public class ListItem {
    private String head;
    private String description;
    private String ImageUrl;

    public ListItem(String head, String description, String imageUrl) {
        this.head = head;
        this.description = description;
        this.ImageUrl = imageUrl;
    }

    public String getHead() {
        return head;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }
}
