-- =============================================
-- STEP 1: Independent Tables (No Foreign Keys)
-- =============================================

INSERT INTO study_period (period_code) VALUES 
('P1'), 
('P2'), 
('P3'), 
('P4');

INSERT INTO course (course_code, course_name) VALUES
  ('AL7106', 'Digital design'),
  ('OM9831', 'Calculus'),
  ('HW1213', 'Linear Algebra'),
  ('OB4248', 'Signal processing'),
  ('HW4527', 'Embeded system'),
  ('NJ2179', 'Data storage'),
  ('TT5533', 'Discrete Mathematics'),
  ('WX3742', 'Algebra and Geometry'),
  ('QR3473', 'Basic Economics'),
  ('GL2258', 'Advance Economics');

INSERT INTO course_layout (course_id, min_student, max_student, hp, valid_from_date) VALUES
  (1, 23, 37, 7.5, '2024-01-01'), -- AL7106
  (2, 24, 35, 4.5, '2024-01-01'), -- OM9831
  (3, 18, 53, 3.5, '2024-01-01'),
  (4, 18, 54, 6.5, '2024-01-01'),
  (5, 17, 59, 3.5, '2024-01-01'),
  (6, 25, 36, 5.5, '2024-01-01'),
  (7, 22, 51, 6.5, '2024-01-01'),
  (8, 22, 41, 5.5, '2024-01-01'),
  (9, 17, 57, 7.5, '2024-01-01'),
  (10, 21, 51, 5.5, '2024-01-01');

INSERT INTO job_title (job_title) VALUES
  ('Professor'), ('Associate Professor'), ('Lecturer'), ('Research Assistant'), 
  ('Lab Technician'), ('Course Coordinator'), ('Department Head'), 
  ('Administrator'), ('HR Manager'), ('Teaching Assistant');

INSERT INTO person (personal_number, first_name, last_name, address) VALUES
  ('12345678901', 'John', 'Doe', '123 Main St, Cityville'),
  ('23456789012', 'Jane', 'Smith', '456 Oak St, Townsville'),
  ('34567890123', 'Alice', 'Johnson', '789 Pine St, Villageburg'),
  ('45678901234', 'Bob', 'Brown', '101 Maple St, Suburbia'),
  ('56789012345', 'Carol', 'White', '202 Birch St, Countryside'),
  ('67890123456', 'David', 'Green', '303 Cedar St, Metrocity'),
  ('78901234567', 'Eve', 'Black', '404 Elm St, Urbania'),
  ('89012345678', 'Frank', 'Blue', '505 Redwood St, Lakeside'),
  ('90123456789', 'Grace', 'Yellow', '606 Fir St, Coastcity'),
  ('12345678902', 'Henry', 'Red', '707 Palm St, Hilltop');

INSERT INTO person_phone (person_id, phone_nr) VALUES
  (1, '123-456-7890'), (2, '234-567-8901'), (3, '345-678-9012'), (4, '456-789-0123'),
  (5, '567-890-1234'), (6, '678-901-2345'), (7, '789-012-3456'), (8, '890-123-4567'),
  (9, '901-234-5678'), (10, '012-345-6789');

INSERT INTO teaching_activity (factor, activity_name) VALUES
  (3.6, 'Lecture'), (2.4, 'Lab'), (2.4, 'Tutorial'), (1.8, 'Seminar');

INSERT INTO department (department_name, manager) VALUES
  ('Computer Science', NULL),     
  ('Mathematics', NULL),          
  ('Electrical Engineering', NULL); 

INSERT INTO employee (employment_id, person_id, job_title_id, supervisor, department_id) VALUES
  ('E001', 1, 1, NULL, 1), -- Employee 1
  ('E002', 2, 2, NULL, 2), -- Employee 2
  ('E003', 3, 3, NULL, 3), -- Employee 3
  ('E004', 4, 4, NULL, 3), -- Employee 4
  ('E005', 5, 5, NULL, 1), -- Employee 5
  ('E006', 6, 6, NULL, 2),
  ('E007', 7, 7, NULL, 3),
  ('E008', 8, 8, NULL, 3),
  ('E009', 9, 9, NULL, 1),
  ('E010', 10, 10, NULL, 2);

INSERT INTO salary_history (employee_id, salary_amount, valid_from) VALUES
  (1, 80000, '2024-01-01'),
  (2, 75000, '2024-01-01'),
  (3, 72000, '2024-01-01'),
  (4, 78000, '2024-01-01'),
  (5, 74000, '2024-01-01'),
  (6, 80000, '2024-01-01'),
  (7, 77000, '2024-01-01'),
  (8, 76000, '2024-01-01'),
  (9, 79000, '2024-01-01'),
  (10, 82000, '2024-01-01');

UPDATE department SET manager = 1 WHERE department_name = 'Computer Science';
UPDATE department SET manager = 2 WHERE department_name = 'Mathematics';
UPDATE department SET manager = 3 WHERE department_name = 'Electrical Engineering';

UPDATE employee SET supervisor = 2 WHERE employment_id = '1';
UPDATE employee SET supervisor = 3 WHERE employment_id = '2';
UPDATE employee SET supervisor = 4 WHERE employment_id = '3';
UPDATE employee SET supervisor = 5 WHERE employment_id = '4';
UPDATE employee SET supervisor = 6 WHERE employment_id = '5';
UPDATE employee SET supervisor = 7 WHERE employment_id = '6';
UPDATE employee SET supervisor = 8 WHERE employment_id = '7';
UPDATE employee SET supervisor = 9 WHERE employment_id = '8';
UPDATE employee SET supervisor = 10 WHERE employment_id = '9';
UPDATE employee SET supervisor = 1 WHERE employment_id = '0';

INSERT INTO course_instance (instance_id, num_students, study_period_id, study_year, course_layout_id) VALUES
  ('AL7106ht25', 30, 1, 2025, 1),
  ('HW1213ht25', 35, 1, 2025, 3),
  ('OM9831ht25', 25, 1, 2025, 2),
  ('OB4248ht25', 40, 1, 2025, 4),
  ('HW4527ht25', 22, 1, 2025, 5),
  ('NJ2179ht25', 29, 2, 2025, 6),
  ('TT5533ht25', 38, 2, 2025, 7),
  ('WX3742ht25', 26, 2, 2025, 8),
  ('QR3473ht25', 32, 2, 2025, 9),
  ('GL2258ht25', 33, 2, 2025, 10);

INSERT INTO planned_activity (course_instance_id, teaching_activity_id, planned_hours)
VALUES
  (1, 1, 24), 
  (1, 2, 16),
  (2, 1, 20),
  (2, 3, 10),
  (3, 1, 18),
  (4, 1, 22),
  (4, 2, 14),
  (5, 2, 15);

INSERT INTO employee_planned_activity (course_instance_id, teaching_activity_id, employee_id,actual_allocated_hours)
VALUES
  (1, 1, 1, 20), 
  (1, 2, 3, 25), 
  (2, 1, 2, 25),
  (2, 3, 2, 15), 
  (3, 1, 2, 20),
  (4, 1, 3, 21), 
  (4, 2, 4, 10),
  (5, 2, 5, 17); 

INSERT INTO skill (skill_name) VALUES 
('Machine Learning'), ('Calculus'), ('Digital Design'), ('Signal Processing'), 
('Networking'), ('Econometrics'), ('Quantum Physics'), ('Construction'), ('Business Strategy');

INSERT INTO employee_skill (employee_id, skill_id) VALUES 
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

