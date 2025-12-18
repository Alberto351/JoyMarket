package view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;

public class AdminView extends MainView {

    private User user;

    public AdminView(Stage stage, User user) {
        super(stage);
        this.user = user;
    }

    @Override
    public void show() {
        Button btnEditStock = new Button("Edit Product Stock");
        Button btnLogout = new Button("Logout");

        btnEditStock.setOnAction(e ->
                new AdminEditStockView(stage, user).show()
        );

        btnLogout.setOnAction(e ->
                new LoginView(stage).show()
        );

        VBox root = new VBox(10, btnEditStock, btnLogout);
        stage.setScene(new Scene(root, 300, 200));
        stage.setTitle("Admin Menu");
        stage.show();
    }
}
