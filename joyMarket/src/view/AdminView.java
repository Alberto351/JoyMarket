package view;

import controller.ProductController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Product;
import model.User;
import java.util.UUID;

/**
 * Admin Dashboard optimized for movie merchandise inventory management.
 * Connects directly to ProductController and Product model.
 */
public class AdminView extends MainView {

    private User user;
    private ProductController productController = new ProductController();
    private TableView<Product> table = new TableView<>();

    // Text fields for user input
    private TextField txtName = new TextField();
    private TextField txtCategory = new TextField();
    private TextField txtPrice = new TextField();
    private TextField txtStock = new TextField();

    public AdminView(Stage stage, User user) {
        super(stage);
        this.user = user;
    }

    @Override
    public void show() {
        // --- 1. TABLE INITIALIZATION ---
        setupProductTable();

        // --- 2. SELECTION LISTENER (Critical for Updates) ---
        // Listens for clicks on table rows to fill the text fields automatically.
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtName.setText(newVal.getName());
                txtCategory.setText(newVal.getCategory());
                txtPrice.setText(String.valueOf(newVal.getPrice()));
                txtStock.setText(String.valueOf(newVal.getStock()));
            }
        });

        // --- 3. UI COMPONENTS ---
        VBox sidebar = createSidebar();
        HBox topBar = createTopBar();

        // --- 4. MAIN LAYOUT ASSEMBLY ---
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(table);
        root.setLeft(sidebar);

        // Load data from SQL database immediately
        refreshTableData();

        stage.setScene(new Scene(root, 950, 500));
        stage.setTitle("JoymarKet - Admin Inventory Control");
        stage.show();
    }

    /**
     * Configures the columns of the TableView based on the Product model.
     */
    private void setupProductTable() {
        TableColumn<Product, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getIdProduct()));

        TableColumn<Product, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));

        TableColumn<Product, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCategory()));

        TableColumn<Product, Number> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getPrice()));

        TableColumn<Product, Number> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getStock()));

        table.getColumns().addAll(colId, colName, colCategory, colPrice, colStock);
    }

    /**
     * Creates the sidebar containing the input form and action buttons.
     */
    private VBox createSidebar() {
        txtName.setPromptText("Product Name");
        txtCategory.setPromptText("Category");
        txtPrice.setPromptText("Price");
        txtStock.setPromptText("Stock");

        Button btnAdd = new Button("Add New Product");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAdd.setOnAction(e -> handleAddAction());

        Button btnUpdate = new Button("Update Selected");
        btnUpdate.setMaxWidth(Double.MAX_VALUE);
        btnUpdate.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: black; -fx-font-weight: bold;");
        btnUpdate.setOnAction(e -> handleUpdateAction());

        Button btnDelete = new Button("Delete Selected");
        btnDelete.setMaxWidth(Double.MAX_VALUE);
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDelete.setOnAction(e -> handleDeleteAction());

        VBox form = new VBox(10, new Label("Product Details"), txtName, txtCategory, txtPrice, txtStock, btnAdd, btnUpdate, btnDelete);
        form.setPadding(new Insets(20));
        form.setPrefWidth(250);
        form.setStyle("-fx-background-color: #f8f9fa;");
        return form;
    }

    /**
     * Creates the header with greeting and logout navigation.
     */
    private HBox createTopBar() {
        Label lblAdmin = new Label("Administrator: " + user.getFullName());
        lblAdmin.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> new LoginView(stage).show());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox top = new HBox(15, lblAdmin, spacer, btnLogout);
        top.setPadding(new Insets(10));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle("-fx-background-color: #dfe6e9; -fx-border-color: #b2bec3; -fx-border-width: 0 0 1 0;");
        return top;
    }

    /**
     * Action: Inserts a new product into the database.
     */
    private void handleAddAction() {
        try {
            if (txtName.getText().isEmpty() || txtCategory.getText().isEmpty()) {
                alert("Please fill in all text fields.");
                return;
            }

            Product newProduct = new Product();
            // Generating a unique ID as requested by the DAO's insert query
            newProduct.setIdProduct("PRD-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
            newProduct.setName(txtName.getText());
            newProduct.setCategory(txtCategory.getText());
            newProduct.setPrice(Double.parseDouble(txtPrice.getText()));
            newProduct.setStock(Integer.parseInt(txtStock.getText()));

            if (productController.addProduct(newProduct)) {
                alert("Product successfully added!");
                refreshTableData();
                clearForm();
            }
        } catch (NumberFormatException ex) {
            alert("Price and Stock must be valid numbers.");
        }
    }

    /**
     * Action: Updates the currently selected product in the database.
     */
    private void handleUpdateAction() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert("Please select a product from the table to update.");
            return;
        }

        try {
            selected.setName(txtName.getText());
            selected.setCategory(txtCategory.getText());
            selected.setPrice(Double.parseDouble(txtPrice.getText()));
            selected.setStock(Integer.parseInt(txtStock.getText()));

            if (productController.updateProduct(selected)) {
                alert("Product updated successfully!");
                refreshTableData();
                clearForm();
            }
        } catch (NumberFormatException ex) {
            alert("Price and Stock must be valid numbers.");
        }
    }

    /**
     * Action: Deletes the selected product from the database.
     */
    private void handleDeleteAction() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert("Please select a product to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (productController.deleteProduct(selected.getIdProduct())) {
                    refreshTableData();
                    clearForm();
                }
            }
        });
    }

    /**
     * Fetches the latest data from the Controller and updates the table.
     */
    private void refreshTableData() {
        table.setItems(FXCollections.observableArrayList(productController.getAllProducts()));
    }

    /**
     * Resets input fields and clears table selection.
     */
    private void clearForm() {
        txtName.clear();
        txtCategory.clear();
        txtPrice.clear();
        txtStock.clear();
        table.getSelectionModel().clearSelection();
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}