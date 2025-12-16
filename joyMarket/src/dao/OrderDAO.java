package dao;

import model.Order;
import utils.Connect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private Connect connect = Connect.getInstance();

    // CREATE ORDER
    public boolean createOrder(Order order) {
        String query = """
            INSERT INTO orders (idOrder, idUser, status, orderDate, totalPrice)
            VALUES (?, ?, ?, ?, ?)
        """;

        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, order.getIdOrder());
            ps.setString(2, order.getIdUser());
            ps.setString(3, order.getStatus());
            ps.setDate(4, new java.sql.Date(order.getOrderDate().getTime())); // LocalDate
            ps.setDouble(5, order.getTotalPrice());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // GET ORDERS BY USER
    public List<Order> getOrdersByUser(String idUser) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE idUser = ?";

        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idUser);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // UPDATE ORDER STATUS
    public boolean updateOrderStatus(String idOrder, String status) {
        String query = "UPDATE orders SET status = ? WHERE idOrder = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, status);
            ps.setString(2, idOrder);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // HELPER
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setIdOrder(rs.getString("idOrder"));
        order.setIdUser(rs.getString("idUser"));
        order.setStatus(rs.getString("status"));
        order.setOrderDate(rs.getDate("orderDate"));
        order.setTotalPrice(rs.getDouble("totalPrice"));
        return order;
    }
}
