package com.quizapp.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                // Create registry builder
                StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
                
                // Configure settings
                Map<String, Object> settings = new HashMap<>();
                settings.put(Environment.DRIVER, "oracle.jdbc.OracleDriver");
                settings.put(Environment.URL, "jdbc:oracle:thin:@localhost:1521:XE");
                settings.put(Environment.USER, "system");
                settings.put(Environment.PASS, "pass");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.Oracle12cDialect");
                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.HBM2DDL_AUTO, "update");
                settings.put(Environment.C3P0_MIN_SIZE, "5");
                settings.put(Environment.C3P0_MAX_SIZE, "20");
                settings.put(Environment.C3P0_TIMEOUT, "300");
                settings.put(Environment.C3P0_MAX_STATEMENTS, "50");
                
                // Apply settings
                registryBuilder.applySettings(settings);
                
                // Create registry
                registry = registryBuilder.build();
                
                // Create MetadataSources
                MetadataSources sources = new MetadataSources(registry);
                
                // Add annotated classes
                sources.addAnnotatedClass(com.quizapp.entity.User.class);
                sources.addAnnotatedClass(com.quizapp.entity.Quiz.class);
                sources.addAnnotatedClass(com.quizapp.entity.Question.class);
                sources.addAnnotatedClass(com.quizapp.entity.Option.class);
                sources.addAnnotatedClass(com.quizapp.entity.QuizAttempt.class);
                sources.addAnnotatedClass(com.quizapp.entity.Answer.class);
                
                // Create Metadata
                Metadata metadata = sources.getMetadataBuilder().build();
                
                // Create SessionFactory
                sessionFactory = metadata.getSessionFactoryBuilder().build();
                
                System.out.println("Hibernate SessionFactory created successfully using programmatic configuration");
                
            } catch (Exception e) {
                e.printStackTrace();
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}