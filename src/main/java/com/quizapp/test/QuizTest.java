package com.quizapp.test;

import com.quizapp.dao.QuizDAO;
import com.quizapp.dao.UserDAO;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.util.HibernateUtil;

public class QuizTest {
    public static void main(String[] args) {
        try {
            // Get or create the admin user
            UserDAO userDAO = new UserDAO(HibernateUtil.getSessionFactory());
            User admin = userDAO.findByUsername("admin");
            
            if (admin == null) {
                admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setRole("ADMIN");
                admin.setEmail("admin@quizapp.com");
                admin.setFirstName("Admin");
                admin.setLastName("User");
                userDAO.save(admin);
                System.out.println("Created admin user with ID: " + admin.getUserId());
            } else {
                System.out.println("Found admin user w"
                		+ "ith ID: " + admin.getUserId());
            }
            
            // Create a new quiz
            Quiz quiz = new Quiz("Test Quiz", "A test quiz", 10, admin);
            
            // Save the quiz
            QuizDAO quizDAO = new QuizDAO(HibernateUtil.getSessionFactory());
            quizDAO.save(quiz);
            
            System.out.println("Quiz created successfully with ID: " + quiz.getQuizId());
            
            // Retrieve all quizzes
            quizDAO.findAll().forEach(q -> 
                System.out.println("Quiz: " + q.getTitle() + " by " + q.getCreatedBy().getUsername()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}