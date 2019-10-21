package com.example.sabixyz.model;

public class CategoriesList {
    private String id;
    private String title;
    private String ImageUrl;
    private String description;
    private String short_description;

    public CategoriesList(String id, String title,String description,String short_description, String imageUrl) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.ImageUrl = imageUrl;
        this.short_description = short_description;
    }

    public String getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public String getShortDescription() {
        return short_description;
    }
    public String getTitle() {
        return title;
    }
    public String getImageUrl() {
        return ImageUrl;
    }

}
