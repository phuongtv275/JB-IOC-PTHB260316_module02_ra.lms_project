-- ============================================================
-- LMS - Learning Management System
-- PostgreSQL Database Procedures for Course Script
-- ============================================================

-- Enrollment View: danh sách tất cả các khóa học với tên khóa học và tên sinh viên
CREATE OR REPLACE VIEW vw_enrollment_join_student_course AS
SELECT e.id, e.student_id, e.course_id, e.registered_at, e.status,
       c.name AS course_name, s.name AS student_name, c.instructor
FROM enrollment e
    JOIN course c ON e.course_id = c.id
    JOIN student s ON e.student_id = s.id;

-- Tìm kiếm enrollment theo id
CREATE OR REPLACE PROCEDURE search_enrollment_by_id(
    INOUT p_cursors REFCURSOR,
    p_id INT
) LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursors FOR
    SELECT * FROM vw_enrollment_join_student_course
    WHERE id = p_id
    ORDER BY id;
END;
$$;

-- Tìm kiếm enrollment theo student id
CREATE OR REPLACE PROCEDURE search_enrollment_by_student_id(
    INOUT p_cursors REFCURSOR,
    p_student_id INT
) LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursors FOR
        SELECT * FROM vw_enrollment_join_student_course
        WHERE student_id = p_student_id
        ORDER BY id;
END;
$$;

-- Danh sách toàn bộ enrollments
CREATE OR REPLACE PROCEDURE get_all_enrollments (
    INOUT p_cursors REFCURSOR
) LANGUAGE plpgsql AS
$$
BEGIN
    OPEN p_cursors FOR
    SELECT * FROM vw_enrollment_join_student_course;
END;
$$;

-- Kiểm tra sinh viên đã đăng ký khóa học hay chưa
CREATE OR REPLACE PROCEDURE check_student_enrolled_course(
    OUT p_result REFCURSOR,
    IN  p_student_id INT,
    IN  p_course_id INT
)
    LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_result FOR
    SELECT 1 FROM enrollment
    WHERE student_id = p_student_id AND course_id = p_course_id AND status != 'CANCEL'::enrollment_status;
END;
$$;

-- Thêm mới enrollment
CREATE OR REPLACE PROCEDURE add_enrollment(
    p_student_id INT,
    p_course_id INT,
    p_status VARCHAR(10) DEFAULT 'WAITING',
    INOUT p_id INT DEFAULT NULL
)
    LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO enrollment (student_id, course_id, status)
    VALUES (p_student_id, p_course_id, p_status::enrollment_status)
    RETURNING id INTO p_id;
END;
$$;

-- Chỉnh sửa trạng thái enrollment
CREATE OR REPLACE PROCEDURE update_enrollment_status(
    p_id INT,
    p_status VARCHAR(10)
)
    LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE enrollment
    SET status = p_status::enrollment_status
    WHERE id = p_id;
END;
$$;

-- Xóa enrollment theo id enrollment
CREATE OR REPLACE PROCEDURE delete_enroll_by_id (p_id INT)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM enrollment
    WHERE id = p_id;
END;
$$;

-- Tìm khóa học theo id khóa học từ view vw_enrollment_join_student_course
CREATE OR REPLACE PROCEDURE search_course_by_course_id_join_enrollment_student(
    INOUT p_cursors REFCURSOR,
    p_course_id INT
) LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursors FOR
    SELECT * FROM vw_enrollment_join_student_course
    WHERE course_id = p_course_id
    ORDER BY registered_at DESC;
END;
$$;

-- Thống kê tổng số khóa học ───────────────────────
CREATE OR REPLACE FUNCTION fn_count_all_courses()
    RETURNS INT
    LANGUAGE plpgsql
AS $$
DECLARE
    total INT;
BEGIN
    SELECT COUNT(*) INTO total FROM course;
    RETURN total;
END;
$$;

-- Thống kê tổng số học viên (role = STUDENT)
CREATE OR REPLACE FUNCTION fn_count_all_students()
    RETURNS INT
    LANGUAGE plpgsql
AS $$
DECLARE
    total INT;
BEGIN
    SELECT COUNT(*) INTO total FROM student WHERE role = 'STUDENT';
    RETURN total;
END;
$$;

-- Thống kê số SV đăng ký theo từng khóa
CREATE OR REPLACE FUNCTION fn_count_students_per_course()
    RETURNS refcursor
    LANGUAGE plpgsql
AS $$
DECLARE
    cur refcursor := 'count_per_course_cursor';
BEGIN
    OPEN cur FOR
        SELECT c.id, c.name, COUNT(e.id) AS cnt
        FROM course c
                 LEFT JOIN enrollment e ON e.course_id = c.id AND e.status != 'CANCEL'
        GROUP BY c.id, c.name
        ORDER BY c.id ASC;
    RETURN cur;
END;
$$;

-- Thống kê top N khóa đông SV nhất
CREATE OR REPLACE FUNCTION fn_top_courses_by_enrollment(p_limit INT)
    RETURNS refcursor
    LANGUAGE plpgsql
AS $$
DECLARE
    cur refcursor := 'top_courses_cursor';
BEGIN
    OPEN cur FOR
        SELECT c.id, c.name, COUNT(e.id) AS cnt
        FROM course c
                 LEFT JOIN enrollment e ON e.course_id = c.id AND e.status != 'CANCEL'
        GROUP BY c.id, c.name
        ORDER BY cnt DESC, c.id ASC
        LIMIT p_limit;
    RETURN cur;
END;
$$;

-- Thống kê khóa học có > threshold SV
CREATE OR REPLACE FUNCTION fn_courses_with_enrollment_above(p_threshold INT)
    RETURNS refcursor
    LANGUAGE plpgsql
AS $$
DECLARE
    cur refcursor := 'courses_above_cursor';
BEGIN
    OPEN cur FOR
        SELECT c.id, c.name, COUNT(e.id) AS cnt
        FROM course c
                 LEFT JOIN enrollment e ON e.course_id = c.id AND e.status != 'CANCEL'
        GROUP BY c.id, c.name
        HAVING COUNT(e.id) > p_threshold
        ORDER BY cnt DESC, c.id ASC;
    RETURN cur;
END;
$$;