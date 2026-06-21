-- ============================================================
-- LMS - Learning Management System
-- PostgreSQL Database Procedures for Course Script
-- ============================================================

-- Enrollment View: danh sách tất cả các khóa học với tên khóa học và tên sinh viên
CREATE OR REPLACE VIEW vw_enrollment_join_student_course AS
SELECT e.id, e.student_id, e.course_id, e.registered_at, e.status,
       c.name AS course_name, s.name AS student_name
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