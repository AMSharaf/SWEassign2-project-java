package com.example.demo7;

import java.time.LocalDate;

public class Transaction {
    private double amount;
    private LocalDate date;
    private category category;

    private boolean isExpense;

    public Transaction(double amount, category category, boolean isExpense) {
        this.amount = amount;
        this.category = category;
        this.date = LocalDate.now();
        this.isExpense = isExpense;
    }

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
            return typeString + " - Amount: $" + amount + ", Date: " + date + ", Category: " + category.getcategory();
        else
            return typeString + " - Amount: $" + amount + ", Date: " + date;
    }

}