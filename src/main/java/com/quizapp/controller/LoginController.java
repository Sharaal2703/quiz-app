package com.quizapp.controller;

import com.quizapp.dao.UserDAO;
import com.quizapp.entity.User;
import com.quizapp.util.CurrentUserManager;
import com.quizapp.util.HibernateUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private final UserDAO userDAO;

    public LoginController() {
        this.userDAO = new UserDAO(HibernateUtil.getSessionFactory());
        System.out.println("✅ LoginController initialized");
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        System.out.println("Login attempt - Username: '" + username + "', Password length: " + password.length());

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password.");
            return;
        }

        try {
            User user = userDAO.findByUsername(username);

            if (user == null) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "No user found with username: " + username);
                return;
            }

            if (!password.equals(user.getPassword())) {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect password.");
                return;
            }

            System.out.println("✅ Login successful for user: " + user.getUsername() + " (" + user.getRole() + ")");
            CurrentUserManager.setCurrentUser(user);

            loadDashboard(user);

        } catch (Exception e) {
            System.err.println("❌ Error during login: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Login Error", "Failed to login: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegisterButtonAction(ActionEvent event) {
        try {
            URL resourceUrl = getClass().getResource("/view/register.fxml");
            if (resourceUrl == null) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Registration page not found.");
                return;
            }

            Parent root = FXMLLoader.load(resourceUrl);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quiz Platform - Register");
            stage.show();
            System.out.println("➡️ Navigated to Register page");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load registration screen: " + e.getMessage());
        }
    }

    private void loadDashboard(User user) {
        try {
            String dashboardPath;
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                dashboardPath = "/view/admin_dashboard.fxml";
            } else {
                dashboardPath = "/view/student_dashboard.fxml";
            }

            URL resourceUrl = getClass().getResource(dashboardPath);
            if (resourceUrl == null) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Dashboard file not found: " + dashboardPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Quiz Platform - " + user.getRole() + " Dashboard");
            stage.show();

            System.out.println("✅ Dashboard loaded successfully for " + user.getUsername());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load dashboard: " + e.getMessage());
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
