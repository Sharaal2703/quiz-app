package com.quizapp;

import com.quizapp.util.DatabaseInitializer;
import com.quizapp.util.DatabaseReset;
import com.quizapp.util.HibernateUtil;

public class Launcher {
    public static void main(String[] args) {
        // Check if reset flag is provided
        boolean resetDatabase = args.length > 0 && "--reset".equals(args[0]);
        
        if (resetDatabase) {
            System.out.println("Resetting database...");
            DatabaseReset dbReset = new DatabaseReset(HibernateUtil.getSessionFactory());
            dbReset.resetDatabase();
            
            System.out.println("Reinitializing database...");
            DatabaseInitializer dbInitializer = new DatabaseInitializer(HibernateUtil.getSessionFactory());
            dbInitializer.initializeDatabase();
            
            System.out.println("Database reset and initialization completed.");
            return;
        }
        
        // Launch the application normally
        MainApp.main(args);
    }
}