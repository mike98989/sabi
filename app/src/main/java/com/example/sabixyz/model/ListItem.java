package com.example.sabixyz.model;

public class ListItem {
    private String head;
    private String description;
    private String ImageUrl;
    private String id;
    private String author;
    private String amount;

    public ListItem(String head, String description,String author, String imageUrl, String Id, String amount) {
        this.head = head;
        this.description = description;
        this.author = author;
        this.ImageUrl = imageUrl;
        this.id = Id;
        this.amount = amount;
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
    public String getId() {
        return id;
    }
    public String getAuthor(){return author; }
    public String getAmount(){return amount; }
}
