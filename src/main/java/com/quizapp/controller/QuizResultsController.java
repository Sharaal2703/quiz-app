package com.quizapp.controller;

import com.quizapp.dao.AnswerDAO;
import com.quizapp.dao.OptionDAO;
import com.quizapp.entity.Answer;
import com.quizapp.entity.Option;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.util.HibernateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class QuizResultsController {
    @FXML
    private Label titleLabel;
    
    @FXML
    private Label scoreLabel;
    
    @FXML
    private Label timeTakenLabel;
    
    @FXML
    private VBox answersContainer;
    
    private AnswerDAO answerDAO;
    private OptionDAO optionDAO;
    private QuizAttempt quizAttempt;

    public QuizResultsController() {
        this.answerDAO = new AnswerDAO(HibernateUtil.getSessionFactory());
        this.optionDAO = new OptionDAO(HibernateUtil.getSessionFactory());
    }

    public void setQuizAttempt(QuizAttempt quizAttempt) {
        this.quizAttempt = quizAttempt;
        
        // Set quiz details
        titleLabel.setText("Results for: " + quizAttempt.getQuiz().getTitle());
        scoreLabel.setText("Score: " + quizAttempt.getScore() + "%");
        
        // Calculate time taken
        if (quizAttempt.getStartTime() != null && quizAttempt.getEndTime() != null) {
            long minutes = java.time.Duration.between(quizAttempt.getStartTime(), quizAttempt.getEndTime()).toMinutes();
            timeTakenLabel.setText("Time Taken: " + minutes + " minutes");
        }
        
        // Load answers
        loadAnswers();
    }

    private void loadAnswers() {
        List<Answer> answers = answerDAO.findByAttemptId(quizAttempt.getAttemptId());
        
        for (Answer answer : answers) {
            // Create question label
            Label questionLabel = new Label(answer.getQuestion().getQuestionText());
            questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10px 0 5px 0;");
            
            // Create selected answer label
            String selectedAnswerText = "Not answered";
            boolean isCorrect = false;
            
            if (answer.getSelectedOption() != null) {
                selectedAnswerText = "Your answer: " + answer.getSelectedOption().getOptionText();
                isCorrect = answer.getSelectedOption().isCorrect();
            }
            
            Label selectedAnswerLabel = new Label(selectedAnswerText);
            selectedAnswerLabel.setStyle("-fx-padding: 0 0 0 20px;");
            
            // If incorrect, show correct answer
            if (!isCorrect && answer.getSelectedOption() != null) {
                // Find correct option
                List<Option> options = optionDAO.findByQuestionId(answer.getQuestion().getQuestionId());
                for (Option option : options) {
                    if (option.isCorrect()) {
                        Label correctAnswerLabel = new Label("Correct answer: " + option.getOptionText());
                        correctAnswerLabel.setStyle("-fx-text-fill: green; -fx-padding: 0 0 0 20px;");
                        answersContainer.getChildren().addAll(questionLabel, selectedAnswerLabel, correctAnswerLabel);
                        break;
                    }
                }
            } else if (isCorrect) {
                selectedAnswerLabel.setStyle("-fx-text-fill: green; -fx-padding: 0 0 0 20px;");
                answersContainer.getChildren().addAll(questionLabel, selectedAnswerLabel);
            } else {
                answersContainer.getChildren().addAll(questionLabel, selectedAnswerLabel);
            }
        }
    }
}