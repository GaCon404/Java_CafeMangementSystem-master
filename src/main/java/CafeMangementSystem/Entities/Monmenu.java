package CafeMangementSystem.Entities;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;

@Entity
public class Monmenu {
    private Integer mamon;
    private String tenmon;
    private BigDecimal giaban;
    private Boolean trangthai;
    private String hinhanh;
    private Collection<ChitietHoadon> chitietHoadonsByMamon;
    private Collection<LichsuGiamon> lichsuGiamonsByMamon;

    private BooleanProperty trangThaiBanProperty;
    public BooleanProperty TrangThaiBanProperty() {
        if(trangThaiBanProperty == null) {
            //System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            trangThaiBanProperty = new SimpleBooleanProperty(this.getTrangthai());
        }
        return trangThaiBanProperty;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mamon", nullable = false)
    public Integer getMamon() {
        return mamon;
    }

    public void setMamon(Integer mamon) {
        this.mamon = mamon;
    }

    @Basic
    @Column(name = "tenmon", nullable = false, length = 30)
    public String getTenmon() {
        return tenmon;
    }

    public void setTenmon(String tenmon) {
        this.tenmon = tenmon;
    }

    @Basic
    @Column(name = "giaban", nullable = false)
    public BigDecimal getGiaban() {
        return giaban;
    }

    public void setGiaban(BigDecimal giaban) {
        this.giaban = giaban;
    }

    @Basic
    @Column(name = "trangthai", nullable = false)
    public Boolean getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(Boolean trangthai) {
        this.trangthai = trangthai;
    }

    @Basic
    @Column(name = "hinhanh", nullable = false, length = 50)
    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Monmenu monmenu = (Monmenu) o;

        if (mamon != null ? !mamon.equals(monmenu.mamon) : monmenu.mamon != null) return false;
        if (tenmon != null ? !tenmon.equals(monmenu.tenmon) : monmenu.tenmon != null) return false;
        if (giaban != null ? !giaban.equals(monmenu.giaban) : monmenu.giaban != null) return false;
        if (trangthai != null ? !trangthai.equals(monmenu.trangthai) : monmenu.trangthai != null) return false;
        if (hinhanh != null ? !hinhanh.equals(monmenu.hinhanh) : monmenu.hinhanh != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mamon != null ? mamon.hashCode() : 0;
        result = 31 * result + (tenmon != null ? tenmon.hashCode() : 0);
        result = 31 * result + (giaban != null ? giaban.hashCode() : 0);
        result = 31 * result + (trangthai != null ? trangthai.hashCode() : 0);
        result = 31 * result + (hinhanh != null ? hinhanh.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "monmenuByMamon")
    public Collection<ChitietHoadon> getChitietHoadonsByMamon() {
        return chitietHoadonsByMamon;
    }

    public void setChitietHoadonsByMamon(Collection<ChitietHoadon> chitietHoadonsByMamon) {
        this.chitietHoadonsByMamon = chitietHoadonsByMamon;
    }

    @OneToMany(mappedBy = "monmenuByMamon")
    public Collection<LichsuGiamon> getLichsuGiamonsByMamon() {
        return lichsuGiamonsByMamon;
    }

    public void setLichsuGiamonsByMamon(Collection<LichsuGiamon> lichsuGiamonsByMamon) {
        this.lichsuGiamonsByMamon = lichsuGiamonsByMamon;
    }

    @Override
    public String toString() {
        return "Monmenu{" +
                "mamon=" + mamon +
                ", tenmon='" + tenmon + '\'' +
                ", giaban=" + giaban +
                ", trangthai=" + trangthai +
                ", hinhanh='" + hinhanh + '\'' +
                ", trangThaiBanProperty=" + trangThaiBanProperty +
                '}';
    }
}
