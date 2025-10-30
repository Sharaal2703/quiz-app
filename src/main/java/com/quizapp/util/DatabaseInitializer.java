package com.quizapp.util;

import com.quizapp.dao.UserDAO;
import com.quizapp.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class DatabaseInitializer {
    private final SessionFactory sessionFactory;
    
    public DatabaseInitializer(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public void initializeDatabase() {
        try {
            System.out.println("Starting database initialization...");
            
            // Check if tables exist before executing SQL script
            if (!tablesExist()) {
                System.out.println("Tables do not exist, executing SQL script...");
                // Execute SQL setup script
                executeSqlScript();
            } else {
                System.out.println("Tables already exist, skipping SQL script execution.");
            }
            
            // Insert initial data using Hibernate
            System.out.println("Inserting initial data...");
            insertInitialData();
            
            System.out.println("Database initialization completed successfully.");
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean tablesExist() {
        try (Session session = sessionFactory.openSession()) {
            // Check if Users table exists
            try {
                session.createNativeQuery("SELECT COUNT(*) FROM Users").getSingleResult();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
    
    private void executeSqlScript() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            
            try {
                // Read the SQL script from resources
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("database/setup.sql");
                if (inputStream == null) {
                    throw new RuntimeException("SQL script not found: database/setup.sql");
                }
                
                String sqlScript = new BufferedReader(new InputStreamReader(inputStream))
                        .lines()
                        .collect(Collectors.joining("\n"));
                
                // Split the script into individual statements
                String[] statements = sqlScript.split(";");
                
                // Execute each statement
                for (String statement : statements) {
                    if (!statement.trim().isEmpty()) {
                        try {
                            session.createNativeQuery(statement).executeUpdate();
                        } catch (Exception e) {
                            // Some statements might fail if they already exist
                            System.out.println("SQL statement execution note: " + e.getMessage());
                        }
                    }
                }
                
                transaction.commit();
                System.out.println("SQL script executed successfully.");
            } catch (Exception e) {
                if (transaction.getStatus() == TransactionStatus.ACTIVE) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
    
    private void insertInitialData() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            
            try {
                // Check if admin user already exists
                User adminUser = session.createQuery(
                    "FROM User WHERE username = :username", User.class)
                    .setParameter("username", "admin")
                    .uniqueResult();
                
                if (adminUser == null) {
                    // Create default admin user
                    adminUser = new User();
                    adminUser.setUsername("admin");
                    adminUser.setPassword("admin123");
                    adminUser.setRole("ADMIN");
                    adminUser.setEmail("admin@quizapp.com");
                    adminUser.setFirstName("Admin");
                    adminUser.setLastName("User");
                    
                    session.save(adminUser);
                    System.out.println("Default admin user created.");
                } else {
                    System.out.println("Admin user already exists.");
                }
                
                // Check if student user already exists
                User studentUser = session.createQuery(
                    "FROM User WHERE username = :username", User.class)
                    .setParameter("username", "student")
                    .uniqueResult();
                
                if (studentUser == null) {
                    // Create default student user
                    studentUser = new User();
                    studentUser.setUsername("student");
                    studentUser.setPassword("student123");
                    studentUser.setRole("STUDENT");
                    studentUser.setEmail("student@quizapp.com");
                    studentUser.setFirstName("Student");
                    studentUser.setLastName("User");
                    
                    session.save(studentUser);
                    System.out.println("Default student user created.");
                } else {
                    System.out.println("Student user already exists.");
                }
                
                transaction.commit();
                System.out.println("Initial data insertion completed successfully.");
            } catch (Exception e) {
                if (transaction.getStatus() == TransactionStatus.ACTIVE) {
                    transaction.rollback();
                }
                throw e;
            }
        } catch (Exception e) {
            System.err.println("Error inserting initial data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}