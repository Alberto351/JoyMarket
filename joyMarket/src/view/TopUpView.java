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

        Label lblTitle = new Label("Top Up Balance");
        lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label lblBalance = new Label();

        // tampilkan balance
        if (user instanceof Customer) {
            Customer c = (Customer) user;
            lblBalance.setText("Current Balance: " + c.getBalance());
        }

        TextField txtAmount = new TextField();
        txtAmount.setPromptText("Input amount");

        Label lblMessage = new Label();

        Button btnTopUp = new Button("Top Up");
        Button btnBack = new Button("Back");

        btnTopUp.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(txtAmount.getText());

                boolean success = userController.topUpBalance(user, amount);

                if (success) {
                    lblMessage.setText("Top up successful!");
                    Customer c = (Customer) user;
                    lblBalance.setText("Current Balance: " + c.getBalance());
                } else {
                    lblMessage.setText("Top up failed");
                }

            } catch (NumberFormatException ex) {
                lblMessage.setText("Amount must be a number");
            }
        });

        btnBack.setOnAction(e ->
                new CustomerMainView(stage, user).show()
        );

        VBox root = new VBox(10,
                lblTitle,
                lblBalance,
                txtAmount,
                btnTopUp,
                btnBack,
                lblMessage
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 350, 300));
        stage.setTitle("Top Up");
        stage.show();
    }
}
