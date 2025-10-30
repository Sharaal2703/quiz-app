-- Drop existing tables (optional)
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE Answers CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE Options CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE Questions CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE Quiz_Attempts CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE Quizzes CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/
BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE Users CASCADE CONSTRAINTS';
EXCEPTION
    WHEN OTHERS THEN NULL;
END;
/

-- Create sequences
CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE quizzes_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE questions_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE options_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE quiz_attempts_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE answers_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- Create Users table
CREATE TABLE Users (
    user_id NUMBER DEFAULT users_seq.NEXTVAL PRIMARY KEY,
    username VARCHAR2(50) UNIQUE NOT NULL,
    password VARCHAR2(100) NOT NULL,
    role VARCHAR2(20) NOT NULL CHECK (role IN ('ADMIN', 'STUDENT')),
    email VARCHAR2(100) UNIQUE NOT NULL,
    first_name VARCHAR2(50) NOT NULL,
    last_name VARCHAR2(50) NOT NULL
);

-- Create Quizzes table
CREATE TABLE Quizzes (
    quiz_id NUMBER DEFAULT quizzes_seq.NEXTVAL PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    description VARCHAR2(500),
    time_limit NUMBER NOT NULL,
    created_by NUMBER NOT NULL,
    created_date TIMESTAMP DEFAULT SYSTIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES Users(user_id)
);

-- Create Questions table
CREATE TABLE Questions (
    question_id NUMBER DEFAULT questions_seq.NEXTVAL PRIMARY KEY,
    quiz_id NUMBER NOT NULL,
    question_text VARCHAR2(1000) NOT NULL,
    question_type VARCHAR2(20) NOT NULL CHECK (question_type IN ('MULTIPLE_CHOICE', 'TRUE_FALSE')),
    FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id) ON DELETE CASCADE
);

-- Create Options table
CREATE TABLE Options (
    option_id NUMBER DEFAULT options_seq.NEXTVAL PRIMARY KEY,
    question_id NUMBER NOT NULL,
    option_text VARCHAR2(500) NOT NULL,
    is_correct NUMBER(1) NOT NULL CHECK (is_correct IN (0, 1)),
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);

-- Create Quiz_Attempts table
CREATE TABLE Quiz_Attempts (
    attempt_id NUMBER DEFAULT quiz_attempts_seq.NEXTVAL PRIMARY KEY,
    quiz_id NUMBER NOT NULL,
    user_id NUMBER NOT NULL,
    start_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    end_time TIMESTAMP,
    score NUMBER,
    FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- Create Answers table
CREATE TABLE Answers (
    answer_id NUMBER DEFAULT answers_seq.NEXTVAL PRIMARY KEY,
    attempt_id NUMBER NOT NULL,
    question_id NUMBER NOT NULL,
    selected_option_id NUMBER,
    FOREIGN KEY (attempt_id) REFERENCES Quiz_Attempts(attempt_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (selected_option_id) REFERENCES Options(option_id) ON DELETE CASCADE
);

-- Insert default admin user
INSERT INTO Users (username, password, role, email, first_name, last_name)
VALUES ('admin', 'admin123', 'ADMIN', 'admin@quizapp.com', 'Admin', 'User');

-- Insert sample student user
INSERT INTO Users (username, password, role, email, first_name, last_name)
VALUES ('student', 'student123', 'STUDENT', 'student@quizapp.com', 'Student', 'User');

COMMIT;