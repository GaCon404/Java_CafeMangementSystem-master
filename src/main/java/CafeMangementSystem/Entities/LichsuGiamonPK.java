package CafeMangementSystem.Entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

public class LichsuGiamonPK implements Serializable {
    private Integer mamon;
    private Timestamp thoidiemcapnhat;

    @Column(name = "mamon", nullable = false)
    @Id
    public Integer getMamon() {
        return mamon;
    }

    public void setMamon(Integer mamon) {
        this.mamon = mamon;
    }

    @Column(name = "thoidiemcapnhat", nullable = false)
    @Id
    public Timestamp getThoidiemcapnhat() {
        return thoidiemcapnhat;
    }

    public void setThoidiemcapnhat(Timestamp thoidiemcapnhat) {
        this.thoidiemcapnhat = thoidiemcapnhat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LichsuGiamonPK that = (LichsuGiamonPK) o;

        if (mamon != null ? !mamon.equals(that.mamon) : that.mamon != null) return false;
        if (thoidiemcapnhat != null ? !thoidiemcapnhat.equals(that.thoidiemcapnhat) : that.thoidiemcapnhat != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mamon != null ? mamon.hashCode() : 0;
        result = 31 * result + (thoidiemcapnhat != null ? thoidiemcapnhat.hashCode() : 0);
        return result;
    }
}
