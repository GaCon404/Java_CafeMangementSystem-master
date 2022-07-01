package CafeMangementSystem.Entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class ChitietHoadonPK implements Serializable {
    private Integer mahoadon;
    private Integer mamon;

    @Column(name = "mahoadon", nullable = false)
    @Id
    public Integer getMahoadon() {
        return mahoadon;
    }

    public void setMahoadon(Integer mahoadon) {
        this.mahoadon = mahoadon;
    }

    @Column(name = "mamon", nullable = false)
    @Id
    public Integer getMamon() {
        return mamon;
    }

    public void setMamon(Integer mamon) {
        this.mamon = mamon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChitietHoadonPK that = (ChitietHoadonPK) o;

        if (mahoadon != null ? !mahoadon.equals(that.mahoadon) : that.mahoadon != null) return false;
        if (mamon != null ? !mamon.equals(that.mamon) : that.mamon != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mahoadon != null ? mahoadon.hashCode() : 0;
        result = 31 * result + (mamon != null ? mamon.hashCode() : 0);
        return result;
    }
}
