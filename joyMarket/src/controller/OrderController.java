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

        // VALIDASI USER
        if (user == null) return false;

        // Pastikan user adalah Customer
        if (!"Customer".equalsIgnoreCase(user.getRole())) {
            return false;
        }

        Customer customer = (Customer) user;

        // 1. Get cart
        Cart cart = cartDAO.getCartByUser(customer.getIdUser());
        if (cart == null || cart.getItems().isEmpty()) {
            return false;
        }

        // 2. Hitung total
        double total = cartDAO.getCartTotal(customer.getIdUser());

        // 3. Promo validation
        if (promoCode != null && !promoCode.isBlank()) {
            Promo promo = promoDAO.getValidPromoByCode(promoCode);
            if (promo == null) {
                return false;
            }
            total -= promo.getDiscount(total);
            if (total < 0) total = 0;
        }

        // 4. Balance check
        if (customer.getBalance() < total) {
            return false;
        }

        // 5. Create order
        String orderId = orderDAO.createOrder(customer, total);
        if (orderId == null) {
            return false;
        }

        // 6. Insert order items + update stock
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            int qty = item.getQuantity();

            orderItemDAO.insertOrderItem(
                    orderId,
                    product.getIdProduct(),
                    qty,
                    product.getPrice()
            );

            productDAO.updateStock(
                    product.getIdProduct(),
                    product.getStock() - qty
            );
        }

     // 7. Deduct balance
        customerDAO.updateBalance(
                customer.getIdUser(),
                customer.getBalance() - total
        );

        // 8. Clear cart
        cartDAO.clearCart(customer.getIdUser());

        return true;
    }
}
