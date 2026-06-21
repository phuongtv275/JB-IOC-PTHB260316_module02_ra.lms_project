package business;

import model.Student;

import java.util.List;
import java.util.Optional;

public interface IStudentService {
    /**
     * Đăng nhập: kiểm tra email + password.
     * Trả về Student nếu hợp lệ, empty nếu sai thông tin.
     */
    Optional<Student> login(String email, String password);

    /** Hiển thị danh sách sinh viên **/
    List<Student> getAllStudents();

    /** Tìm sinh viên theo id **/
    Optional<Student> getStudentById(int id);

    /**
     * Thêm mới học viên.
     * @param dobStr định dạng yyyy-MM-dd
     * @param sexStr "Nam" hoặc "Nữ"
     * @return thông báo lỗi nếu có, null nếu thành công
     */
    String addStudent(String name, String dobStr, String email, String sexStr, String phone, String password);

    /** Cập nhật từng trường — trả về thông báo lỗi nếu có, null nếu thành công */
    String updateStudentName(int id, String newName);
    String updateStudentDob(int id, String newDobStr);
    String updateStudentEmail(int id, String newEmail);
    String updateStudentPhone(int id, String newPhone);
    String updateStudentSex(int id, String newSexStr);

    /** Trả về true nếu xóa thành công, false nếu không tìm thấy */
    boolean deleteStudent(int id);

    /** Tìm theo tên, email, hoặc id */
    List<Student> searchStudent(String keyword);

    List<Student> getSortedStudents(String field, String direction);

    /**
     * Đổi mật khẩu — xác thực bằng mật khẩu cũ.
     * @return thông báo lỗi nếu có, null nếu thành công
     */
    String changePassword(int studentId, String oldPassword, String newPassword, String confirmPassword);
}
