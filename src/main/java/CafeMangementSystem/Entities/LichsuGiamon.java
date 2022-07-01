package CafeMangementSystem.Entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "lichsu_giamon", schema = "quancafe")
@IdClass(LichsuGiamonPK.class)
public class LichsuGiamon {
    private Integer mamon;
    private Timestamp thoidiemcapnhat;
    private BigDecimal giacapnhat;
    private Monmenu monmenuByMamon;

    @Id
    @Column(name = "mamon", nullable = false)
    public Integer getMamon() {
        return mamon;
    }

    public void setMamon(Integer mamon) {
        this.mamon = mamon;
    }

    @Id
    @Column(name = "thoidiemcapnhat", nullable = false)
    public Timestamp getThoidiemcapnhat() {
        return thoidiemcapnhat;
    }

    public void setThoidiemcapnhat(Timestamp thoidiemcapnhat) {
        this.thoidiemcapnhat = thoidiemcapnhat;
    }

    @Basic
    @Column(name = "giacapnhat", nullable = false)
    public BigDecimal getGiacapnhat() {
        return giacapnhat;
    }

    public void setGiacapnhat(BigDecimal giacapnhat) {
        this.giacapnhat = giacapnhat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LichsuGiamon that = (LichsuGiamon) o;

        if (mamon != null ? !mamon.equals(that.mamon) : that.mamon != null) return false;
        if (thoidiemcapnhat != null ? !thoidiemcapnhat.equals(that.thoidiemcapnhat) : that.thoidiemcapnhat != null)
            return false;
        if (giacapnhat != null ? !giacapnhat.equals(that.giacapnhat) : that.giacapnhat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mamon != null ? mamon.hashCode() : 0;
        result = 31 * result + (thoidiemcapnhat != null ? thoidiemcapnhat.hashCode() : 0);
        result = 31 * result + (giacapnhat != null ? giacapnhat.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "mamon", referencedColumnName = "mamon", nullable = false, insertable=false, updatable=false)
    public Monmenu getMonmenuByMamon() {
        return monmenuByMamon;
    }

    public void setMonmenuByMamon(Monmenu monmenuByMamon) {
        this.monmenuByMamon = monmenuByMamon;
    }
}
