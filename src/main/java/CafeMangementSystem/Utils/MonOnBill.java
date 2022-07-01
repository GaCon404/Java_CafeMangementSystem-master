package CafeMangementSystem.Utils;

import javafx.beans.property.IntegerProperty;

import java.math.BigDecimal;

public class MonOnBill {
    private Integer mamon;
    private String tenmon;
    private BigDecimal giaban;
    private Integer soluong;

    public MonOnBill(Integer mamon, String tenmon, BigDecimal giaban, Integer soluong) {
        this.mamon = mamon;
        this.tenmon = tenmon;
        this.giaban = giaban;
        this.soluong = soluong;
    }

    public Integer getMamon() {
        return mamon;
    }

    public void setMamon(Integer mamon) {
        this.mamon = mamon;
    }

    public String getTenmon() {
        return tenmon;
    }

    public void setTenmon(String tenmon) {
        this.tenmon = tenmon;
    }

    public BigDecimal getGiaban() {
        return giaban;
    }

    public void setGiaban(BigDecimal giaban) {
        this.giaban = giaban;
    }


    public Integer getSoluong() {
        return soluong;
    }

    public void setSoluong(Integer soluong) {
        this.soluong = soluong;
    }

    @Override
    public String toString() {
        return "Bill{" +
                " Mã món: " + mamon +
                " Tên món: " + tenmon +
                " Giá bán: " + giaban +
                " Số lượng: " + soluong +
                "}";
    }
}
