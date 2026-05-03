package com.example.demo7;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:masroofy.db";

    public DatabaseManager() {
        createTables();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void createTables() {
        String drop="DROP TABLE IF EXISTS users ;";
        String drop2="DROP TABLE IF EXISTS expenses;";
        String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT UNIQUE NOT NULL,"
                + "password TEXT NOT NULL"
                + ");";

        String expensesTable = "CREATE TABLE IF NOT EXISTS expenses ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER NOT NULL,"
                + "amount REAL NOT NULL,"
                + "category TEXT NOT NULL,"
                + "date TEXT NOT NULL,"
                + "FOREIGN KEY(user_id) REFERENCES users(id)"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
//            stmt.execute(drop);
//            stmt.execute(drop2);
            stmt.execute(usersTable);
            stmt.execute(expensesTable);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }


    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Registration failed (username might exist): " + e.getMessage());
            return false;
        }
    }

    public int authenticateUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Return the user's ID
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return -1;
    }


    public void addExpense(int userId, double amount, String category, String date) {
        String sql = "INSERT INTO expenses(user_id, amount, category, date) VALUES(?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, category);
            pstmt.setString(4, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving expense: " + e.getMessage());
        }
    }

    public List<Expense> getUserExpenses(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT amount, category, date FROM expenses WHERE user_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                double amount = rs.getDouble("amount");
                String categoryName = rs.getString("category");
                LocalDate date = LocalDate.parse(rs.getString("date"));

                Expense exp = new Expense(amount, new category(categoryName), date);
                expenses.add(exp);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching expenses: " + e.getMessage());
        }
        return expenses;
    }
}