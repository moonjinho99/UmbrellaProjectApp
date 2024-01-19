package com.example.umbrella.dto;

import java.util.Date;

public class UmbrellaCode {

    int umbrellacode;
    int price;
    String rentalId;
    String photo;
    int rentalStatus;
    Date rentalTime;
    Date returnTime;

    public int getUmbrellacode() {
        return umbrellacode;
    }

    public void setUmbrellacode(int umbrellacode) {
        this.umbrellacode = umbrellacode;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(int rentalStatus) {
        this.rentalStatus = rentalStatus;
    }

    public Date getRentalTime() {
        return rentalTime;
    }

    public void setRentalTime(Date rentalTime) {
        this.rentalTime = rentalTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    @Override
    public String toString() {
        return "UmbrellaCode{" +
                "umbrellacode=" + umbrellacode +
                ", price=" + price +
                ", rentalId='" + rentalId + '\'' +
                ", photo='" + photo + '\'' +
                ", rentalStatus=" + rentalStatus +
                ", rentalTime=" + rentalTime +
                ", returnTime=" + returnTime +
                '}';
    }
}
