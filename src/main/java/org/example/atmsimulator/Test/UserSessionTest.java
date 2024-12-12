package org.example.atmsimulator.Test;

import org.example.atmsimulator.DatabaseConn;
import org.example.atmsimulator.UserSession;
import org.junit.Test;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserSessionTest {
    private static Connection connection;

    @Test
    public void testCreateUserSession() throws SQLException {
        UserSession user = UserSession.getInstance();
        user.setId(1);
        user.setName("Alice");
        user.setSurname("Smith");
        user.setDateOfBirth(Date.valueOf("1990-01-01"));
        user.setGender("Female");
        user.setCardNumber("1234-1234-1234-1234");
        user.setPINCode("1234");
        user.setTelephone("0123456789");
        user.setBalance(1000.00);
        user.setClientName("Alice");
        user.setClientNumber("9876543210");
        user.setClientId(1);
        assertEquals(1, user.getId());
    }

    @Test
    public void testReadUserSession() {
        UserSession user = UserSession.getInstance();
        assertEquals("Alice", user.getName());
        assertEquals("Smith", user.getSurname());
    }

    @Test
    public void testUpdateUserSession() {
        UserSession user = UserSession.getInstance();
        user.setTelephone("0987654321");
        assertEquals("0987654321", user.getTelephone());
    }
}
