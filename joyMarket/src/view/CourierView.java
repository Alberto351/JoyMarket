package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;

public class CourierView extends MainView {

    private User user;

    public CourierView(Stage stage, User user) {
        super(stage);
        this.user = user;
    }

    @Override
    public void show() {

        Label lblTitle = new Label("Courier Dashboard");
        lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label lblWelcome = new Label("Welcome, " + user.getFullName());
        Label lblInfo = new Label("Delivery feature is not implemented yet.");

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e ->
                new LoginView(stage).show()
        );

        VBox root = new VBox(15,
                lblTitle,
                lblWelcome,
                lblInfo,
                btnLogout
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 350, 250));
        stage.setTitle("JoymarKet - Courier");
        stage.show();
    }
}