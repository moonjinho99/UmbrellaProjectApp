package com.example.umbrella.dto;

public class ReturnBoxDto {

    String returnBoxcode;
    String returnBoxAddr;
    int returnUmbrellaCnt;
    String returnBoxDetailcode;
    int umbrellacode;

    public String getReturnBoxcode() {
        return returnBoxcode;
    }

    public void setReturnBoxcode(String returnBoxcode) {
        this.returnBoxcode = returnBoxcode;
    }

    public String getReturnBoxAddr() {
        return returnBoxAddr;
    }

    public void setReturnBoxAddr(String returnBoxAddr) {
        this.returnBoxAddr = returnBoxAddr;
    }

    public int getReturnUmbrellaCnt() {
        return returnUmbrellaCnt;
    }

    public void setReturnUmbrellaCnt(int returnUmbrellaCnt) {
        this.returnUmbrellaCnt = returnUmbrellaCnt;
    }

    public String getReturnBoxDetailcode() {
        return returnBoxDetailcode;
    }

    public void setReturnBoxDetailcode(String returnBoxDetailcode) {
        this.returnBoxDetailcode = returnBoxDetailcode;
    }

    public int getUmbrellacode() {
        return umbrellacode;
    }

    public void setUmbrellacode(int umbrellacode) {
        this.umbrellacode = umbrellacode;
    }

    @Override
    public String toString() {
        return "ReturnBoxDto{" +
                "returnBoxcode='" + returnBoxcode + '\'' +
                ", returnBoxAddr='" + returnBoxAddr + '\'' +
                ", returnUmbrellaCnt=" + returnUmbrellaCnt +
                ", returnBoxDetailcode='" + returnBoxDetailcode + '\'' +
                ", umbrellacode=" + umbrellacode +
                '}';
    }
}
