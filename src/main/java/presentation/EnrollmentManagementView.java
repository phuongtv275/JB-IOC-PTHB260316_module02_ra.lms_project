package presentation;

import business.ICourseService;
import business.IEnrollmentService;
import business.impl.CourseServiceImpl;
import business.impl.EnrollmentServiceImpl;
import model.Course;
import model.Enrollment;
import utils.ConsoleUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class EnrollmentManagementView {

    private final IEnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final ICourseService courseService = new CourseServiceImpl();

    private static final String[] COURSE_HEADERS = {"ID", "Tên khóa học", "Thời lượng (h)", "Giảng viên"};
    private static final int[]    COURSE_WIDTHS  = {4, 30, 14, 22};

    private static final String[] ENROLLMENT_HEADERS = {"ID ĐK", "Học viên", "Ngày đăng ký", "Trạng thái"};
    private static final int[]    ENROLLMENT_WIDTHS  = {6, 24, 19, 12};

    private static final String[] WAITING_ENROLLMENT_HEADERS = {"ID ĐK", "Học viên", "Khóa học", "Giảng viên", "Ngày đăng ký", "Trạng thái"};
    private static final int[]    WAITING_ENROLLMENT_WIDTHS  = {6, 24, 30, 24, 19, 12};

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void show() {
        while (true) {
            String[] options = {
                    "Xem danh sách SV đăng ký theo khóa học",
                    "Duyệt đăng ký (WAITING -> CONFIRM)",
                    "Xóa SV khỏi khóa học"
            };
            ConsoleUtil.printMenu("QUẢN LÝ ĐĂNG KÝ KHÓA HỌC", options, "Quay lại menu Admin");

            int choice = ConsoleUtil.readInt("  Lựa chọn: ");
            System.out.println();

            switch (choice) {
                case 1 -> viewEnrollmentsByCourse();
                case 2 -> approveEnrollment();
                case 3 -> removeStudentFromCourse();
                case 0 -> { return; }
                default -> ConsoleUtil.printError("Lựa chọn không hợp lệ.");
            }

            ConsoleUtil.pressEnterToContinue();
        }
    }

    // ── 1. Xem danh sách SV đăng ký theo khóa học ───────────────────

    private void viewEnrollmentsByCourse() {
        ConsoleUtil.printTitle("CHỌN KHÓA HỌC");
        ConsoleUtil.printTable(COURSE_HEADERS, toCourseRows(courseService.getAllCourses()), COURSE_WIDTHS);

        int courseId = ConsoleUtil.readInt("  Nhập ID khóa học (0 để hủy): ");
        if (courseId == 0) return;

        Optional<Course> courseOpt = courseService.getCourseById(courseId);
        if (courseOpt.isEmpty()) {
            ConsoleUtil.printError("Không tìm thấy khóa học với ID = " + courseId);
            return;
        }

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        ConsoleUtil.printTitle("HỌC VIÊN ĐĂNG KÝ — " + courseOpt.get().getName());
        ConsoleUtil.printTable(ENROLLMENT_HEADERS, toEnrollmentRows(enrollments), ENROLLMENT_WIDTHS);
        ConsoleUtil.printInfo("Tổng số: " + enrollments.size() + " đăng ký.");
    }

    // ── 2. Duyệt đăng ký ─────────────────────────────────────────

    private void approveEnrollment() {
        ConsoleUtil.printTitle("DUYỆT/TỪ CHỐI ĐĂNG KÝ KHÓA HỌC");

        ConsoleUtil.printTable(WAITING_ENROLLMENT_HEADERS, toWaitingEnrollmentRows(enrollmentService.getEnrollments()), WAITING_ENROLLMENT_WIDTHS);

        int enrollmentId = ConsoleUtil.readInt("  Nhập ID đăng ký cần duyệt (0 để hủy): ");
        if (enrollmentId == 0) return;

        String action = ConsoleUtil.readLine("   Duyệt đăng ký khóa học này (Y/N): ");
        if (action.equalsIgnoreCase("y")) {
            String error = enrollmentService.approveEnrollment(enrollmentId);
            if (error != null) {
                ConsoleUtil.printError(error);
            } else {
                ConsoleUtil.printSuccess("Đã duyệt đăng ký #" + enrollmentId + " -> CONFIRM.");
            }
        } else if (action.equalsIgnoreCase("n")) {
            String error = enrollmentService.denyEnrollment(enrollmentId);
            if (error != null) {
                ConsoleUtil.printError(error);
            } else {
                ConsoleUtil.printSuccess("Đã từ chối đăng ký #" + enrollmentId + " -> DENIED.");
            }
        } else {
            ConsoleUtil.printError("Vui lòng lựa cho hợp lệ!");
        }

    }

    // ── 4. Xóa SV khỏi khóa học ────────────────────────────────────

    private void removeStudentFromCourse() {
        ConsoleUtil.printTitle("XÓA HỌC VIÊN KHỎI KHÓA HỌC");

        int enrollmentId = ConsoleUtil.readInt("  Nhập ID đăng ký cần xóa (0 để hủy): ");
        if (enrollmentId == 0) return;

        ConsoleUtil.printWarning("Thao tác này sẽ xóa hẳn bản ghi đăng ký, không thể hoàn tác.");
        String confirm = ConsoleUtil.readLine("  Xác nhận (Y/N): ");

        if (!confirm.equalsIgnoreCase("Y")) {
            ConsoleUtil.printInfo("Đã hủy thao tác.");
            return;
        }

        String error = enrollmentService.removeStudentFromCourse(enrollmentId);
        if (error != null) {
            ConsoleUtil.printError(error);
        } else {
            ConsoleUtil.printSuccess("Đã xóa đăng ký #" + enrollmentId + " khỏi khóa học.");
        }
    }

    // ── Helpers ──────────────────────────────────────────────────

    private String[][] toCourseRows(List<Course> courses) {
        return courses.stream().map(c -> new String[]{
                String.valueOf(c.getId()),
                c.getName(),
                c.getDuration() + " h",
                c.getInstructor()
        }).toArray(String[][]::new);
    }

    private String[][] toEnrollmentRows(List<Enrollment> enrollments) {
        return enrollments.stream().map(e -> new String[]{
                String.valueOf(e.getId()),
                e.getStudentName(),
                e.getRegisteredAt() != null ? e.getRegisteredAt().format(DATE_TIME_FORMATTER) : "",
                e.getStatus().name()
        }).toArray(String[][]::new);
    }

    private String[][] toWaitingEnrollmentRows(List<Enrollment> enrollments) {
        return enrollments.stream().filter(e -> e.getStatus().name().equalsIgnoreCase("WAITING"))
                .map(e -> new String[]{
                String.valueOf(e.getId()),
                e.getStudentName(),
                e.getCourseName(),
                e.getInstructor(),
                e.getRegisteredAt() != null ? e.getRegisteredAt().format(DATE_TIME_FORMATTER) : "",
                e.getStatus().name()
        }).toArray(String[][]::new);
    }
}
