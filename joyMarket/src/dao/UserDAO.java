package dao;

import model.Customer;
import model.User;
import utils.Connect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private Connect connect = Connect.getInstance();

    // LOGIN
    public User login(String email, String password) {
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // GET USERS BY ROLE (List<User>)
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    // GET USER BY ID
    public User getUserById(String idUser) {
        String query = "SELECT * FROM users WHERE idUser = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idUser);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Insert User
    public boolean insertUser(User user) {
        String query = "INSERT INTO users VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, user.getIdUser());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAddress());
            ps.setDouble(7, 0); // balance awal
            ps.setString(8, user.getRole());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // UPDATE BALANCE
    public boolean updateBalance(String idUser, double newBalance) {
        String query = "UPDATE users SET balance = ? WHERE idUser = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setDouble(1, newBalance);
            ps.setString(2, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // HELPER METHOD (VERY IMPORTANT)
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setIdUser(rs.getString("idUser"));
        user.setFullName(rs.getString("fullName"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setRole(rs.getString("role"));
        
        if ("Customer".equalsIgnoreCase(rs.getString("role"))) {
            Customer c = new Customer();
            c.setIdUser(user.getIdUser());
            c.setFullName(user.getFullName());
            c.setEmail(user.getEmail());
            c.setPassword(user.getPassword());
            c.setPhone(user.getPhone());
            c.setAddress(user.getAddress());
            c.setRole(user.getRole());
            c.setBalance(rs.getDouble("balance"));
            return c;
        }

        return user;
    }
}
