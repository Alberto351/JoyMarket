package view;

import controller.CartController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Cart;
import model.CartItem;
import model.User;

public class CartView extends MainView {
    private User user;
    private CartController cartController = new CartController();

    public CartView(Stage stage, User user) {
        super(stage);
        this.user = user;
    }

    @Override
    public void show() {
        TableView<CartItem> table = new TableView<>();

        // Column: Product Name
        TableColumn<CartItem, String> colName = new TableColumn<>("Product");
        colName.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getProduct().getName()));

        // Column: Price
        TableColumn<CartItem, Number> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleDoubleProperty(data.getValue().getProduct().getPrice()));

        // Column: Current Quantity
        TableColumn<CartItem, Number> colQty = new TableColumn<>("Qty");
        colQty.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getQuantity()));

        // --- NEW COLUMN: Update Quantity (Requirement Implementation) ---
        TableColumn<CartItem, Void> colUpdate = new TableColumn<>("Update Quantity");
        colUpdate.setCellFactory(param -> new TableCell<>() {
            private final TextField txtNewQty = new TextField();
            private final Button btnUpdate = new Button("Update");
            private final HBox container = new HBox(5, txtNewQty, btnUpdate);

            {
                txtNewQty.setPromptText("New Qty");
                txtNewQty.setPrefWidth(70);
                
                btnUpdate.setOnAction(e -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    String input = txtNewQty.getText();

                    // VALIDATIONS based on requirements
                    try {
                        // 1. Must be filled
                        if (input.trim().isEmpty()) {
                            alert("Count must be filled.");
                            return;
                        }

                        // 2. Must be numeric
                        int newCount = Integer.parseInt(input);

                        // 3. Must be between 1 and available stock
                        int availableStock = item.getProduct().getStock();
                        if (newCount < 1 || newCount > availableStock) {
                            alert("Count must be between 1 and " + availableStock);
                            return;
                        }

                        // Call Controller to update SQL
                        boolean success = cartController.updateQuantity(user, item.getProduct().getIdProduct(), newCount);
                        if (success) {
                            alert("Quantity updated successfully!");
                            show(); // Refresh the view to show updated totals
                        } else {
                            alert("Update failed.");
                        }

                    } catch (NumberFormatException ex) {
                        alert("Count must be numeric.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });

        // Column: Subtotal
        TableColumn<CartItem, Number> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(data -> {
            double sub = data.getValue().getQuantity() * data.getValue().getProduct().getPrice();
            return new javafx.beans.property.SimpleDoubleProperty(sub);
        });

        // Add all columns including the new Update column
        table.getColumns().addAll(colName, colPrice, colQty, colUpdate, colSubtotal);

        // Load Cart Data
        Cart cart = cartController.getCart(user);
        ObservableList<CartItem> items = FXCollections.observableArrayList(cart.getItems());
        table.setItems(items);

        // Bottom Summary
        Label lblTotal = new Label("Total: $" + calculateTotal(items));
        lblTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Button btnCheckout = new Button("Checkout");
        Button btnBack = new Button("Back to Shop");
        
        btnBack.setOnAction(e -> new CustomerMainView(stage, user).show());
        
        btnCheckout.setOnAction(e -> {
            if(items.isEmpty()) {
                alert("Your cart is empty!");
                return;
            }
            handleCheckout(stage);
        });

        HBox bottomBar = new HBox(15, btnBack, lblTotal, btnCheckout);
        bottomBar.setPadding(new Insets(15));
        
        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setBottom(bottomBar);

        stage.setScene(new Scene(root, 850, 450)); // Adjusted width for new column
        stage.setTitle("JoymarKet - Your Cart");
        stage.show();
    }

    private double calculateTotal(ObservableList<CartItem> items) {
        return items.stream().mapToDouble(i -> i.getQuantity() * i.getProduct().getPrice()).sum();
    }

    private void handleCheckout(Stage stage) {
        cartController.clearCart(user);
        alert("Checkout Successful! Thank you for your purchase.");
        new CustomerMainView(stage, user).show();
    }

    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cart Update");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}