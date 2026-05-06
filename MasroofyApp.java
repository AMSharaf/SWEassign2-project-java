package com.example.demo7;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MasroofyApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        MasroofyUIController guiController = new MasroofyUIController();

        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/com/example/demo7/login.fxml"));
        loginLoader.setController(guiController);
        Scene loginScene = new Scene(loginLoader.load());

        FXMLLoader signupLoader = new FXMLLoader(getClass().getResource("/com/example/demo7/signup.fxml"));
        signupLoader.setController(guiController);
        Scene signupScene = new Scene(signupLoader.load());

        FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/com/example/demo7/Dashboard.fxml"));
        dashboardLoader.setController(guiController);
        Scene dashboardScene = new Scene(dashboardLoader.load());

        FXMLLoader budgetLoader = new FXMLLoader(getClass().getResource("/com/example/demo7/BudgetScene.fxml"));
        budgetLoader.setController(guiController);
        Scene addBudgetScene = new Scene(budgetLoader.load());

        guiController.setScenes(primaryStage, loginScene, signupScene, dashboardScene, addBudgetScene);

        primaryStage.setTitle("Masroofy - Expense Tracker");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}