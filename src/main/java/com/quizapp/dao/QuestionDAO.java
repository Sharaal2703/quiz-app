package com.quizapp.dao;

import com.quizapp.entity.Question;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class QuestionDAO {
    private SessionFactory sessionFactory;

    public QuestionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Question findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Question.class, id);
        }
    }

    public void save(Question question) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(question);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error saving question: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(Question question) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(question);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error updating question: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(Question question) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(question);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error deleting question: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Question> findByQuizId(Long quizId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Question> query = session.createNativeQuery(
                "SELECT * FROM Questions WHERE quiz_id = :quizId", Question.class);
            query.setParameter("quizId", quizId);
            return query.list();
        } catch (Exception e) {
            System.err.println("Error finding questions by quiz: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}