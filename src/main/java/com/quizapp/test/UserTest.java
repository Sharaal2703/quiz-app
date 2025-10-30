package com.quizapp.test;

import com.quizapp.dao.UserDAO;
import com.quizapp.entity.User;
import com.quizapp.util.HibernateUtil;

public class UserTest {
    public static void main(String[] args) {
        try {
            UserDAO userDAO = new UserDAO(HibernateUtil.getSessionFactory());
            
            // Check if admin user exists
            User admin = userDAO.findByUsername("admin");
            if (admin != null) {
                System.out.println("Admin user found:");
                System.out.println("  Username: '" + admin.getUsername() + "'");
                System.out.println("  Password: '" + admin.getPassword() + "' (length: " + admin.getPassword().length() + ")");
                System.out.println("  Role: '" + admin.getRole() + "'");
                System.out.println("  Email: '" + admin.getEmail() + "'");
                System.out.println("  First Name: '" + admin.getFirstName() + "'");
                System.out.println("  Last Name: '" + admin.getLastName() + "'");
            } else {
                System.out.println("Admin user not found");
            }
            
            // Check if student user exists
            User student = userDAO.findByUsername("student");
            if (student != null) {
                System.out.println("\nStudent user found:");
                System.out.println("  Username: '" + student.getUsername() + "'");
                System.out.println("  Password: '" + student.getPassword() + "' (length: " + student.getPassword().length() + ")");
                System.out.println("  Role: '" + student.getRole() + "'");
                System.out.println("  Email: '" + student.getEmail() + "'");
                System.out.println("  First Name: '" + student.getFirstName() + "'");
                System.out.println("  Last Name: '" + student.getLastName() + "'");
            } else {
                System.out.println("Student user not found");
            }
            
            // Test password comparison
            if (admin != null) {
                String testPassword = "admin123";
                boolean match = testPassword.equals(admin.getPassword());
                System.out.println("\nPassword comparison test:");
                System.out.println("  Test password: '" + testPassword + "'");
                System.out.println("  Stored password: '" + admin.getPassword() + "'");
                System.out.println("  Match: " + match);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}