package CafeMangementSystem.Entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chitiet_hoadon", schema = "quancafe")
@IdClass(ChitietHoadonPK.class)
public class ChitietHoadon {
    private Integer mahoadon;
    private Integer mamon;
    private Integer soluong;
    private BigDecimal giaban;
    private BigDecimal tongtien;
    private Hoadon hoadonByMahoadon;
    private Monmenu monmenuByMamon;

    @Id
    @Column(name = "mahoadon", nullable = false)
    public Integer getMahoadon() {
        return mahoadon;
    }

    public void setMahoadon(Integer mahoadon) {
        this.mahoadon = mahoadon;
    }

    @Id
    @Column(name = "mamon", nullable = false)
    public Integer getMamon() {
        return mamon;
    }

    public void setMamon(Integer mamon) {
        this.mamon = mamon;
    }

    @Basic
    @Column(name = "soluong", nullable = true)
    public Integer getSoluong() {
        return soluong;
    }

    public void setSoluong(Integer soluong) {
        this.soluong = soluong;
    }

    @Basic
    @Column(name = "giaban", nullable = true)
    public BigDecimal getGiaban() {
        return giaban;
    }

    public void setGiaban(BigDecimal giaban) {
        this.giaban = giaban;
    }

    @Basic
    @Column(name = "tongtien", nullable = false)
    public BigDecimal getTongtien() {
        return tongtien;
    }

    public void setTongtien(BigDecimal tongtien) {
        this.tongtien = tongtien;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChitietHoadon that = (ChitietHoadon) o;

        if (mahoadon != null ? !mahoadon.equals(that.mahoadon) : that.mahoadon != null) return false;
        if (mamon != null ? !mamon.equals(that.mamon) : that.mamon != null) return false;
        if (soluong != null ? !soluong.equals(that.soluong) : that.soluong != null) return false;
        if (giaban != null ? !giaban.equals(that.giaban) : that.giaban != null) return false;
        if (tongtien != null ? !tongtien.equals(that.tongtien) : that.tongtien != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mahoadon != null ? mahoadon.hashCode() : 0;
        result = 31 * result + (mamon != null ? mamon.hashCode() : 0);
        result = 31 * result + (soluong != null ? soluong.hashCode() : 0);
        result = 31 * result + (giaban != null ? giaban.hashCode() : 0);
        result = 31 * result + (tongtien != null ? tongtien.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "mahoadon", referencedColumnName = "mahoadon", nullable = false, insertable = false, updatable = false)
    public Hoadon getHoadonByMahoadon() {
        return hoadonByMahoadon;
    }

    public void setHoadonByMahoadon(Hoadon hoadonByMahoadon) {
        this.hoadonByMahoadon = hoadonByMahoadon;
    }

    @ManyToOne
    @JoinColumn(name = "mamon", referencedColumnName = "mamon", nullable = false, insertable = false, updatable = false)
    public Monmenu getMonmenuByMamon() {
        return monmenuByMamon;
    }

    public void setMonmenuByMamon(Monmenu monmenuByMamon) {
        this.monmenuByMamon = monmenuByMamon;
    }

    @Override
    public String toString() {
        return "ChitietHoadon{" +
                " | Mã HĐ: " + mahoadon +
                " | Mã món: " + mamon +
                " | SL: " + soluong +
                " | Giá bán: " + giaban +
                " | Tổng tiền: " + tongtien +
                "}";
    }
}
