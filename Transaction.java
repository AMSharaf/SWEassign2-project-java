package com.example.demo7;

import java.time.LocalDate;

public class Transaction {
    private double amount;
    private LocalDate date;
    private category category;

    // NEW: Boolean to track if this is an expense (true) or income (false)
    private boolean isExpense;

    // Constructor for when the user doesn't provide a date (defaults to today)
    public Transaction(double amount, category category, boolean isExpense) {
        this.amount = amount;
        this.category = category;
        this.date = LocalDate.now();
        this.isExpense = isExpense;
    }

    // Constructor for when the user provides a specific date (e.g., when loading from database)
    public Transaction(double amount, category category, LocalDate date, boolean isExpense) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.isExpense = isExpense;
    }

    public Transaction(double amount, boolean isExpense) {
        this.amount = amount;
        this.date = LocalDate.now();;
        this.isExpense = isExpense;
    }

    // Getters
    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public category getCategory() {
        return category;
    }

    public boolean isExpense() {
        return isExpense;
    }

    // Placeholder methods
    public void save(){
        System.out.println("Transaction saved");
    }

    public void update(){
        System.out.println("Transaction updated");
    }

    public void delete(){
        System.out.println("Transaction deleted");
    }

    @Override
    public String toString() {
        String typeString = isExpense ? "EXPENSE" : "INCOME";
        if(category!=null)
            return typeString + " - Amount: $" + amount + ", Date: " + date + ", Category: " + category.toString();
        else
            return typeString + " - Amount: $" + amount + ", Date: " + date;
    }

}