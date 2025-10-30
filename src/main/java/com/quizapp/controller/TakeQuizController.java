package com.quizapp.controller;

import com.quizapp.dao.*;
import com.quizapp.entity.*;
import com.quizapp.util.CurrentUserManager;
import com.quizapp.util.HibernateUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TakeQuizController {
    @FXML
    private Label titleLabel;
    
    @FXML
    private Label descriptionLabel;
    
    @FXML
    private Label timeLimitLabel;
    
    @FXML
    private Label timerLabel;
    
    @FXML
    private VBox questionsContainer;
    
    @FXML
    private Button submitButton;
    
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;
    private OptionDAO optionDAO;
    private QuizAttemptDAO quizAttemptDAO;
    private AnswerDAO answerDAO;
    
    private Quiz quiz;
    private QuizAttempt quizAttempt;
    private Map<Long, ToggleGroup> questionToggleGroups = new HashMap<>();
    private Timer timer;
    private int timeRemaining;

    public TakeQuizController() {
        this.quizDAO = new QuizDAO(HibernateUtil.getSessionFactory());
        this.questionDAO = new QuestionDAO(HibernateUtil.getSessionFactory());
        this.optionDAO = new OptionDAO(HibernateUtil.getSessionFactory());
        this.quizAttemptDAO = new QuizAttemptDAO(HibernateUtil.getSessionFactory());
        this.answerDAO = new AnswerDAO(HibernateUtil.getSessionFactory());
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        this.timeRemaining = quiz.getTimeLimit() * 60; // Convert minutes to seconds
        
        // Set quiz details
        titleLabel.setText(quiz.getTitle());
        descriptionLabel.setText(quiz.getDescription());
        timeLimitLabel.setText("Time Limit: " + quiz.getTimeLimit() + " minutes");
        
        // Create quiz attempt
        User currentUser = CurrentUserManager.getCurrentUser();
        this.quizAttempt = new QuizAttempt(quiz, currentUser);
        quizAttemptDAO.save(quizAttempt);
        
        // Load questions
        loadQuestions();
        
        // Start timer
        startTimer();
    }

    private void loadQuestions() {
        List<Question> questions = questionDAO.findByQuizId(quiz.getQuizId());
        
        for (Question question : questions) {
            // Create question label
            Label questionLabel = new Label(question.getQuestionText());
            questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10px 0 5px 0;");
            
            // Create toggle group for options
            ToggleGroup toggleGroup = new ToggleGroup();
            questionToggleGroups.put(question.getQuestionId(), toggleGroup);
            
            // Load options
            List<Option> options = optionDAO.findByQuestionId(question.getQuestionId());
            
            // Create radio buttons for options
            VBox optionsBox = new VBox(5);
            for (Option option : options) {
                RadioButton optionButton = new RadioButton(option.getOptionText());
                optionButton.setToggleGroup(toggleGroup);
                optionButton.setUserData(option.getOptionId());
                optionsBox.getChildren().add(optionButton);
            }
            
            // Add question and options to container
            questionsContainer.getChildren().addAll(questionLabel, optionsBox);
        }
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining <= 0) {
                    timer.cancel();
                    javafx.application.Platform.runLater(() -> {
                        handleSubmitAction(null);
                    });
                } else {
                    int minutes = timeRemaining / 60;
                    int seconds = timeRemaining % 60;
                    javafx.application.Platform.runLater(() -> {
                        timerLabel.setText(String.format("Time Remaining: %02d:%02d", minutes, seconds));
                    });
                    timeRemaining--;
                }
            }
        }, 0, 1000);
    }

    @FXML
    private void handleSubmitAction(ActionEvent event) {
        // Stop the timer
        if (timer != null) {
            timer.cancel();
        }
        
        // Calculate score
        int correctAnswers = 0;
        int totalQuestions = questionToggleGroups.size();
        
        // Save answers
        for (Map.Entry<Long, ToggleGroup> entry : questionToggleGroups.entrySet()) {
            Long questionId = entry.getKey();
            ToggleGroup toggleGroup = entry.getValue();
            
            if (toggleGroup.getSelectedToggle() != null) {
                Long selectedOptionId = (Long) toggleGroup.getSelectedToggle().getUserData();
                
                // Create answer
                Question question = questionDAO.findById(questionId);
                Option selectedOption = optionDAO.findById(selectedOptionId);
                Answer answer = new Answer(quizAttempt, question, selectedOption);
                answerDAO.save(answer);
                
                // Check if answer is correct
                if (selectedOption.isCorrect()) {
                    correctAnswers++;
                }
            }
        }
        
        // Calculate score percentage
        int score = (int) ((double) correctAnswers / totalQuestions * 100);
        
        // Update quiz attempt
        quizAttempt.setEndTime(LocalDateTime.now());
        quizAttempt.setScore(score);
        quizAttemptDAO.update(quizAttempt);
        
        // Show result
        showAlert(Alert.AlertType.INFORMATION, "Quiz Submitted", 
                "Your quiz has been submitted.\nScore: " + score + "%\nCorrect Answers: " + correctAnswers + "/" + totalQuestions);
        
        // Close the window
        Stage stage = (Stage) submitButton.getScene().getWindow();
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