package com.quizapp.dao;

import com.quizapp.entity.Answer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class AnswerDAO {
    private SessionFactory sessionFactory;

    public AnswerDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Answer findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Answer.class, id);
        }
    }

    public void save(Answer answer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(answer);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error saving answer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(Answer answer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(answer);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error updating answer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(Answer answer) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(answer);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error deleting answer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Answer> findByAttemptId(Long attemptId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Answer> query = session.createNativeQuery(
                "SELECT * FROM Answers WHERE attempt_id = :attemptId", Answer.class);
            query.setParameter("attemptId", attemptId);
            return query.list();
        } catch (Exception e) {
            System.err.println("Error finding answers by attempt: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}