-- Planend hours calculations  

-- Calculate the total hours (with the multiplication factor) along with the break-ups for each activity, 
-- current years' course instances 

With base AS ( -- temporary calculates stored hours
    SELECT
        -- renaming (AS) for the output
        c.course_code AS course_code,
        ci.instance_id AS course_instance_id,
        cl.hp AS hp,
        sp.period_code AS study_period,
        ci.num_students AS num_students,

        -- Sum of individual activity categories
        SUM(CASE WHEN ta.activity_name = 'Lecture'
                THEN pa.planned_hours * ta.factor ELSE 0 END) AS lecture_hours,

        SUM(CASE WHEN ta.activity_name = 'Tutorial'
                THEN pa.planned_hours * ta.factor ELSE 0 END) AS tutorial_hours,

        SUM(CASE WHEN ta.activity_name = 'Lab'
                THEN pa.planned_hours * ta.factor ELSE 0 END) AS lab_hours,

        SUM(CASE WHEN ta.activity_name = 'Seminar'
                THEN pa.planned_hours * ta.factor ELSE 0 END) AS seminar_hours,

        SUM(CASE WHEN ta.activity_name = 'Other Overhead'
                THEN pa.planned_hours * ta.factor ELSE 0 END) AS other_overhead_hours,

    -- inner join 
    FROM course_instance ci
    JOIN course_layout cl   ON cl.course_layout_id = ci.course_layout_id
    JOIN course c           ON c.course_id = cl.course_id
    JOIN study_period sp    ON sp.study_period_id = ci.study_period_id
    JOIN planned_activity pa  ON pa.course_instance_id = ci.course_instance_id
    JOIN teaching_activity ta ON ta.teaching_activity_id = pa.teaching_activity_id

    -- only current year's course instances
    WHERE ci.study_year = EXTRACT(YEAR FROM CURRENT_DATE)

    -- adds together SUM row
    GROUP BY 
        c.course_code,
        ci.instance_id,
        cl.hp,
        sp.period_code,
        ci.num_students
)

SELECT -- add derived hours and totals 
    *, 
    (32 + 0.725 * num_students) AS exam_hours,
    (2 * hp + 28 + 0.2 * num_students) AS admin_hours, 

    (
        lecture_hours + tutorial_hours + lab_hours + seminar_hours + other_overhead_hours, exam_hours, admin_hours
    ) AS total_hours

FROM base 
ORDER BY c.course_code, ci.instance_id;


-- Actual allocated hours for a course

-- Total allocated hours (with multiplication factors) for a teacher
-- Only for the current years' course instances 


-- List employee ids & names of all teachers who are allocated in more than a specific number of course
-- instances during current period 
