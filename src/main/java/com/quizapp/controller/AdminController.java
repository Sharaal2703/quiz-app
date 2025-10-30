package com.quizapp.controller;

import com.quizapp.dao.QuizDAO;
import com.quizapp.dao.UserDAO;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
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

public class AdminController {
    @FXML
    private TableView<Quiz> quizTable;
    @FXML
    private TableColumn<Quiz, Long> idColumn;
    @FXML
    private TableColumn<Quiz, String> titleColumn;
    @FXML
    private TableColumn<Quiz, String> descriptionColumn;
    @FXML
    private TableColumn<Quiz, Integer> timeLimitColumn;
    @FXML
    private Button createQuizButton;
    @FXML
    private Button editQuizButton;
    @FXML
    private Button deleteQuizButton;
    @FXML
    private Button logoutButton;
    
    private QuizDAO quizDAO;
    private UserDAO userDAO;
    private ObservableList<Quiz> quizList = FXCollections.observableArrayList();

    public AdminController() {
        this.quizDAO = new QuizDAO(HibernateUtil.getSessionFactory());
        this.userDAO = new UserDAO(HibernateUtil.getSessionFactory());
        System.out.println("AdminController initialized");
    }

    @FXML
    private void initialize() {
        System.out.println("AdminController initialize method called");
        
        // Ensure current user is properly set up
        User currentUser = CurrentUserManager.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Current user: " + currentUser.getUsername() + 
                             (currentUser.getUserId() != null ? " (ID: " + currentUser.getUserId() + ")" : " (No ID)"));
            
            // If user doesn't have an ID, try to find them in the database
            if (currentUser.getUserId() == null) {
                User dbUser = userDAO.findByUsername(currentUser.getUsername());
                if (dbUser != null) {
                    CurrentUserManager.setCurrentUser(dbUser);
                    System.out.println("Found user in database with ID: " + dbUser.getUserId());
                }
            }
        } else {
            System.out.println("No current user in AdminController!");
        }
        
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("quizId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        timeLimitColumn.setCellValueFactory(new PropertyValueFactory<>("timeLimit"));
        
        // Load quiz data
        loadQuizData();
        
        // Set table items
        quizTable.setItems(quizList);
    }

    private void loadQuizData() {
        quizList.clear();
        try {
            List<Quiz> quizzes = quizDAO.findAll();
            if (quizzes != null) {
                quizList.addAll(quizzes);
                System.out.println("Loaded " + quizzes.size() + " quizzes");
            }
        } catch (Exception e) {
            System.err.println("Error loading quizzes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateQuizAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/create_quiz.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Create Quiz");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Refresh quiz list when window closes
            stage.setOnHidden(e -> loadQuizData());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to open create quiz window.");
        }
    }

    @FXML
    private void handleEditQuizAction(ActionEvent event) {
        Quiz selectedQuiz = quizTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a quiz to edit.");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/edit_quiz.fxml"));
            Parent root = loader.load();
            
            EditQuizController controller = loader.getController();
            controller.setQuiz(selectedQuiz);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Quiz");
            stage.setScene(new Scene(root));
            stage.show();
            
            stage.setOnHidden(e -> loadQuizData());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to open edit quiz window.");
        }
    }

    @FXML
    private void handleDeleteQuizAction(ActionEvent event) {
        Quiz selectedQuiz = quizTable.getSelectionModel().getSelectedItem();
        if (selectedQuiz == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a quiz to delete.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the quiz: " + selectedQuiz.getTitle() + "?");
        
        if (alert.showAndWait().get() == javafx.scene.control.ButtonType.OK) {
            try {
                quizDAO.delete(selectedQuiz);
                loadQuizData();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Quiz deleted successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete quiz: " + e.getMessage());
            }
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