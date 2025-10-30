package com.quizapp.controller;

import com.quizapp.dao.QuizDAO;
import com.quizapp.dao.UserDAO;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.util.CurrentUserManager;
import com.quizapp.util.HibernateUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateQuizController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField timeLimitField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private QuizDAO quizDAO;
    private UserDAO userDAO;

    public CreateQuizController() {
        this.quizDAO = new QuizDAO(HibernateUtil.getSessionFactory());
        this.userDAO = new UserDAO(HibernateUtil.getSessionFactory());
        System.out.println("CreateQuizController initialized");
    }

    @FXML
    private void initialize() {
        System.out.println("CreateQuizController initialize method called");
        
        // Debug current user
        User currentUser = CurrentUserManager.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Current user in CreateQuizController: " + 
                currentUser.getUsername() + " (ID: " + currentUser.getUserId() + ", Role: " + currentUser.getRole() + ")");
        } else {
            System.out.println("❌ ERROR: No current user in CreateQuizController!");
        }
    }

    @FXML
    private void handleSaveAction(ActionEvent event) {
        System.out.println("=== Starting Quiz Creation ===");
        
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String timeLimitText = timeLimitField.getText().trim();
        
        System.out.println("Form data - Title: '" + title + "', Description: '" + description + "', Time Limit: '" + timeLimitText + "'");
        
        // Validation
        if (title.isEmpty() || timeLimitText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Title and time limit are required.");
            return;
        }
        
        int timeLimit;
        try {
            timeLimit = Integer.parseInt(timeLimitText);
            if (timeLimit <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Time limit must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Time limit must be a valid number.");
            return;
        }
        
        // Get the current user
        User currentUser = CurrentUserManager.getCurrentUser();
        if (currentUser == null) {
            System.out.println("❌ ERROR: Current user is null!");
            showAlert(Alert.AlertType.ERROR, "Authentication Error", "You must be logged in to create a quiz.");
            return;
        }
        
        System.out.println("Current user details: " + currentUser.getUsername() + 
                          " (ID: " + currentUser.getUserId() + ", Role: " + currentUser.getRole() + ")");
        
        // If user doesn't have an ID, we need to handle this
        if (currentUser.getUserId() == null) {
            System.out.println("⚠️ WARNING: User has no ID, attempting to find in database...");
            
            try {
                User dbUser = userDAO.findByUsername(currentUser.getUsername());
                if (dbUser != null) {
                    System.out.println("✅ Found user in database: " + dbUser.getUserId());
                    currentUser = dbUser;
                    CurrentUserManager.setCurrentUser(currentUser);
                } else {
                    System.out.println("❌ ERROR: User not found in database!");
                    showAlert(Alert.AlertType.ERROR, "User Error", "User not found in database. Please login again.");
                    return;
                }
            } catch (Exception e) {
                System.err.println("❌ ERROR finding user: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to find user: " + e.getMessage());
                return;
            }
        }
        
        // Create the quiz
        System.out.println("Creating quiz object...");
        Quiz quiz = new Quiz(title, description, timeLimit, currentUser);
        System.out.println("Quiz object created: " + quiz.getTitle() + " by " + quiz.getCreatedBy().getUsername());
        
        try {
            System.out.println("Attempting to save quiz...");
            quizDAO.save(quiz);
            System.out.println("✅ SUCCESS: Quiz saved with ID: " + quiz.getQuizId());
            
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Quiz created successfully!\nQuiz ID: " + quiz.getQuizId() + 
                "\nTitle: " + quiz.getTitle());
            
            // Clear the form for next quiz
            titleField.clear();
            descriptionArea.clear();
            timeLimitField.clear();
            
        } catch (Exception e) {
            System.err.println("❌ ERROR saving quiz: " + e.getMessage());
            e.printStackTrace();
            
            // Detailed error information
            String errorMessage = "Failed to create quiz: " + e.getMessage();
            if (e.getCause() != null) {
                errorMessage += "\nCause: " + e.getCause().getMessage();
                if (e.getCause().getCause() != null) {
                    errorMessage += "\nRoot Cause: " + e.getCause().getCause().getMessage();
                }
            }
            
            showAlert(Alert.AlertType.ERROR, "Database Error", errorMessage);
        }
        
        System.out.println("=== Quiz Creation Process Completed ===");
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}