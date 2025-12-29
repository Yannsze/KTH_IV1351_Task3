-- 1. Clean up
DROP TABLE IF EXISTS employee_planned_activity CASCADE;
DROP TABLE IF EXISTS planned_activity CASCADE;
DROP TABLE IF EXISTS employee_skill CASCADE;
DROP TABLE IF EXISTS skill CASCADE;
DROP TABLE IF EXISTS salary_history CASCADE;
DROP TABLE IF EXISTS employee CASCADE;
DROP TABLE IF EXISTS job_title CASCADE;
DROP TABLE IF EXISTS person_phone CASCADE;
DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS department CASCADE;
DROP TABLE IF EXISTS course_instance CASCADE;
DROP TABLE IF EXISTS study_period CASCADE;
DROP TABLE IF EXISTS course_layout CASCADE;
DROP TABLE IF EXISTS course CASCADE;
DROP TABLE IF EXISTS teaching_activity CASCADE;

-- 2. Create Tables
CREATE TABLE course (
    course_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    course_code VARCHAR(10) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL
);

CREATE TABLE course_layout (
    course_layout_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    course_id INT NOT NULL, -- FK to course
    min_student INT NOT NULL,
    max_student INT NOT NULL,
    hp FLOAT NOT NULL,
    valid_from_date DATE NOT NULL DEFAULT CURRENT_DATE,
    -- Constraint: A specific course can only have one layout start on a specific day
    UNIQUE (course_id, valid_from_date) 
);

CREATE TABLE study_period (
    study_period_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
    period_code VARCHAR(2) UNIQUE NOT NULL -- e.g., 'P1', 'P2'
);

CREATE TABLE course_instance (
    course_instance_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    instance_id VARCHAR(100) UNIQUE NOT NULL, 
    num_students INT NOT NULL,
    study_period_id INT NOT NULL,
    study_year INT NOT NULL,
    course_layout_id INT NOT NULL 
);

CREATE TABLE department (
    department_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    department_name VARCHAR(100) UNIQUE NOT NULL,
    manager INT -- FK to employee (added later)
);

CREATE TABLE person (
    person_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    personal_number VARCHAR(12) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    address VARCHAR(100) NOT NULL
);

CREATE TABLE person_phone (
    phone_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    person_id INT NOT NULL, -- FK to person
    phone_nr VARCHAR(20) NOT NULL
);

