package view;

import controller.OrderController;
import controller.UserController;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Order;
import model.User;

import java.util.List;

/**
 * View for Admins to assign couriers to orders ready for delivery.
 */
public class AdminAssignOrderView extends MainView {

    private User admin;
    private OrderController orderController = new OrderController();
    private UserController userController = new UserController();
    private TableView<Order> table = new TableView<>();
    private ComboBox<User> courierCombo = new ComboBox<>();

    public AdminAssignOrderView(Stage stage, User admin) {
        super(stage);
        this.admin = admin;
    }

    @Override
    public void show() {
        // --- 1. TABLE SETUP ---
        setupTable();

        // --- 2. COURIER SELECTION FORM ---
        Label lblCourier = new Label("Select Courier:");
        
        // Populate dropdown with users who have the role 'Courier'
        // This ensures the courier "exists in the database"
        List<User> couriers = userController.getUsersByRole("Courier");
        courierCombo.setItems(FXCollections.observableArrayList(couriers));
        courierCombo.setPromptText("-- Choose Courier --");
        
        // Define how the courier name appears in the dropdown
        courierCombo.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getFullName());
            }
        });

        Button btnAssign = new Button("Assign to Courier");
        btnAssign.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAssign.setOnAction(e -> handleAssign());

        VBox form = new VBox(10, lblCourier, courierCombo, btnAssign);
        form.setPadding(new Insets(20));
        form.setPrefWidth(250);
        form.setAlignment(Pos.TOP_CENTER);

        // --- 3. TOP BAR & NAVIGATION ---
        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> new AdminView(stage, admin).show());
        
        HBox topBar = new HBox(15, btnBack, new Label("Assign Order to Courier"));
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #dfe6e9;");

        // --- 4. LAYOUT ASSEMBLY ---
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(table);
        root.setLeft(form);

        refreshData();

        stage.setScene(new Scene(root, 900, 500));
        stage.setTitle("JoymarKet Admin - Assign Delivery");
        stage.show();
    }

    /**
     * Logic for the assignment and validation.
     */
    private void handleAssign() {
        Order selectedOrder = table.getSelectionModel().getSelectedItem();
        User selectedCourier = courierCombo.getValue();

        // VALIDATION: Must be selected
        if (selectedOrder == null) {
            alert("Please select an order from the table first.");
            return;
        }

        // VALIDATION: Courier must be selected
        if (selectedCourier == null) {
            alert("Validation Error: Courier must be selected.");
            return;
        }

        // Execution: Update the order with the courier ID
        boolean success = orderController.assignCourier(selectedOrder.getIdOrder(), selectedCourier.getIdUser());
        
        if (success) {
            alert("Order " + selectedOrder.getIdOrder() + " is now being delivered by " + selectedCourier.getFullName());
            refreshData();
            courierCombo.getSelectionModel().clearSelection();
        } else {
            alert("Failed to update order. Check database constraints.");
        }
    }

    private void setupTable() {
        TableColumn<Order, String> colId = new TableColumn<>("Order ID");
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getIdOrder()));

        TableColumn<Order, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus()));

        TableColumn<Order, Number> colTotal = new TableColumn<>("Total Price");
        colTotal.setCellValueFactory(d -> new javafx.beans.property.SimpleDoubleProperty(d.getValue().getTotalPrice()));

        table.getColumns().addAll(colId, colStatus, colTotal);
    }

    private void refreshData() {
        // Fetch only orders that are "Ready for Delivery" or "Pending"
        table.setItems(FXCollections.observableArrayList(orderController.getPendingOrders()));
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}