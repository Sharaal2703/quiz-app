package com.quizapp.controller;

import java.io.IOException;

import com.quizapp.dao.QuizDAO;
import com.quizapp.entity.Quiz;
import com.quizapp.util.HibernateUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditQuizController {
    @FXML
    private TextField titleField;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private TextField timeLimitField;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button addQuestionButton;
    
    private QuizDAO quizDAO;
    private Quiz quiz;

    public EditQuizController() {
        this.quizDAO = new QuizDAO(HibernateUtil.getSessionFactory());
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        titleField.setText(quiz.getTitle());
        descriptionArea.setText(quiz.getDescription());
        timeLimitField.setText(String.valueOf(quiz.getTimeLimit()));
    }

    @FXML
    private void handleSaveAction(ActionEvent event) {
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String timeLimitText = timeLimitField.getText();
        
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
        
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setTimeLimit(timeLimit);
        
        quizDAO.update(quiz);
        
        showAlert(Alert.AlertType.INFORMATION, "Success", "Quiz updated successfully.");
        
        // Close the window
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAddQuestionAction(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/add_question.fxml"));
            javafx.scene.Parent root = loader.load();
            
            AddQuestionController controller = loader.getController();
            controller.setQuiz(quiz);
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Add Question");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to open add question window.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}