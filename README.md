# 📚 LMS - Learning Management System

Hệ thống quản lý học tập (Learning Management System) xây dựng bằng **Java** theo mô hình **Console Application**, sử dụng kiến trúc phân lớp rõ ràng (Presentation Layer  →  Business Layer  →  DAO Layer  →  PostgreSQL
   (View)               (Service)          (DAO)         (Stored Procedures)).

---

## 🛠️ Công nghệ sử dụng

| Thành phần        | Chi tiết                        |
|-------------------|---------------------------------|
| Ngôn ngữ          | Java 25                         |
| Build tool        | Maven                           |
| Cơ sở dữ liệu     | PostgreSQL                      |
| JDBC Driver       | `org.postgresql:postgresql:42.6.0` |
| Mã hóa mật khẩu   | `org.mindrot:jbcrypt:0.4`       |
| Stored Procedures | PL/pgSQL (PostgreSQL)           |

---

## 📁 Cấu trúc dự án
```
ra.lms_project/ 
├── src/ 
│ ├── main/ 
│ │ ├── java/ 
│ │ │ ├── Main.java # Điểm khởi động ứng dụng 
│ │ │ ├── model/ # Lớp thực thể (Entity) 
│ │ │ │ ├── Student.java 
│ │ │ │ ├── Course.java 
│ │ │ │ └── Enrollment.java 
│ │ │ ├── enums/ # Enum dùng chung 
│ │ │ │ ├── Role.java # ADMIN, STUDENT 
│ │ │ │ └── EnrollmentStatus.java # WAITING, CONFIRM, DENIED, CANCEL 
│ │ │ ├── dao/ # Data Access Object (tầng truy cập CSDL) 
│ │ │ │ ├── ICourseDAO.java 
│ │ │ │ ├── IStudentDAO.java 
│ │ │ │ ├── IEnrollmentDAO.java 
│ │ │ │ └── impl/ 
│ │ │ │     ├── CourseDAOImpl.java 
│ │ │ │     ├── StudentDAOImpl.java 
│ │ │ │     └── EnrollmentDAOImpl.java 
│ │ │ ├── business/ # Business Logic (tầng nghiệp vụ) 
│ │ │ │ ├── ICourseService.java 
│ │ │ │ ├── IStudentService.java 
│ │ │ │ ├── IEnrollmentService.java 
│ │ │ │ └── impl/ 
│ │ │ │     ├── CourseServiceImpl.java 
│ │ │ │     ├── StudentServiceImpl.java 
│ │ │ │     └── EnrollmentServiceImpl.java 
│ │ │ ├── presentation/ # Tầng giao diện console (View) 
│ │ │ │ ├── LoginView.java 
│ │ │ │ ├── AdminView.java 
│ │ │ │ ├── StudentView.java 
│ │ │ │ ├── CourseView.java 
│ │ │ │ ├── StudentManagementView.java 
│ │ │ │ └── EnrollmentManagementView.java 
│ │ │ └── utils/ # Tiện ích dùng chung 
│ │ │   ├── DBUtil.java # Kết nối CSDL 
│ │ │   └── ConsoleUtil.java # Hiển thị console (màu, bảng, menu) 
│ │ └── resources/ 
│ │ ├── database_create.sql # Script tạo CSDL + dữ liệu mẫu 
│ │ ├── course_procedures.sql # Stored procedures cho khóa học 
│ │ ├── student_procedures.sql # Stored procedures cho học viên 
│ │ └── enrollment_procedures.sql # Stored procedures + View cho đăng ký 
├── pom.xml └── README.md
```

---

## 🗄️ Cơ sở dữ liệu

### Các bảng chính

| Bảng         | Mô tả                                               |
|--------------|-----------------------------------------------------|
| `student`    | Lưu thông tin tài khoản (cả Admin và Student)       |
| `course`     | Lưu thông tin khóa học                              |
| `enrollment` | Lưu đăng ký khóa học của học viên                   |

### Enum types (PostgreSQL)
- `role_type`: `ADMIN`, `STUDENT`
- `enrollment_status`: `WAITING`, `CONFIRM`, `DENIED`, `CANCEL`

### View
- `vw_enrollment_join_student_course`: JOIN 3 bảng `enrollment`, `course`, `student` — hỗ trợ truy vấn đăng ký kèm tên học viên và tên khóa học.

