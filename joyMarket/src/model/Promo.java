package model;

import java.util.Date;

public class Promo {

    private String idPromo;
    private String code;
    private String discountType;
    private double discountValue;
    private boolean isActive;
    private Date expiredAt;

    public Promo() {}

    public String getIdPromo() {
        return idPromo;
    }

    public void setIdPromo(String idPromo) {
        this.idPromo = idPromo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    /**
     * Docx: hitung discount
     */
    public double getDiscount(double total) {
        if ("Percentage".equalsIgnoreCase(discountType)) {
            return total * (discountValue / 100);
        }
        return discountValue;
    }
}
