package controller;

import dao.CartDAO;
import dao.ProductDAO;
import model.Cart;
import model.Product;
import model.User;

public class CartController {

    private CartDAO cartDAO;
    private ProductDAO productDAO;

    public CartController() {
        this.cartDAO = new CartDAO();
        this.productDAO = new ProductDAO();
    }

    /**
     * DOCX: Customer Add Product to Cart
     */
    public boolean addToCart(User user, String idProduct, int count) {

        // Basic validation
        if (user == null || idProduct == null) return false;
        if (count < 1) return false;

        // Fetch product for stock validation
        Product product = productDAO.getProductById(idProduct);
        if (product == null) return false;

        // DOCX rule: count must be within available stock
        if (count > product.getStock()) return false;

        // If item already exists → update quantity
        if (cartDAO.itemExists(user.getIdUser(), idProduct)) {
            return cartDAO.updateQuantity(
                    user.getIdUser(),
                    idProduct,
                    count
            );
        }

        // Otherwise → insert new cart item
        return cartDAO.addToCart(
                user.getIdUser(),
                idProduct,
                count
        );
    }

    // VIEW CART
    public Cart getCart(User user) {
        if (user == null) return null;
        return cartDAO.getCartByUser(user.getIdUser());
    }

    // UPDATE QUANTITY
    public boolean updateQuantity(User user, String idProduct, int newQty) {

        if (user == null || idProduct == null) return false;

        // If quantity < 1 → remove item
        if (newQty < 1) {
            return cartDAO.removeFromCart(
                    user.getIdUser(),
                    idProduct
            );
        }

        return cartDAO.updateQuantity(
                user.getIdUser(),
                idProduct,
                newQty
        );
    }

    // REMOVE ITEM FROM CART
    public boolean removeFromCart(User user, String idProduct) {

        if (user == null || idProduct == null) return false;

        return cartDAO.removeFromCart(
                user.getIdUser(),
                idProduct
        );
    }

    // CLEAR CART
    public boolean clearCart(User user) {

        if (user == null) return false;

        return cartDAO.clearCart(user.getIdUser());
    }
}
