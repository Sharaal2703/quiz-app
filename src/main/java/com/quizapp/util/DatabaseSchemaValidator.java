package com.quizapp.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class DatabaseSchemaValidator {
    private final SessionFactory sessionFactory;
    
    public DatabaseSchemaValidator(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public boolean validateSchema() {
        try (Session session = sessionFactory.openSession()) {
            // Check if all required tables exist
            String[] tableNames = {
                "Users", "Quizzes", "Questions", "Options", "Quiz_Attempts", "Answers"
            };
            
            for (String tableName : tableNames) {
                if (!tableExists(session, tableName)) {
                    System.err.println("Table not found: " + tableName);
                    return false;
                }
            }
            
            System.out.println("All required tables exist.");
            return true;
        } catch (Exception e) {
            System.err.println("Schema validation failed: " + e.getMessage());
            return false;
        }
    }
    
    private boolean tableExists(Session session, String tableName) {
        try {
            // Oracle specific query to check if table exists
            Query<Integer> query = session.createNativeQuery(
                "SELECT COUNT(*) FROM ALL_TABLES WHERE TABLE_NAME = :tableName");
            query.setParameter("tableName", tableName.toUpperCase());
            
            Integer count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error checking table existence for " + tableName + ": " + e.getMessage());
            return false;
        }
    }
}