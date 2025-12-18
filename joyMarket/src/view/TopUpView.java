package view;

import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Customer;
import model.User;

public class TopUpView extends MainView {

    private User user;
    private UserController userController = new UserController();

    public TopUpView(Stage stage, User user) {
        super(stage);
        this.user = user;
    }

    @Override
    public void show() {
        // --- UI Elements ---
        Label lblTitle = new Label("Top Up Balance");
        lblTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label lblBalance = new Label("Current Balance: 0.0");
        lblBalance.setStyle("-fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");

        // Display current balance from User object
        if (user instanceof Customer) {
            Customer c = (Customer) user;
            lblBalance.setText("Current Balance: $" + String.format("%.2f", c.getBalance()));
        }

        TextField txtAmount = new TextField();
        txtAmount.setPromptText("Enter amount (e.g. 50.00)");
        txtAmount.setMaxWidth(200);

        Label lblMessage = new Label();
        lblMessage.setWrapText(true);

        Button btnTopUp = new Button("Confirm Top Up");
        Button btnBack = new Button("Back to Market");

        // --- Styling ---
        btnTopUp.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20;");
        btnBack.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 8 20;");
        btnTopUp.setMinWidth(150);
        btnBack.setMinWidth(150);

        // --- Logic & Validations ---
        btnTopUp.setOnAction(e -> {
            String input = txtAmount.getText().trim();

            // 1. Validation: Must be filled
            if (input.isEmpty()) {
                lblMessage.setText("Error: Amount must be filled.");
                lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            try {
                // 2. Validation: Must be numeric
                double amount = Double.parseDouble(input);

                // 3. Validation: Must be positive (greater than 0)
                if (amount <= 0) {
                    lblMessage.setText("Error: Amount must be greater than 0.");
                    lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                    return;
                }

                // Call Controller to update SQL Database
                boolean success = userController.topUpBalance(user, amount);

                if (success) {
                    lblMessage.setText("Top up successful! Balance updated.");
                    lblMessage.setStyle("-fx-text-fill: #27ae60;");
                    
                    // Sync the local Customer object so UI stays updated
                    if (user instanceof Customer) {
                        Customer c = (Customer) user;
                        // Assuming userController.topUpBalance updates the c.balance field internally
                        lblBalance.setText("Current Balance: $" + String.format("%.2f", c.getBalance()));
                    }
                    txtAmount.clear();
                } else {
                    lblMessage.setText("Top up failed. Please try again.");
                    lblMessage.setStyle("-fx-text-fill: #e74c3c;");
                }

            } catch (NumberFormatException ex) {
                lblMessage.setText("Error: Amount must be a valid number.");
                lblMessage.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        btnBack.setOnAction(e -> new CustomerMainView(stage, user).show());

        // --- Layout ---
        VBox root = new VBox(15,
                lblTitle,
                lblBalance,
                new Label("Top Up Amount:"),
                txtAmount,
                btnTopUp,
                btnBack,
                lblMessage
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #ecf0f1;");

        stage.setScene(new Scene(root, 400, 350));
        stage.setTitle("JoymarKet - Wallet");
        stage.show();
    }
}