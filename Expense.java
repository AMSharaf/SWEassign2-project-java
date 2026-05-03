package com.example.demo7;

import java.time.LocalDate;



public class Expense {
    private double amount ;
    private LocalDate date;
    private category category;

    // Keep your original constructor...
    public Expense(double amount, category category) {
        this.amount = amount;
        this.category = category;
        this.date = LocalDate.now();
    }

    public Expense(double amount, category category, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.date = date;
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


    public void save(){
        System.out.println("Expense saved");
    }
    public void update(){
        System.out.println("Expense updated");
    }
    public void delete(){
        System.out.println("Expense deleted");
    }
    @Override
    public String toString() {
        return "amount: " + amount + ", date: " + date + ", category: " + category;
    }

}
