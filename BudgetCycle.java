package com.example.demo7;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BudgetCycle {
    private int id;
    private double totalAllowance;
    private LocalDate startDate;
    private LocalDate endDate;
    private double remainingBalance;

    // Updated Constructor to include id and name
    public BudgetCycle(int id, double totalAllowance, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.totalAllowance = totalAllowance;
        this.remainingBalance = totalAllowance; // Starts full
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public double calculateDailyLimit() {
        LocalDate today = LocalDate.now();
        LocalDate effectiveStart = today.isBefore(startDate) ? startDate : today;
        long daysLeft = ChronoUnit.DAYS.between(effectiveStart, endDate);

        if (daysLeft <= 0) {
            return remainingBalance;
        }
        return remainingBalance / daysLeft;
    }

    public void updateBalance(double amount) {
        this.remainingBalance -= amount;
    }

    public void addIncome(double amount) {
        this.remainingBalance += amount;
        this.totalAllowance += amount;
    }

    // Getters
    public int getId() { return id; }
    public double getRemainingBalance() { return remainingBalance; }
    public double getTotalAllowance() { return totalAllowance; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }

    @Override
    public String toString() {
        return   " (" + remainingBalance + " left)";
    }
}