package view;

import javafx.stage.Stage;

public abstract class MainView {
    protected Stage stage;
    public MainView(Stage stage) { this.stage = stage; }
    public abstract void show();
}