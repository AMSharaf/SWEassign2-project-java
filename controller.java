package com.example.demo7;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class controller {
    private List<Expense> expenses;
    private BudgetCycle budgetCycle;
    private NotificationManager notification;
    private Report report = new Report();

    private DatabaseManager dbManager;
    private int loggedInUserId = -1;

    public controller() {
        expenses = new ArrayList<>();
        notification = new NotificationManager();
        dbManager = new DatabaseManager();
    }
    public boolean validateInput(double amount) {
        if(budgetCycle.getTotalAllowance()-amount<0) {
            return false;
        }
        else return true;
    }
    public void createBudget(double total,int days){
        budgetCycle= new BudgetCycle(total,LocalDate.now(),LocalDate.now().plusDays(days));
    }
    public void addExpense(double amount, category category){
        if (!validateInput(amount)){
            System.out.println("INVALID INPUT");
            return;
        }
        Expense expense = new Expense(amount, category);

        if (loggedInUserId != -1) {
            dbManager.addExpense(loggedInUserId, amount, category.getcategory(), expense.getDate().toString());
        }

        expenses.add(expense);
        budgetCycle.updateBalance(amount);
        checkThreshold();
    }

    public double calculateDailyLimit(){
        return budgetCycle.calculateDailyLimit();
    }

    public List<Expense> getTransictions() {
        return expenses;
    }
    public void checkThreshold(){
        double total = budgetCycle.getTotalAllowance();
        double remaining = budgetCycle.getRemainingBalance();
        double spent = total - remaining;

        double percentage = (spent/total)*100;

        if (percentage>=80){
            notification.sendalert("you have spent "+ percentage+"% of your buget!");
        }
    }

    public void generateReport(){
        report.generate(expenses);
    }

    public boolean register(String username, String password) {
        return dbManager.registerUser(username, password);
    }

    public boolean login(String username, String password) {
        int userId = dbManager.authenticateUser(username, password);
        if (userId != -1) {
            this.loggedInUserId = userId;
            this.expenses = dbManager.getUserExpenses(userId);

            for(Expense e : expenses) {
                budgetCycle.updateBalance(e.getAmount());
            }
            return true;
        }
        return false;
    }

    public void logout() {
        this.loggedInUserId = -1;
        this.expenses.clear(); // clear RAM
    }

}
