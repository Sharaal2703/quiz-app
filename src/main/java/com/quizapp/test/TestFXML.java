package com.quizapp.test;

import javafx.fxml.FXMLLoader;
import java.net.URL;

public class TestFXML {
    public static void main(String[] args) {
        try {
            // Test if FXML files can be loaded
            URL loginURL = TestFXML.class.getResource("/fxml/Login.fxml");
            URL adminURL = TestFXML.class.getResource("/fxml/AdminDashboard.fxml");
            URL studentURL = TestFXML.class.getResource("/fxml/StudentDashboard.fxml");
            
            System.out.println("Login.fxml: " + (loginURL != null ? "FOUND" : "NOT FOUND"));
            System.out.println("AdminDashboard.fxml: " + (adminURL != null ? "FOUND" : "NOT FOUND"));
            System.out.println("StudentDashboard.fxml: " + (studentURL != null ? "FOUND" : "NOT FOUND"));
            
            if (loginURL != null) {
                FXMLLoader loader = new FXMLLoader(loginURL);
                Object root = loader.load();
                System.out.println("Login.fxml loaded successfully!");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}