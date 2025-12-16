package dao;

import database.Connect;
import model.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private Connect connect = Connect.getInstance();

    // GET ALL PRODUCTS
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // GET PRODUCT BY ID
    public Product getProductById(String idProduct) {
        String query = "SELECT * FROM products WHERE idProduct = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idProduct);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // INSERT PRODUCT
    public boolean insertProduct(Product product) {
    	String query = "INSERT INTO products VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
        	ps.setString(1, product.getIdProduct());
        	ps.setString(2, product.getName());
        	ps.setDouble(3, product.getPrice());
        	ps.setInt(4, product.getStock());
        	ps.setString(5, product.getCategory());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE PRODUCT
    public boolean updateProduct(Product product) {
    	String query = "UPDATE products SET name = ?, price = ?, stock = ?, category = ? WHERE idProduct = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
        	ps.setString(1, product.getName());
        	ps.setDouble(2, product.getPrice());
        	ps.setInt(3, product.getStock());
        	ps.setString(4, product.getCategory());
        	ps.setString(5, product.getIdProduct());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE PRODUCT
    public boolean deleteProduct(String idProduct) {
        String query = "DELETE FROM products WHERE idProduct = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setString(1, idProduct);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE STOCK (USED IN ORDER)
    public boolean updateStock(String idProduct, int newStock) {
        String query = "UPDATE products SET stock = ? WHERE idProduct = ?";
        PreparedStatement ps = connect.prepareStatement(query);

        try {
            ps.setInt(1, newStock);
            ps.setString(2, idProduct);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // HELPER METHOD
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setIdProduct(rs.getString("idProduct"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        product.setStock(rs.getInt("stock"));
        product.setCategory(rs.getString("category")); // ✅
        return product;
    }
}
