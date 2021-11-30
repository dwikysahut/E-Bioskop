package com.dwikyhutomo.e_bioskop.model;


public class Movie {
    private int id;
    private String title;
    private String description;
    private String date;
    private Double rating;
    private String genre;
    private String image;
    private int stock;

    public String getShowTime() {
        return showTime;
    }

    private String showTime;

    public Movie(int id, String title, String description, String date, Double rating, String genre, String image, int stock, String showTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.rating = rating;
        this.genre = genre;
        this.image = image;
        this.stock = stock;
        this.showTime = showTime;
    }

    public int getStock() {
        return stock;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public Double getRating() {
        return rating;
    }

    public String getGenre() {
        return genre;
    }

    public String getImage() {
        return image;
    }
}

