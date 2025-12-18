package dao;

import model.Customer;
import utils.Connect;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomerDAO {

    private Connect connect = Connect.getInstance();

    public boolean insertCustomer(Customer customer) {

        String sql = """
            INSERT INTO customer (idUser, gender, balance)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            ps.setString(1, customer.getIdUser());
            ps.setString(2, customer.getGender());
            ps.setDouble(3, customer.getBalance());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
