package hbm.fraudDetectionSystem.GeneralComponent.SpringLogic.CurrAddtTrans;

import java.io.Serializable;
import java.util.Objects;

public class CurrAddtTransPK implements Serializable {

    private Long utrnno;

    private String attr;

    // Constructors

    public CurrAddtTransPK() {}

    public CurrAddtTransPK(Long utrnno, String attr) {
        this.utrnno = utrnno;
        this.attr = attr;
    }

    // Getters and setters

    public Long getUtrnno() {
        return utrnno;
    }

    public void setUtrnno(Long utrnno) {
        this.utrnno = utrnno;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    // Overrides

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrAddtTransPK)) return false;
        CurrAddtTransPK that = (CurrAddtTransPK) o;
        return Objects.equals(getUtrnno(), that.getUtrnno()) &&
                Objects.equals(getAttr(), that.getAttr());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUtrnno(), getAttr());
    }

}