CREATE TABLE job_title (
    job_title_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    job_title VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE skill (
    skill_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    skill_name VARCHAR(500) UNIQUE NOT NULL
);

CREATE TABLE employee_skill (
    employee_id INT NOT NULL,
    skill_id INT NOT NULL,
    PRIMARY KEY (employee_id, skill_id)
);

CREATE TABLE employee (
    employee_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employment_id VARCHAR(10) UNIQUE NOT NULL,
    person_id INT NOT NULL,
    job_title_id INT NOT NULL,
    supervisor INT,
    department_id INT
);

CREATE TABLE salary_history (
    salary_history_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id INT NOT NULL,
    salary_amount INT NOT NULL,
    valid_from DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE teaching_activity (
    teaching_activity_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    factor FLOAT NOT NULL,
    activity_name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE planned_activity (
    teaching_activity_id INT NOT NULL,
    course_instance_id INT NOT NULL,
    planned_hours INT NOT NULL,
    
    PRIMARY KEY (teaching_activity_id, course_instance_id)
);

CREATE TABLE employee_planned_activity (
    employee_id INT NOT NULL,
    teaching_activity_id INT NOT NULL,
    course_instance_id INT NOT NULL,
    actual_allocated_hours FLOAT(50),
    
    PRIMARY KEY (employee_id, teaching_activity_id, course_instance_id)
);

-- ALTER for foreign keys
-- Course & Layout
ALTER TABLE course_layout ADD CONSTRAINT fk_layout_course 
    FOREIGN KEY (course_id) REFERENCES course(course_id);

-- Course Instance
ALTER TABLE course_instance ADD CONSTRAINT fk_instance_layout 
    FOREIGN KEY (course_layout_id) REFERENCES course_layout(course_layout_id);
ALTER TABLE course_instance ADD CONSTRAINT fk_instance_period 
    FOREIGN KEY (study_period_id) REFERENCES study_period(study_period_id);

-- Department
ALTER TABLE department ADD CONSTRAINT fk_dept_manager 
    FOREIGN KEY (manager) REFERENCES employee(employee_id);

-- Person Phone
ALTER TABLE person_phone ADD CONSTRAINT fk_phone_person 
    FOREIGN KEY (person_id) REFERENCES person(person_id);

-- Employee Links
ALTER TABLE employee ADD CONSTRAINT fk_emp_person 
    FOREIGN KEY (person_id) REFERENCES person(person_id);
ALTER TABLE employee ADD CONSTRAINT fk_emp_job 
    FOREIGN KEY (job_title_id) REFERENCES job_title(job_title_id);
ALTER TABLE employee ADD CONSTRAINT fk_emp_supervisor 
    FOREIGN KEY (supervisor) REFERENCES employee(employee_id);
ALTER TABLE employee ADD CONSTRAINT fk_emp_dept 
    FOREIGN KEY (department_id) REFERENCES department(department_id);

-- Employee Skills
ALTER TABLE employee_skill ADD CONSTRAINT fk_empskill_emp 
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id);
ALTER TABLE employee_skill ADD CONSTRAINT fk_empskill_skill 
    FOREIGN KEY (skill_id) REFERENCES skill(skill_id);

-- Salary
ALTER TABLE salary_history ADD CONSTRAINT fk_salary_emp 
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id);

-- Activities
ALTER TABLE planned_activity 
    ADD CONSTRAINT fk_pa_teaching_activity
    FOREIGN KEY (teaching_activity_id) 
    REFERENCES teaching_activity (teaching_activity_id);
ALTER TABLE planned_activity 
    ADD CONSTRAINT fk_pa_course_instance
    FOREIGN KEY (course_instance_id) 
    REFERENCES course_instance (course_instance_id);

-- Employee Planned Activities
ALTER TABLE employee_planned_activity 
    ADD CONSTRAINT fk_epa_employee
    FOREIGN KEY (employee_id) 
    REFERENCES employee (employee_id);
ALTER TABLE employee_planned_activity 
    ADD CONSTRAINT fk_epa_planned_activity
    FOREIGN KEY (teaching_activity_id, course_instance_id) 
    REFERENCES planned_activity (teaching_activity_id, course_instance_id);

-- Advance, Check if the teacher already hold 4 lessons
CREATE OR REPLACE FUNCTION check_teacher_workload() 
RETURNS TRIGGER AS $$
DECLARE
    target_period_id INT;
    target_year INT;
    current_course_count INT;
    is_already_teaching_this_course BOOLEAN;
BEGIN
    -- 1. Find the period_id and year directly from the course_instance
    -- We can use NEW.course_instance_id because it is now in the employee table!
    SELECT study_period_id, study_year
    INTO target_period_id, target_year
    FROM course_instance
    WHERE course_instance_id = NEW.course_instance_id;

    -- 2. Count how many DISTINCT course instances this employee is teaching in that period
    SELECT COUNT(DISTINCT course_instance_id)
    INTO current_course_count
    FROM employee_planned_activity
    WHERE employee_id = NEW.employee_id
      AND course_instance_id IN (
          SELECT course_instance_id 
          FROM course_instance 
          WHERE study_period_id = target_period_id 
          AND study_year = target_year
      );

    -- 3. The Constraint Check
    IF current_course_count >= 4 THEN
        -- Check if they are already in this course (adding more hours to same course is OK)
        SELECT EXISTS (
            SELECT 1
            FROM employee_planned_activity
            WHERE employee_id = NEW.employee_id
              AND course_instance_id = NEW.course_instance_id
        ) INTO is_already_teaching_this_course;

        IF NOT is_already_teaching_this_course THEN
            RAISE EXCEPTION 'Teacher % is already allocated to 4 courses in Period ID % Year %', 
                NEW.employee_id, target_period_id, target_year
                USING ERRCODE = '72000'; -- Custom error code
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER enforce_max_course_load
BEFORE INSERT ON employee_planned_activity
FOR EACH ROW
EXECUTE FUNCTION check_teacher_workload();