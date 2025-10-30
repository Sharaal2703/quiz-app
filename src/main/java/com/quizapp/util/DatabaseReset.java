package com.quizapp.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;

public class DatabaseReset {
    private final SessionFactory sessionFactory;
    
    public DatabaseReset(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public void resetDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            
            try {
                // Disable foreign key constraints temporarily
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Answers DISABLE CONSTRAINT FK_ANSWERS_QUIZATTEMPT'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Answers DISABLE CONSTRAINT FK_ANSWERS_QUESTION'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Answers DISABLE CONSTRAINT FK_ANSWERS_SELECTEDOPTION'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Options DISABLE CONSTRAINT FK_OPTIONS_QUESTION'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Questions DISABLE CONSTRAINT FK_QUESTIONS_QUIZ'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Quiz_Attempts DISABLE CONSTRAINT FK_QUIZATTEMPTS_QUIZ'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Quiz_Attempts DISABLE CONSTRAINT FK_QUIZATTEMPTS_USER'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Quizzes DISABLE CONSTRAINT FK_QUIZZES_CREATEDBY'; END;").executeUpdate();
                
                // Delete data in reverse order of dependencies
                session.createNativeQuery("DELETE FROM Answers").executeUpdate();
                session.createNativeQuery("DELETE FROM Options").executeUpdate();
                session.createNativeQuery("DELETE FROM Questions").executeUpdate();
                session.createNativeQuery("DELETE FROM Quiz_Attempts").executeUpdate();
                session.createNativeQuery("DELETE FROM Quizzes").executeUpdate();
                session.createNativeQuery("DELETE FROM Users").executeUpdate();
                
                // Re-enable foreign key constraints
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Answers ENABLE CONSTRAINT FK_ANSWERS_QUIZATTEMPT'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Answers ENABLE CONSTRAINT FK_ANSWERS_QUESTION'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Answers ENABLE CONSTRAINT FK_ANSWERS_SELECTEDOPTION'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Options ENABLE CONSTRAINT FK_OPTIONS_QUESTION'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Questions ENABLE CONSTRAINT FK_QUESTIONS_QUIZ'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Quiz_Attempts ENABLE CONSTRAINT FK_QUIZATTEMPTS_QUIZ'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Quiz_Attempts ENABLE CONSTRAINT FK_QUIZATTEMPTS_USER'; END;").executeUpdate();
                session.createNativeQuery("BEGIN EXECUTE IMMEDIATE 'ALTER TABLE Quizzes ENABLE CONSTRAINT FK_QUIZZES_CREATEDBY'; END;").executeUpdate();
                
                transaction.commit();
                System.out.println("Database reset completed successfully.");
            } catch (Exception e) {
                if (transaction.getStatus() == TransactionStatus.ACTIVE) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }
}