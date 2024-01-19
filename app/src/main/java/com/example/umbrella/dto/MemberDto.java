package com.example.umbrella.dto;

public class MemberDto {

    String id;
    String pw;
    String name;
    String phone;
    int rentalUmbCnt;
    int level;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRentalUmbCnt() {
        return rentalUmbCnt;
    }

    public void setRentalUmbCnt(int rentalUmbCnt) {
        this.rentalUmbCnt = rentalUmbCnt;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "MemberDto{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", rentalUmbCnt=" + rentalUmbCnt +
                ", level=" + level +
                '}';
    }

}
