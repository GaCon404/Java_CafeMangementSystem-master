package CafeMangementSystem.Entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
public class Hoadon {
    private Integer mahoadon;
    private Integer mucgiamgia;
    private BigDecimal thanhtien;
    private BigDecimal tientra;
    private BigDecimal tienthoi;
    private LocalDateTime ngaygiaodich;
    private Integer nvlaphoadon;
    private Collection<ChitietHoadon> chitietHoadonsByMahoadon;
    private Nhanvien nhanvienByNvlaphoadon;

    public Hoadon() {
    }

    public Hoadon(Integer mahoadon, Integer mucgiamgia, BigDecimal thanhtien, BigDecimal tientra, BigDecimal tienthoi, LocalDateTime ngaygiaodich, Integer nvlaphoadon) {
        this.mahoadon = mahoadon;
        this.mucgiamgia = mucgiamgia;
        this.thanhtien = thanhtien;
        this.tientra = tientra;
        this.tienthoi = tienthoi;
        this.ngaygiaodich = ngaygiaodich;
        this.nvlaphoadon = nvlaphoadon;
    }

    @Id
    @Column(name = "mahoadon", nullable = false)
    public Integer getMahoadon() {
        return mahoadon;
    }

    public void setMahoadon(Integer mahoadon) {
        this.mahoadon = mahoadon;
    }

    @Basic
    @Column(name = "mucgiamgia", nullable = true)
    public Integer getMucgiamgia() {
        return mucgiamgia;
    }

    public void setMucgiamgia(Integer mucgiamgia) {
        this.mucgiamgia = mucgiamgia;
    }

    @Basic
    @Column(name = "thanhtien", nullable = false)
    public BigDecimal getThanhtien() {
        return thanhtien;
    }

    public void setThanhtien(BigDecimal thanhtien) {
        this.thanhtien = thanhtien;
    }

    @Basic
    @Column(name = "tientra", nullable = false)
    public BigDecimal getTientra() {
        return tientra;
    }

    public void setTientra(BigDecimal tientra) {
        this.tientra = tientra;
    }

    @Basic
    @Column(name = "tienthoi", nullable = false)
    public BigDecimal getTienthoi() {
        return tienthoi;
    }

    public void setTienthoi(BigDecimal tienthoi) {
        this.tienthoi = tienthoi;
    }

    @Basic
    @Column(name = "ngaygiaodich", nullable = false)
    public LocalDateTime getNgaygiaodich() {
        return ngaygiaodich;
    }

    public void setNgaygiaodich(LocalDateTime ngaygiaodich) {
        this.ngaygiaodich = ngaygiaodich;
    }

    @Basic
    @Column(name = "nvlaphoadon", nullable = false)
    public Integer getNvlaphoadon() {
        return nvlaphoadon;
    }

    public void setNvlaphoadon(Integer nvlaphoadon) {
        this.nvlaphoadon = nvlaphoadon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hoadon hoadon = (Hoadon) o;

        if (mahoadon != null ? !mahoadon.equals(hoadon.mahoadon) : hoadon.mahoadon != null) return false;
        if (mucgiamgia != null ? !mucgiamgia.equals(hoadon.mucgiamgia) : hoadon.mucgiamgia != null) return false;
        if (thanhtien != null ? !thanhtien.equals(hoadon.thanhtien) : hoadon.thanhtien != null) return false;
        if (tientra != null ? !tientra.equals(hoadon.tientra) : hoadon.tientra != null) return false;
        if (tienthoi != null ? !tienthoi.equals(hoadon.tienthoi) : hoadon.tienthoi != null) return false;
        if (ngaygiaodich != null ? !ngaygiaodich.equals(hoadon.ngaygiaodich) : hoadon.ngaygiaodich != null)
            return false;
        if (nvlaphoadon != null ? !nvlaphoadon.equals(hoadon.nvlaphoadon) : hoadon.nvlaphoadon != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mahoadon != null ? mahoadon.hashCode() : 0;
        result = 31 * result + (mucgiamgia != null ? mucgiamgia.hashCode() : 0);
        result = 31 * result + (thanhtien != null ? thanhtien.hashCode() : 0);
        result = 31 * result + (tientra != null ? tientra.hashCode() : 0);
        result = 31 * result + (tienthoi != null ? tienthoi.hashCode() : 0);
        result = 31 * result + (ngaygiaodich != null ? ngaygiaodich.hashCode() : 0);
        result = 31 * result + (nvlaphoadon != null ? nvlaphoadon.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "hoadonByMahoadon")
    public Collection<ChitietHoadon> getChitietHoadonsByMahoadon() {
        return chitietHoadonsByMahoadon;
    }

    public void setChitietHoadonsByMahoadon(Collection<ChitietHoadon> chitietHoadonsByMahoadon) {
        this.chitietHoadonsByMahoadon = chitietHoadonsByMahoadon;
    }

    @ManyToOne
    @JoinColumn(name = "nvlaphoadon", referencedColumnName = "manv", nullable = false, insertable = false, updatable = false)
    public Nhanvien getNhanvienByNvlaphoadon() {
        return nhanvienByNvlaphoadon;
    }

    public void setNhanvienByNvlaphoadon(Nhanvien nhanvienByNvlaphoadon) {
        this.nhanvienByNvlaphoadon = nhanvienByNvlaphoadon;
    }

    public void setALlNewValue(Hoadon newObj) {
        this.mahoadon = newObj.mahoadon;
        this.mucgiamgia = newObj.mucgiamgia;
        this.thanhtien = newObj.thanhtien;
        this.tientra = newObj.tientra;
        this.tienthoi = newObj.tienthoi;
        this.ngaygiaodich = newObj.ngaygiaodich;
        this.nvlaphoadon = newObj.nvlaphoadon;
    }

    @Override
    public String toString() {
        return "Mã hóa đơn: " + this.mahoadon +
                " | Mức giảm giá: " + this.mucgiamgia +
                " | Thành tiền: " + this.thanhtien +
                " | Tiền trả: " + this.tientra +
                " | Tiền thối: " + this.tienthoi +
                " | Ngày giao dịch: " + this.ngaygiaodich +
                " | Nhân viên lập (manv: "+ this.nvlaphoadon +
                ")";
    }
}
