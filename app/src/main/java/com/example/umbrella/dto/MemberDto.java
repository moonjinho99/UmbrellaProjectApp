package com.example.umbrella.dto;

public class MemberDto {

    String id;
    String pw;
    String name;
    String phone;
    int rentalUmbCnt;
    int level;

    // 로그인

    public MemberDto(String id, int level, String pw, int rentalUmbCnt) {
        this.id = id;
        this.level = level;
        this.pw = pw;
        this.rentalUmbCnt = rentalUmbCnt;
    }

    // 아이디 중복확인
    public MemberDto(String id) {
        this.id = id;
    }

    // 회원가입
    public MemberDto(String id, String name, String pw, String phone) {
        this.id = id;
        this.name = name;
        this.pw = pw;
        this.phone = phone;
    }

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
