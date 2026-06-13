package business;

import model.Student;

import java.util.Optional;

public interface IStudentService {
    /**
     * Đăng nhập: kiểm tra email + password.
     * Trả về Student nếu hợp lệ, empty nếu sai thông tin.
     */
    Optional<Student> login(String email, String password);
}
