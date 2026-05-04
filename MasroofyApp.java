package com.example.demo7;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MasroofyApp extends Application {

    private controller appController;
    private final double INITIAL_BUDGET = 0;

    private Stage window;
    private Scene loginScene, signupScene, dashboardScene,addBudgetScene, historyScene;

    private Label balanceLabel;
    private Label dailyLimitLabel;
    private TextField expenseAmountField;
    private TextField incomeAmountField;
    private ComboBox<String> categoryBox;
    private ListView<String> historyListView;

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Masroofy - Expense Tracker");

        appController = new controller();

        initLoginScene();
        initSignupScene();
        initDashboardScene();
        initAddBudgetScene();

        window.setScene(loginScene);
        window.show();
    }
    private void initAddBudgetScene() {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Label title = new Label("Masroofy - Add Budget");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        amountField.setMaxWidth(250);

        TextField numberofDaysField = new TextField();
        numberofDaysField.setPromptText("Days");
        numberofDaysField.setMaxWidth(250);
        Button back = new Button("back");
        back.setOnAction(event -> {
            amountField.clear();
            numberofDaysField.clear();
            window.setScene(dashboardScene);
        });
        back.setStyle("-fx-font-size: 24px;");
        back.setAlignment(Pos.TOP_LEFT);

        Button addBudgetBtn = new Button("add budget");
        addBudgetBtn.setStyle("-fx-font-weight: bold; -fx-pref-width: 100px;");
        addBudgetBtn.setOnAction(e -> {
            double amount = Double.parseDouble(amountField.getText());
            try {
                int days= Integer.parseInt(numberofDaysField.getText());
                appController.createBudget(amount,days);
                refreshUI();
            } catch (NumberFormatException ex) {
                showAlert("Please enter numbers only for the amount.");
            }
            amountField.clear();
            numberofDaysField.clear();
            appController.clearTransactions();
            window.setScene(dashboardScene);
        });

        back.setAlignment(Pos.TOP_LEFT);
        layout.getChildren().addAll(back,title, amountField, numberofDaysField, addBudgetBtn);

        addBudgetScene = new Scene(layout, 800, 400);

    }
    private void initLoginScene() {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Label title = new Label("Masroofy - Login");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(250);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(250);

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-font-weight: bold; -fx-pref-width: 100px;");
        loginBtn.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();

            // Database Validation
            if (appController.login(user, pass)) {
                userField.clear();
                passField.clear();

                window.setScene(dashboardScene); // Go to Dashboard
                refreshUI(); // Load data from DB!
            } else {
                showAlert("Invalid username or password.");
            }
        });

        Button goToSignupBtn = new Button("Don't have an account? Sign Up");
        goToSignupBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: blue; -fx-underline: true;");
        goToSignupBtn.setOnAction(e -> {
            userField.clear();
            passField.clear();
            window.setScene(signupScene); // Go to Signup
        });

        layout.getChildren().addAll(title, userField, passField, loginBtn, goToSignupBtn);
        loginScene = new Scene(layout, 800, 400);
    }

    private void initSignupScene() {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Label title = new Label("Masroofy - Sign Up");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(250);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(250);

        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Confirm Password");
        confirmPassField.setMaxWidth(250);

        Button signupBtn = new Button("Sign Up");
        signupBtn.setStyle("-fx-font-weight: bold; -fx-pref-width: 100px;");
        signupBtn.setOnAction(e -> {
            String user = userField.getText();
            String pass = passField.getText();
            String conf = confirmPassField.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                showAlert("Please fill in all fields.");
            } else if (!pass.equals(conf)) {
                showAlert("Passwords do not match.");
            } else {
                // Save to Database
                if (appController.register(user, pass)) {
                    showAlert("Account created successfully! Please log in.");
                    userField.clear();
                    passField.clear();
                    confirmPassField.clear();
                    window.setScene(loginScene); // Send back to login
                } else {
                    showAlert("Username already exists. Please choose another.");
                }
            }
        });

        Button goToLoginBtn = new Button("Already have an account? Log In");
        goToLoginBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: blue; -fx-underline: true;");
        goToLoginBtn.setOnAction(e -> {
            userField.clear();
            passField.clear();
            confirmPassField.clear();
            window.setScene(loginScene); // Go to Login
        });

        layout.getChildren().addAll(title, userField, passField, confirmPassField, signupBtn, goToLoginBtn);
        signupScene = new Scene(layout, 800, 400);
    }

    private void initDashboardScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(15));

        mainLayout.setTop(createDashboardHeader());
        mainLayout.setCenter(createFormsContainer());
        mainLayout.setRight(createHistoryView());

        dashboardScene = new Scene(mainLayout, 800, 400);
    }

    private BorderPane createDashboardHeader() {
        BorderPane topSection = new BorderPane();
        topSection.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-color: #ccc;");

        VBox dashboardStats = new VBox(5);
        dashboardStats.setAlignment(Pos.CENTER);

        Label title = new Label("Current Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        balanceLabel = new Label("Remaining Balance: $0.00");
        dailyLimitLabel = new Label("Safe Daily Limit: $0.00");

        Button reportBtn = new Button("Print Report to Console");
        reportBtn.setOnAction(e -> appController.generateReport());

        dashboardStats.getChildren().addAll(title, balanceLabel, dailyLimitLabel, reportBtn);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #ff4c4c; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> {
            appController.logout(); // Clear session data in backend
            appController.createBudget(INITIAL_BUDGET, 30); // Reset UI budget
            window.setScene(loginScene); // Send user back to login
        });
        Button addBudget = new Button("add Budget");
        addBudget.setStyle("-fx-background-color: #ff4c4c; -fx-text-fill: white; -fx-font-weight: bold;");
        addBudget.setOnAction(e -> {
            window.setScene(addBudgetScene);
        });

        topSection.setCenter(dashboardStats);
        topSection.setRight(logoutBtn);
        topSection.setLeft(addBudget);
        BorderPane.setAlignment(logoutBtn, Pos.TOP_RIGHT);

        return topSection;
    }

    private VBox createExpenseForm() {
        VBox expenseForm = new VBox(10);
        expenseForm.setPadding(new Insets(20));

        Label formTitle = new Label("Log New Expense");
        formTitle.setStyle("-fx-font-weight: bold;");

        expenseAmountField = new TextField();
        expenseAmountField.setPromptText("Enter Amount");

        categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Food", "Transport", "Education", "Entertainment");
        categoryBox.setPromptText("Select Category");

        Button submitBtn = new Button("Add Expense");
        submitBtn.setOnAction(e -> handleAddExpense());

        expenseForm.getChildren().addAll(formTitle, expenseAmountField, categoryBox, submitBtn);
        return expenseForm;
    }
    private VBox createIncomeForm() {
        VBox IncomeForm = new VBox(10);
        IncomeForm.setPadding(new Insets(20));

        Label formTitle = new Label("Log New Income");
        formTitle.setStyle("-fx-font-weight: bold;");

        incomeAmountField = new TextField();
        incomeAmountField.setPromptText("Enter Amount");

        Button submitBtn = new Button("Add Income");
        submitBtn.setOnAction(e -> handleAddIncome());

        IncomeForm.getChildren().addAll(formTitle, incomeAmountField, submitBtn);
        return IncomeForm;
    }
    private HBox createFormsContainer() {
        HBox formsContainer = new HBox(30);

        formsContainer.setAlignment(Pos.CENTER);

        formsContainer.getChildren().addAll(createExpenseForm(), createIncomeForm());

        return formsContainer;
    }
    private VBox createHistoryView() {
        VBox historySection = new VBox(10);
        historySection.setPadding(new Insets(10));

        Label historyTitle = new Label("Transaction History");
        historyTitle.setStyle("-fx-font-weight: bold;");

        historyListView = new ListView<>();
        historyListView.setPrefWidth(300);

        Button refreshBtn = new Button("Refresh History");
        refreshBtn.setOnAction(e -> refreshUI());
        Button allHisotryBtn = new Button("All Transactions");
        allHisotryBtn.setOnAction(e -> {
            historyListView.getItems().clear();
            for (Transaction transaction : appController.getAllTransactions()) {
                historyListView.getItems().add(transaction.toString());
            }
        });
        historySection.getChildren().addAll(historyTitle, historyListView, refreshBtn, allHisotryBtn);
        return historySection;
    }

    private void handleAddExpense() {
        try {
            double amount = Double.parseDouble(expenseAmountField.getText());
            String selectedCategory = categoryBox.getValue();
            if (selectedCategory != null && appController.validateInput(amount)  ) {
                category newCat = new category(selectedCategory);
                //System.out.println(newCat.getcategory());
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



    private void refreshUI() {
        historyListView.getItems().clear();
        double totalSpent = 0;

        for (Transaction transaction : appController.getTransictions()) {
            historyListView.getItems().add(transaction.toString());
            if(transaction.isExpense()){
                totalSpent += transaction.getAmount();
            }
        }

        double currentBalance = appController.getRemainingBalance();
        balanceLabel.setText(String.format("Remaining Balance: $%.2f", currentBalance));

        double safeLimit = appController.calculateDailyLimit();
        dailyLimitLabel.setText(String.format("Safe Daily Limit: $%.2f", safeLimit));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("System Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}