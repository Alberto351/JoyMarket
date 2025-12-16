package controller;

import dao.UserDAO;
import model.User;

public class UserController {

    private UserDAO userDAO;

    public UserController() {
        userDAO = new UserDAO();
    }

    public User login(String email, String password) {

        // Validation (based on case requirement)
        if (email == null || password == null) {
            return null;
        }

        if (email.isEmpty() || password.isEmpty()) {
            return null;
        }

        // Call DAO
        User user = userDAO.login(email, password);

        // If not found → login failed
        if (user == null) {
            return null;
        }

        // Login success
        return user;
    }

    /**
     * Helper: check role
     */
    public boolean isAdmin(User user) {
        return user != null && "Admin".equalsIgnoreCase(user.getRole());
    }

    public boolean isCustomer(User user) {
        return user != null && "Customer".equalsIgnoreCase(user.getRole());
    }

    public boolean isCourier(User user) {
        return user != null && "Courier".equalsIgnoreCase(user.getRole());
    }
}
