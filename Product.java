package com.example.loginform;

public class Product {
    private  String name;
    private String price;
    private String PLU;

    public String getPLU() {
        return PLU;
    }

    public String getPrice() {
        return price;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPLU(String PLU) {
        this.PLU = PLU;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    //TO DO getter and setter
    @Override
    public String toString() {
        return name + " - $" + price;
    }
}
