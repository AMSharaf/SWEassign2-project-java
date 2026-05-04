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
        String drop = "DROP TABLE IF EXISTS users;";
        String drop2 = "DROP TABLE IF EXISTS transactions;";
        String drop3 = "DROP TABLE IF EXISTS budgets;";

        String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT UNIQUE NOT NULL,"
                + "password TEXT NOT NULL"
                + ");";

        String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER NOT NULL,"
                + "budget_id INTEGER NOT NULL,"
                + "amount REAL NOT NULL,"
                + "category TEXT ,"
                + "date TEXT NOT NULL,"
                + "is_expense BOOLEAN NOT NULL," // Keeps the boolean we added earlier
                + "FOREIGN KEY(user_id) REFERENCES users(id),"
                + "FOREIGN KEY(budget_id) REFERENCES budgets(id)"
                + ");";

        String budgetsTable = "CREATE TABLE IF NOT EXISTS budgets ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER NOT NULL,"
                + "amount REAL NOT NULL,"
                + "remaining REAL NOT NULL,"
                + "start_date TEXT NOT NULL,"
                + "end_date TEXT NOT NULL,"
                + "FOREIGN KEY(user_id) REFERENCES users(id)"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
//            stmt.execute(drop);
//            stmt.execute(drop2);
//            stmt.execute(drop3);
            stmt.execute(usersTable);
            stmt.execute(transactionsTable);
            stmt.execute(budgetsTable);
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
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return -1;
    }

    public void addTransaction(int userId, double amount, String category, String date, boolean isExpense) {
        String getBudgetSql = "SELECT id FROM budgets WHERE user_id = ?";

        String insertSql = "INSERT INTO transactions(user_id, budget_id, amount, category, date, is_expense) VALUES(?, ?, ?, ?, ?, ?)";

        String updateBudgetSql;
        if (isExpense) {
            updateBudgetSql = "UPDATE budgets SET amount = amount - ? WHERE user_id = ? ";
        } else {
            updateBudgetSql = "UPDATE budgets SET amount = amount + ? WHERE user_id = ? ";
        }

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement getBudgetStmt = conn.prepareStatement(getBudgetSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateBudgetSql)) {

                getBudgetStmt.setInt(1, userId);
                ResultSet rs = getBudgetStmt.executeQuery();

                int budgetId = -1;
                if (rs.next()) {
                    budgetId = rs.getInt("id");
                } else {
                    System.out.println("Cannot add transaction: No active budget found for this user.");
                    conn.rollback();
                    return;
                }

                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, budgetId);
                insertStmt.setDouble(3, amount);
                insertStmt.setString(4, category);
                insertStmt.setString(5, date);
                insertStmt.setBoolean(6, isExpense);
                insertStmt.executeUpdate();

                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, userId);
                int rowsUpdated = updateStmt.executeUpdate();

                conn.commit();

                if (rowsUpdated == 0) {
                    System.out.println("Transaction saved, but no active budget cycle was found to update.");
                } else {
                    System.out.println("Transaction saved and budget updated successfully!");
                }

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction failed, rolling back: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    public List<Transaction> getUserTransactions(int userId) {
        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT amount, category, date, is_expense FROM transactions WHERE budget_id = (SELECT id FROM budgets WHERE user_id = ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                double amount = rs.getDouble("amount");
                String categoryName = rs.getString("category");
                LocalDate date = LocalDate.parse(rs.getString("date"));
                boolean is_exp = rs.getBoolean("is_expense");

                Transaction trans;
                if(is_exp) {
                    trans = new Transaction(amount, new category(categoryName), date, is_exp);
                } else {
                    trans = new Transaction(amount, null, date, is_exp);
                }
                transactions.add(trans);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }
        return transactions;
    }
    public List<Transaction> getAllUserTransactions(int userId) {

        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT amount, category, date, is_expense FROM transactions WHERE user_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();



            while (rs.next()) {

                double amount = rs.getDouble("amount");

                String categoryName = rs.getString("category");

                LocalDate date = LocalDate.parse(rs.getString("date"));

                boolean is_exp = rs.getBoolean("is_expense");

                Transaction trans;
                if(is_exp)
                    trans = new Transaction(amount, new category(categoryName), date,is_exp );
                else trans= new Transaction(amount, null, date,is_exp);

                transactions.add(trans);

            }

        } catch (SQLException e) {

            System.out.println("Error fetching expenses: " + e.getMessage());

        }

        return transactions;

    }


    public void addBudgetCycle(int userId, double amount, String startDate, String endDate) {
        String deleteSql = "DELETE FROM budgets WHERE user_id = ?";
        String insertSql = "INSERT INTO budgets(user_id, amount,remaining, start_date, end_date) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                deleteStmt.setInt(1, userId);
                int deletedRows = deleteStmt.executeUpdate();
                if (deletedRows > 0) {
                    System.out.println("Overwriting " + deletedRows + " old budget(s)...");
                }

                insertStmt.setInt(1, userId);
                insertStmt.setDouble(2, amount);
                insertStmt.setDouble(3, amount);
                insertStmt.setString(4, startDate);
                insertStmt.setString(5, endDate);
                insertStmt.executeUpdate();

                conn.commit();
                System.out.println("New budget cycle saved successfully!");

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error saving budget cycle, rolling back: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    public BudgetCycle getUserBudgetCycle(int userId) {
        String sql = "SELECT id, amount, start_date, end_date FROM budgets WHERE user_id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                double amount = rs.getDouble("amount");
                LocalDate startDate = LocalDate.parse(rs.getString("start_date"));
                LocalDate endDate = LocalDate.parse(rs.getString("end_date"));
                return new BudgetCycle(id, amount, startDate, endDate);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching budget cycle: " + e.getMessage());
        }
        return null;
    }
}