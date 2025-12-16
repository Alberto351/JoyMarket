package controller;

import dao.ProductDAO;
import model.Product;

import java.util.List;

public class ProductController {

    private ProductDAO productDAO;

    public ProductController() {
        this.productDAO = new ProductDAO();
    }

    /**
     * View product catalog
     */
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    /**
     * View product detail
     */
    public Product getProductById(String idProduct) {
        if (idProduct == null || idProduct.isBlank()) {
            return null;
        }
        return productDAO.getProductById(idProduct);
    }

    /**
     * Admin: add new product
     */
    public boolean addProduct(Product product) {
        if (product == null) return false;
        return productDAO.insertProduct(product);
    }

    /**
     * Admin: update product
     */
    public boolean updateProduct(Product product) {
        if (product == null || product.getIdProduct() == null) {
            return false;
        }
        return productDAO.updateProduct(product);
    }

    /**
     * Admin: delete product
     */
    public boolean deleteProduct(String idProduct) {
        if (idProduct == null || idProduct.isBlank()) {
            return false;
        }
        return productDAO.deleteProduct(idProduct);
    }
}
