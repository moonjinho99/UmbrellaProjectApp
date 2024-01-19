package com.example.umbrella.dto;

public class CenterDto {

    String centercode;
    String centerAddr;
    String regId;

    public String getCentercode() {
        return centercode;
    }

    public void setCentercode(String centercode) {
        this.centercode = centercode;
    }

    public String getCenterAddr() {
        return centerAddr;
    }

    public void setCenterAddr(String centerAddr) {
        this.centerAddr = centerAddr;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    @Override
    public String toString() {
        return "CenterDto{" +
                "centercode='" + centercode + '\'' +
                ", centerAddr='" + centerAddr + '\'' +
                ", regId='" + regId + '\'' +
                '}';
    }
}