### Indexes
- `idx_student_name`, `idx_student_email`
- `idx_course_name`
- `idx_enrollment_student`, `idx_enrollment_course`

---

## ✅ Tính năng đã triển khai

### 🔐 Xác thực (Authentication)
- Đăng nhập bằng **email + mật khẩu**
- Mật khẩu được mã hóa bằng **BCrypt**
- Phân quyền theo vai trò: **ADMIN** và **STUDENT**
- Đăng xuất và quay lại màn hình đăng nhập

---

### 👨‍💼 Chức năng Admin

#### Quản lý Khóa học (`CourseView`)
- Hiển thị danh sách tất cả khóa học
- Thêm mới khóa học (tên, thời lượng, giảng viên)
- Chỉnh sửa khóa học (từng trường riêng lẻ: tên / thời lượng / giảng viên)
- Xóa khóa học (có xác nhận)
- Tìm kiếm khóa học theo tên (ILIKE)
- Sắp xếp theo tên hoặc ID (tăng/giảm dần)

#### Quản lý Học viên (`StudentManagementView`)
- Hiển thị danh sách tất cả học viên
- Thêm mới học viên (tên, ngày sinh, email, giới tính, SĐT, mật khẩu)
- Chỉnh sửa học viên (từng trường riêng lẻ: tên / ngày sinh / email / SĐT / giới tính)
- Xóa học viên (có xác nhận, cascade xóa đăng ký liên quan)
- Tìm kiếm theo **tên**, **email** hoặc **ID**
- Sắp xếp theo tên hoặc ID (tăng/giảm dần)

#### Quản lý Đăng ký (`EnrollmentManagementView`)
- Xem danh sách học viên đăng ký theo từng khóa học
- Duyệt đăng ký: chuyển trạng thái `WAITING` → `CONFIRM`
- Từ chối đăng ký: chuyển trạng thái `WAITING` → `DENIED`
- Xóa hẳn bản ghi đăng ký của học viên khỏi khóa học

---

### 🎓 Chức năng Học viên (`StudentView`)
- Xem danh sách tất cả khóa học / tìm kiếm khóa học theo tên
- Đăng ký khóa học (trạng thái ban đầu: `WAITING`)
- Xem danh sách khóa học đã đăng ký, sắp xếp theo:
    - Tên khóa học (tăng/giảm)
    - Ngày đăng ký (tăng/giảm)
- Hủy đăng ký (chỉ được hủy khi đang ở trạng thái `WAITING`)
- Đổi mật khẩu (xác thực bằng mật khẩu cũ, yêu cầu xác nhận mật khẩu mới)

---

## 🚀 Hướng dẫn cài đặt & chạy

### 1. Yêu cầu
- Java 25+
- PostgreSQL đang chạy
- Maven

### 2. Thiết lập CSDL
Chạy lần lượt các file SQL trong thư mục `src/main/resources/`:
```sql
-- Bước 1: Tạo database và bảng
\i database_create.sql

-- Bước 2: Tạo stored procedures cho Course
\i course_procedures.sql

-- Bước 3: Tạo stored procedures cho Student
\i student_procedures.sql

-- Bước 4: Tạo stored procedures cho Enrollment
\i enrollment_procedures.sql
```
---
### 3. Cấu hình kết nối
Cập nhật thông tin kết nối PostgreSQL trong file `src/main/java/utils/DBUtil.java`.

---
### 4. Build & Chạy
```shell script
mvn compile
mvn exec:java -Dexec.mainClass="Main"
```
---
### 🔑 Tài khoản mặc định (Seed Data)
| Vai trò | Email           | Mật khẩu |
|---------|-----------------|-------|
| ADMIN   | admin@lms.com   | admin |
| STUDENT | student@lms.com | student |

---
```aiignore
Presentation Layer  →  Business Layer  →  DAO Layer  →  PostgreSQL
   (View)               (Service)          (DAO)         (Stored Procedures)
```
- **Presentation**: Giao tiếp với người dùng qua console, gọi Service
- **Business**: Xử lý logic nghiệp vụ, validate đầu vào, gọi DAO
- **DAO**: Thực thi truy vấn thông qua Stored Procedures
- **Utils**: quản lý kết nối JDBC, hỗ trợ hiển thị bảng/menu/màu sắc trên console `DBUtil``ConsoleUtil`