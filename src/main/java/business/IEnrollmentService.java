package business;

import model.Enrollment;

import java.util.List;

public interface IEnrollmentService {

    List<Enrollment> getEnrollmentsByStudent(int studentId);

    List<Enrollment> getSortedEnrollmentsByStudent(int studentId, String field, String direction);

    List<Enrollment> getEnrollments();

    /** Đăng ký khóa học — trả về thông báo lỗi nếu có, null nếu thành công */
    String registerCourse(int studentId, int courseId);

    /** Hủy đăng ký — chỉ cho phép khi status đang là WAITING. Trả về lỗi nếu có, null nếu thành công */
    String cancelEnrollment(int enrollmentId, int studentId);

    // ── Admin: quản lý đăng ký ──────────────────────────────────────

    /** Danh sách SV đăng ký theo 1 khóa học */
    List<Enrollment> getEnrollmentsByCourse(int courseId);

    /** Admin duyệt 1 đăng ký — chỉ cho phép duyệt khi đang WAITING. Trả về lỗi nếu có, null nếu thành công */
    String approveEnrollment(int enrollmentId);

    /** Admin từ chối 1 đăng ký — chỉ cho phép khi đang WAITING. Trả về lỗi nếu có, null nếu thành công */
    String denyEnrollment(int enrollmentId);

    /** Admin xóa hẳn 1 SV khỏi khóa học (xóa bản ghi enrollment). Trả về lỗi nếu có, null nếu thành công */
    String removeStudentFromCourse(int enrollmentId);
}