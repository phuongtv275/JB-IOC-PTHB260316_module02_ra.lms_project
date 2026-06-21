-- ============================================================
-- SCRIPT CHÈN DỮ LIỆU BỔ SUNG (SEED DATA)
-- Mật khẩu mặc định cho toàn bộ sinh viên mới: 123456
-- Chuỗi hash: $2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay
-- ============================================================

-- 1. Chèn thêm 20 khóa học
INSERT INTO course (name, duration, instructor) VALUES
                                                    ('Lập trình Python cơ bản', 60, 'Nguyễn Văn A'),
                                                    ('Cấu trúc dữ liệu và Giải thuật', 75, 'Trần Thị B'),
                                                    ('Phát triển Web với Spring Boot', 90, 'Lê Văn C'),
                                                    ('Thiết kế giao diện với ReactJS', 45, 'Phạm Thị D'),
                                                    ('Trí tuệ nhân tạo nhập môn', 60, 'Nguyễn Văn A'),
                                                    ('Học sâu (Deep Learning) với PyTorch', 80, 'Trần Thị B'),
                                                    ('Quản trị Cơ sở dữ liệu PostgreSQL nâng cao', 50, 'Lê Văn C'),
                                                    ('Kiến trúc hệ thống và Microservices', 70, 'Phạm Thị D'),
                                                    ('Lập trình hướng đối tượng với Java', 60, 'Nguyễn Văn A'),
                                                    ('Điện toán đám mây với AWS', 55, 'Trần Thị B'),
                                                    ('DevOps và CI/CD cơ bản', 50, 'Lê Văn C'),
                                                    ('Kiểm thử phần mềm tự động', 40, 'Phạm Thị D'),
                                                    ('Hệ điều hành và Kiến trúc máy tính', 60, 'Nguyễn Văn A'),
                                                    ('Phân tích dữ liệu với Python', 65, 'Trần Thị B'),
                                                    ('Xử lý ngôn ngữ tự nhiên (NLP)', 75, 'Lê Văn C'),
                                                    ('Thị giác máy tính (Computer Vision)', 80, 'Phạm Thị D'),
                                                    ('Kỹ năng giao tiếp cho Lập trình viên', 30, 'Nguyễn Văn A'),
                                                    ('Quản lý dự án theo mô hình Agile/Scrum', 35, 'Trần Thị B'),
                                                    ('Bảo mật thông tin và Ứng dụng Web', 55, 'Lê Văn C'),
                                                    ('Lập trình di động với Flutter', 70, 'Phạm Thị D');

