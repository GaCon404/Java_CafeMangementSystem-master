package CafeMangementSystem.Entities;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;


@Entity
@Table(name = "nhanvien", uniqueConstraints = { @UniqueConstraint(columnNames = { "tendangnhap" }) })
public class Nhanvien {
    private Integer manv;
    private String tennv;
    private Date ngaysinh;
    private String dienthoai;
    private String diachi;
    private String tendangnhap;
    private String matkhau;
//    @Enumerated(EnumType.STRING) - because of using Attribute Converter then we do not need
    private String chucvu;
    private Boolean trangthai;
    private Collection<Hoadon> hoadonsByManv;

    public Nhanvien() {
    }

    public Nhanvien(Integer manv, String tennv, Date ngaysinh, String dienthoai, String diachi, String tendangnhap, String matkhau, String chucvu, Boolean trangthai) {
        this.manv = manv;
        this.tennv = tennv;
        this.ngaysinh = ngaysinh;
        this.dienthoai = dienthoai;
        this.diachi = diachi;
        this.tendangnhap = tendangnhap;
        this.matkhau = matkhau;
        this.chucvu = chucvu;
        this.trangthai = trangthai;
    }

    @Id
    @Column(name = "manv", nullable = false)
    public Integer getManv() {
        return manv;
    }

    public void setManv(Integer manv) {
        this.manv = manv;
    }

    @Basic
    @Column(name = "tennv", nullable = false, length = 25)
    public String getTennv() {
        return tennv;
    }

    public void setTennv(String tennv) {
        this.tennv = tennv;
    }

    @Basic
    @Column(name = "ngaysinh", nullable = false)
    public Date getNgaysinh() {
        return ngaysinh;
    }

    public void setNgaysinh(Date ngaysinh) {
        this.ngaysinh = ngaysinh;
    }

    @Basic
    @Column(name = "dienthoai", nullable = false, length = 12)
    public String getDienthoai() {
        return dienthoai;
    }

    public void setDienthoai(String dienthoai) {
        this.dienthoai = dienthoai;
    }

    @Basic
    @Column(name = "diachi", nullable = false, length = 90)
    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    @Basic
    @Column(name = "tendangnhap", nullable = false, length = 32)
    public String getTendangnhap() {
        return tendangnhap;
    }

    public void setTendangnhap(String tendangnhap) {
        this.tendangnhap = tendangnhap;
    }

    @Basic
    @Column(name = "matkhau", nullable = false, length = 256)
    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    @Basic
    @Column(name = "chucvu", nullable = false)
    public String getChucvu() {
        return chucvu;
    }

    public void setChucvu(String chucvu) {
        this.chucvu = chucvu;
    }

    @Basic
    @Column(name = "trangthai", nullable = false)
    public Boolean getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(Boolean trangthai) {
        this.trangthai = trangthai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Nhanvien nhanvien = (Nhanvien) o;

        if (manv != null ? !manv.equals(nhanvien.manv) : nhanvien.manv != null) return false;
        if (tennv != null ? !tennv.equals(nhanvien.tennv) : nhanvien.tennv != null) return false;
        if (ngaysinh != null ? !ngaysinh.equals(nhanvien.ngaysinh) : nhanvien.ngaysinh != null) return false;
        if (dienthoai != null ? !dienthoai.equals(nhanvien.dienthoai) : nhanvien.dienthoai != null) return false;
        if (diachi != null ? !diachi.equals(nhanvien.diachi) : nhanvien.diachi != null) return false;
        if (tendangnhap != null ? !tendangnhap.equals(nhanvien.tendangnhap) : nhanvien.tendangnhap != null)
            return false;
        if (matkhau != null ? !matkhau.equals(nhanvien.matkhau) : nhanvien.matkhau != null) return false;
        if (chucvu != null ? !chucvu.equals(nhanvien.chucvu) : nhanvien.chucvu != null) return false;
        if (trangthai != null ? !trangthai.equals(nhanvien.trangthai) : nhanvien.trangthai != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = manv != null ? manv.hashCode() : 0;
        result = 31 * result + (tennv != null ? tennv.hashCode() : 0);
        result = 31 * result + (ngaysinh != null ? ngaysinh.hashCode() : 0);
        result = 31 * result + (dienthoai != null ? dienthoai.hashCode() : 0);
        result = 31 * result + (diachi != null ? diachi.hashCode() : 0);
        result = 31 * result + (tendangnhap != null ? tendangnhap.hashCode() : 0);
        result = 31 * result + (matkhau != null ? matkhau.hashCode() : 0);
        result = 31 * result + (chucvu != null ? chucvu.hashCode() : 0);
        result = 31 * result + (trangthai != null ? trangthai.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "nhanvienByNvlaphoadon")
    public Collection<Hoadon> getHoadonsByManv() {
        return hoadonsByManv;
    }

    public void setHoadonsByManv(Collection<Hoadon> hoadonsByManv) {
        this.hoadonsByManv = hoadonsByManv;
    }

    // tu viet them

    @Override
    public String toString() {
        return " MaNv: " + this.manv +
                " | TenNv: " + this.tennv +
                " | SDT: " + this.dienthoai +
                " | Dia chi: " + this.diachi +
                " | Ngay sinh: " + this.ngaysinh.toString() +
                " | Chuc vu: " + this.chucvu +
                " | Trang thai: " + (this.trangthai ? "Active" : "Blocked");
    }

    public void setALlNewValue(Nhanvien newObj) {
        // this.manv
        this.tennv = newObj.tennv;
        this.ngaysinh = newObj.ngaysinh;
        this.dienthoai = newObj.dienthoai;
        this.diachi = newObj.diachi;
        this.tendangnhap = newObj.tendangnhap;
        this.matkhau = newObj.matkhau;
        this.chucvu = newObj.chucvu;
        this.trangthai = newObj.trangthai;
    }
}
