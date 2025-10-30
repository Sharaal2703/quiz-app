package com.quizapp.dao;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizResult;
import com.quizapp.entity.User;
import com.quizapp.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class QuizResultDAO {
    
    public QuizResult saveQuizResult(QuizResult quizResult) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(quizResult);
            transaction.commit();
            return quizResult;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return null;
        }
    }
    
    public List<QuizResult> getResultsByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<QuizResult> query = session.createQuery("FROM QuizResult WHERE user = :user ORDER BY completedDate DESC", QuizResult.class);
            query.setParameter("user", user);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<QuizResult> getResultsByQuiz(Quiz quiz) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<QuizResult> query = session.createQuery("FROM QuizResult WHERE quiz = :quiz ORDER BY completedDate DESC", QuizResult.class);
            query.setParameter("quiz", quiz);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Double getAverageScoreByQuiz(Quiz quiz) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Double> query = session.createQuery("SELECT AVG(score) FROM QuizResult WHERE quiz = :quiz", Double.class);
            query.setParameter("quiz", quiz);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}