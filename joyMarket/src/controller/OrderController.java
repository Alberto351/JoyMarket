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

    public OrderController() {
        this.cartDAO = new CartDAO();
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.productDAO = new ProductDAO();
        this.promoDAO = new PromoDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * DOCX: Customer Checkout
     */
    public boolean checkout(User user, String promoCode) {

        if (user == null) return false;

        // 1. Get cart and total FROM DB
        Cart cart = cartDAO.getCartByUser(user.getIdUser());
        if (cart == null || cart.getItems().isEmpty()) {
            return false;
        }

        double total = cartDAO.getCartTotal(user.getIdUser());

        // 2. Promo validation (DOCX rule)
        if (promoCode != null && !promoCode.isBlank()) {
            Promo promo = promoDAO.getPromoByCode(promoCode);
            if (promo == null) {
                return false; // promo must exist
            }
            total -= promo.getDiscount();
            if (total < 0) total = 0;
        }

        // 3. Balance check (DOCX rule)
        if (user.getBalance() < total) {
            return false;
        }

        // 4. Create order header
        String orderId = orderDAO.createOrder(user, total);
        if (orderId == null) {
            return false;
        }

        // 5. Insert order items + update stock
        for (CartItem item : cart.getItems()) {

            Product product = item.getProduct();
            int qty = item.getQuantity();

            // Insert order item
            orderItemDAO.insertOrderItem(
                    orderId,
                    product.getIdProduct(),
                    qty,
                    product.getPrice()
            );

            // Reduce stock
            int newStock = product.getStock() - qty;
            productDAO.updateStock(product.getIdProduct(), newStock);
        }

        // 6. Deduct user balance
        userDAO.updateBalance(
                user.getIdUser(),
                user.getBalance() - total
        );

        // 7. Clear cart
        cartDAO.clearCart(user.getIdUser());

        return true;
    }
}
