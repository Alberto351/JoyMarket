package controller;

import dao.*;
import model.*;

public class OrderController {

    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private ProductDAO productDAO;
    private PromoDAO promoDAO;
    private UserDAO userDAO;
    private CustomerDAO customerDAO;

    public OrderController() {
        this.cartDAO = new CartDAO();
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.productDAO = new ProductDAO();
        this.promoDAO = new PromoDAO();
        this.userDAO = new UserDAO();
        this.customerDAO = new CustomerDAO();
    }

    /**
     * DOCX: Customer Checkout
     */
    public boolean checkout(User user, String promoCode) {
        if (user == null || !"Customer".equalsIgnoreCase(user.getRole())) return false;

        Customer customer = (Customer) user;

        // 1. Fetch Cart
        Cart cart = cartDAO.getCartByUser(customer.getIdUser());
        if (cart == null || cart.getItems().isEmpty()) return false;

        // 2. Calculate Total
        double total = cartDAO.getCartTotal(customer.getIdUser());

        // 3. Promo Application
        if (promoCode != null && !promoCode.isBlank()) {
            Promo promo = promoDAO.getValidPromoByCode(promoCode.trim());
            if (promo == null) return false; // Or handle as "Invalid Code" alert
            total -= promo.getDiscount(total);
            if (total < 0) total = 0;
        }

        // 4. Balance check
        if (customer.getBalance() < total) return false;

        // 5. Create Order Header
        String orderId = orderDAO.createOrder(customer, total);
        if (orderId == null) return false;

        // 6. Process Items & Update Stock
        for (CartItem item : cart.getItems()) {
            orderItemDAO.insertOrderItem(orderId, item.getProduct().getIdProduct(), item.getQuantity(), item.getProduct().getPrice());
            productDAO.updateStock(item.getProduct().getIdProduct(), item.getProduct().getStock() - item.getQuantity());
        }

        // 7. Deduct Balance (Database and Local Object)
        double newBalance = customer.getBalance() - total;
        customerDAO.updateBalance(customer.getIdUser(), newBalance);
        customer.setBalance(newBalance); // IMPORTANT: Update the local object for the UI

        // 8. Clear Cart
        cartDAO.clearCart(customer.getIdUser());

        return true;
    }
}
