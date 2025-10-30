package com.quizapp.service;

import com.quizapp.dao.UserDAO;
import com.quizapp.entity.User;
import com.quizapp.util.HibernateUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DatabaseService extends Service<Void> {
    
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    System.out.println("Background database initialization started...");
                    
                    UserDAO userDAO = new UserDAO(HibernateUtil.getSessionFactory());
                    
                    // Check if admin user exists
                    User adminUser = userDAO.findByUsername("admin");
                    if (adminUser == null) {
                        adminUser = new User();
                        adminUser.setUsername("admin");
                        adminUser.setPassword("admin123");
                        adminUser.setRole("ADMIN");
                        adminUser.setEmail("admin@quizapp.com");
                        adminUser.setFirstName("Admin");
                        adminUser.setLastName("User");
                        userDAO.save(adminUser);
                        System.out.println("Admin user created");
                    }
                    
                    // Check if student user exists
                    User studentUser = userDAO.findByUsername("student");
                    if (studentUser == null) {
                        studentUser = new User();
                        studentUser.setUsername("student");
                        studentUser.setPassword("student123");
                        studentUser.setRole("STUDENT");
                        studentUser.setEmail("student@quizapp.com");
                        studentUser.setFirstName("Student");
                        studentUser.setLastName("User");
                        userDAO.save(studentUser);
                        System.out.println("Student user created");
                    }
                    
                    System.out.println("Database initialization completed");
                } catch (Exception e) {
                    System.err.println("Database initialization error: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}