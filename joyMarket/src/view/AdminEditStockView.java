package view;

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

public class AdminEditStockView extends MainView {

    private User user;
    private ProductController productController = new ProductController();
    private TableView<Product> table = new TableView<>();

    public AdminEditStockView(Stage stage, User user) {
        super(stage);
        this.user = user;
    }

    @Override
    public void show() {

        // Tabel
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

        TableColumn<Product, Number> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getStock()
                )
        );

        table.getColumns().addAll(colId, colName, colStock);
        loadData();

        // Input
        TextField txtStock = new TextField();
        txtStock.setPromptText("New Stock");

        Button btnUpdate = new Button("Update Stock");
        Label lblMessage = new Label();

        btnUpdate.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                lblMessage.setText("Select product first");
                return;
            }

            try {
                int newStock = Integer.parseInt(txtStock.getText());

                if (newStock < 0) {
                    lblMessage.setText("Stock must be >= 0");
                    return;
                }

                selected.setStock(newStock);
                boolean success = productController.updateProduct(selected);

                if (success) {
                    lblMessage.setText("Stock updated");
                    loadData();
                } else {
                    lblMessage.setText("Update failed");
                }

            } catch (NumberFormatException ex) {
                lblMessage.setText("Stock must be a number");
            }
        });

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> new LoginView(stage).show());

        HBox bottom = new HBox(10, txtStock, btnUpdate, btnLogout, lblMessage);
        bottom.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setBottom(bottom);

        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle("Admin - Edit Product Stock");
        stage.show();
    }

    private void loadData() {
        List<Product> products = productController.getAllProducts();
        ObservableList<Product> data = FXCollections.observableArrayList(products);
        table.setItems(data);
    }
}
