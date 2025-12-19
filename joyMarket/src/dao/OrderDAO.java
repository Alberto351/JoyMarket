package dao;

import model.User;
import utils.Connect;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

public class OrderDAO {

    private Connect connect = Connect.getInstance();
    
    /**
     * Create new order (Order Header)
     * Called during checkout process
     *
     * @return idOrder if success, null if failed
     */
    public String createOrder(User user, double totalPrice) {

        String idOrder = generateOrderId();
        String status = "Pending";
        Date orderDate = Date.valueOf(LocalDate.now());

        String query = """
            INSERT INTO orders (idOrder, idUser, status, orderDate, totalPrice)
            VALUES (?, ?, ?, ?, ?)
        """;

        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idOrder);
            ps.setString(2, user.getIdUser());
            ps.setString(3, status);
            ps.setDate(4, orderDate);
            ps.setDouble(5, totalPrice);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0 ? idOrder : null;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean assignOrder(String idOrder, String idCourier) {
        String query = "UPDATE orders SET idCourier = ?, status = 'On Delivery' WHERE idOrder = ?";
        
        try (PreparedStatement ps = connect.prepareStatement(query)) {
            ps.setString(1, idCourier);
            ps.setString(2, idOrder);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Generate unique Order ID
     * Example: ORD-8F3A2C
     */
    private String generateOrderId() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
