package dao;

import enums.EnrollmentStatus;
import model.Enrollment;

import java.util.List;
import java.util.Optional;

public interface IEnrollmentDAO {

    Optional<Enrollment> findById(int id);

    /** Tìm tất cả đăng ký của 1 học viên (kèm tên khóa học, join course) */
    List<Enrollment> findByStudentId(int studentId);

    /** Sắp xếp đăng ký của 1 học viên: field = "courseName" | "registeredAt" */
    List<Enrollment> findByStudentIdSorted(int studentId, String field, String direction);

    List<Enrollment> getEnrollments();

    /** Kiểm tra học viên đã đăng ký khóa học này chưa (bất kỳ trạng thái nào) */
    boolean existsByStudentAndCourse(int studentId, int courseId);

    void save(Enrollment enrollment);

    void updateStatus(int id, EnrollmentStatus status);

    void deleteById(int id);

    // ── Admin: quản lý đăng ký theo khóa học ────────────────────────

    /** Tất cả đăng ký (kèm tên SV, tên khóa) của 1 khóa học, mới nhất trước */
    List<Enrollment> findByCourseId(int courseId);
}
