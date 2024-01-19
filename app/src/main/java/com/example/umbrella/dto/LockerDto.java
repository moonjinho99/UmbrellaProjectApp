package com.example.umbrella.dto;

public class LockerDto {

    String lockercode;
    String centercode;
    String lockerAddr;
    String lockerDetailcode;
    int umbrellacode;
    int lockerStatus;
    String lockerPw;

    public String getLockercode() {
        return lockercode;
    }

    public void setLockercode(String lockercode) {
        this.lockercode = lockercode;
    }

    public String getCentercode() {
        return centercode;
    }

    public void setCentercode(String centercode) {
        this.centercode = centercode;
    }

    public String getLockerAddr() {
        return lockerAddr;
    }

    public void setLockerAddr(String lockerAddr) {
        this.lockerAddr = lockerAddr;
    }

    public String getLockerDetailcode() {
        return lockerDetailcode;
    }

    public void setLockerDetailcode(String lockerDetailcode) {
        this.lockerDetailcode = lockerDetailcode;
    }

    public int getUmbrellacode() {
        return umbrellacode;
    }

    public void setUmbrellacode(int umbrellacode) {
        this.umbrellacode = umbrellacode;
    }

    public int getLockerStatus() {
        return lockerStatus;
    }

    public void setLockerStatus(int lockerStatus) {
        this.lockerStatus = lockerStatus;
    }

    public String getLockerPw() {
        return lockerPw;
    }

    public void setLockerPw(String lockerPw) {
        this.lockerPw = lockerPw;
    }

    @Override
    public String toString() {
        return "LockerDto{" +
                "lockercode='" + lockercode + '\'' +
                ", centercode='" + centercode + '\'' +
                ", lockerAddr='" + lockerAddr + '\'' +
                ", lockerDetailcode='" + lockerDetailcode + '\'' +
                ", umbrellacode=" + umbrellacode +
                ", lockerStatus=" + lockerStatus +
                ", lockerPw='" + lockerPw + '\'' +
                '}';
    }
}
