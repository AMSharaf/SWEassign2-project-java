package com.example.demo7;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class MasroofyUIController {

    private controller appController = new controller();
    private final double INITIAL_BUDGET = 0;

    private Stage window;
    private Scene loginScene, signupScene, dashboardScene, addBudgetScene;

    private double total_expense=0;


    @FXML private TextField loginUserField;
    @FXML private PasswordField loginPassField;

    @FXML private TextField signupUserField;
    @FXML private PasswordField signupPassField;
    @FXML private PasswordField signupConfirmPassField;

    @FXML private Label balanceLabel;
    @FXML private Label dailyLimitLabel;
    @FXML private TextField incomeAmountField;
    @FXML private TextField expenseAmountField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private ListView<String> historyListView;
    @FXML private Label totalExpense;



    @FXML private TextField budgetAmountField;
    @FXML private TextField budgetDaysField;

    // Called automatically by JavaFX after an FXML file is loaded
    @FXML
    public void initialize() {
        if (categoryBox != null) {
            categoryBox.getItems().clear();
            categoryBox.getItems().addAll("Food", "Transport", "Education", "Entertainment");
        }
    }

    public void setScenes(Stage window, Scene login, Scene signup, Scene dashboard, Scene addBudget) {
        this.window = window;
        this.loginScene = login;
        this.signupScene = signup;
        this.dashboardScene = dashboard;
        this.addBudgetScene = addBudget;
    }


    @FXML
    private void handleLogin() {
        String user = loginUserField.getText();
        String pass = loginPassField.getText();

        if (appController.login(user, pass)) {
            loginUserField.clear();
            loginPassField.clear();
            window.setScene(dashboardScene);
            refreshUI();
        } else {
            showAlert("Invalid username or password.");
        }
    }

    @FXML
    private void handleSignup() {
        String user = signupUserField.getText();
        String pass = signupPassField.getText();
        String conf = signupConfirmPassField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showAlert("Please fill in all fields.");
        } else if (!pass.equals(conf)) {
            showAlert("Passwords do not match.");
        } else {
            if (appController.register(user, pass)) {
                showAlert("Account created successfully! Please log in.");
                signupUserField.clear();
                signupPassField.clear();
                signupConfirmPassField.clear();
                window.setScene(loginScene);
            } else {
                showAlert("Username already exists. Please choose another.");
            }
        }
    }

    @FXML
    private void handleAddExpense() {
        try {
            double amount = Double.parseDouble(expenseAmountField.getText());
            String selectedCategory = categoryBox.getValue();
            if (selectedCategory != null && appController.validateInput(amount)) {
                category newCat = new category(selectedCategory);
                appController.addExpense(amount, newCat);
                expenseAmountField.clear();
                categoryBox.setValue(null);
                refreshUI();
            } else {
                showAlert("Please enter a valid amount and select a category.");
            }
        } catch (NumberFormatException ex) {
            showAlert("Please enter numbers only for the amount.");
        }
    }

    @FXML
    private void handleAddIncome() {
        try {
            double amount = Double.parseDouble(incomeAmountField.getText());
            appController.addIncome(amount);
            incomeAmountField.clear();
            refreshUI();
        } catch (NumberFormatException ex) {
            showAlert("Please enter numbers only for the amount.");
        }
    }

    @FXML
    private void handleAddBudget() {
        try {
            double amount = Double.parseDouble(budgetAmountField.getText());
            int days = Integer.parseInt(budgetDaysField.getText());
            appController.createBudget(amount, days);
            refreshUI();
            budgetAmountField.clear();
            budgetDaysField.clear();
            appController.clearTransactions();
            window.setScene(dashboardScene);
            refreshUI();
        } catch (NumberFormatException ex) {
            showAlert("Please enter numbers only for the amount and days.");
        }
    }

    @FXML
    private void handleLogout() {
        appController.logout();
        appController.createBudget(INITIAL_BUDGET, 30);
        window.setScene(loginScene);
    }

    @FXML
    private void showAllHistory() {
        historyListView.getItems().clear();
        for (Transaction transaction : appController.getAllTransactions()) {
            historyListView.getItems().add(transaction.toString());
        }
    }

    @FXML
    private void generateReport() {
        appController.generateReport();
    }

    @FXML
    private void refreshUI() {
        total_expense=0;
        if (historyListView == null) return;

        historyListView.getItems().clear();
        for (Transaction transaction : appController.getTransictions()) {
            historyListView.getItems().add(transaction.toString());
            if(transaction.isExpense()==true){
                total_expense+=transaction.getAmount();
            }

        }
        totalExpense.setText(String.format("%.2f EGP",total_expense));
        balanceLabel.setText(String.format("%.2f EGP", appController.getRemainingBalance()));
        dailyLimitLabel.setText(String.format("%.2f EGP", appController.calculateDailyLimit()));
    }

    // --- Scene Navigation Switchers ---
    @FXML private void goToSignup() { window.setScene(signupScene); }
    @FXML private void goToLogin() { window.setScene(loginScene); }
    @FXML private void goToAddBudget() { window.setScene(addBudgetScene); }
    @FXML private void goToDashboard() { window.setScene(dashboardScene); }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("System Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}