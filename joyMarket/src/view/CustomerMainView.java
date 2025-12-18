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

public class CustomerMainView extends MainView {

    private User user;
    private ProductController productController = new ProductController();

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

        // Root
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(table);

        stage.setScene(new Scene(root, 700, 400));
        stage.setTitle("JoymarKet - Customer");
        stage.show();
    }
}
