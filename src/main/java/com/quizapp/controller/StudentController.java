package com.quizapp.controller;

import com.quizapp.dao.QuizDAO;
import com.quizapp.dao.QuizAttemptDAO;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.util.CurrentUserManager;
import com.quizapp.util.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class StudentController {
    @FXML
    private TableView<Quiz> availableQuizTable;
    
    @FXML
    private TableColumn<Quiz, Long> availableIdColumn;
    
    @FXML
    private TableColumn<Quiz, String> availableTitleColumn;
    
    @FXML
    private TableColumn<Quiz, String> availableDescriptionColumn;
    
    @FXML
    private TableColumn<Quiz, Integer> availableTimeLimitColumn;
    
    @FXML
    private TableView<QuizAttempt> attemptTable;
    
    @FXML
    private TableColumn<QuizAttempt, Long> attemptIdColumn;
    
    @FXML
    private TableColumn<QuizAttempt, String> attemptQuizTitleColumn;
    
    @FXML
    private TableColumn<QuizAttempt, String> attemptStartTimeColumn;
    
    @FXML
    private TableColumn<QuizAttempt, String> attemptEndTimeColumn;
    
    @FXML
    private TableColumn<QuizAttempt, Integer> attemptScoreColumn;
    
    @FXML
    private Button takeQuizButton;
    
    @FXML
    private Button viewResultsButton;
    
    @FXML
    private Button logoutButton;
    
    private QuizDAO quizDAO;
    private QuizAttemptDAO quizAttemptDAO;
    private ObservableList<Quiz> availableQuizList = FXCollections.observableArrayList();
    private ObservableList<QuizAttempt> attemptList = FXCollections.observableArrayList();

    public StudentController() {
        this.quizDAO = new QuizDAO(HibernateUtil.getSessionFactory());
        this.quizAttemptDAO = new QuizAttemptDAO(HibernateUtil.getSessionFactory());
    }

    @FXML
    private void initialize() {
        // Set up the columns in the available quiz table
        availableIdColumn.setCellValueFactory(new PropertyValueFactory<>("quizId"));
        availableTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        availableDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        availableTimeLimitColumn.setCellValueFactory(new PropertyValueFactory<>("timeLimit"));
        
        // Set up the columns in the attempt table
        attemptIdColumn.setCellValueFactory(new PropertyValueFactory<>("attemptId"));
        attemptQuizTitleColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getQuiz().getTitle()));
        attemptStartTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        attemptEndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        attemptScoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        
        // Load data
        loadAvailableQuizzes();
        loadQuizAttempts();
        
        // Set the table items
        availableQuizTable.setItems(availableQuizList);
        attemptTable.setItems(attemptList);
    }

    private void loadAvailableQuizzes() {
        availableQuizList.clear();
        List<Quiz> quizzes = quizDAO.findAll();
        availableQuizList.addAll(quizzes);
    }

    private void loadQuizAttempts() {
        attemptList.clear();
        Long userId = CurrentUserManager.getCurrentUser().getUserId();
        List<QuizAttempt> attempts = quizAttemptDAO.findByUserId(userId);
        attemptList.addAll(attempts);
    }

    @FXML
    private void handleTakeQuizAction(ActionEvent event) {
        Quiz selectedQuiz = availableQuizTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a quiz to take.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/take_quiz.fxml"));
            Parent root = loader.load();
            
            TakeQuizController controller = loader.getController();
            controller.setQuiz(selectedQuiz);
            
            Stage stage = new Stage();
            stage.setTitle("Take Quiz: " + selectedQuiz.getTitle());
            stage.setScene(new Scene(root));
            stage.show();
            
            // Refresh the attempt table after closing the take quiz window
            stage.setOnHidden(e -> loadQuizAttempts());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to open quiz window.");
        }
    }

    @FXML
    private void handleViewResultsAction(ActionEvent event) {
        QuizAttempt selectedAttempt = attemptTable.getSelectionModel().getSelectedItem();
        if (selectedAttempt == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an attempt to view results.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/quiz_results.fxml"));
            Parent root = loader.load();
            
            QuizResultsController controller = loader.getController();
            controller.setQuizAttempt(selectedAttempt);
            
            Stage stage = new Stage();
            stage.setTitle("Quiz Results");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to open results window.");
        }
    }

    @FXML
    private void handleLogoutAction(ActionEvent event) {
        CurrentUserManager.clearCurrentUser();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quiz Platform - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to return to login screen.");
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