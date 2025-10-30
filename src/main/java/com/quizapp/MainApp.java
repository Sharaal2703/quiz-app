package com.quizapp;

import com.quizapp.util.HibernateUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    @Override
    public void init() throws Exception {
        // Temporarily skip database initialization to get the UI working
        System.out.println("Skipping database initialization for now...");
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            System.out.println("Starting application UI...");
            
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            
            System.out.println("FXML loaded successfully");
            
            // Create the scene with explicit dimensions
            Scene scene = new Scene(root, 600, 400);
            
            // Set up the stage
            primaryStage.setTitle("Online Quiz Platform");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            
            // Show the stage
            primaryStage.show();
            
            System.out.println("Login screen displayed successfully.");
        } catch (Exception e) {
            System.err.println("Error starting application UI: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Application Error", 
                    "Failed to start the application: " + e.getMessage());
            
            // Exit the application if we can't load the login screen
            Platform.exit();
        }
    }
    
    @Override
    public void stop() throws Exception {
        System.out.println("Shutting down application...");
        HibernateUtil.shutdown();
        System.out.println("Application shutdown and resources cleaned up.");
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        System.out.println("Launching application...");
        launch(args);
    }
}