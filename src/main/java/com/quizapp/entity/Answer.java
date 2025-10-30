package com.quizapp.entity;

import javax.persistence.*;

@Entity
@Table(name = "Answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt quizAttempt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private Option selectedOption;
    
    // Constructors
    public Answer() {}
    
    public Answer(QuizAttempt quizAttempt, Question question, Option selectedOption) {
        this.quizAttempt = quizAttempt;
        this.question = question;
        this.selectedOption = selectedOption;
    }
    
    // Getters and Setters
    public Long getAnswerId() {
        return answerId;
    }
    
    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }
    
    public QuizAttempt getQuizAttempt() {
        return quizAttempt;
    }
    
    public void setQuizAttempt(QuizAttempt quizAttempt) {
        this.quizAttempt = quizAttempt;
    }
    
    public Question getQuestion() {
        return question;
    }
    
    public void setQuestion(Question question) {
        this.question = question;
    }
    
    public Option getSelectedOption() {
        return selectedOption;
    }
    
    public void setSelectedOption(Option selectedOption) {
        this.selectedOption = selectedOption;
    }
}