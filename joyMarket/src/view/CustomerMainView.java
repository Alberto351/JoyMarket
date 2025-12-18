package view;

import controller.CartController;
import controller.ProductController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Product;
import model.User;
import java.util.List;

public class CustomerMainView extends MainView {

    private User user;
    private ProductController productController = new ProductController();
    private CartController cartController = new CartController();


    public CustomerMainView(Stage stage, User user) {
        super(stage);
        this.user = user;
    }

    @Override
    public void show() {

        // Tabel Produk
        TableView<Product> table = new TableView<>();

        TableColumn<Product, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getIdProduct()
                )
        );

        TableColumn<Product, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getName()
                )
        );

        TableColumn<Product, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCategory()
                )
        );

        TableColumn<Product, Number> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getPrice()
                )
        );

        TableColumn<Product, Number> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getStock()
                )
        );

        table.getColumns().addAll(colId, colName, colCategory, colPrice, colStock);

        // Untuk Load data
        List<Product> products = productController.getAllProducts();
        ObservableList<Product> data = FXCollections.observableArrayList(products);
        table.setItems(data);

        // Top bar
        Label lblWelcome = new Label("Welcome, " + user.getFullName());

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> new LoginView(stage).show());

        HBox topBar = new HBox(10, lblWelcome, btnLogout);
        topBar.setPadding(new Insets(10));
        
        
     // --- Bottom Bar: Selection and Navigation ---
        TextField txtQty = new TextField();
        txtQty.setPromptText("Qty");
        txtQty.setPrefWidth(60);

        Button btnAddToCart = new Button("Add to Cart");
        Button btnViewCart = new Button("View Cart"); 

        // Styling for a clean UI
        btnAddToCart.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnViewCart.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox bottomBar = new HBox(10, new Label("Qty:"), txtQty, btnAddToCart, btnViewCart);
        bottomBar.setPadding(new Insets(10));

        // Navigation to CartView
        btnViewCart.setOnAction(e -> {
            new CartView(stage, user).show();
        });

        // Add to Cart Logic
        btnAddToCart.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                alert("Please select a product from the table first.");
                return;
            }

            try {
                if (txtQty.getText().isEmpty()) {
                    alert("Quantity must be filled.");
                    return;
                }
                
                int qty = Integer.parseInt(txtQty.getText());
                
                // Validation: Must be between 1 and available stock
                if (qty < 1 || qty > selected.getStock()) {
                    alert("Quantity must be between 1 and " + selected.getStock());
                    return;
                }

                boolean success = cartController.addToCart(user, selected, qty);
                if (success) {
                    alert("Added " + qty + " " + selected.getName() + " to cart!");
                    txtQty.clear();
                    // Refresh table to show updated stock if necessary
                    table.refresh();
                } else {
                    alert("Failed to add product.");
                }
            } catch (NumberFormatException ex) {
                alert("Quantity must be numeric.");
            }
        });

        // Layout setup
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(table);
        root.setBottom(bottomBar); // Critical: ensures buttons appear

        stage.setScene(new Scene(root, 700, 400));
        stage.setTitle("JoymarKet - Customer");
        stage.show();
    }
    
    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
