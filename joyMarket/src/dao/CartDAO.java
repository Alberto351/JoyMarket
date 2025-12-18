package dao;

import model.Cart;
import model.CartItem;
import model.Product;
import utils.Connect;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CartDAO {

    private Connect connect = Connect.getInstance();

    // GET CART BY USER
    public Cart getCartByUser(String idUser) {
        Cart cart = new Cart();
        cart.setIdUser(idUser);

        String query = """
            SELECT p.idProduct, p.name, p.price, p.stock, p.category, c.quantity
            FROM cart_items c
            JOIN products p ON c.idProduct = p.idProduct
            WHERE c.idUser = ?
        """;

        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idUser);
            ResultSet rs = ps.executeQuery();

            ArrayList<CartItem> items = new ArrayList<>();

            while (rs.next()) {
                Product product = new Product();
                product.setIdProduct(rs.getString("idProduct"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setStock(rs.getInt("stock"));
                product.setCategory(rs.getString("category"));

                CartItem item = new CartItem();
                item.setProduct(product);
                item.setQuantity(rs.getInt("quantity"));

                items.add(item);
            }

            cart.setItems(items);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cart;
    }
    
    // CHECK ITEM 
    public boolean itemExists(String idUser, String idProduct) {
        String query = "SELECT * FROM cart_items WHERE idUser = ? AND idProduct = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idUser);
            ps.setString(2, idProduct);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // GET TOTAL IN CART
    public double getCartTotal(String idUser) {
        String query = """
            SELECT SUM(p.price * c.quantity) AS total
            FROM cart_items c
            JOIN products p ON c.idProduct = p.idProduct
            WHERE c.idUser = ?
        """;

        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    // ADD ITEM TO CART
    public boolean addToCart(String idUser, String idProduct, int quantity) {
        String query = """
            INSERT INTO cart_items (idUser, idProduct, quantity)
            VALUES (?, ?, ?)
        """;

        PreparedStatement ps = connect.prepareStatement(query);
        
        try {
            ps.setString(1, idUser);
            ps.setString(2, idProduct);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE ITEM QUANTITY
    public boolean updateQuantity(String idUser, String idProduct, int quantity) {
        String query = "UPDATE cart_items SET quantity = ? WHERE idUser = ? AND idProduct = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setInt(1, quantity);
            ps.setString(2, idUser);
            ps.setString(3, idProduct);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Get Quantity
    
    public int getQuantity(String idUser, String idProduct) {
        String query = """
            SELECT quantity FROM cart_items
            WHERE idUser = ? AND idProduct = ?
        """;

        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idUser);
            ps.setString(2, idProduct);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    // REMOVE ITEM FROM CART
    public boolean removeFromCart(String idUser, String idProduct) {
        String query = "DELETE FROM cart_items WHERE idUser = ? AND idProduct = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idUser);
            ps.setString(2, idProduct);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // CLEAR CART (AFTER CHECKOUT)
    public boolean clearCart(String idUser) {
        String query = "DELETE FROM cart_items WHERE idUser = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
