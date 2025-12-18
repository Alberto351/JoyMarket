package view;

import controller.CartController;
import controller.ProductController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import model.Customer;
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

        //  1. TABLE INITIALIZATION 

        TableView<Product> table = new TableView<>();

        TableColumn<Product, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getIdProduct()));

        TableColumn<Product, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<Product, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));

        TableColumn<Product, Number> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()));

        TableColumn<Product, Number> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStock()));

        table.getColumns().addAll(colId, colName, colCategory, colPrice, colStock);

        // 2. GET PRODUCT
        List<Product> products = productController.getAllProducts();
        ObservableList<Product> data = FXCollections.observableArrayList(products);
        table.setItems(data);

        // 3. TOP BAR SETUP 
        Label lblWelcome = new Label("Welcome, " + user.getFullName());
        lblWelcome.setStyle("-fx-font-weight: bold;");

        Label lblBalance = new Label();
        // Dynamically checks if the user is a Customer to show their wallet balance.
        if (user instanceof Customer) {
            Customer c = (Customer) user;
            lblBalance.setText("Balance: $" + String.format("%.2f", c.getBalance()));
            lblBalance.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }

        // TOP UP BUTTON
        Button btnTopUp = new Button("Top Up");
        btnTopUp.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: black;");
        btnTopUp.setOnAction(e -> new TopUpView(stage, user).show());

        // LOGOUT BUTTON
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> new LoginView(stage).show());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(15, lblWelcome, lblBalance, btnTopUp, spacer, btnLogout);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        // 4. BOTTOM BAR SETUP 

        TextField txtQty = new TextField();
        txtQty.setPromptText("Qty");
        txtQty.setPrefWidth(60);

        Button btnAddToCart = new Button("Add to Cart");
        Button btnViewCart = new Button("View Cart"); 

        btnAddToCart.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnViewCart.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox bottomBar = new HBox(10, new Label("Qty:"), txtQty, btnAddToCart, btnViewCart);
        bottomBar.setPadding(new Insets(10));

        // VIEW CART
        btnViewCart.setOnAction(e -> {
            new CartView(stage, user).show();
        });

        // ADD TO CART
        btnAddToCart.setOnAction(e -> {
            handleAddToCart(table, txtQty);
        });

        // --- 5. ROOT LAYOUT ASSEMBLY ---
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(table);
        root.setBottom(bottomBar);

        stage.setScene(new Scene(root, 750, 450));
        stage.setTitle("JoymarKet - Customer Dashboard");
        stage.show();
    }

    private void handleAddToCart(TableView<Product> table, TextField txtQty) {
        Product selected = table.getSelectionModel().getSelectedItem();
        
        // Validation: Must select an item.
        if (selected == null) {
            alert("Please select a product from the table first.");
            return;
        }

        try {
            // Validation: Quantity must not be empty.
            if (txtQty.getText().trim().isEmpty()) {
                alert("Quantity must be filled.");
                return;
            }
            
            int qty = Integer.parseInt(txtQty.getText());
            
            // Validation: Must be between 1 and available stock.
            if (qty < 1 || qty > selected.getStock()) {
                alert("Quantity must be between 1 and " + selected.getStock());
                return;
            }

            // Attempting to save to database cart_items table.
            boolean success = cartController.addToCart(user, selected, qty);
            if (success) {
                alert("Added " + qty + " " + selected.getName() + " to cart!");
                txtQty.clear();
                table.refresh(); // Visually refreshes the table.
            } else {
                alert("Failed to add product to database.");
            }
        } catch (NumberFormatException ex) {
            alert("Quantity must be numeric.");
        }
    }
    
    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}