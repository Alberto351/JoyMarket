package dao;

import utils.Connect;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderItemDAO {

    private Connect connect = Connect.getInstance();
    
    /**
     * Insert item into order_items table
     * Called during checkout process
     */
    public boolean insertOrderItem(
            String idOrder,
            String idProduct,
            int quantity,
            double price
    ) {

        String query = """
            INSERT INTO order_items (idOrder, idProduct, quantity, price)
            VALUES (?, ?, ?, ?)
        """;

        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idOrder);
            ps.setString(2, idProduct);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
