package com.quizapp.controller;

import com.quizapp.dao.OptionDAO;
import com.quizapp.dao.QuestionDAO;
import com.quizapp.entity.Option;
import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.util.HibernateUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class AddQuestionController {
    @FXML
    private TextArea questionTextArea;
    
    @FXML
    private ToggleGroup questionTypeGroup;
    
    @FXML
    private RadioButton multipleChoiceRadio;
    
    @FXML
    private RadioButton trueFalseRadio;
    
    @FXML
    private TextField option1Field;
    
    @FXML
    private TextField option2Field;
    
    @FXML
    private TextField option3Field;
    
    @FXML
    private TextField option4Field;
    
    @FXML
    private ToggleGroup correctOptionGroup;
    
    @FXML
    private RadioButton option1Radio;
    
    @FXML
    private RadioButton option2Radio;
    
    @FXML
    private RadioButton option3Radio;
    
    @FXML
    private RadioButton option4Radio;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private QuestionDAO questionDAO;
    private OptionDAO optionDAO;
    private Quiz quiz;

    public AddQuestionController() {
        this.questionDAO = new QuestionDAO(HibernateUtil.getSessionFactory());
        this.optionDAO = new OptionDAO(HibernateUtil.getSessionFactory());
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    @FXML
    private void initialize() {
        // Add listener to question type group to show/hide appropriate fields
        questionTypeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == multipleChoiceRadio) {
                option1Field.setDisable(false);
                option2Field.setDisable(false);
                option3Field.setDisable(false);
                option4Field.setDisable(false);
                option1Radio.setDisable(false);
                option2Radio.setDisable(false);
                option3Radio.setDisable(false);
                option4Radio.setDisable(false);
            } else if (newVal == trueFalseRadio) {
                option1Field.setDisable(false);
                option1Field.setText("True");
                option2Field.setDisable(false);
                option2Field.setText("False");
                option3Field.setDisable(true);
                option3Field.setText("");
                option4Field.setDisable(true);
                option4Field.setText("");
                option1Radio.setDisable(false);
                option2Radio.setDisable(false);
                option3Radio.setDisable(true);
                option4Radio.setDisable(true);
            }
        });
        
        // Default to multiple choice
        multipleChoiceRadio.setSelected(true);
    }

    @FXML
    private void handleSaveAction(ActionEvent event) {
        String questionText = questionTextArea.getText();
        String questionType = multipleChoiceRadio.isSelected() ? "MULTIPLE_CHOICE" : "TRUE_FALSE";
        
        if (questionText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Question text is required.");
            return;
        }
        
        if (questionType.equals("MULTIPLE_CHOICE")) {
            String option1Text = option1Field.getText();
            String option2Text = option2Field.getText();
            String option3Text = option3Field.getText();
            String option4Text = option4Field.getText();
            
            if (option1Text.isEmpty() || option2Text.isEmpty() || option3Text.isEmpty() || option4Text.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "All four options are required for multiple choice questions.");
                return;
            }
            
            RadioButton selectedOption = (RadioButton) correctOptionGroup.getSelectedToggle();
            if (selectedOption == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select the correct option.");
                return;
            }
            
            // Create question
            Question question = new Question(quiz, questionText, questionType);
            questionDAO.save(question);
            
            // Create options
            Option option1 = new Option(question, option1Text, selectedOption == option1Radio);
            Option option2 = new Option(question, option2Text, selectedOption == option2Radio);
            Option option3 = new Option(question, option3Text, selectedOption == option3Radio);
            Option option4 = new Option(question, option4Text, selectedOption == option4Radio);
            
            optionDAO.save(option1);
            optionDAO.save(option2);
            optionDAO.save(option3);
            optionDAO.save(option4);
        } else {
            // True/False question
            RadioButton selectedOption = (RadioButton) correctOptionGroup.getSelectedToggle();
            if (selectedOption == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select the correct option.");
                return;
            }
            
            // Create question
            Question question = new Question(quiz, questionText, questionType);
            questionDAO.save(question);
            
            // Create options
            Option option1 = new Option(question, "True", selectedOption == option1Radio);
            Option option2 = new Option(question, "False", selectedOption == option2Radio);
            
            optionDAO.save(option1);
            optionDAO.save(option2);
        }
        
        showAlert(Alert.AlertType.INFORMATION, "Success", "Question added successfully.");
        
        // Clear fields for next question
        questionTextArea.clear();
        option1Field.clear();
        option2Field.clear();
        option3Field.clear();
        option4Field.clear();
        correctOptionGroup.selectToggle(null);
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