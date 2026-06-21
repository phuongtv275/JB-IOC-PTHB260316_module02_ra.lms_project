package business.impl;

import business.IEnrollmentService;
import dao.IEnrollmentDAO;
import dao.ICourseDAO;
import dao.IStudentDAO;
import dao.impl.EnrollmentDAOImpl;
import dao.impl.CourseDAOImpl;
import dao.impl.StudentDAOImpl;
import enums.EnrollmentStatus;
import model.Course;
import model.Enrollment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnrollmentServiceImpl implements IEnrollmentService {

    private final IEnrollmentDAO enrollmentDAO;
    private final ICourseDAO courseDAO;
    private final IStudentDAO studentDAO;

    public EnrollmentServiceImpl() {
        this.enrollmentDAO = new EnrollmentDAOImpl();
        this.courseDAO     = new CourseDAOImpl();
        this.studentDAO    = new StudentDAOImpl();
    }

    // ── getEnrollmentsByStudent ───────────────────────────────────

    @Override
    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        return enrollmentDAO.findByStudentId(studentId);
    }

    // ── getSortedEnrollmentsByStudent ─────────────────────────────

    @Override
    public List<Enrollment> getSortedEnrollmentsByStudent(int studentId, String field, String direction) {
        return enrollmentDAO.findByStudentIdSorted(studentId, field, direction);
    }

    // ── getEnrollmentsByStudent ───────────────────────────────────

    @Override
    public List<Enrollment> getEnrollments() {
        return enrollmentDAO.getEnrollments();
    }

    // ── registerCourse ────────────────────────────────────────────

    @Override
    public String registerCourse(int studentId, int courseId) {
        if (studentDAO.findById(studentId).isEmpty())
            return "Không tìm thấy học viên với ID = " + studentId;

        Optional<model.Course> courseOpt = courseDAO.findById(courseId);
        if (courseOpt.isEmpty())
            return "Không tìm thấy khóa học với ID = " + courseId;

        if (enrollmentDAO.existsByStudentAndCourse(studentId, courseId))
            return "Bạn đã đăng ký khóa học này rồi.";

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setStatus(EnrollmentStatus.WAITING);

        enrollmentDAO.save(enrollment);
        return null;
    }

    // ── cancelEnrollment ──────────────────────────────────────────

    @Override
    public String cancelEnrollment(int enrollmentId, int studentId) {
        Optional<Enrollment> opt = enrollmentDAO.findById(enrollmentId);
        if (opt.isEmpty())
            return "Không tìm thấy đăng ký với ID = " + enrollmentId;

        Enrollment enrollment = opt.get();

        // Chỉ cho phép học viên hủy chính đăng ký của mình
        if (enrollment.getStudentId() != studentId)
            return "Bạn không có quyền hủy đăng ký này.";

        if (enrollment.getStatus() != EnrollmentStatus.WAITING)
            return "Chỉ có thể hủy đăng ký đang ở trạng thái WAITING (chưa được xác nhận). " +
                    "Trạng thái hiện tại: " + enrollment.getStatus();

        enrollmentDAO.updateStatus(enrollmentId, EnrollmentStatus.CANCEL);
        return null;
    }

    // ── getEnrollmentsByCourse (Admin) ──────────────────────────────

    @Override
    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        return enrollmentDAO.findByCourseId(courseId);
    }

    // ── approveEnrollment (Admin) ────────────────────────────────────

    @Override
    public String approveEnrollment(int enrollmentId) {
        Optional<Enrollment> opt = enrollmentDAO.findById(enrollmentId);
        if (opt.isEmpty())
            return "Không tìm thấy đăng ký với ID = " + enrollmentId;

        Enrollment enrollment = opt.get();
        if (enrollment.getStatus() != EnrollmentStatus.WAITING)
            return "Chỉ có thể duyệt đăng ký đang ở trạng thái WAITING. " +
                    "Trạng thái hiện tại: " + enrollment.getStatus();

        enrollmentDAO.updateStatus(enrollmentId, EnrollmentStatus.CONFIRM);
        return null;
    }

    // ── denyEnrollment (Admin) ───────────────────────────────────────

    @Override
    public String denyEnrollment(int enrollmentId) {
        Optional<Enrollment> opt = enrollmentDAO.findById(enrollmentId);
        if (opt.isEmpty())
            return "Không tìm thấy đăng ký với ID = " + enrollmentId;

        Enrollment enrollment = opt.get();
        if (enrollment.getStatus() != EnrollmentStatus.WAITING)
            return "Chỉ có thể từ chối đăng ký đang ở trạng thái WAITING. " +
                    "Trạng thái hiện tại: " + enrollment.getStatus();

        enrollmentDAO.updateStatus(enrollmentId, EnrollmentStatus.DENIED);
        return null;
    }

    // ── removeStudentFromCourse (Admin xóa hẳn bản ghi) ──────────────

    @Override
    public String removeStudentFromCourse(int enrollmentId) {
        Optional<Enrollment> opt = enrollmentDAO.findById(enrollmentId);
        if (opt.isEmpty())
            return "Không tìm thấy đăng ký với ID = " + enrollmentId;

        enrollmentDAO.deleteById(enrollmentId);
        return null;
    }

    // ── getOverallStats ───────────────────────────────────────────

    @Override
    public int[] getOverallStats() {
        return new int[]{ enrollmentDAO.countAllCourses(), enrollmentDAO.countAllStudents() };
    }

    // ── getStudentCountPerCourse ──────────────────────────────────

    @Override
    public Map<String, Long> getStudentCountPerCourse() {
        Map<Integer, Long> raw = enrollmentDAO.countStudentsPerCourse();
        Map<String, Long> result = new LinkedHashMap<>();

        for (Map.Entry<Integer, Long> entry : raw.entrySet()) {
            String courseName = courseDAO.findById(entry.getKey())
                    .map(Course::getName)
                    .orElse("Khóa học #" + entry.getKey());
            result.put("[" + entry.getKey() + "] " + courseName, entry.getValue());
        }
        return result;
    }

    // ── getTop5CoursesByEnrollment ────────────────────────────────

    @Override
    public List<Object[]> getTop5CoursesByEnrollment() {
        return enrollmentDAO.topCoursesByEnrollment(5);
    }

    // ── getCoursesAboveThreshold ──────────────────────────────────

    @Override
    public List<Object[]> getCoursesAboveThreshold(int threshold) {
        return enrollmentDAO.coursesWithEnrollmentAbove(threshold);
    }
}
