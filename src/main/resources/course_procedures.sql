-- ============================================================
-- LMS - Learning Management System
-- PostgreSQL Database Procedures for Course Script
-- ============================================================

-- Hiển thị danh sách khóa học
CREATE OR REPLACE PROCEDURE get_courses(INOUT p_cursor REFCURSOR)
LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursor FOR
    SELECT id, name, duration, instructor, created_at
    FROM course
    ORDER BY id ASC;
END;
$$;

-- Thêm mới khóa học
CREATE OR REPLACE PROCEDURE add_course(p_name VARCHAR(100), p_duration INT, p_instructor VARCHAR(100), INOUT p_id INT DEFAULT NULL)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO course (name, duration, instructor)
    VALUES (p_name, p_duration, p_instructor)
    RETURNING id INTO p_id;
END;
$$;

-- Chỉnh sửa thông tin khóa học
CREATE OR REPLACE PROCEDURE update_course(p_id INT, p_name VARCHAR(100), p_duration INT, p_instructor VARCHAR(100))
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE course
    SET name = p_name, duration = p_duration, instructor = p_instructor
    WHERE id = p_id;
END;
$$;

-- Xóa khóa học theo id
CREATE OR REPLACE PROCEDURE delete_course(p_id INT)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM course
    WHERE id = p_id;
END;
$$;

-- Tim kiếm khóa học theo tên
CREATE OR REPLACE PROCEDURE search_course_by_name(INOUT p_cursor REFCURSOR, p_name VARCHAR(100))
LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursor FOR
    SELECT id, name, duration, instructor, created_at
    FROM course
    WHERE name ILIKE '%' || p_name || '%'
    ORDER BY id ASC;
END;
$$;

-- Tim kiếm khóa học theo tên
CREATE OR REPLACE PROCEDURE search_course_by_id(INOUT p_cursor REFCURSOR, p_id INT)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursor FOR
        SELECT id, name, duration, instructor, created_at
        FROM course
        WHERE id = p_id
        ORDER BY id ASC;
END;
$$;
