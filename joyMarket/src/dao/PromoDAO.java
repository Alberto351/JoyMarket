package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Promo;
import utils.Connect;

public class PromoDAO {

    private Connect connect = Connect.getInstance();

    // Validate promo by code
    public Promo getValidPromoByCode(String code) {
        String query = """
            SELECT * FROM promos
            WHERE code = ?
              AND isActive = 1
              AND (expiredAt IS NULL OR expiredAt >= CURDATE())
        """;

        PreparedStatement ps = connect.prepareStatement(query);
        
        try {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToPromo(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Promo mapResultSetToPromo(ResultSet rs) throws SQLException {
        Promo p = new Promo();
        p.setIdPromo(rs.getString("idPromo"));
        p.setCode(rs.getString("code"));
        p.setDiscountType(rs.getString("discountType"));
        p.setDiscountValue(rs.getDouble("discountValue"));
        p.setActive(rs.getBoolean("isActive"));
        p.setExpiredAt(rs.getDate("expiredAt"));
        return p;
    }
}
