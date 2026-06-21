package presentation;

import business.ICourseService;
import business.IEnrollmentService;
import business.IStudentService;
import business.impl.CourseServiceImpl;
import business.impl.EnrollmentServiceImpl;
import business.impl.StudentServiceImpl;
import model.Course;
import model.Enrollment;
import model.Student;
import utils.ConsoleUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentView {

    private final Student student;
    private final ICourseService courseService = new CourseServiceImpl();
    private final IEnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final IStudentService studentService = new StudentServiceImpl();

    private static final String[] COURSE_HEADERS = {"ID", "Tên khóa học", "Thời lượng (h)", "Giảng viên"};
    private static final int[]    COURSE_WIDTHS  = {4, 30, 14, 22};

    private static final String[] ENROLLMENT_HEADERS = {"ID", "Khóa học", "Ngày đăng ký", "Trạng thái"};
    private static final int[]    ENROLLMENT_WIDTHS  = {4, 30, 19, 12};

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public StudentView(Student student) {
        this.student = student;
    }

    public void show() {
        while (true) {
            String[] options = {
                    "Xem danh sách khóa học",
                    "Đăng ký khóa học",
                    "Xem khóa học đã đăng ký",
                    "Hủy đăng ký",
                    "Đổi mật khẩu"
            };
            ConsoleUtil.printMenu("MENU HỌC VIÊN — " + student.getName(), options, "Đăng xuất");

            int choice = ConsoleUtil.readInt("  Lựa chọn: ");
            System.out.println();

            switch (choice) {
                case 1 -> viewCourses();
                case 2 -> registerCourse();
                case 3 -> viewMyEnrollments();
                case 4 -> cancelEnrollment();
                case 5 -> changePassword();
                case 0 -> {
                    ConsoleUtil.printInfo("Đã đăng xuất.");
                    return;
                }
                default -> ConsoleUtil.printError("Lựa chọn không hợp lệ.");
            }

            ConsoleUtil.pressEnterToContinue();
        }
    }

    // ── 1. Xem danh sách khóa học (+ tìm kiếm theo tên) ─────────────

    private void viewCourses() {
        String[] subOptions = { "Xem tất cả khóa học", "Tìm kiếm theo tên" };
        ConsoleUtil.printMenu("XEM KHÓA HỌC", subOptions, "Quay lại");

        int choice = ConsoleUtil.readInt("  Lựa chọn: ");
        List<Course> courses;

        switch (choice) {
            case 1 -> courses = courseService.getAllCourses();
            case 2 -> {
                String keyword = ConsoleUtil.readLine("  Nhập từ khóa: ");
                courses = courseService.searchCourseByName(keyword);
            }
            case 0 -> { return; }
            default -> {
                ConsoleUtil.printError("Lựa chọn không hợp lệ.");
                return;
            }
        }

        ConsoleUtil.printPaginatedTable("DANH SÁCH KHÓA HỌC", COURSE_HEADERS, toCourseRows(courses), COURSE_WIDTHS);
    }

    // ── 2. Đăng ký khóa học ───────────────────────────────────────

    private void registerCourse() {
        ConsoleUtil.printTitle("ĐĂNG KÝ KHÓA HỌC");
        ConsoleUtil.printPaginatedTable("DANH SÁCH KHÓA HỌC", COURSE_HEADERS,
                toCourseRows(courseService.getAllCourses()), COURSE_WIDTHS);

        int courseId = ConsoleUtil.readInt("  Nhập ID khóa học muốn đăng ký (0 để hủy): ");
        if (courseId == 0) return;

        try {
            String error = enrollmentService.registerCourse(student.getId(), courseId);
            if (error != null) {
                ConsoleUtil.printError(error);
            } else {
                ConsoleUtil.printSuccess("Đăng ký thành công! Trạng thái: WAITING (chờ xác nhận).");
            }
        } catch (RuntimeException e) {
            ConsoleUtil.printError("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // ── 3. Xem khóa học đã đăng ký (+ sắp xếp) ──────────────────────

    private void viewMyEnrollments() {
        String[] subOptions = {
                "Xem tất cả (mới đăng ký trước)",
                "Sắp xếp theo tên khóa học — Tăng dần",
                "Sắp xếp theo tên khóa học — Giảm dần",
                "Sắp xếp theo ngày đăng ký — Tăng dần",
                "Sắp xếp theo ngày đăng ký — Giảm dần"
        };
        ConsoleUtil.printMenu("KHÓA HỌC ĐÃ ĐĂNG KÝ", subOptions, "Quay lại");

        int choice = ConsoleUtil.readInt("  Lựa chọn: ");
        List<Enrollment> enrollments;

        switch (choice) {
            case 1 -> enrollments = enrollmentService.getEnrollmentsByStudent(student.getId());
            case 2 -> enrollments = enrollmentService.getSortedEnrollmentsByStudent(student.getId(), "courseName", "ASC");
            case 3 -> enrollments = enrollmentService.getSortedEnrollmentsByStudent(student.getId(), "courseName", "DESC");
            case 4 -> enrollments = enrollmentService.getSortedEnrollmentsByStudent(student.getId(), "registeredAt", "ASC");
            case 5 -> enrollments = enrollmentService.getSortedEnrollmentsByStudent(student.getId(), "registeredAt", "DESC");
            case 0 -> { return; }
            default -> {
                ConsoleUtil.printError("Lựa chọn không hợp lệ.");
                return;
            }
        }

        ConsoleUtil.printPaginatedTable("KHÓA HỌC ĐÃ ĐĂNG KÝ — " + student.getName(),
                ENROLLMENT_HEADERS, toEnrollmentRows(enrollments), ENROLLMENT_WIDTHS);
    }

    // ── 4. Hủy đăng ký ───────────────────────────────────────────

    private void cancelEnrollment() {
        ConsoleUtil.printTitle("HỦY ĐĂNG KÝ KHÓA HỌC");

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student.getId());
        ConsoleUtil.printTable(ENROLLMENT_HEADERS, toEnrollmentRows(enrollments), ENROLLMENT_WIDTHS);
        ConsoleUtil.printInfo("Lưu ý: chỉ có thể hủy đăng ký đang ở trạng thái WAITING.");

        int enrollmentId = ConsoleUtil.readInt("  Nhập ID đăng ký cần hủy (0 để hủy thao tác): ");
        if (enrollmentId == 0) return;

        ConsoleUtil.printWarning("Bạn có chắc muốn hủy đăng ký này không?");
        String confirm = ConsoleUtil.readLine("  Xác nhận (Y/N): ");

        if (!confirm.equalsIgnoreCase("Y")) {
            ConsoleUtil.printInfo("Đã hủy thao tác.");
            return;
        }

        String error = enrollmentService.cancelEnrollment(enrollmentId, student.getId());
        if (error != null) {
            ConsoleUtil.printError(error);
        } else {
            ConsoleUtil.printSuccess("Đã hủy đăng ký thành công.");
        }
    }

    // ── 5. Đổi mật khẩu ───────────────────────────────────────────

    private void changePassword() {
        ConsoleUtil.printTitle("ĐỔI MẬT KHẨU");

        String oldPassword     = ConsoleUtil.readLine("  Mật khẩu cũ          : ");
        String newPassword     = ConsoleUtil.readLine("  Mật khẩu mới         : ");
        String confirmPassword = ConsoleUtil.readLine("  Xác nhận mật khẩu mới: ");

        String error = studentService.changePassword(student.getId(), oldPassword, newPassword, confirmPassword);
        if (error != null) {
            ConsoleUtil.printError(error);
        } else {
            ConsoleUtil.printSuccess("Đổi mật khẩu thành công! Vui lòng dùng mật khẩu mới ở lần đăng nhập sau.");
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
                e.getCourseName(),
                e.getRegisteredAt() != null ? e.getRegisteredAt().format(DATE_TIME_FORMATTER) : "",
                e.getStatus().name()
        }).toArray(String[][]::new);
    }
}
