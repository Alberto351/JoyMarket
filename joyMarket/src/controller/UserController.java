package controller;

import model.User;
import repository.UserDA;

public class UserController {

    private UserDA userDA;

    public UserController() {
        userDA = new UserDA();
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
        User user = userDA.login(email, password);

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
