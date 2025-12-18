package view;

import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class RegisterView extends MainView {

    private UserController uc = new UserController();

    public RegisterView(Stage stage) {
        super(stage);
    }

    @Override
    public void show() {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));

        Label lblTitle = new Label("Customer Register");
        lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField txtName = new TextField();
        TextField txtEmail = new TextField();
        txtEmail.setPromptText("must end with @gmail.com");

        PasswordField txtPass = new PasswordField();
        PasswordField txtConfPass = new PasswordField();

        TextField txtPhone = new TextField();
        TextArea txtAddress = new TextArea();
        txtAddress.setPrefRowCount(3);

        // Gender
        ToggleGroup tgGender = new ToggleGroup();
        RadioButton rbMale = new RadioButton("Male");
        RadioButton rbFemale = new RadioButton("Female");
        rbMale.setToggleGroup(tgGender);
        rbFemale.setToggleGroup(tgGender);
        HBox hbGender = new HBox(10, rbMale, rbFemale);

        Button btnRegister = new Button("Register");
        Button btnBack = new Button("Back to Login");

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: red;");

        // Register Action
        btnRegister.setOnAction(e -> {
            String gender =
                    rbMale.isSelected() ? "Male" :
                    rbFemale.isSelected() ? "Female" : null;

            String result = uc.validateRegister(
                    txtName.getText(),
                    txtEmail.getText(),
                    txtPass.getText(),
                    txtConfPass.getText(),
                    txtPhone.getText(),
                    txtAddress.getText(),
                    gender
            );

            if(result.equals("Success")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Registration successful! Please login.");
                alert.showAndWait();
                new LoginView(stage).show();
            } else {
                lblError.setText(result);
            }
        });

        btnBack.setOnAction(e -> new LoginView(stage).show());

        // Layout
        grid.add(lblTitle, 0, 0, 2, 1);

        grid.add(new Label("Full Name:"), 0, 2);
        grid.add(txtName, 1, 2);

        grid.add(new Label("Email:"), 0, 3);
        grid.add(txtEmail, 1, 3);

        grid.add(new Label("Password:"), 0, 4);
        grid.add(txtPass, 1, 4);

        grid.add(new Label("Confirm Password:"), 0, 5);
        grid.add(txtConfPass, 1, 5);

        grid.add(new Label("Phone:"), 0, 6);
        grid.add(txtPhone, 1, 6);

        grid.add(new Label("Address:"), 0, 7);
        grid.add(txtAddress, 1, 7);

        grid.add(new Label("Gender:"), 0, 8);
        grid.add(hbGender, 1, 8);

        grid.add(btnRegister, 0, 9);
        grid.add(btnBack, 1, 9);

        grid.add(lblError, 0, 10, 2, 1);

        stage.setScene(new Scene(grid, 520, 600));
        stage.setTitle("Customer Register");
        stage.show();
    }
}
