package com.quizapp.test;

import com.quizapp.util.HibernateUtil;

public class TestHibernate {
    public static void main(String[] args) {
        try {
            HibernateUtil.getSessionFactory();
            System.out.println("Hibernate configuration successful!");
            HibernateUtil.shutdown();
        } catch (Exception e) {
            System.err.println("Hibernate configuration failed:");
            e.printStackTrace();
        }
    }
}
