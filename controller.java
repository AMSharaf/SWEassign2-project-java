package com.example.demo7;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class controller {
    private List<Transaction> transactions ;
    private BudgetCycle budgetCycle;
    private NotificationManager notification;
    private Report report = new Report();
    private DatabaseManager dbManager;
    private int loggedInUserId = -1;

    public controller() {
        transactions = new ArrayList<>();
        notification = new NotificationManager();
        dbManager = new DatabaseManager();
    }
    public boolean validateInput(double amount) {
        if(budgetCycle.getRemainingBalance()-amount<0) {
            return false;
        }
        else return true;
    }
    public void createBudget(double total,int days){
        budgetCycle= new BudgetCycle(loggedInUserId,total,LocalDate.now(),LocalDate.now().plusDays(days));
        if (loggedInUserId != -1) {
            dbManager.addBudgetCycle(loggedInUserId,total,LocalDate.now().toString(),LocalDate.now().plusDays(days).toString());
        }

    }
    public void addExpense(double amount, category category){
        if (!validateInput(amount)){
            System.out.println("INVALID INPUT");
            return;
        }
        Transaction expense = new Transaction(amount, category, true);

        if (loggedInUserId != -1) {
            dbManager.addTransaction(loggedInUserId, amount, category.getcategory(), expense.getDate().toString(), true);
        }
        transactions.add(expense);
        budgetCycle.updateBalance(amount);
        checkThreshold();
    }

    public void addIncome(double amount){
        Transaction income = new Transaction(amount,false);

        if (loggedInUserId != -1) {
            dbManager.addTransaction(loggedInUserId, amount,null, income.getDate().toString(), false);
        }
        transactions.add(income);

        budgetCycle.addIncome(amount);
        checkThreshold();

    }
    public void clearTransactions(){
        transactions.clear();
    }
    public void getAllTransactions(){
        if(loggedInUserId != -1) {
            dbManager.getAllUserTransactions(loggedInUserId);
        }
    }

    public double calculateDailyLimit(){
        return budgetCycle.calculateDailyLimit();
    }

    public List<Transaction> getTransictions() {
        return transactions;
    }
    public double getRemainingBalance(){
        return budgetCycle.getRemainingBalance();
    }
    public double getTotalAllowance(){
        return budgetCycle.getTotalAllowance();
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
        report.generate(transactions);
    }

    public boolean register(String username, String password) {
        return dbManager.registerUser(username, password);
    }

    public boolean login(String username, String password) {
        int userId = dbManager.authenticateUser(username, password);
        if (userId != -1) {
            if(dbManager.getUserBudgetCycle(userId)==null){
                System.out.println("INVALID INPUT");
                dbManager.addBudgetCycle(userId,0,LocalDate.now().toString(),LocalDate.now().plusDays(30).toString());
            }
            this.loggedInUserId = userId;
            this.transactions = dbManager.getUserTransactions(userId);
            this.budgetCycle= dbManager.getUserBudgetCycle(userId);
            return true;
        }
        return false;
    }

    public void logout() {
        this.loggedInUserId = -1;
        this.transactions.clear(); // clear RAM
    }

}
