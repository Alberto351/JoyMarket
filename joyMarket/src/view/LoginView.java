package view;

import controller.UserController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.User;

public class LoginView extends MainView {

    private UserController userController = new UserController();

    public LoginView(Stage stage) {
        super(stage);
    }

    @Override
    public void show() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);

        Label lblTitle = new Label("JoymarKet Login");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Email");

        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        Button btnLogin = new Button("Login");

        btnLogin.setOnAction(e -> {
            User user = userController.login(
                txtEmail.getText(),
                txtPassword.getText()
            );

            if (user == null) {
                lblError.setText("Invalid email or password");
                return;
            }

            // Redirect based on role
            switch (user.getRole()) {
                case "Admin":
                    new AdminView(stage, user).show();
                    break;
                case "Customer":
                    new CustomerView(stage, user).show();
                    break;
                case "Courier":
                    new CourierView(stage, user).show();
                    break;
                default:
                    lblError.setText("Unknown role");
            }
        });

        grid.add(lblTitle, 0, 0, 2, 1);
        grid.add(new Label("Email"), 0, 1);
        grid.add(txtEmail, 1, 1);
        grid.add(new Label("Password"), 0, 2);
        grid.add(txtPassword, 1, 2);
        grid.add(btnLogin, 0, 3, 2, 1);
        grid.add(lblError, 0, 4, 2, 1);

        stage.setScene(new Scene(grid, 400, 300));
        stage.setTitle("JoymarKet");
        stage.show();
    }
}
