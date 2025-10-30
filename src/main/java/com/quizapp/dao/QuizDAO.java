package com.quizapp.dao;

import com.quizapp.entity.Quiz;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class QuizDAO {
    private SessionFactory sessionFactory;

    public QuizDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Quiz findById(Long id) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            return session.get(Quiz.class, id);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void save(Quiz quiz) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            
            // Debug information
            System.out.println("Saving quiz: " + quiz.getTitle());
            System.out.println("Quiz creator: " + quiz.getCreatedBy().getUsername() + 
                             " (ID: " + quiz.getCreatedBy().getUserId() + ")");
            
            session.save(quiz);
            session.getTransaction().commit();
            System.out.println("Quiz saved with ID: " + quiz.getQuizId());
        } catch (Exception e) {
            System.err.println("Error saving quiz: " + e.getMessage());
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to save quiz: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void update(Quiz quiz) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.update(quiz);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error updating quiz: " + e.getMessage());
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void delete(Quiz quiz) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(quiz);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error deleting quiz: " + e.getMessage());
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Quiz> findAll() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Query<Quiz> query = session.createQuery("FROM Quiz", Quiz.class);
            return query.list();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<Quiz> findByCreatedBy(Long userId) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Query<Quiz> query = session.createQuery(
                "FROM Quiz WHERE createdBy.userId = :userId", Quiz.class);
            query.setParameter("userId", userId);
            return query.list();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}