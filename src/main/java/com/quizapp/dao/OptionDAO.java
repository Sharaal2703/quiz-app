package com.quizapp.dao;

import com.quizapp.entity.Option;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class OptionDAO {
    private SessionFactory sessionFactory;

    public OptionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Option findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Option.class, id);
        }
    }

    public void save(Option option) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(option);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error saving option: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(Option option) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(option);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error updating option: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(Option option) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(option);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error deleting option: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Option> findByQuestionId(Long questionId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Option> query = session.createNativeQuery(
                "SELECT * FROM Options WHERE question_id = :questionId", Option.class);
            query.setParameter("questionId", questionId);
            return query.list();
        } catch (Exception e) {
            System.err.println("Error finding options by question: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}