package com.quizapp.dao;

import com.quizapp.entity.QuizAttempt;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class QuizAttemptDAO {
    private SessionFactory sessionFactory;

    public QuizAttemptDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public QuizAttempt findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(QuizAttempt.class, id);
        }
    }

    public void save(QuizAttempt quizAttempt) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(quizAttempt);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error saving quiz attempt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(QuizAttempt quizAttempt) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(quizAttempt);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error updating quiz attempt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(QuizAttempt quizAttempt) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(quizAttempt);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error deleting quiz attempt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<QuizAttempt> findByUserId(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<QuizAttempt> query = session.createNativeQuery(
                "SELECT * FROM Quiz_Attempts WHERE user_id = :userId", QuizAttempt.class);
            query.setParameter("userId", userId);
            return query.list();
        } catch (Exception e) {
            System.err.println("Error finding quiz attempts by user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<QuizAttempt> findByQuizId(Long quizId) {
        try (Session session = sessionFactory.openSession()) {
            Query<QuizAttempt> query = session.createNativeQuery(
                "SELECT * FROM Quiz_Attempts WHERE quiz_id = :quizId", QuizAttempt.class);
            query.setParameter("quizId", quizId);
            return query.list();
        } catch (Exception e) {
            System.err.println("Error finding quiz attempts by quiz: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}