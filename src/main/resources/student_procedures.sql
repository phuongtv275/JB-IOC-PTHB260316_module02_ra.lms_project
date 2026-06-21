-- ============================================================
-- LMS - Learning Management System
-- PostgreSQL Database Procedures for Students Script
-- ============================================================

-- Tim kiếm sinh viên theo email
CREATE OR REPLACE PROCEDURE search_student_by_email(INOUT p_cursor REFCURSOR, p_email VARCHAR(100))
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursor FOR
        SELECT id, name, dob, email, sex, phone, role, created_at
        FROM student
        WHERE role = 'STUDENT' AND email = p_email
        ORDER BY id ASC;
END;
$$;

-- Hiển thị danh sách sinh viên
CREATE OR REPLACE PROCEDURE get_students(INOUT p_cursor REFCURSOR)
LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursor FOR
    SELECT id, name, dob, email, sex, phone, role, password, created_at
    FROM student
    WHERE role = 'STUDENT'
    ORDER BY id ASC;
END;
$$;

-- Thêm mới sinh viên
CREATE OR REPLACE PROCEDURE add_student(
    p_name VARCHAR(100),
    p_dob DATE,
    p_email VARCHAR(100),
    p_sex BOOLEAN,
    p_phone VARCHAR(20),
    p_password VARCHAR(255),
    INOUT p_id INT DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO student (name, dob, email, sex, phone, password)
    VALUES (p_name, p_dob, p_email, p_sex, p_phone, p_password)
    RETURNING id INTO p_id;
END;
$$;

-- Chỉnh sửa thông tin sinh viên
CREATE OR REPLACE PROCEDURE update_student(
    p_id INT,
    p_name VARCHAR(100),
    p_dob DATE,
    p_sex BOOLEAN,
    p_phone VARCHAR(20)
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE student
    SET name = p_name, dob = p_dob, sex = p_sex, phone = p_phone
    WHERE role = 'STUDENT' AND id = p_id;
END;
$$;

-- Xóa sinh viên theo id
CREATE OR REPLACE PROCEDURE delete_student(p_id INT)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM student
    WHERE id = p_id;
END;
$$;

-- Tim kiếm sinh viên theo tên hoặc email hoặc id
CREATE OR REPLACE PROCEDURE search_student_by_name_or_email_or_id(
    INOUT p_cursor REFCURSOR,
    p_keyword VARCHAR(100))
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursor FOR
        SELECT id, name, dob, email, sex, phone, role, password, created_at
        FROM student
        WHERE role = 'STUDENT'
          AND (
              name ILIKE '%' || p_keyword || '%'
                  OR email ILIKE '%' || p_keyword || '%'
                  OR (p_keyword ~ '^\d+$' AND id = p_keyword::INTEGER)
              )
        ORDER BY id ASC;
END;
$$;

-- Tim kiếm sinh viên theo tên
CREATE OR REPLACE PROCEDURE search_student_by_name(INOUT p_cursor REFCURSOR, p_name VARCHAR(100))
LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursor FOR
    SELECT id, name, dob, email, sex, phone, role, password, created_at
    FROM student
    WHERE name ILIKE '%' || p_name || '%'
    ORDER BY id ASC;
END;
$$;

-- Tim kiếm sinh viên theo id
CREATE OR REPLACE PROCEDURE search_student_by_id(INOUT p_cursor REFCURSOR, p_id INT)
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursor FOR
        SELECT id, name, dob, email, sex, phone, role, password, created_at
        FROM student
        WHERE role = 'STUDENT' AND id = p_id
        ORDER BY id ASC;
END;
$$;

-- Kiểm tra email đã được sử dụng
CREATE OR REPLACE PROCEDURE check_email_exists(
    OUT p_result REFCURSOR,
    IN  p_email      VARCHAR,
    IN  p_exclude_id INT
)
    LANGUAGE plpgsql AS $$
BEGIN
    OPEN p_result FOR
        SELECT 1 FROM student WHERE email = p_email AND id != p_exclude_id;
END;
$$;

-- Chỉnh sửa mật khẩu sinh viên
CREATE OR REPLACE PROCEDURE update_student_password(
    p_id INT,
    p_password VARCHAR(255)
)
    LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE student
    SET password = p_password
    WHERE role = 'STUDENT' AND id = p_id;
END;
$$;