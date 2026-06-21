package business;

import model.Enrollment;

import java.util.List;

public interface IEnrollmentService {

    List<Enrollment> getEnrollmentsByStudent(int studentId);

    List<Enrollment> getSortedEnrollmentsByStudent(int studentId, String field, String direction);

    /** Đăng ký khóa học — trả về thông báo lỗi nếu có, null nếu thành công */
    String registerCourse(int studentId, int courseId);

    /** Hủy đăng ký — chỉ cho phép khi status đang là WAITING. Trả về lỗi nếu có, null nếu thành công */
    String cancelEnrollment(int enrollmentId, int studentId);
}