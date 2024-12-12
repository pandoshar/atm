package org.example.atmsimulator.Test;

import org.example.atmsimulator.Transaction;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class TransactionTest {
    private static Connection connection;

    @Test
    public void testCreateTransaction() throws SQLException {
        int accountId = 1;
        double amount = 150.00;
        String insertTransactionQuery = "INSERT INTO transaction (account_id, transaction_type, amount) VALUES (?, 'Withdrawal', ?)";

        try (PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {
            insertTransactionStmt.setInt(1, accountId);
            insertTransactionStmt.setDouble(2, amount);
            insertTransactionStmt.executeUpdate();
        }

        Transaction transaction = findTransactionById(1);
        assertNotNull(transaction);
        assertEquals(accountId, transaction.getTransactionId());
        assertEquals("Withdrawal", transaction.getTransactionType());
        assertEquals(amount, transaction.getAmount());
    }

    @Test
    public void testReadTransaction() throws SQLException {
        Transaction transaction = findTransactionById(1);
        assertNotNull(transaction);
        assertEquals("Withdrawal", transaction.getTransactionType());
    }

    @Test
    public void testUpdateTransaction() throws SQLException {
        Transaction transaction = findTransactionById(1);
        transaction.setAmount(200.00);

        String updateTransactionQuery = "UPDATE transaction SET amount = ? WHERE transaction_id = ?";
        try (PreparedStatement updateTransactionStmt = connection.prepareStatement(updateTransactionQuery)) {
            updateTransactionStmt.setDouble(1, transaction.getAmount());
            updateTransactionStmt.setInt(2, transaction.getTransactionId());
            updateTransactionStmt.executeUpdate();
        }

        assertEquals(200.00, findTransactionById(1).getAmount());
    }

    @Test
    public void testDeleteTransaction() throws SQLException {
        deleteTransaction(1);
        assertNull(findTransactionById(1));
    }

    static Transaction findTransactionById(int id) throws SQLException {
        String selectQuery = "SELECT * FROM transaction WHERE transaction_id = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, id);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                return new Transaction(rs.getInt("transaction_id"), rs.getString("transaction_type"), rs.getDouble("amount"), rs.getString("transaction_date"));
            }
        }
        return null;
    }

    static void deleteTransaction(int id) throws SQLException {
        String deleteQuery = "DELETE FROM transaction WHERE transaction_id = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
            deleteStmt.setInt(1, id);
            deleteStmt.executeUpdate();
        }
    }
}
