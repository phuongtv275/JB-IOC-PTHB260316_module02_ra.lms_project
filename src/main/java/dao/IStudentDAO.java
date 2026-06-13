package dao;

import model.Student;

import java.util.Optional;

public interface IStudentDAO {

    /**
     * Tìm học viên theo email (dùng cho đăng nhập).
     */
    Optional<Student> findByEmail(String email);
}
