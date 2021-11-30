package com.dwikyhutomo.e_bioskop.model;

public class Order {
    private String idOrder;
    private String idMovie;
    private String titleMovie;
    private String imageMovie;
    private String dateShowMovie;
    private String showTimeMovie;
    private String idUser;
    private String jumlah;
    private String status;
    private String kode;
    private String dateAdded;

    public String getTitleMovie() {
        return titleMovie;
    }

    public String getImageMovie() {
        return imageMovie;
    }

    public String getDateShowMovie() {
        return dateShowMovie;
    }

    public String getShowTimeMovie() {
        return showTimeMovie;
    }

    public Order(String idOrder, String idMovie, String titleMovie, String imageMovie, String dateShowMovie, String showTimeMovie, String idUser, String jumlah, String status, String kode, String dateAdded) {
        this.idOrder = idOrder;
        this.idMovie = idMovie;
        this.titleMovie = titleMovie;
        this.imageMovie = imageMovie;
        this.dateShowMovie = dateShowMovie;
        this.showTimeMovie = showTimeMovie;
        this.idUser = idUser;
        this.jumlah = jumlah;
        this.status = status;
        this.kode = kode;
        this.dateAdded = dateAdded;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public String getIdMovie() {
        return idMovie;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getStatus() {
        return status;
    }

    public String getKode() {
        return kode;
    }

    public String getDateAdded() {
        return dateAdded;
    }
}
