package controller;

import dao.UserDAO;
import model.Customer;
import model.User;

public class UserController {

    private UserDAO userDAO;

    public UserController() {
        userDAO = new UserDAO();
    }

    // Login
    public User login(String email, String password) {

        if (email == null || password == null) return null;

        if (email.isEmpty() || password.isEmpty()) return null;

        return userDAO.login(email, password);
    }

    // Role check
    public boolean isAdmin(User user) {
        return user != null && "Admin".equalsIgnoreCase(user.getRole());
    }

    public boolean isCustomer(User user) {
        return user != null && "Customer".equalsIgnoreCase(user.getRole());
    }

    public boolean isCourier(User user) {
        return user != null && "Courier".equalsIgnoreCase(user.getRole());
    }

    // Validate Customer Register
    public String validateRegister(
            String name,
            String email,
            String password,
            String confirmPassword,
            String phone,
            String address,
            String gender
    ) {

        // 1. Tidak boleh kosong
        if (name.isEmpty() || email.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty() ||
            phone.isEmpty() || address.isEmpty()) {
            return "All fields must be filled";
        }

        // 2. Email harus gmail
        if (!email.endsWith("@gmail.com")) {
            return "Email must end with @gmail.com";
        }

        // 3. Password minimal 6
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }

        // 4. Password harus sama
        if (!password.equals(confirmPassword)) {
            return "Password and confirmation do not match";
        }

        // 5. Gender wajib
        if (gender == null) {
            return "Gender must be selected";
        }

        // 6. Generate Customer ID
        String generatedId = generateCustomerId();

        // 7. Buat customer baru
        Customer customer = new Customer();
        customer.setIdUser(generatedId);
        customer.setFullName(name);
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setPhone(phone);
        customer.setAddress(address);
        customer.setGender(gender);
        customer.setBalance(0);
        customer.setRole("Customer");

        boolean success = userDAO.insertUser(customer);

        return success ? "Success" : "Register failed";
    }
    
    // Generate ID
    private String generateCustomerId() {
        return "CU" + System.currentTimeMillis();
    }

    
    // Top Up Balance
    public boolean topUpBalance(User user, double amount) {

        // validasi dasar
        if (user == null) return false;
        if (!(user instanceof Customer)) return false;
        if (amount <= 0) return false;

        Customer customer = (Customer) user;

        double newBalance = customer.getBalance() + amount;

        boolean updated = userDAO.updateBalance(
                customer.getIdUser(),
                newBalance
        );

        if (updated) {
            // update object agar sinkron
            customer.setBalance(newBalance);
        }

        return updated;
    }
}
