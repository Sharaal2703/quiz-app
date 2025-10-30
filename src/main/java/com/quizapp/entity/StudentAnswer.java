package com.quizapp.entity;

import javax.persistence.*;

@Entity
@Table(name = "student_answer")
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_seq")
    @SequenceGenerator(name = "answer_seq", sequenceName = "seq_answer_id", allocationSize = 1)
    private Long answerId;
    
    @ManyToOne
    @JoinColumn(name = "result_id", nullable = false)
    private QuizResult quizResult;
    
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @ManyToOne
    @JoinColumn(name = "selected_option_id")
    private AnswerOption selectedOption;
    
    @Column(name = "is_correct")
    private Boolean isCorrect = false;
    
    // Constructors
    public StudentAnswer() {}
    
    public StudentAnswer(QuizResult quizResult, Question question, AnswerOption selectedOption) {
        this.quizResult = quizResult;
        this.question = question;
        this.selectedOption = selectedOption;
        this.isCorrect = selectedOption != null && selectedOption.getIsCorrect();
    }
    
    // Getters and Setters
    public Long getAnswerId() { return answerId; }
    public void setAnswerId(Long answerId) { this.answerId = answerId; }
    
    public QuizResult getQuizResult() { return quizResult; }
    public void setQuizResult(QuizResult quizResult) { this.quizResult = quizResult; }
    
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    
    public AnswerOption getSelectedOption() { return selectedOption; }
    public void setSelectedOption(AnswerOption selectedOption) { 
        this.selectedOption = selectedOption;
        this.isCorrect = selectedOption != null && selectedOption.getIsCorrect();
    }
    
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
}