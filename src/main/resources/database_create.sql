-- ============================================================
-- LMS - Learning Management System
-- PostgreSQL Database Initialization Script
-- ============================================================

-- Tạo database lms, sử dụng schema mặc định (public)
CREATE DATABASE lms;

-- Xóa các bảng nếu đã tồn tại (theo thứ tự phụ thuộc FK)
DROP TABLE IF EXISTS enrollment;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS student;

-- Xóa các type enum nếu đã tồn tại
DROP TYPE IF EXISTS role_type;
DROP TYPE IF EXISTS enrollment_status;

-- ============================================================
-- 1. ENUM Types
-- ============================================================

CREATE TYPE role_type AS ENUM ('ADMIN', 'STUDENT');

CREATE TYPE enrollment_status AS ENUM ('WAITING', 'DENIED', 'CANCEL', 'CONFIRM');

-- ============================================================
-- 2. Bảng student (Học viên)
-- ============================================================

CREATE TABLE student (
                         id          SERIAL          PRIMARY KEY,
                         name        VARCHAR(100)    NOT NULL,
                         dob         DATE            NOT NULL,
                         email       VARCHAR(100)    NOT NULL UNIQUE,
                         sex         BOOLEAN         NOT NULL,               -- TRUE = Nam, FALSE = Nữ
                         phone       VARCHAR(20),
                         role        role_type       NOT NULL DEFAULT 'STUDENT',
                         password    VARCHAR(255)    NOT NULL,
                         created_at  DATE            NOT NULL DEFAULT CURRENT_DATE
);

COMMENT ON TABLE  student               IS 'Bảng lưu thông tin học viên và admin';
COMMENT ON COLUMN student.id            IS 'Mã định danh học viên';
COMMENT ON COLUMN student.name          IS 'Họ tên học viên';
COMMENT ON COLUMN student.dob           IS 'Ngày sinh';
COMMENT ON COLUMN student.email         IS 'Địa chỉ email (duy nhất)';
COMMENT ON COLUMN student.sex           IS 'Giới tính: TRUE = Nam, FALSE = Nữ';
COMMENT ON COLUMN student.phone         IS 'Số điện thoại (có thể NULL)';
COMMENT ON COLUMN student.role          IS 'Quyền hạn: ADMIN hoặc STUDENT';
COMMENT ON COLUMN student.password      IS 'Mật khẩu đã mã hóa';
COMMENT ON COLUMN student.created_at    IS 'Ngày tạo tài khoản';

-- ============================================================
-- 3. Bảng course (Khóa học)
-- ============================================================

CREATE TABLE course (
                        id          SERIAL          PRIMARY KEY,
                        name        VARCHAR(100)    NOT NULL,
                        duration    INT             NOT NULL CHECK (duration > 0),  -- Thời lượng tính bằng giờ
                        instructor  VARCHAR(100)    NOT NULL,
                        created_at  DATE            NOT NULL DEFAULT CURRENT_DATE
);

COMMENT ON TABLE  course                IS 'Bảng lưu thông tin khóa học';
COMMENT ON COLUMN course.id             IS 'Mã định danh khóa học';
COMMENT ON COLUMN course.name           IS 'Tên khóa học';
COMMENT ON COLUMN course.duration       IS 'Thời lượng khóa học (giờ)';
COMMENT ON COLUMN course.instructor     IS 'Giảng viên phụ trách';
COMMENT ON COLUMN course.created_at     IS 'Ngày thêm khóa học';

-- ============================================================
-- 4. Bảng enrollment (Đăng ký khóa học)
-- ============================================================

CREATE TABLE enrollment (
                            id              SERIAL              PRIMARY KEY,
                            student_id      INT                 NOT NULL,
                            course_id       INT                 NOT NULL,
                            registered_at   TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            status          enrollment_status   NOT NULL DEFAULT 'WAITING',

    -- Ràng buộc khóa ngoại
                            CONSTRAINT fk_enrollment_student
                                FOREIGN KEY (student_id) REFERENCES student(id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,

                            CONSTRAINT fk_enrollment_course
                                FOREIGN KEY (course_id) REFERENCES course(id)
                                    ON DELETE CASCADE
                                    ON UPDATE CASCADE,

    -- Một học viên không thể đăng ký cùng một khóa học hai lần
                            CONSTRAINT uq_enrollment_student_course
                                UNIQUE (student_id, course_id)
);

COMMENT ON TABLE  enrollment                IS 'Bảng lưu thông tin đăng ký khóa học';
COMMENT ON COLUMN enrollment.id             IS 'Mã định danh đăng ký';
COMMENT ON COLUMN enrollment.student_id     IS 'Mã học viên (FK → student.id)';
COMMENT ON COLUMN enrollment.course_id      IS 'Mã khóa học (FK → course.id)';
COMMENT ON COLUMN enrollment.registered_at  IS 'Thời điểm đăng ký';
COMMENT ON COLUMN enrollment.status        IS 'Trạng thái: WAITING | DENIED | CANCEL | CONFIRM';

-- ============================================================
-- 5. Index hỗ trợ tìm kiếm
-- ============================================================

-- Tìm kiếm học viên theo tên (ILIKE / tìm tương đối)
CREATE INDEX idx_student_name       ON student(name);
-- Tìm kiếm theo email
CREATE INDEX idx_student_email      ON student(email);

-- Tìm kiếm khóa học theo tên
CREATE INDEX idx_course_name        ON course(name);

-- Tra cứu đăng ký theo học viên hoặc khóa học
CREATE INDEX idx_enrollment_student ON enrollment(student_id);
CREATE INDEX idx_enrollment_course  ON enrollment(course_id);

-- ============================================================
-- 6. Dữ liệu mẫu (seed data)
-- ============================================================

-- Tài khoản Admin mặc định (password: admin)
INSERT INTO student (name, dob, email, sex, phone, role, password)
VALUES ('Administrator', '1990-01-01', 'admin@lms.com', TRUE, NULL, 'ADMIN', '$2a$12$LaDPC3r7FTbo0HFStKuRoubBcD26sgVhxqtyjIvUrBbUTfU91hEvy');

-- Tài khoản Student mặc định (password: student)
INSERT INTO student (name, dob, email, sex, phone, role, password)
VALUES ('Student', '2003-01-01', 'student@lms.com', FALSE, '0987654321', 'STUDENT', '$2a$12$yjKCy1OggEpXvRpuvHPs9e.6AoIdE6RRshNyx.HE8NEjImM5QEcii');

-- Một số khóa học mẫu
INSERT INTO course (name, duration, instructor) VALUES
                                                    ('Java cơ bản',         60, 'Nguyễn Văn A'),
                                                    ('Java nâng cao',       80, 'Trần Thị B'),
                                                    ('Cơ sở dữ liệu',      40, 'Lê Văn C'),
                                                    ('Lập trình Web',       90, 'Phạm Thị D');

-- 7. Tạo procedures cho đăng nhập
-- Tim kiếm người dùng theo email
CREATE OR REPLACE PROCEDURE search_user_by_email(INOUT p_cursor REFCURSOR, p_email VARCHAR(100))
    LANGUAGE plpgsql
AS $$
BEGIN
    OPEN p_cursor FOR
        SELECT id, name, dob, email, sex, phone, role, password, created_at
        FROM student
        WHERE email = p_email
        ORDER BY id ASC;
END;
$$;