-- 2. Chèn thêm 33 sinh viên
INSERT INTO student (name, dob, email, sex, phone, role, password) VALUES
                                                                       ('Nguyễn Văn Bình', '2004-05-12', 'binh.nv@lms.com', TRUE, '0912345678', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Trần Thị Cẩm', '2003-08-22', 'cam.tt@lms.com', FALSE, '0923456789', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Lê Hoàng Dung', '2004-01-15', 'dung.lh@lms.com', FALSE, '0934567890', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Phạm Minh Đức', '2003-11-30', 'duc.pm@lms.com', TRUE, '0945678901', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Võ Thị Anh', '2004-03-05', 'anh.vt@lms.com', FALSE, '0956789012', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Hoàng Quốc Bảo', '2002-09-18', 'bao.hq@lms.com', TRUE, '0967890123', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Phan Thu Châu', '2004-07-25', 'chau.pt@lms.com', FALSE, '0978901234', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Vũ Tiến Đạt', '2003-04-14', 'dat.vt@lms.com', TRUE, '0989012345', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Đặng Mỹ Hạnh', '2004-12-01', 'hanh.dm@lms.com', FALSE, '0990123456', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Bùi Quang Huy', '2003-02-28', 'huy.bq@lms.com', TRUE, '0901234567', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Đỗ Thúy Kiều', '2004-10-10', 'kieu.dt@lms.com', FALSE, '0911234567', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Hồ Văn Long', '2003-06-20', 'long.hv@lms.com', TRUE, '0922345678', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Ngô Thị Minh', '2004-08-15', 'minh.nt@lms.com', FALSE, '0933456789', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Dương Hồng Nam', '2002-12-25', 'nam.dh@lms.com', TRUE, '0944567890', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Lý Thanh Phong', '2003-05-09', 'phong.lt@lms.com', TRUE, '0955678901', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Trịnh Đình Quân', '2004-02-14', 'quan.td@lms.com', TRUE, '0966789012', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Vương Thảo Nguyên', '2003-07-19', 'nguyen.vt@lms.com', FALSE, '0977890123', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Đinh Gia Bảo', '2004-09-02', 'bao.dg@lms.com', TRUE, '0988901234', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Lâm Minh Triết', '2003-03-31', 'triet.lm@lms.com', TRUE, '0999012345', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Mai Phương Thảo', '2004-11-11', 'thao.mp@lms.com', FALSE, '0900123456', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Phùng Tiến Thành', '2003-01-28', 'thanh.pt@lms.com', TRUE, '0911123456', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Hà Thị Ngọc', '2004-06-17', 'ngoc.ht@lms.com', FALSE, '0922234567', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Cao Minh Khôi', '2003-10-04', 'khoi.cm@lms.com', TRUE, '0933345678', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Diệp Tú Anh', '2004-04-29', 'anh.dt@lms.com', FALSE, '0944456789', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Tạ Văn Hùng', '2002-08-13', 'hung.tv@lms.com', TRUE, '0955567890', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Quách Bảo Châu', '2004-02-23', 'chau.qb@lms.com', FALSE, '0966678901', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Lương Hải Yến', '2003-12-07', 'yen.lh@lms.com', FALSE, '0977789012', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Châu Vĩnh Khang', '2004-05-19', 'khang.cv@lms.com', TRUE, '0988890123', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Lưu Tuấn Kiệt', '2003-07-08', 'kiet.lt@lms.com', TRUE, '0999901234', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Thiều Bảo Trâm', '2004-03-16', 'tram.tb@lms.com', FALSE, '0900012345', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Nghiêm Xuân Mạnh', '2003-09-24', 'manh.nx@lms.com', TRUE, '0911112345', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Tăng Thành Công', '2004-01-01', 'cong.tt@lms.com', TRUE, '0922223456', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay'),
                                                                       ('Đoàn Minh Khang', '2003-06-30', 'khang.dm@lms.com', TRUE, '0933334567', 'STUDENT', '$2a$12$vI8aWBnW3fID.ve4OJF9G.0ldCwkW9G83E5ov76OXY3DSfGptm9ay');

-- 3. Tạo ngẫu nhiên chính xác 200 bản ghi enrollment duy nhất
-- Sử dụng khối lệnh ẩn danh DO PL/pgSQL để quét ID động, tránh lỗi Conflict hoặc hụt dòng.
DO $$
    DECLARE
        v_student_id INT;
        v_course_id  INT;
        v_count      INT := 0;
        v_status     enrollment_status;
        v_statuses   enrollment_status[] := ARRAY['WAITING'::enrollment_status, 'DENIED'::enrollment_status, 'CANCEL'::enrollment_status, 'CONFIRM'::enrollment_status];
    BEGIN
        -- Vòng lặp sẽ tiếp tục cho đến khi tổng số dòng trong bảng enrollment đạt đúng 200 bản ghi
        WHILE v_count < 200 LOOP
                -- Lấy ngẫu nhiên id từ bảng student và course hiện tại
                SELECT id INTO v_student_id FROM student ORDER BY random() LIMIT 1;
                SELECT id INTO v_course_id FROM course ORDER BY random() LIMIT 1;

                -- Lấy ngẫu nhiên trạng thái đăng ký
                v_status := v_statuses[floor(random() * 4 + 1)];

                -- Thêm vào bảng enrollment, nếu bị trùng cặp (student_id, course_id) thì bỏ qua
                INSERT INTO enrollment (student_id, course_id, status, registered_at)
                VALUES (
                           v_student_id,
                           v_course_id,
                           v_status,
                           CURRENT_TIMESTAMP - (random() * INTERVAL '30 days') -- Thời gian đăng ký ngẫu nhiên trong vòng 30 ngày qua
                       )
                ON CONFLICT (student_id, course_id) DO NOTHING;

                -- Cập nhật lại số lượng bản ghi thực tế đã chèn thành công
                SELECT COUNT(*) INTO v_count FROM enrollment;
            END LOOP;
    END $$;

-- Kiểm tra lại số lượng dữ liệu sau khi bổ sung
SELECT 'student' AS table_name, COUNT(*) FROM student
UNION ALL
SELECT 'course' AS table_name, COUNT(*) FROM course
UNION ALL
SELECT 'enrollment' AS table_name, COUNT(*) FROM enrollment;